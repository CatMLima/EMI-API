package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;

import java.util.List;

public interface StudyActivityService {
    StudyActivity save (StudyActivity studyActivity);
    void delete (StudyActivity studyActivity);
    void deleteAll();
    boolean existsById(Long id);
    StudyActivity findById(long id);
    List<StudyActivity> findAll();
    List<StudyActivity> findByUser(User user);
    List<StudyActivity> searchByTitleOrDescription(String query, User user);

    List<StudyActivity> findAllPublicAndUserActivities(User user);

    List<StudyActivity> findActiveStudyActivity(User user);

    /*
    Methods related to locations.
     */
    Location findByBuilding(Building building);
    List<Location> findByUserCountLessThanEqual(int userCount);
    Location save(Location location);
    List<Location> findAllLocations();
    List<Location> findBuildingAlphabetically();
}
