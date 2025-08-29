package com.scity.user.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final int code;

    public BadRequestException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public BadRequestException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BadRequestException(String objectName, String fieldName, Object value) {
        super(objectName + " đã tồn tại " + fieldName + ": " + value);
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public int getCode() {
        return this.code;
    }
}
