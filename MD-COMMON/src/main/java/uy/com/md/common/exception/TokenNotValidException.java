package uy.com.md.common.exception;

public class TokenNotValidException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4736697464406453182L;

	public TokenNotValidException() {
    }

    public TokenNotValidException(String message) {
        super(message);
    }

    public TokenNotValidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "TokenNotValidException: " + getMessage();
    }

}
