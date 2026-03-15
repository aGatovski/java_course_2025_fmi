package bg.sofia.uni.fmi.mjt.music.model;

public record ServerResponse(ResponseType type, String message, StreamReadyResponse streamInfo) {

    public static ServerResponse message(String message) {
        return new ServerResponse(ResponseType.MESSAGE, message, null);
    }

    public static ServerResponse streamReady(StreamReadyResponse streamInfo) {
        return new ServerResponse(ResponseType.STREAM_READY, null, streamInfo);
    }

    public static ServerResponse stopAck() {
        return new ServerResponse(ResponseType.STOP_ACK, null, null);
    }
}
