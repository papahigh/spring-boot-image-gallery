package pipeline;


import pipeline.TempBlob.FileName;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.FilenameUtils.getExtension;

public enum MediaType {

    IMAGE_AVIF("image/avif", "AV1 Image File Format", "avif"),
    IMAGE_JPEG("image/jpeg", "Joint Photographic Experts Group", "jpg"),
    IMAGE_HEIC("image/heif", "High Efficiency Image File Format", "heic"),
    IMAGE_HEIF("image/heif", "High Efficiency Image File Format", "heif"),
    IMAGE_PNG("image/png", "Portable Network Graphics", "png"),
    IMAGE_TIFF("image/tiff", "Tagged Image File Format", "tiff"),
    IMAGE_WEBP("image/webp", "Web Pictures", "webp"),
    ;

    public final String typeName;
    public final String longName;
    public final String extension;

    MediaType(String typeName, String longName, String extension) {
        this.typeName = typeName;
        this.longName = longName;
        this.extension = extension;
    }

    public String randomFilename() {
        return FileName.randomFilename(extension);
    }

    public static MediaType ofTypeName(String typeName) {
        for (var it : MediaType.values()) {
            if (it.typeName.equals(typeName))
                return it;
        }
        return null;
    }

    public static MediaType ofFileName(String fileName) {
        return typeByExtension.get(getExtension(fileName));
    }

    private static final Map<String, MediaType> typeByExtension = new HashMap<>() {{
        put("avif", IMAGE_AVIF);
        put("jpe", IMAGE_JPEG);
        put("jpg", IMAGE_JPEG);
        put("jpeg", IMAGE_JPEG);
        put("heic", IMAGE_HEIC);
        put("heif", IMAGE_HEIF);
        put("png", IMAGE_PNG);
        put("tif", IMAGE_TIFF);
        put("tiff", IMAGE_TIFF);
        put("webp", IMAGE_WEBP);
    }};
}
