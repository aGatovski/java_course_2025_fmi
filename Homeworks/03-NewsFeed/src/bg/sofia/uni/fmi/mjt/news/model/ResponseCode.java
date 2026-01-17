package bg.sofia.uni.fmi.mjt.news.model;

public enum ResponseCode {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    TOO_MANY_REQUESTS(429),
    SERVER_ERROR(500),
    UNKNOWN(0);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResponseCode fromInt(int code) {
        for (ResponseCode status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
