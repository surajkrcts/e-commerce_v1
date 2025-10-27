package com.genc.e_commerce.exception;

public class AuthFailedException extends RuntimeException {

    public AuthFailedException() { super(); }
    public AuthFailedException(String message) {
        super(message); }


}
