package pipeline;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public final class Pipeline {

    private final List<PipelineStep> steps;

    public UnitOfWork process(TempBlob blob) {
        var unitOfWork = UnitOfWork.of(blob);
        steps.forEach(unitOfWork::accept);
        return unitOfWork;
    }


    public interface PipelineStep {
        void process(UnitOfWork unitOfWork) throws Exception;
    }
}
