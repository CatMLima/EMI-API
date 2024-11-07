package is.hi.hbv501g.team20.Persistence.Repository;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByUser(User user);
    @Query("SELECT c FROM Coffee c WHERE c.user = :user AND c.activity = :activity")
    Optional<Coffee> findByUserAndActivity(@Param("user") User user, @Param("activity") StudyActivity activity);
    @Query("SELECT COUNT(c) FROM Coffee c WHERE c.activity = :activity")
    long countByActivity(@Param("activity") StudyActivity activity);

}
