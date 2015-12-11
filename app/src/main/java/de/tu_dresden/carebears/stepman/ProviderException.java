package de.tu_dresden.carebears.stepman;

/**
 * Created by jan on 11.12.15.
 */
public class ProviderException extends Exception {

    ProviderException() {
        super();
    }

    ProviderException(String message) {
        super(message);
    }

    ProviderException(String message, Throwable throwable) {
        super(message, throwable);
    }

    ProviderException(Throwable throwable) {
        super(throwable);
    }
}