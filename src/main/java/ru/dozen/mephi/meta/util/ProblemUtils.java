package ru.dozen.mephi.meta.util;

import lombok.experimental.UtilityClass;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

@UtilityClass
public class ProblemUtils {

    public ThrowableProblem forStatus(StatusType type, String details) {
        return Problem.valueOf(type, details);
    }

    public ThrowableProblem notFound(String details) {
        return forStatus(Status.NOT_FOUND, details);
    }

    public ThrowableProblem badRequest(String details) {
        return forStatus(Status.BAD_REQUEST, details);
    }

    public ThrowableProblem forbidden(String details) {
        return forStatus(Status.FORBIDDEN, details);
    }
}
