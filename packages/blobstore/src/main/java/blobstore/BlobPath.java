package blobstore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public record BlobPath(@NotNull List<String> parts) {

    public static final BlobPath ROOT = new BlobPath(List.of());
    public static final String SEPARATOR = "/";

    @Override
    public List<String> parts() {
        return List.copyOf(parts);
    }

    public String lastPart() {
        if (parts.isEmpty()) return "";
        return parts.getLast();
    }

    public BlobPath parent() {
        int size = size();
        return switch (size) {
            case 0, 1 -> ROOT;
            default -> new BlobPath(parts.subList(0, size - 1));
        };
    }

    public BlobPath resolve(String part) {
        return resolve(BlobPath.of(part));
    }

    public BlobPath resolve(BlobPath other) {
        return new BlobPath(new ArrayList<>(parts) {{
            addAll(other.parts);
        }});
    }

    public int size() {
        return parts.size();
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    @Override
    public String toString() {
        return String.join(SEPARATOR, parts);
    }

    public static BlobPath of(String path) {
        var split = path.split(SEPARATOR);
        var parts = new ArrayList<String>(split.length);
        for (var part : split)
            if (!part.isEmpty())
                parts.add(part);
        return new BlobPath(parts);
    }
}
