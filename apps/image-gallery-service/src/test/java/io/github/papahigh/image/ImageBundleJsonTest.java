package io.github.papahigh.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.papahigh.types.ImageBundle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
public class ImageBundleJsonTest {

    private static final ImageBundle BUNDLE = ImageBundle.builder()
            .webp("http://localhost:8080/image.webp")
            .avif("http://localhost:8080/image.avif")
            .jpeg("http://localhost:8080/image.jpeg")
            .build();

    private static final String JSON = """
            {
                "avif": "http://localhost:8080/image.avif",
                "jpeg": "http://localhost:8080/image.jpeg",
                "webp": "http://localhost:8080/image.webp"
            }
            """.replaceAll("\\s+", "");

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws JsonProcessingException {
        var output = objectMapper.writeValueAsString(BUNDLE);
        assertEquals(JSON, output);
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        var output = objectMapper.readValue(JSON, ImageBundle.class);
        assertEquals(BUNDLE, output);
    }
}
