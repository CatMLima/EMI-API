package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import is.hi.hbv501g.team20.Persistence.Repository.LocationRepository;
import is.hi.hbv501g.team20.Persistence.Repository.StudyActivityRepository;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.StudyActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyActivityServiceImplementation implements StudyActivityService {

    //UserRepository userRepo;
    @Autowired
    StudyActivityRepository studyActRepo;
    @Autowired
    UserRepository userRepo;

    @Autowired
    LocationRepository locRepo;

    @Override
    public StudyActivity save(StudyActivity studyActivity) {
        return studyActRepo.save(studyActivity);
    }

    @Override
    public void delete(StudyActivity studyActivity) {
        studyActRepo.delete(studyActivity);
    }

    @Override
    public void deleteAll() {
        studyActRepo.deleteAll();
    }

    @Override
    public void deleteAllByUser(User user) { studyActRepo.deleteAllByUser(user); }

    @Override
    public boolean existsById(Long id) {
        return studyActRepo.existsById(id);
    }

    @Override
    public StudyActivity findById(long id) {
        return studyActRepo.findById(id);
    }

    @Override
    public List<StudyActivity> findAll() {
        return studyActRepo.findAll();
    }

    @Override
    public List<StudyActivity> findByUser(User user) { return studyActRepo.findByUser(user); }

    @Override
    public List<StudyActivity> searchByTitleOrDescription(String query, User user) {
            return studyActRepo.searchStudyActivityPublicUser(query, user);
    }

    @Override
    public List<StudyActivity> findAllPublicAndUserActivities(User user){
        List<StudyActivity> activities = new ArrayList<StudyActivity>();
        if (user.getPrivacy() == 1) {
            activities = studyActRepo.findByUser(user);
            activities.addAll(studyActRepo.findAllPublicActivities());
        } else {
            activities = studyActRepo.findAllPublicActivities();
        }
        return activities;
    }

    @Override
    public List<StudyActivity> findActiveStudyActivity(User user){
        return studyActRepo.findActiveByUser(user);
    }


    /*
    Methods related to locations
     */

    @Override
    public Location findByBuilding(Building building) {
        return locRepo.findByBuilding(building);
    }

    @Override
    public List<Location> findByUserCountLessThanEqual(int userCount) {
        return locRepo.findByUserCountLessThanEqual(userCount);
    }


    @Override
    public Location save(Location location) {
        return locRepo.save(location);
    }

    @Override
    public List<Location> findAllLocations() {
        return locRepo.findAll();
    }

    @Override
    public List<Location> findBuildingAlphabetically() {
        return locRepo.findAllByOrderByBuildingAsc();
    }


}
