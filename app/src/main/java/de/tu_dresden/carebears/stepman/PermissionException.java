package de.tu_dresden.carebears.stepman;

/**
 * Created by jan on 11.12.15.
 */
public class PermissionException extends Exception {

    PermissionException() {
        super();
    }

    PermissionException(String message) {
        super(message);
    }

    PermissionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    PermissionException(Throwable throwable) {
        super(throwable);
    }
}
