package pipeline;


public enum MediaType {

    IMAGE_JPEG("image/jpeg"),
    IMAGE_HEIC("image/heic"),
    IMAGE_HEIF("image/heif"),
    IMAGE_PNG("image/png"),
    IMAGE_TIFF("image/tiff"),
    IMAGE_WEBP("image/webp"),
    ;

    public final String typeName;

    MediaType(String typeName) {
        this.typeName = typeName;
    }

    public static MediaType fromTypeName(String typeName) {
        for (var it : MediaType.values()) {
            if (it.typeName.equals(typeName))
                return it;
        }
        return null;
    }
}
