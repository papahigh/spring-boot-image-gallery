package pipeline;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


@Builder
public final class Pipeline {

    @Singular
    private final List<PipelineStep> steps;


    public UnitOfWork process(TempBlob blob) {
        var unitOfWork = UnitOfWork.of(blob);
        steps.forEach(unitOfWork::accept);
        return unitOfWork;
    }


    public interface PipelineStep {
        void process(UnitOfWork unitOfWork) throws Exception;
    }

    @RequiredArgsConstructor
    public static class ConditionalStep implements PipelineStep {
        private final Predicate<UnitOfWork> predicate;
        private final PipelineStep step;

        @Override
        public void process(UnitOfWork unitOfWork) throws Exception {
            if (predicate.test(unitOfWork)) {
                step.process(unitOfWork);
            }
        }
    }

    public static class ContentTypeStep extends ConditionalStep {
        public ContentTypeStep(PipelineStep step, String... contentTypes) {
            super(createPredicate(contentTypes), step);
        }

        private static Predicate<UnitOfWork> createPredicate(String... contentTypes) {
            var set = Set.of(contentTypes);
            return unit -> set.contains(unit.target().contentType());
        }
    }
}
