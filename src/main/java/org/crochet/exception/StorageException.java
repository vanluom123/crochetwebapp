package org.crochet.exception;

public class StorageException extends DecoratedRuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, int messageCode) {
        super(message, messageCode);
    }
}
