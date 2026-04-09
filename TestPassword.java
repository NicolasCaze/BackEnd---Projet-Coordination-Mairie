import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";
        String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        System.out.println("Testing password validation:");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Matches: " + encoder.matches(rawPassword, storedHash));
        
        // Generate new hash
        String newHash = encoder.encode(rawPassword);
        System.out.println("\nNew hash for 'password123': " + newHash);
    }
}
