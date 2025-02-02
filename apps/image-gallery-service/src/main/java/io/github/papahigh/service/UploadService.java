package io.github.papahigh.service;


import blobstore.BlobStore.BlobStoreException;
import io.github.papahigh.types.ImageDetails;
import org.jetbrains.annotations.NotNull;
import pipeline.TempBlob;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public interface UploadService {

    CompletableFuture<ImageDetails> upload(@NotNull TempBlob tempBlob) throws IOException, BlobStoreException;

}
