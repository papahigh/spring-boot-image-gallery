# pipeline-image

The **`pipeline-image`** module provides a flexible and extensible API for building image processing pipelines.
The module offers integration with [Deep Java Library](https://djl.ai/),
[ImageMagick](https://imagemagick.org/), and [metadata-extractor](https://github.com/drewnoakes/metadata-extractor).


## Java API

The `ImagePipeline` class serves as the entry point for building configurable and extensible
image processing pipelines. It provides a fluent API to define a sequence of processing steps
that can be applied to image artifacts. The steps include classification, metadata extraction,
and general image manipulation using ImageMagick.


```java
import pipeline.MediaType;
import pipeline.TempBlob;
import pipeline.image.ImagePipeline;
import java.nio.file.Files;
import java.nio.file.Path;

class Example {

    public static void main(String[] args) {

        // @formatter:off
        var pipeline = ImagePipeline.builder()
                .extractMetadata()
                    .outputName("metadata")
                .and()
                .imageMagick()
                    .outputName("thumbnail_jpg")
                    .outputType(MediaType.IMAGE_JPEG)
                    .resize("300")
                    .quality("80%")
                    .strip()
                .and()
                .imageMagick()
                    .outputName("thumbnail_webp")
                    .outputType(MediaType.IMAGE_WEBP)
                    .resize("200x200")
                    .quality("80%")
                    .strip()
                .and()
                .classifyImage()
                    .outputName("classification")
                    .predictorFactory(zooModel::newPredictor)
                    .topK(10)
                .build();
        // @formatter:on
        
        var blob = TempBlob.builder("dog.jpg")
                .mediaType(MediaType.IMAGE_JPEG)
                .content(Files.newInputStream(Path.of("dog.jpg")))
                .build();

        var output = pipeline.process(blob);
        
        System.out.println(output.getArtifact("thumbnail_jpg"));
        System.out.println(output.getArtifact("thumbnail_webp"));
        System.out.println(output.getArtifact("classification"));
        System.out.println(output.getArtifact("metadata"));
    }
}
```