package fi.bizhop.kiekkohamsteri.exception;

import lombok.Getter;

public class HttpResponseException extends Exception {
    @Getter
    private final int statusCode;

    public HttpResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
