package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.StudyActivityRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private StudyActivityRepository studyActivityRepository;

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

        if (lastActivity == null || now.isAfter(lastActivity.plusDays(1))){
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
}
