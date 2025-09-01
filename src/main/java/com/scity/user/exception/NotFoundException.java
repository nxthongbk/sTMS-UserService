package com.scity.user.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
@Data
public class NotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(String objectName, Object value) {
        super("Không tìm thấy " + objectName + " với id: " + value);
    }

    public NotFoundException(String objectName, String fieldName, Object value) {
        super("Không tìm thấy " + objectName + " với " + fieldName + ": " + value);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
