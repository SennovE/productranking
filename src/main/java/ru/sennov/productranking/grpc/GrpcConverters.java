package ru.sennov.productranking.grpc;

import com.google.protobuf.Timestamp;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.util.StringUtils;

final class GrpcConverters {

    private GrpcConverters() {
    }

    static UUID requiredUuid(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return parseUuid(value, fieldName);
    }

    static UUID optionalUuid(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return parseUuid(value, fieldName);
    }

    static String uuidToString(UUID value) {
        return value == null ? "" : value.toString();
    }

    static Instant optionalInstant(boolean present, Timestamp value, String fieldName) {
        if (!present) {
            return null;
        }
        return instant(value, fieldName);
    }

    static Timestamp timestamp(Instant value) {
        if (value == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(value.getEpochSecond())
                .setNanos(value.getNano())
                .build();
    }

    static BigDecimal optionalBigDecimal(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a decimal value", exception);
        }
    }

    private static UUID parseUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID", exception);
        }
    }

    private static Instant instant(Timestamp value, String fieldName) {
        try {
            return Instant.ofEpochSecond(value.getSeconds(), value.getNanos());
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid timestamp", exception);
        }
    }
}
