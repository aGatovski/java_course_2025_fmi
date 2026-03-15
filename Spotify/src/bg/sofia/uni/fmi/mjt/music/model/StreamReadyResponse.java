package bg.sofia.uni.fmi.mjt.music.model;

public record StreamReadyResponse(String encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize,
                                  float frameRate, boolean bigEndian, String clientId, String song) { }