package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.document.DocumentIndexService;
import com.mholodniuk.searchthedocs.document.model.SearchableRoom;
import com.mholodniuk.searchthedocs.document.model.SearchableUser;
import com.mholodniuk.searchthedocs.file.FileService;
import com.mholodniuk.searchthedocs.file.dto.FileUploadResponse;
import com.mholodniuk.searchthedocs.file.exception.FileSavingException;
import com.mholodniuk.searchthedocs.management.user.UserRepository;
import com.mholodniuk.searchthedocs.management.document.dto.*;
import com.mholodniuk.searchthedocs.management.document.mapper.DocumentMapper;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mholodniuk.searchthedocs.file.mock.S3Mock.MOCK_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DocumentIndexService documentIndexService;
    private final FileService fileService;

    public FileUploadResponse uploadDocument(MultipartFile file, Long roomId, Long ownerId) {
        try {
            final var filename = file.getOriginalFilename();
            final var bytes = file.getBytes();
            log.info("Saving file with name: {}", filename);
            var createDocumentRequest = buildRequest(file, ownerId, roomId);

            var createdDocument = saveDocument(createDocumentRequest);
            var documentId = documentIndexService.indexDocument(
                    bytes,
                    createdDocument.id().toString(),
                    file.getContentType(),
                    filename,
                    new SearchableUser(
                            createdDocument.owner().id(),
                            createdDocument.owner().username(),
                            createdDocument.owner().displayName(),
                            createdDocument.owner().email()
                    ),
                    new SearchableRoom(
                            createdDocument.room().id(),
                            createdDocument.room().name()
                    ));
            var saveStatus = fileService.saveFile(bytes, documentId);

            return FileUploadResponse.builder()
                    .id(documentId)
                    .filename(filename)
                    .info(saveStatus.status)
                    .owner(createdDocument.owner().username())
                    .room(createdDocument.room().name())
                    .build();

        } catch (Throwable e) {
            log.error("Failed to save file with name: {}", file.getOriginalFilename());
            throw new FileSavingException(e);
        }
    }

    DocumentResponse saveDocument(CreateDocumentRequest createDocumentRequest) {
        if (documentRepository.existsByNameAndRoomId(createDocumentRequest.name(), createDocumentRequest.roomId())) {
            var error = new ErrorMessage("name", "Room already contains document with given name", List.of(createDocumentRequest.name(), createDocumentRequest.roomId()));
            throw new InvalidResourceUpdateException("Cannot create entity", error);
        }
        var owner = userRepository.findById(createDocumentRequest.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("No customer with id %s found".formatted(createDocumentRequest.ownerId())));

        var room = roomRepository.findById(createDocumentRequest.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(createDocumentRequest.roomId())));
        room.setModifiedAt(LocalDateTime.now());

        var fileLocation = new FileLocation(createDocumentRequest.storage(), createDocumentRequest.filePath());

        var document = new Document();
        document.setId(UUID.randomUUID());
        document.setUploadedAt(LocalDateTime.now());
        document.setName(createDocumentRequest.name());
        document.setTags(Collections.emptyList());
        document.setContentType(createDocumentRequest.contentType());
        document.setOwner(owner);
        document.setRoom(room);
        document.setFileLocation(fileLocation);

        documentRepository.save(document);

        return DocumentMapper.toResponse(document);
    }

    void deleteByDocumentId(String documentId) {
        documentRepository.deleteById(UUID.fromString(documentId));
        documentIndexService.deleteDocumentById(documentId);
        log.info("Delete document result: " + fileService.deleteFile(documentId));
    }

    TagsAssignedResponse assignTags(String documentId, AssignTagsRequest assignTagsRequest) {
        var document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() -> new ResourceNotFoundException("No document with id %s found".formatted(documentId)));

        var filteredTags = assignTagsRequest.tags().stream().filter(tag -> tag != null && !tag.isEmpty()).toList();
        document.setTags(filteredTags);
        documentRepository.save(document);

        return new TagsAssignedResponse(documentId, document.getTags());
    }

    public List<DocumentDTO> findDocumentsInRoom(Long roomId) {
        return documentRepository.findAllByRoomId(roomId).stream()
                .map(DocumentMapper::toDTO)
                .toList();
    }

    public Optional<DocumentResponse> findDocumentById(UUID id) {
        return documentRepository.findByIdWithExtraInfo(id).map(DocumentMapper::toResponse);
    }

    private CreateDocumentRequest buildRequest(MultipartFile file, Long ownerId, Long roomId) {
        return CreateDocumentRequest.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .filePath("/tmp/s3/" + MOCK_BUCKET_NAME + "/" + file.getOriginalFilename())
                .storage("LOCAL")
                .ownerId(ownerId)
                .roomId(roomId)
                .build();
    }
}
