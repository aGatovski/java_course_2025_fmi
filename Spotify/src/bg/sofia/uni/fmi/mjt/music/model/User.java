package bg.sofia.uni.fmi.mjt.music.model;

import java.util.Objects;

public record User(String email, String password) {

    private static final String DELIMITER = ",";

    private static final int EMAIL_IDX = 0;
    private static final int PASSWORD_IDX = 1;

    public User {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }

    public static User of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Input line cannot be null or empty");
        }

        final String[] attributes = line.split(DELIMITER);

        String email = attributes[EMAIL_IDX].trim();
        String password = attributes[PASSWORD_IDX].trim();

        return new User(email, password);
    }

    public String toCsv() {
        return email + DELIMITER + password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
