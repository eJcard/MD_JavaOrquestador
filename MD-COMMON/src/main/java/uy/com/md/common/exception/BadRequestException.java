package uy.com.md.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BadRequestException extends Exception {

    private static final long serialVersionUID = -9064915734428051959L;

    private String field;
    private String type;

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
    public BadRequestException(String field, String type, String message) {
        super(message);
        this.field = field;
        this.type = type;
    }

    @Override
    public String toString() {
        return "BadRequestException: " + getMessage();
    }

}