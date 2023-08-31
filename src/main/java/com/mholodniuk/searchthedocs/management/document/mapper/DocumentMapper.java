package com.mholodniuk.searchthedocs.management.document.mapper;

import com.mholodniuk.searchthedocs.management.customer.mapper.CustomerMapper;
import com.mholodniuk.searchthedocs.management.document.Document;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentDTO;
import com.mholodniuk.searchthedocs.management.document.dto.DocumentResponse;
import com.mholodniuk.searchthedocs.management.room.mapper.RoomMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentMapper {
    public static DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .contentType(document.getContentType())
                .tags(document.getTags())
                .uploadedAt(document.getUploadedAt())
                .filePath(document.getFileLocation().getPath())
                .storage(document.getFileLocation().getStorageProvider())
                .room(RoomMapper.toDTO(document.getRoom()))
                .owner(CustomerMapper.toDTO(document.getOwner()))
                .build();
    }

    public static DocumentDTO toDTO(Document document) {
        return DocumentDTO.builder()
                .id(document.getId())
                .name(document.getName())
                .contentType(document.getContentType())
                .tags(document.getTags())
                .uploadedAt(document.getUploadedAt())
                .filePath(document.getFileLocation().getPath())
                .storage(document.getFileLocation().getStorageProvider())
                .build();
    }

    public static Collection<DocumentDTO> toDTO(Collection<Document> documents) {
        return documents.stream().map(DocumentMapper::toDTO).toList();
    }
}
