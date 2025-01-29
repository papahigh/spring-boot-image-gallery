package pipeline;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.io.file.PathUtils;
import pipeline.Pipeline.PipelineStep;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;


@ToString
@RequiredArgsConstructor
public final class UnitOfWork implements AutoCloseable {

    private final Map<String, Object> artifacts = new HashMap<>();

    public TempBlob input() {
        return getArtifact(INPUT_ARTIFACT_NAME, TempBlob.class);
    }

    public <T> void addArtifact(String name, T artifact) {
        if (artifacts.containsKey(name))
            throw new RuntimeException("Artifact '%s' already exists".formatted(name));
        artifacts.put(name, artifact);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArtifact(String name, Class<T> type) {
        return (T) artifacts.get(name);
    }

    public Object getArtifact(String name) {
        return artifacts.get(name);
    }

    public boolean hasArtifact(String name) {
        return artifacts.containsKey(name);
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
        PathUtils.deleteDirectory(this.input().directory());
    }

    public interface ArtifactNames {
        String INPUT_ARTIFACT_NAME = "unitOfWork:input";
        String METADATA_ARTIFACT_NAME = "unitOfWork:metadata";
    }
}
