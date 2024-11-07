package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.StudyActivityRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginServiceImplementation  implements LoginService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    StudyActivityServiceImplementation studyActivityServiceImplementation;
    @Autowired
    private StudyActivityRepository studyActivityRepository;

    public LoginServiceImplementation(UserRepository userRepo) {
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
    public User login(User user) {
        User doesExist = findByEmail(user.getEmail());
        if(doesExist != null){
            if(doesExist.getPassword().equals(user.getPassword())){
                return doesExist;
            }
        }
        return null;
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

    // Just to insure correct functionality for the previous users
    @Override
    public User updateStreak(long id){
        User user = findById(id);
        List<StudyActivity> activities = user.getActivities();
        LocalDate lastActivityDate = new Date(activities.get(activities.size() - 1).getDate().getTime())
                .toLocalDate(); // For java.sql.Date
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if (Objects.isNull(user.getStreak())) {
            if (activities.isEmpty()) {
                user.setStreak(0);
            } else {
                user.setStreak(calculateStreak(user));
            }
        } else if (!lastActivityDate.isEqual(today)) {
            // Adds +1 days to the streak for the first study activity of today
            user.setStreak(user.getStreak() + 1);
        } else if (!lastActivityDate.isEqual(yesterday)) {
            // Returns streak to 0, if there were no activities yesterday
            user.setStreak(0);
        }

        return userRepo.save(user);
    }

    private Integer calculateStreak(User user) {
        Integer count = 0;
        // For other users without a defined streak we follow the process below
        if (Objects.isNull(user.getStreak())) {
            List<java.util.Date> activityDates = studyActivityRepository.getActivitiesDatesByUser(user);

            List<LocalDate> localDates = activityDates.stream()
                    .map(date -> new Date(date.getTime()).toLocalDate())
                    .distinct()
                    .sorted(Comparator.reverseOrder()) // Sorts in descending order
                    .collect(Collectors.toList());

            LocalDate yesterday = LocalDate.now().minusDays(1);

            if (!localDates.contains(yesterday)) {
                return count;
            } else {
                for (int i = 0; i < localDates.size() - 1; i++) {
                    LocalDate currentDate = localDates.get(i);
                    LocalDate nextDate = localDates.get(i + 1);

                    if (currentDate.minusDays(1).isEqual(nextDate)) {
                        count++;
                    } else {
                        break;
                    }
                }
            }
        }
        return count;
    }

    /*
    Stat calculation service methods
     */

    // Calculate the total amount of time studied.
    public String totalTime(User user){
        List<StudyActivity> activities = studyActivityRepository.findByUser(user);
        Duration duration;
        Duration total = Duration.ZERO;
        for (StudyActivity studyActivity : activities) {
            total = total.plus(studyActivity.getDuration());
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
            total = total.plus(studyActivity.getDuration());
        }

        int totalSessions = totalSessions(user);
        long average = total.getSeconds()/totalSessions;
        return formatDuration(Duration.ofSeconds(average));
    }

    // Find favourite study spot
    public String favouriteLocation(User user){
        Location favourite = studyActivityRepository.findFavouriteLocationByUser(user);
        return favourite.getBuilding().toString();
    }

    public String formatDuration(Duration duration){
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
