package bg.sofia.uni.fmi.mjt.news.model;

public enum ResponseStatus {
    OK("ok"),
    ERROR("error");

    private final String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ResponseStatus fromString(String value) {
        if (value == null) {
            return null;
        }

        for (ResponseStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown status!");
    }
}
