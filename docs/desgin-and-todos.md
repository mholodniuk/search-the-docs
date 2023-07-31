## Links

- [index file via spring boot - 1](https://discuss.elastic.co/t/how-to-index-and-store-pdf-file-in-elastic-search-using-spring-boot/220289/35)
- [index file via spring boot - 2](https://discuss.elastic.co/t/how-to-index-text-files-pdf-doc-txt-in-java/321678/5)
- [sample app](https://github.com/andreluiz1987/search-store) 
- [maximum document size](https://discuss.elastic.co/t/maximum-document-size/13086)
- [java api client highlight](https://medium.com/@andre.luiz1987/highlighting-java-api-client-866de2cfc699)
- [java api client source](https://medium.com/@andre.luiz1987/more-like-this-query-mlt-java-api-client-f69145593a1b)
- [embed pdf display](https://itnext.io/you-dont-need-external-packages-to-view-pdf-in-angular-e47779f86595)
- [spring data elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#new-features)
- [implementing custom save, saveAll, search](https://medium.com/@luthfihrz/basic-operations-of-elasticsearch-with-spring-boot-and-spring-data-b1aa241ad9c6)

## TODOS
- add tests
- figure out how to handle saving AND indexing -> make it testable
- store all metadata of documents/files in DB -> add reindexing feature 
- bulk indexing pages


### Example usages
POST documents/_search
```json
{
  "query": {
    "query_string": {
      "default_field": "content",
      "query": "phrase to search :)"
    }
  },
  "_source": [
    "_id"
  ],
  "highlight": {
    "fragment_size": 50,
    "fields": {
      "text": {}
    }
  }
}
```

### TODOS
- while uploading pdf split content into pages -> for ignore_above


### Design
- room logic -> room as a place where users "upload" a file, they can be shared among other users
- database -> user and room management (sth light)
- google oauth for easy access
- elasticsearch returns already highlighted result with <em></em> tags -> present it wisely on frontend