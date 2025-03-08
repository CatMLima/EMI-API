package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.StudyActivityRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.JWTService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    StudyActivityServiceImplementation studyActivityServiceImplementation;
    @Autowired
    CoffeeServiceImplementation coffeeServiceImplementation;
    @Autowired
    private StudyActivityRepository studyActivityRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    public UserServiceImplementation(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findById(long id){
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User updatePrivacy(long id, int privacy){
        User user = findById(id);
        List<StudyActivity> activities = user.getActivities();
        user.changePrivacy(privacy);
        activities.forEach(activity -> activity.setPrivacy(user));
        return userRepo.save(user);
    }

    @Override
    public void deleteUser(User user){
        studyActivityServiceImplementation.deleteAllByUser(user);
        coffeeServiceImplementation.deleteAllByUser(user);
        userRepo.delete(user);
    }

    // Calculate the total amount of time studied.
    public String totalTime(User user){
        List<StudyActivity> activities = studyActivityRepository.findByUser(user);
        Duration total = Duration.ZERO;
        for (StudyActivity studyActivity : activities) {
            Duration duration = studyActivity.getDuration();
            if (duration != null) {
                total = total.plus(duration);

            }
        }
        return formatDuration(total);
    }

    // Calculate the total number of study activities completed.
    public int totalSessions(User user){
        List<StudyActivity> activities = studyActivityRepository.findByUser(user);
        return activities.size();
    }

    // Calculate the average study time for each activity.
    public String average (User user){
        List<StudyActivity> activities = studyActivityRepository.findByUser(user);
        Duration total = Duration.ZERO;

        for(StudyActivity studyActivity : activities){
            Duration duration = studyActivity.getDuration();
            if(duration != null){
            total = total.plus(duration);
        }}

        int totalSessions = totalSessions(user);
        long average;
        if (totalSessions == 0) {
            average = 0;
        } else {
            average = total.getSeconds()/totalSessions;
        }
        return formatDuration(Duration.ofSeconds(average));
    }

    // Find favourite study spot
    public String favouriteLocation(User user){
        Location favourite = studyActivityRepository.findFavouriteLocationByUser(user);
        if (favourite == null) {
            return "";
        } else {
            return favourite.getBuilding().toString();
        }
    }


    // method used by the rest api user controller and normal controller
    @Override
    public User registerNewUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setIsActive(0);
        user.setStreak(0);
        user.setPrivacy(0);
        return userRepo.save(user);
    }

    @Override
    public boolean checkPassword(User user,String password){
        return encoder.matches(password,user.getPassword());
    }


    // Used by the REST controller for logging in
    @Override
    public String verify(User user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if(auth.isAuthenticated())
            return jwtService.generateToken(user.getEmail());

        return "Failed";
    }

    // used by rest controller for changing password (ALL THE BELOW THREE METHODS)
    @Override
    public boolean checkOldPassword(User user, String oldPassword){
        return encoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public boolean checkNewPassword(String newPassword, String confirmPassword){
        return newPassword.equals(confirmPassword);
    }

    @Override
    public User changePassword(User user, String newPassword){
        user.setPassword(encoder.encode(newPassword));
        return userRepo.save(user);
    }


    // Used by REST user controller when a user is fetched to refresh their data in case something about them has not been updated.
    @Override
    public User updateUser(User user) {
        if (user.getPrivacy() == null){
            user.setPrivacy(0);
        }

        if (user.getStreak() == null){
            user = initializeStreak(user);
        }

        if (user.getStreak() != 0){
            checkStreak(user);
        }

        if (user.getPrivacy() == null || user.privacy != 0 && user.privacy != 1){
            updatePrivacy(user.getId(),0);
        }

        return user;
    }


    // Format the data before showing it.
    public String formatDuration(Duration duration){
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Streak related methods
    public User checkStreak(User user){
        LocalDate lastActivity = user.getLastActivityDate();
        LocalDate now = LocalDate.now();

        if (user.getStreak() == null || lastActivity == null || now.isAfter(lastActivity.plusDays(1))){
            user.setStreak(0);
            return userRepo.save(user);
        }
        return userRepo.save(user);
    }

    // updating the streak when a user finishes an activity
    @Override
    public User updateStreak(User user) {
        LocalDate lastActivity = user.getLastActivityDate();
        LocalDate now = LocalDate.now();

        if (lastActivity == null){
            user.setLastActivityDate(now);
            user.setStreak(1);
        }

        if (lastActivity != null && lastActivity.equals(now)){
            user.setLastActivityDate(now);
        }

        if (lastActivity != null && !lastActivity.equals(now)){
            user.setLastActivityDate(now);
            user.setStreak(user.getStreak() + 1);
        }
        return userRepo.save(user);
    }

    @Override
    public User initializeStreak(User user) {
        user.setStreak(0);
        return userRepo.save(user);
    }

    @Override
    public
    Long getOngoingId(User user){
        List<StudyActivity> activities = studyActivityRepository.findByUser(user);
        for (StudyActivity activity : activities) {
            if (activity.getIsActive() != null && activity.getIsActive() == 0) {
                return activity.getId();
            }
        }
        return null;
    }
}
