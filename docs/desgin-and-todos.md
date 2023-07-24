## Links

- [index file via spring boot - 1](https://discuss.elastic.co/t/how-to-index-and-store-pdf-file-in-elastic-search-using-spring-boot/220289/35)
- [index file via spring boot - 2](https://discuss.elastic.co/t/how-to-index-text-files-pdf-doc-txt-in-java/321678/5)
- [sample app](https://github.com/andreluiz1987/search-store) 
- [maximum document size](https://discuss.elastic.co/t/maximum-document-size/13086)

## TODOS
- add tests
- figure out how to handle saving AND indexing -> make it testable


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
- 