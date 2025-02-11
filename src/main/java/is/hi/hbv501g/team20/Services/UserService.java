package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.dto.LoginRequest;

import java.util.List;

public interface UserService {
    User save(User user);

    User updatePrivacy(long id, int privacy);

    List<User> findAll();
    User findByEmail(String username);
    User findById(long id);
    User checkStreak(User user);
    User updateStreak(User user);
    User initializeStreak(User user);
    void deleteUser(User user);
    String totalTime (User user);
    String average(User user);
    int totalSessions(User user);
    String favouriteLocation(User user);

    // method called by the rest api
    User registerNewUser(User user);

    String verify(User user);

    boolean checkOldPassword(User user, String oldPassword);
    boolean checkNewPassword(String newPassword, String confirmPassword);

    User changePassword(User user, String newPassword);

    User updateUser(User user);

    boolean checkPassword(User user, String password);
}
