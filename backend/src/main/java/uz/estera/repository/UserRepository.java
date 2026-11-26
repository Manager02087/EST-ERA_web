package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
