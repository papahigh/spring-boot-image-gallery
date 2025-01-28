package pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import pipeline.Pipeline.PipelineStep;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;


@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class UnitOfWork implements AutoCloseable {

    private final Map<String, Object> artifacts = new HashMap<>();

    public TempBlob input() {
        return getArtifact(INPUT_ARTIFACT_NAME, TempBlob.class);
    }

    public <T> void addArtifact(String name, T artifact) {
        this.artifacts.put(name, artifact);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArtifact(String name, Class<T> type) {
        return (T) this.artifacts.get(name);
    }

    public boolean hasArtifact(String name) {
        return this.artifacts.containsKey(name);
    }

    @SneakyThrows
    void accept(PipelineStep step) {
        step.process(this);
    }

    public static UnitOfWork of(TempBlob blob) {
        var unitOfWork = new UnitOfWork();
        unitOfWork.artifacts.put(INPUT_ARTIFACT_NAME, blob);
        return unitOfWork;
    }

    @Override
    public void close() throws IOException {
        Files.deleteIfExists(this.input().directory());
    }

    public interface ArtifactNames {
        String INPUT_ARTIFACT_NAME = "unitOfWork:input";
        String METADATA_ARTIFACT_NAME = "unitOfWork:metadata";
    }
}
