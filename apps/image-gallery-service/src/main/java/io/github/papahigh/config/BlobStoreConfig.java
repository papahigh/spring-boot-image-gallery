package io.github.papahigh.config;


import blobstore.BlobStore;
import blobstore.fs.FsBlobStore;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import static io.github.papahigh.config.BlobStoreConfig.FsBlobStoreProperties;


@Configuration
@EnableConfigurationProperties({FsBlobStoreProperties.class})
public class BlobStoreConfig {

    @Bean
    @ConditionalOnProperty(value = "blobstore.fs.enabled")
    public BlobStore fsBlobStore(FsBlobStoreProperties properties) {
        return FsBlobStore.builder()
                .rootPath(Path.of(properties.getRootPath()))
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Data
    @ConfigurationProperties(prefix = "blobstore.fs")
    public static class FsBlobStoreProperties {
        private boolean enabled = true;
        private String baseUrl = "http://localhost:8080/uploads/";
        private String rootPath = "/tmp/blobstore/";
        private String cacheControl = "public, max-age=31536000";
    }
}
