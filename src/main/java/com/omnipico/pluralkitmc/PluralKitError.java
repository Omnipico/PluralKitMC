package com.omnipico.pluralkitmc;

public class PluralKitError {
    String message;
    Integer retryAfter;
    Integer code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(int retryAfter) {
        this.retryAfter = retryAfter;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
