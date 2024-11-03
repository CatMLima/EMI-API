package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.StudyActivityRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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


    @Override
    public User updateStreak(long id){
        User user = findById(id);
        List<StudyActivity> activities = user.getActivities();

        if (activities.isEmpty()) {
            user.setStreak(0);
            return userRepo.save(user);
        }

        LocalDate lastActivityDate = new java.sql.Date(activities.get(activities.size() - 1).getDate().getTime())
                    .toLocalDate(); // For java.sql.Date
        LocalDate OneBeforeLastActivity = new java.sql.Date(activities.get(activities.size() - 2).getDate().getTime())
                .toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);


        if (!lastActivityDate.isEqual(today) && !lastActivityDate.isBefore(today) || !OneBeforeLastActivity.isEqual(yesterday)) {
            // If NO study activity was completed yesterday or today
            user.setStreak(0);
        } else if (OneBeforeLastActivity.isEqual(yesterday) && lastActivityDate.isEqual(today)) {
            // If no activity was completed yesterday
            user.setStreak(user.getStreak() + 1);
        } else if (!OneBeforeLastActivity.isEqual(yesterday) && lastActivityDate.isEqual(today)) {
            // First study activity of the streak
            user.setStreak(1);
        }

        return userRepo.save(user);
    }

}
