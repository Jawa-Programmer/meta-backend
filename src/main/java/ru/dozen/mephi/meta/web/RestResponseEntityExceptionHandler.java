package ru.dozen.mephi.meta.web;

import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ThrowableProblem.class)
    protected ResponseEntity<Object> problemHandler(ThrowableProblem ex, WebRequest request) {
        var statusCode = Optional.ofNullable(ex.getStatus())
                .map(StatusType::getStatusCode)
                .map(HttpStatusCode::valueOf)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        var res = ProblemDetail.forStatusAndDetail(statusCode, ex.getDetail());
        if (ex.getTitle() != null) {
            res.setTitle(ex.getTitle());
        }
        if (ex.getType() != null) {
            res.setType(ex.getType());
        }
        return handleExceptionInternal(ex, res, new HttpHeaders(), statusCode, request);
    }
}