package bg.sofia.uni.fmi.mjt.music.server.repository;

public interface UserRepository {

    boolean register(String email, String password);

    boolean login(String email, String password, String clientId);

    boolean logout(String clientId);

    boolean isLoggedIn(String clientId);

    String getEmailByClientId(String clientId);

    void save();
}
