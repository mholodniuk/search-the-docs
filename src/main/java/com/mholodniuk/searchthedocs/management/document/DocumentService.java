package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.customer.CustomerRepository;
import com.mholodniuk.searchthedocs.management.document.dto.CreateDocumentRequest;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentDTO;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentResponse;
import com.mholodniuk.searchthedocs.management.document.mapper.DocumentMapper;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;


    public DocumentDTO createDocument(CreateDocumentRequest createDocumentRequest) {
        if (documentRepository.existsByNameAndRoomId(createDocumentRequest.name(), createDocumentRequest.roomId())) {
            var errors = List.of(new ErrorMessage("name", "Room already contains document with given name", List.of(createDocumentRequest.name(), createDocumentRequest.roomId())));
            throw new InvalidResourceUpdateException("Cannot create entity", errors);
        }
        var owner = customerRepository.findById(createDocumentRequest.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("No customer with id %s found".formatted(createDocumentRequest.ownerId())));
        var room = roomRepository.findById(createDocumentRequest.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(createDocumentRequest.roomId())));
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

        return DocumentMapper.toDTO(document);
    }

    public List<DocumentDTO> findAllDocuments() {
        return documentRepository.findAll().stream()
                .map(DocumentMapper::toDTO)
                .toList();
    }

    public List<DocumentDTO> findDocumentsInRoom(Long roomId) {
        return documentRepository.findAllByRoomId(roomId).stream()
                .map(DocumentMapper::toDTO)
                .toList();
    }

    public Optional<DocumentResponse> findDocumentById(UUID id) {
        return documentRepository.findByIdWithExtraInfo(id).map(DocumentMapper::toResponse);
    }
}
