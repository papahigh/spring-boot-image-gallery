package pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import pipeline.Pipeline.PipelineStep;

import java.io.IOException;
import java.nio.file.Files;


@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class UnitOfWork implements AutoCloseable {

    private final Artifacts artifacts = new Artifacts();
    private final TempBlob target;

    @SneakyThrows
    void accept(PipelineStep step) {
        step.process(this);
    }

    public static UnitOfWork of(TempBlob blob) {
        return new UnitOfWork(blob);
    }

    @Override
    public void close() throws IOException {
        Files.deleteIfExists(this.target.directory());
    }
}
