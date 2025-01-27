package pipeline;

import java.util.HashMap;
import java.util.Map;


public final class Artifacts {

    private final Map<String, String> metadata = new HashMap<>();
    private final Map<String, Artifact<?>> artifacts = new HashMap<>();

    public void addArtifact(String name, Artifact<?> artifact) {
        this.artifacts.put(name, artifact);
    }

    public Artifact<?> getArtifact(String name) {
        return this.artifacts.get(name);
    }

    public boolean hasArtifact(String name) {
        return this.artifacts.containsKey(name);
    }

    public void deleteArtifact(String name) {
        this.artifacts.remove(name);
    }

    public void addMetadata(String name, String value) {
        this.metadata.put(name, value);
    }

    public boolean hasMetadata(String name) {
        return this.metadata.containsKey(name);
    }

    public void deleteMetadata(String name) {
        this.metadata.remove(name);
    }

    public Map<String, String> metadata() {
        return Map.copyOf(this.metadata);
    }


    public interface Artifact<T> {

        T artifact();

        static TempBlobArtifact of(TempBlob tempBlob) {
            return new TempBlobArtifact(tempBlob);
        }

        record TempBlobArtifact(TempBlob artifact) implements Artifact<TempBlob> {
        }
    }
}
