package errors;

import feign.Response;

public class CustomFeignException extends RuntimeException {
    private final int status;
    private final Response.Body body;

    public CustomFeignException(int status, Response.Body body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public Response.Body getBody() {
        return body;
    }
}
