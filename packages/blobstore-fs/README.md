# blobstore-fs

This is a Blobstore implementation backed by a file system as the storage backend.


## Java API

Below is a quick Java example demonstrating how to build an `FsBlobStore`:

```java
FsBlobStore blobStore = FsBlobStore.builder()
        .baseUrl("http://localhost:8080/uploads/")
        .rootPath(Path.of("/tmp/blobstore"))
        .build();
```

- `baseUrl` is used to generate an external URL for any file stored.
- `rootPath` is the location on the local filesystem where blobs will be written and retrieved.

