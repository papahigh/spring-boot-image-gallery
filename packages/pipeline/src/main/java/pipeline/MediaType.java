package pipeline;


public enum MediaType {

    IMAGE_JPEG("image/jpeg", "jpg"),
    IMAGE_HEIC("image/heic", "heic"),
    IMAGE_HEIF("image/heif", "heif"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_TIFF("image/tiff", "tiff"),
    IMAGE_WEBP("image/webp", "webp"),
    ;

    public final String typeName;
    public final String extension;

    MediaType(String typeName, String extension) {
        this.typeName = typeName;
        this.extension = extension;
    }

    public String randomFilename() {
        return TempBlob.randomFilename(extension);
    }

    public static MediaType of(String typeName) {
        for (var it : MediaType.values()) {
            if (it.typeName.equals(typeName))
                return it;
        }
        return null;
    }
}
