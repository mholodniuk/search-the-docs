package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.document.dto.CreateDocumentRequest;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentDTO;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentResponse;
import com.mholodniuk.searchthedocs.management.document.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentDTO createDocument(CreateDocumentRequest createDocumentRequest) {
        return null;
    }

    public List<DocumentDTO> findAllDocuments() {
        return documentRepository.findAll().stream()
                .map(DocumentMapper::toDTO)
                .toList();
    }

    public Optional<DocumentResponse> findDocumentById(UUID id) {
        return documentRepository.findByIdWithExtraInfo(id).map(DocumentMapper::toResponse);
    }
}
