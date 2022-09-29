package uy.com.md.common.exception;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 1718066005619749653L;

	public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "NotFoundException: " + getMessage();
    }

}
