package org.Technical.exception;

public class DependencyFailureException extends RuntimeException{
    public DependencyFailureException(String message,Throwable cause){
        super(message,cause);
    }
}
