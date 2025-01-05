package ru.dozen.mephi.meta.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProblemUtils {

    public static ThrowableProblem forStatus(StatusType type, String details) {
        return Problem.valueOf(type, details);
    }

    public static ThrowableProblem notFound(String details) {
        return forStatus(Status.NOT_FOUND, details);
    }

    public static ThrowableProblem badRequest(String details) {
        return forStatus(Status.BAD_REQUEST, details);
    }

    public static ThrowableProblem forbidden(String details) {
        return forStatus(Status.FORBIDDEN, details);
    }
}
