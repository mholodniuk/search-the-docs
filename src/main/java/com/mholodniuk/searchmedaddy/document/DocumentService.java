package com.mholodniuk.searchmedaddy.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.mholodniuk.searchmedaddy.file.FileReadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String DOCUMENT_INDEX = "documents";

    public String indexDocument(String filePath) throws IOException {
        Document document = parseFile(filePath);

        var docId = UUID.randomUUID().toString();
        IndexResponse response = elasticsearchClient.index(i -> i
                .id(docId)
                .index(DOCUMENT_INDEX)
                .document(document));

        log.info("File {} located in the directory {} was successfully indexed.", docId, document.path());

        return response.result().toString();
    }

    public Document parseFile(String filePath) {
        try (InputStream stream = new FileInputStream(filePath)) {
            var handler = new BodyContentHandler(-1);
            var parser = new PDFParser();
            parser.parse(stream, handler, new Metadata(), new ParseContext());

            return new Document(filePath, handler.toString());
        } catch (Exception e) {
            throw new FileReadingException(e);
        }
    }
}


/* SAMPLE SEARCH QUERY
POST documents/_search
{
  "query": {
    "query_string": {
      "default_field": "text",
      "query": "Comarch"
    }
  },
  "_source": ["_id"],
  "highlight": {
    "fragment_size": 100,
    "fields": {
      "text": {}
    }
  }
}
*/