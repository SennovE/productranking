package ru.sennov.productranking.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import ru.sennov.productranking.service.ResourceNotFoundException;

final class GrpcExceptionMapper {

    private GrpcExceptionMapper() {
    }

    static StatusRuntimeException toStatusRuntimeException(Exception exception) {
        Status status;
        if (exception instanceof ResourceNotFoundException) {
            status = Status.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT;
        } else if (exception instanceof DataIntegrityViolationException) {
            status = Status.FAILED_PRECONDITION;
        } else {
            status = Status.INTERNAL;
        }

        String description = exception.getMessage();
        if (description == null || description.isBlank()) {
            description = status.getCode().name();
        }
        return status.withDescription(description).withCause(exception).asRuntimeException();
    }
}
