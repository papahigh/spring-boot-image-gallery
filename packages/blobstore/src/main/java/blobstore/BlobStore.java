package blobstore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


/**
 * The BlobStore interface represents a contract for interacting with a generic blob storage system.
 * It provides methods to retrieve, upload, and delete blobs.
 */
public interface BlobStore {

    /**
     * Retrieves a blob from the storage system at the specified path.
     */
    GetBlobResponse getBlob(@NotNull BlobPath blobPath) throws BlobStoreException;

    /**
     * Uploads a blob to the storage system from the given input file path.
     */
    PutBlobResponse putBlob(@NotNull String key, @NotNull Path blobPath) throws BlobStoreException;

    /**
     * Deletes the blob specified by the given path from the storage system.
     */
    DeleteBlobResponse deleteBlob(@NotNull BlobPath blobPath) throws BlobStoreException;


    interface GetBlobResponse {
        @NotNull InputStream inputStream() throws IOException;
    }

    interface DeleteBlobResponse {
        @NotNull BlobPath blobPath();

        boolean deleted();
    }

    interface PutBlobResponse {
        @NotNull BlobPath blobPath();

        @NotNull String externalUrl();
    }

    class BlobStoreException extends Exception {
        public BlobStoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}



