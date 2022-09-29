package uy.com.md.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9064915734428051959L;

	private String field;
	private String type;
	
	public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
    public UserNotFoundException(String field, String type, String message) {
        super(message);
        this.field = field;
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserNotFoundException: " + getMessage();
    }

}
