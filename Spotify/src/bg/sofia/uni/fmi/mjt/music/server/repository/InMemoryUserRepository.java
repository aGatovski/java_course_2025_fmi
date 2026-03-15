package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.loader.ResourcesLoader;
import bg.sofia.uni.fmi.mjt.music.model.User;
import bg.sofia.uni.fmi.mjt.music.validation.PasswordHasher;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentMap<String, User> usersByEmail; // email:user
    private final ConcurrentMap<String, String> loggedInUsers; // clientId:email

    public InMemoryUserRepository() {
        this.usersByEmail = ResourcesLoader.loadUsers();
        this.loggedInUsers = new ConcurrentHashMap<>();
        System.out.println("Loaded " + usersByEmail.size() + " users");
    }

    // Constructor for testing
    InMemoryUserRepository(ConcurrentMap<String, User> usersByEmail, ConcurrentMap<String, String> loggedInUsers) {
        this.usersByEmail = usersByEmail;
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public boolean register(String email, String password) {
        String passwordHash = PasswordHasher.hash(password);
        User newUser = new User(email, passwordHash);

        if (usersByEmail.putIfAbsent(email, newUser) != null) {
            return false;
        }

        save();
        return true;
    }

    @Override
    public boolean login(String email, String password, String clientId) {
        if (isLoggedIn(clientId)) {
            return false;
        }

        User user = usersByEmail.get(email);
        if (user == null || !PasswordHasher.verify(password, user.password())) {
            return false;
        }

        loggedInUsers.put(clientId, email);
        return true;
    }

    @Override
    public boolean logout(String clientId) {
        return loggedInUsers.remove(clientId) != null;
    }

    @Override
    public boolean isLoggedIn(String clientId) {
        return loggedInUsers.containsKey(clientId);
    }

    @Override
    public void save() {
        ResourcesLoader.saveUsers(usersByEmail);
    }

    @Override
    public String getEmailByClientId(String clientId) {
        return loggedInUsers.get(clientId);
    }
}
