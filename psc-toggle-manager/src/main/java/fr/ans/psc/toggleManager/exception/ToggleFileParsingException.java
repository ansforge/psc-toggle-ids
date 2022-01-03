package fr.ans.psc.toggleManager.exception;

public class ToggleFileParsingException extends RuntimeException {

    public ToggleFileParsingException() {
    }

    public ToggleFileParsingException(String message) {
        super(message);
    }

    public ToggleFileParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToggleFileParsingException(Throwable cause) {
        super(cause);
    }

    public ToggleFileParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
