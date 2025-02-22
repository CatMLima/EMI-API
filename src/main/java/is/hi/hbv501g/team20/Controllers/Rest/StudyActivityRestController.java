package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import is.hi.hbv501g.team20.Services.CoffeeService;
import is.hi.hbv501g.team20.Services.StudyActivityService;
import is.hi.hbv501g.team20.Services.UserAuthService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.dto.CreateStudyActivityRequest;
import is.hi.hbv501g.team20.dto.StudyActivityDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.*;


@RestController
public class StudyActivityRestController {

    @Autowired
    private UserAuthService userAuthService;

    private final StudyActivityService studyActivityService;
    private final UserService userService;
    private final CoffeeService coffeeService;

    @Autowired
    public StudyActivityRestController(StudyActivityService studyActivityService, UserService userService, CoffeeService coffeeService) {
        this.studyActivityService = studyActivityService;
        this.userService = userService;
        this.coffeeService = coffeeService;
    }

    @GetMapping("/rest/studyactivity-create")
    public ResponseEntity<StudyActivity> createStudyActivityGet() {
        return ResponseEntity.ok(new StudyActivity());
    }

    @PostMapping("/rest/api/studyactivity-create")
    public ResponseEntity<String> createStudyActivityPost(@RequestBody CreateStudyActivityRequest request) {
        User user = userAuthService.getAuthenticatedUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.setIsActive(0);
        userService.save(user);

        //Convert DTO into StudyActivity entity
        StudyActivity studyActivity = new StudyActivity();
        studyActivity.setUser(user);
        studyActivity.setSubjectName(request.getSubject_name());
        studyActivity.setTitle(request.getTitle());
        studyActivity.setDescription(request.getDescription());
        studyActivity.setSubjectID(request.getSubjectid());
        studyActivity.setBuilding(request.getBuilding());

        studyActivity.setPrivacy(user);
        studyActivity.setDate(new Date());
        studyActivity.setStart(LocalTime.now());
        studyActivity.setIsActive(0);
        studyActivity.setDuration(studyActivity.getStart(), null);

        Building building = studyActivity.getBuilding();
        Location location = studyActivityService.findByBuilding(building);

        if (location == null) {
            location = new Location();
            location.setBuilding(building);
            location.setUserCount(1);
            studyActivityService.save(location);
        } else {
            location.setUserCount(location.getUserCount() + 1);
            studyActivityService.save(location);
        }

        studyActivity.setLocation(location);
        studyActivityService.save(studyActivity);

        return ResponseEntity.status(HttpStatus.CREATED).body("Study activity created.");
    }

