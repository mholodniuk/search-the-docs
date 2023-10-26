package com.mholodniuk.searchthedocs.document;

import com.mholodniuk.searchthedocs.document.dto.PhraseSearchResponse;
import com.mholodniuk.searchthedocs.document.extract.ContentExtractor;
import com.mholodniuk.searchthedocs.document.mapper.SearchResponseMapper;
import com.mholodniuk.searchthedocs.document.model.SearchableDocument;
import com.mholodniuk.searchthedocs.document.model.SearchableRoom;
import com.mholodniuk.searchthedocs.document.model.SearchableUser;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIndexService {
    private final SearchService searchService;
    private final Map<String, ContentExtractor> contentExtractors;
    private final DocumentSearchRepository documentSearchRepository;
    private final RoomService roomService;

    public String indexDocument(byte[] file, String id, String contentType, String filename, SearchableUser user, SearchableRoom room) {
        var contentExtractor = contentExtractors.get(contentType);
        var content = contentExtractor.extract(file);

        var documents = IntStream.range(0, content.size())
                .mapToObj(pageIdx -> new SearchableDocument(id, filename, content.get(pageIdx), pageIdx + 1, room.name(), room.id(), user.username(), user.displayName()))
                .peek(document -> log.debug("Indexing page {} with content: {}", document.getPage(), document.getText()))
                .toList();

        documentSearchRepository.saveAll(documents);
        log.info("Indexed {} page(s) of a file: {}", documents.size(), filename);

        return id;
    }

    public Optional<PhraseSearchResponse> searchDocument(String phrase, Long userId) {

        var availableRooms = roomService.findAvailableRooms(userId).stream()
                .map(ExtendedRoomDto::id)
                .collect(Collectors.toSet());

        try {
            var response = searchService.searchDocumentsByPhrase(phrase, availableRooms.stream().toList());
            var searchResponse = SearchResponseMapper.mapToDto(response);

            return Stream.of(searchResponse)
                    .filter(result -> !result.hits().isEmpty())
                    .findAny();
        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteDocumentById(String documentId) {
        documentSearchRepository.deleteByDocumentId(documentId);
    }
}