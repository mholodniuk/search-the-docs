package com.mholodniuk.searchmedaddy.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@Document(indexName = "documents")
public class SearchableDocument {
    @Id
    private String ID;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Text)
    private String text;
    @Field(type = FieldType.Integer)
    private Integer page;

    public SearchableDocument(String name, String content, int page) {
        this.name = name;
        this.text = content;
        this.page = page;
    }

    public static String getIndexName() {
        return "documents";
    }
}
