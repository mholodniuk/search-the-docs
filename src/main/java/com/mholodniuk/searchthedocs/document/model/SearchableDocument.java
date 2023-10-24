package com.mholodniuk.searchthedocs.document.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(indexName = "documents")
public class SearchableDocument {
    @Id
    private String ID;
    @Field(type = FieldType.Keyword)
    private String documentId;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Text)
    private String text;
    @Field(type = FieldType.Integer)
    private Integer page;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private Date uploadedAt;
    @Field(type = FieldType.Nested)
    private SearchableRoom room;
    @Field(type = FieldType.Nested)
    private SearchableUser owner;

    public SearchableDocument(String id,
                              String name,
                              String content,
                              int page,
                              SearchableRoom room,
                              SearchableUser user) {
        this(id, name, content, page);
        this.room = room;
        this.owner = user;
    }

    public SearchableDocument(String id, String name, String content, int page) {
        this.documentId = id;
        this.name = name;
        this.text = content;
        this.page = page;
    }

    public static String getIndexName() {
        return "documents";
    }
}