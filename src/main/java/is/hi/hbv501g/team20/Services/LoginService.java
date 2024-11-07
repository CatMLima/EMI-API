package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.User;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface LoginService {
    User save(User user);

    User updatePrivacy(long id, int privacy);

    List<User> findAll();
    User findByEmail(String username);
    User login(User user);
    User findById(long id);

    void deleteUser(User user);

    User updateStreak(long id);

    String totalTime (User user);
    String average(User user);
    int totalSessions(User user);
    String favouriteLocation(User user);

}
