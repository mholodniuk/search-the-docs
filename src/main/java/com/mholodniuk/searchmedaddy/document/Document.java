package com.mholodniuk.searchmedaddy.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@org.springframework.data.elasticsearch.annotations.Document(indexName = "documents")
public class Document {
    @Id
    private String ID;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Text)
    private String text;
    @Field(type = FieldType.Integer)
    private Integer page;

    public Document(String name, String content, int page) {
        this.name = name;
        this.text = content;
        this.page = page;
    }

    public static String getIndexName() {
        return "documents";
    }
}
