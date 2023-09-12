package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.document.DocumentIndexService;
import com.mholodniuk.searchthedocs.file.FileService;
import com.mholodniuk.searchthedocs.management.user.UserRepository;
import com.mholodniuk.searchthedocs.management.room.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock
    private DocumentIndexService documentIndexService;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private FileService fileService;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void uploadDocument() {
        var file = new MockMultipartFile("file", "sample2.pdf", "application/pdf", new byte[]{1, 2, 3, 4});

    }

    @Test
    void saveDocument() {
    }

    @Test
    void findAllDocuments() {
    }

    @Test
    void findDocumentsInRoom() {
    }

    @Test
    void findDocumentById() {
    }
}