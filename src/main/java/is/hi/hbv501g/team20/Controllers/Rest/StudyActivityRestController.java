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
import is.hi.hbv501g.team20.dto.OngoingResponse;
import is.hi.hbv501g.team20.dto.StudyActivityDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public ResponseEntity<OngoingResponse> createStudyActivityPost(@RequestBody CreateStudyActivityRequest request) {
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
        studyActivity.setIsActive(0);

        studyActivity.setPrivacy(user);

        Date date = new Date();
        studyActivity.setDate(date);
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

        // Combine the date and start time into a single LocalDateTime.
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime dateTime = LocalDateTime.of(localDate, studyActivity.getStart());
        String formattedStart = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        // Create the response DTO with proper data.
        OngoingResponse response = new OngoingResponse(
                studyActivity.getId(),
                studyActivity.getTitle(),
                studyActivity.getSubjectID(),
                studyActivity.getSubjectName(),
                formattedStart
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rest/studyactivity-active/{id}")
    public ResponseEntity<StudyActivityDTO> activeStudyActivityGet(@PathVariable Long id) {
        StudyActivity sa = studyActivityService.findById(id);
        User user = userAuthService.getAuthenticatedUser();

        if (user == null || sa == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Coffee coffeeCheck = coffeeService.findCoffeeByUserAndActivity(user,sa);
        boolean hasCoffee = false;
        if (coffeeCheck != null) hasCoffee = true;

        StudyActivityDTO dto = new StudyActivityDTO(sa.getId(), sa.getUser().getId(),
                sa.getActivityPicture(), sa.getBuilding(), sa.getLocation(), sa.getDate(),
                sa.getFormattedDuration(), sa.getTitle(), sa.getDescription(), sa.getUser().getName(),
                sa.getSubjectName(), sa.getSubjectID(), sa.getCoffees().size(), hasCoffee);

        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/get/OG")
    public ResponseEntity<StudyActivityDTO> getOG() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<StudyActivity> activeStudyActivity = studyActivityService.findActiveStudyActivity(user);
        if(activeStudyActivity.size() > 0) {
            StudyActivity sa = activeStudyActivity.get(0);
            Coffee coffeeCheck = coffeeService.findCoffeeByUserAndActivity(user,sa);
            boolean hasCoffee = false;
            if (coffeeCheck != null) {
                hasCoffee = true;
            }
            StudyActivityDTO dto = new StudyActivityDTO(sa.getId(), sa.getUser().getId(),
                    sa.getActivityPicture(), sa.getBuilding(), sa.getLocation(), sa.getDate(),
                    sa.getFormattedDuration(), sa.getTitle(), sa.getDescription(), sa.getUser().getName(),
                    sa.getSubjectName(), sa.getSubjectID(), sa.getCoffees().size(), hasCoffee);
            return ResponseEntity.ok(dto);
        }
        else return ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/get/ongoingActivity/{id}")
    public ResponseEntity<OngoingResponse> getOGbyID(@PathVariable Long id) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        OngoingResponse response = new OngoingResponse(
                studyActivity.getId(),
                studyActivity.getTitle(),
                studyActivity.getSubjectID(),
                studyActivity.getSubjectName(),
                studyActivity.getStart().toString()
        );
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/get/ongoingActivity/{user_id}")
    public ResponseEntity<OngoingResponse> getOngoingActivity(@PathVariable Long user_id) {
        User user = userService.findById(user_id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        //List<StudyActivity> activeStudyActivity = studyActivityService.findActiveStudyActivity(user);
        Long currId = userService.getOngoingId(user);

        //for (StudyActivity studyActivity : activeStudyActivity) {currId = studyActivity.getId();}
        if (currId == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        StudyActivity studyActivity = studyActivityService.findById(currId);

        LocalDate localDate = studyActivity.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime dateTime = LocalDateTime.of(localDate, studyActivity.getStart());
        String formattedStart = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        if (studyActivity.getStart() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        OngoingResponse response = new OngoingResponse(
                studyActivity.getId(),
                studyActivity.getTitle(),
                studyActivity.getSubjectID(),
                studyActivity.getSubjectName(),
                formattedStart
        );
        return ResponseEntity.ok(response);
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

        User user = userAuthService.getAuthenticatedUser(); // Pass token if required by your service
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }
        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity == null) {
            return ResponseEntity.notFound().build();
        }

if (!studyActivity.getUser().getId().equals(user.getId())) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own study activities.");
}
        studyActivityService.delete(studyActivity);
        return ResponseEntity.ok("Study activity deleted.");
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
            Coffee coffeeCheck = coffeeService.findCoffeeByUserAndActivity(user,sa);
            boolean hasCoffee = false;
            if (coffeeCheck != null) {
                hasCoffee = true;
            }
            StudyActivityDTO dto = new StudyActivityDTO(sa.getId(), sa.getUser().getId(),
                    sa.getActivityPicture(), sa.getBuilding(), sa.getLocation(), sa.getDate(),
                    sa.getFormattedDuration(), sa.getTitle(), sa.getDescription(), sa.getUser().getName(),
                    sa.getSubjectName(), sa.getSubjectID(), sa.getCoffees().size(),hasCoffee);
            dtoList.add(dto);
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/rest/getActivityByID/{id}")
    public ResponseEntity<StudyActivityDTO> getActivityByID(@PathVariable long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        StudyActivity sa = studyActivityService.findById(id);
        if (sa!= null) {
            Coffee coffeeCheck = coffeeService.findCoffeeByUserAndActivity(user,sa);
            boolean hasCoffee = false;
            if (coffeeCheck != null) {
                hasCoffee = true;
            }

            StudyActivityDTO dto = new StudyActivityDTO(sa.getId(), sa.getUser().getId(),
                    sa.getActivityPicture(), sa.getBuilding(), sa.getLocation(), sa.getDate(),
                    sa.getFormattedDuration(), sa.getTitle(), sa.getDescription(), sa.getUser().getName(),
                    sa.getSubjectName(), sa.getSubjectID(), sa.getCoffees().size(),hasCoffee);

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
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

    @PostMapping(value = "/rest/studyactivity-create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudyActivity> createStudyActivity(@RequestBody StudyActivity studyActivity) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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

    @PutMapping("/rest/studyactivity/{id}/toggle-coffee")
    public ResponseEntity<String> toggleCoffee(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        StudyActivity studyActivity = studyActivityService.findById(id);
        if (studyActivity == null) {
            return ResponseEntity.notFound().build();
        }

        Coffee coffee = coffeeService.findCoffeeByUserAndActivity(user,studyActivity);
        if (coffee != null) {
            coffeeService.removeCoffee(user,studyActivity);
            return ResponseEntity.ok("Coffee removed.");
        }else{
            coffeeService.giveCoffee(user,studyActivity);
            return ResponseEntity.ok("Coffee added.");
        }
    }

}
