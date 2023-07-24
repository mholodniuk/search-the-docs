# search-me-daddy

## Links
https://discuss.elastic.co/t/how-to-index-and-store-pdf-file-in-elastic-search-using-spring-boot/220289/35
https://discuss.elastic.co/t/how-to-index-text-files-pdf-doc-txt-in-java/321678/5

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

