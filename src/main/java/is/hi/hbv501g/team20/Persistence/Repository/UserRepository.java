package is.hi.hbv501g.team20.Persistence.Repository;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByName(String name);
    User findByEmail(String email);
    void delete(User user);
}
