package com.scm.helper;

public class ImageValidationException extends RuntimeException  {
    
    // Constructor that accepts a formatted message and arguments
     public ImageValidationException(String message, Object... args) {
        super(String.format(message, args)); // Format the message with arguments
    }
    
    // Constructor that accepts a formatted message, arguments, and a cause
    public ImageValidationException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause); // Format the message with arguments and set the cause
    }
}