    @GetMapping("/rest/studyactivity-active/{id}")
    public ResponseEntity<StudyActivity> activeStudyActivityGet(@PathVariable Long id) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        return studyActivity != null ? ResponseEntity.ok(studyActivity) : ResponseEntity.notFound().build();
    }

    @PostMapping("/rest/studyactivity-finish/{id}")
    public ResponseEntity<String> finishStudyActivity(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not logged in.");
        }

        StudyActivity studyActivity = studyActivityService.findById(id);
        user.setIsActive(1);
        user = userService.updateStreak(user);
        userService.save(user);

        studyActivity.setEnd_time(LocalTime.now());
        studyActivity.setIsActive(1);
        studyActivity.setDuration(studyActivity.getStart(), studyActivity.getEnd_time());
        studyActivityService.save(studyActivity);

        Location location = studyActivity.getLocation();
        location.setUserCount(location.getUserCount() - 1);
        studyActivityService.save(location);

        return ResponseEntity.ok("Study activity finished.");
    }

    @DeleteMapping("/rest/studyactivity-delete/{id}")
    public ResponseEntity<String> deleteStudyActivity(@PathVariable Long id) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity != null) {
            studyActivityService.delete(studyActivity);
            return ResponseEntity.ok("Study activity deleted.");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/studyactivity-details/{id}")
    public ResponseEntity<StudyActivity> getStudyActivityDetails(@PathVariable Long id) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity != null && studyActivity.getDuration() == null) {
            studyActivity.setDuration(studyActivity.getStart(), studyActivity.getEnd_time());
            studyActivityService.save(studyActivity);
        }
        return studyActivity != null ? ResponseEntity.ok(studyActivity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/studyactivity-edit/{id}")
    public ResponseEntity<StudyActivity> getStudyActivityEdit(@PathVariable("id") long id) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity != null) {
            // Ensure that the old study activity has an updated duration if it's null
            if (studyActivity.getDuration() == null) {
                studyActivity.setDuration(studyActivity.getStart(), studyActivity.getEnd_time());
                studyActivityService.save(studyActivity);
            }
            return ResponseEntity.ok(studyActivity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rest/studyactivity-list")
    public ResponseEntity<List<StudyActivity>> getStudyActivityDetails(HttpSession session) {
        User user = userAuthService.getAuthenticatedUser();
        if (user != null) {
            List<StudyActivity> studyActivities = studyActivityService.findByUser(user);
            return ResponseEntity.ok(studyActivities);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/rest/locations-list")
    public ResponseEntity<List<Location>> getLocationsList(@RequestParam(required = false) Integer userCount) {
        List<Location> locations = (userCount != null) ? studyActivityService.findByUserCountLessThanEqual(userCount) : studyActivityService.findBuildingAlphabetically();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/rest/feed")
    public ResponseEntity<Map<String, Object>> showFeed() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<StudyActivity> allStudyActivities = studyActivityService.findAllPublicAndUserActivities(user);
        List<StudyActivity> activeStudyActivity = studyActivityService.findActiveStudyActivity(user);

        Map<Long, Boolean> userHasGivenCoffee = new HashMap<>();
        for (StudyActivity activity : allStudyActivities) {
            Coffee userCoffee = coffeeService.findCoffeeByUserAndActivity(user, activity);
            userHasGivenCoffee.put(activity.getId(), userCoffee != null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("allStudyActivities", allStudyActivities);
        response.put("activeStudyActivity", activeStudyActivity);
        response.put("userHasGivenCoffee", userHasGivenCoffee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rest/getFeedActivities")
    public ResponseEntity<List<StudyActivityDTO>> getFeedActivities() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<StudyActivity> allStudyActivities = studyActivityService.findAllPublicAndUserActivities(user);
        List<StudyActivityDTO> dtoList = new ArrayList<>();
        for (StudyActivity sa : allStudyActivities) {
            StudyActivityDTO dto = new StudyActivityDTO(sa.getId(), sa.getUser().getId(), sa.getCoffees(),
                    sa.getActivityPicture(), sa.getBuilding(), sa.getLocation(), sa.getDate(),
                    sa.getDuration(), sa.getTitle(), sa.getDescription(), sa.getSubjectName(), sa.getSubjectID());
            dtoList.add(dto);
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/rest/profile")
    public ResponseEntity<User> getUserProfile() {
        User user = userAuthService.getAuthenticatedUser();
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/rest/studyactivity-create")
    public ResponseEntity<StudyActivity> createStudyActivity(@RequestBody StudyActivity studyActivity) {
        StudyActivity studyActivityCreated = studyActivityService.save(studyActivity);
        return ResponseEntity.status(HttpStatus.CREATED).body(studyActivityCreated);
    }

    @PostMapping("/rest/uploadActivityPicture")
    public ResponseEntity<String> uploadActivityPicture(@RequestParam("activityPicture") MultipartFile activityPicture,
                                                        @PathVariable("activityId") Long activityId) {
        StudyActivity studyActivity = studyActivityService.findById(activityId);

        if (studyActivity != null && !activityPicture.isEmpty()) {
            try {
                byte[] bytes = activityPicture.getBytes();
                studyActivity.setActivityPicture(bytes);
                studyActivityService.save(studyActivity);
                return ResponseEntity.ok("Picture uploaded successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading activity picture");
            }
        }
        return ResponseEntity.badRequest().body("No picture uploaded or invalid study activity");
    }

    @GetMapping("/rest/activity/{id}/activityPicture")
    public ResponseEntity<byte[]> getActivityPicture(@PathVariable Long id) {
        StudyActivity activity = studyActivityService.findById(id);
        if (activity != null && activity.getActivityPicture() != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(activity.getActivityPicture());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rest/feed-search")
    public ResponseEntity<List<StudyActivity>> searchStudyActivities(@RequestParam("query") String query) {
        User user = userAuthService.getAuthenticatedUser();
        if (user != null) {
            List<StudyActivity> searchResults = studyActivityService.searchByTitleOrDescription(query, user);
            return ResponseEntity.ok(searchResults);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @GetMapping("/rest/studyactivity/{id}/toggle-coffee")
    public ResponseEntity<String> toggleCoffee(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity != null) {
            Coffee existingCoffee = coffeeService.findCoffeeByUserAndActivity(user,studyActivity);
            if (existingCoffee != null){
                coffeeService.removeCoffee(user, studyActivity);
            } else{
                coffeeService.giveCoffee(user, studyActivity);
            }
            return ResponseEntity.ok("Coffee toggled successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}
