package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.User;
import uz.estera.repository.UserRepository;
import uz.estera.security.JwtService;
import uz.estera.security.UserRole;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public record RegisterReq(String phone, String fullName, String password, String role, String preferredLang) {}
    public record LoginReq(String phone, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        if (users.existsByPhone(req.phone())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone already registered"));
        }
        User u = new User();
        u.setPhone(req.phone());
        u.setFullName(req.fullName());
        u.setPasswordHash(encoder.encode(req.password()));
        // Self-registration only allows CLIENT or MASTER; staff roles are provisioned by SUPER_ADMIN
        UserRole assigned = "MASTER".equalsIgnoreCase(req.role()) ? UserRole.MASTER : UserRole.CLIENT;
        u.setRole(assigned);
        u.setPreferredLang(req.preferredLang() != null ? req.preferredLang() : "uz");
        users.save(u);
        String token = jwt.generateToken(u.getId(), u.getPhone(), u.getRole());
        return ResponseEntity.ok(Map.of("token", token, "userId", u.getId(), "role", u.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        return users.findByPhone(req.phone())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .map(u -> ResponseEntity.ok(Map.of(
                        "token", jwt.generateToken(u.getId(), u.getPhone(), u.getRole()),
                        "userId", u.getId(),
                        "fullName", u.getFullName(),
                        "role", u.getRole().name(),
                        "lang", u.getPreferredLang()
                )))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}
