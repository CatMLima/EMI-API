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
import is.hi.hbv501g.team20.dto.EditActivityRequest;
import is.hi.hbv501g.team20.dto.LocationDTO;
import is.hi.hbv501g.team20.dto.OngoingResponse;
import is.hi.hbv501g.team20.dto.StudyActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Study Activities", description = "APIs for managing study activities.")
@RestController
@RequestMapping("/study")
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

    // -------------------------------------------------------------------------
    // User-centric activity endpoints
    // -------------------------------------------------------------------------

    // POST /study/activities/new — start a new study session
    @PostMapping("/activities/new")
    public ResponseEntity<OngoingResponse> createActivity(@RequestBody CreateStudyActivityRequest request) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        user.setIsActive(0);
        userService.save(user);

        StudyActivity studyActivity = new StudyActivity();
        studyActivity.setUser(user);
        studyActivity.setTitle(request.getTitle());
        studyActivity.setDescription(request.getDescription());
        studyActivity.setSubjectID(request.getSubjectid());
        studyActivity.setSubjectName(request.getSubject_name());
        studyActivity.setBuilding(request.getBuilding());
        studyActivity.setIsActive(0);
        studyActivity.setPrivacy(user);

        Date date = new Date();
        studyActivity.setDate(date);
        studyActivity.setStart(LocalTime.now());
        studyActivity.setDuration(studyActivity.getStart(), null);

        Building building = studyActivity.getBuilding();
        Location location = studyActivityService.findByBuilding(building);
        if (location == null) {
            location = new Location();
            location.setBuilding(building);
            location.setUserCount(1);
        } else {
            location.setUserCount(location.getUserCount() + 1);
        }
        studyActivityService.save(location);
        studyActivity.setLocation(location);
        studyActivityService.save(studyActivity);

        OngoingResponse response = new OngoingResponse(
                studyActivity.getId(),
                studyActivity.getTitle(),
                studyActivity.getSubjectID(),
                studyActivity.getSubjectName(),
                studyActivity.getDuration().toString()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /study/activities — list all of the authenticated user's sessions
    @GetMapping("/activities")
    public ResponseEntity<List<StudyActivityDTO>> getMyActivities() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<StudyActivity> activities = studyActivityService.findByUser(user);
        List<StudyActivityDTO> dtos = activities.stream()
                .map(sa -> toDTO(sa, user))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /study/activities/ongoing — get the current active session
    @GetMapping("/activities/ongoing")
    public ResponseEntity<OngoingResponse> getOngoing() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long ongoingId = userService.getOngoingId(user);
        if (ongoingId == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        StudyActivity sa = studyActivityService.findById(ongoingId);
        if (sa == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        OngoingResponse response = new OngoingResponse(
                sa.getId(),
                sa.getTitle(),
                sa.getSubjectID(),
                sa.getSubjectName(),
                sa.getDuration().toString()
        );
        return ResponseEntity.ok(response);
    }

    // GET /study/activities/{id} — get a single session's details
    @GetMapping("/activities/{id}")
    public ResponseEntity<StudyActivityDTO> getActivity(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(toDTO(sa, user));
    }

    // PUT /study/activities/{id} — edit a session
    @PutMapping("/activities/{id}")
    public ResponseEntity<String> editActivity(@PathVariable Long id, @RequestBody EditActivityRequest request) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();
        if (!sa.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own study sessions.");
        }

        sa.setTitle(request.getTitle());
        sa.setDescription(request.getDescription());
        sa.setSubjectID(request.getSubjectId());
        sa.setSubjectName(request.getSubjectName());
        studyActivityService.save(sa);
        return ResponseEntity.ok("Study session updated.");
    }

    // PATCH /study/activities/{id}/finish — finish an active session
    @PatchMapping("/activities/{id}/finish")
    public ResponseEntity<String> finishActivity(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();
        if (!sa.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only finish your own study sessions.");
        }

        user.setIsActive(1);
        user = userService.updateStreak(user);
        userService.save(user);

        sa.setEnd_time(LocalTime.now());
        sa.setIsActive(1);
        sa.setDuration(sa.getStart(), sa.getEnd_time());
        studyActivityService.save(sa);

        Location location = sa.getLocation();
        if (location != null) {
            location.setUserCount(Math.max(0, location.getUserCount() - 1));
            studyActivityService.save(location);
        }

        return ResponseEntity.ok("Study session finished.");
    }

    // DELETE /study/activities/{id} — delete a session
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();
        if (!sa.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own study sessions.");
        }

        studyActivityService.delete(sa);
        return ResponseEntity.ok("Study session deleted.");
    }

    // POST /study/activities/{id}/picture — upload a picture for a session
    @PostMapping("/activities/{id}/picture")
    public ResponseEntity<String> uploadActivityPicture(@PathVariable Long id,
                                                        @RequestParam("picture") MultipartFile picture) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();
        if (!sa.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only upload pictures to your own sessions.");
        }

        try {
            sa.setActivityPicture(picture.getBytes());
            studyActivityService.save(sa);
            return ResponseEntity.ok("Picture uploaded.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading picture.");
        }
    }

    // GET /study/activities/{id}/picture — get a session's picture
    @GetMapping("/activities/{id}/picture")
    public ResponseEntity<byte[]> getActivityPicture(@PathVariable Long id) {
        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null || sa.getActivityPicture() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(sa.getActivityPicture());
    }

    // -------------------------------------------------------------------------
    // Social / feed endpoints — kept as-is, will be redesigned in a later phase
    // -------------------------------------------------------------------------

    // GET /study/feed - get's the feed of all public study activities
    @GetMapping("/feed")
    public ResponseEntity<Map<String, Object>> showFeed() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

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

    @GetMapping("/getFeedActivities")
    public ResponseEntity<List<StudyActivityDTO>> getFeedActivities() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<StudyActivity> allStudyActivities = studyActivityService.findAllPublicAndUserActivities(user);
        List<StudyActivityDTO> dtoList = allStudyActivities.stream()
                .map(sa -> toDTO(sa, user))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/locations-list")
    public ResponseEntity<?> getLocationsList(@RequestParam(required = false) Integer userCount) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");

        List<Location> locations = (userCount != null)
                ? studyActivityService.findByUserCountLessThanEqual(userCount)
                : studyActivityService.findBuildingAlphabetically();

        List<LocationDTO> locationsDTO = locations.stream()
                .map(l -> new LocationDTO(l.getBuilding(), l.getUserCount()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationsDTO);
    }

    @GetMapping("/feed-search")
    public ResponseEntity<List<StudyActivityDTO>> searchStudyActivities(@RequestParam("query") String query) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<StudyActivity> results = studyActivityService.searchByTitleOrDescription(query, user);
        List<StudyActivityDTO> dtos = results.stream()
                .map(sa -> toDTO(sa, user))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/studyactivity/{id}/toggle-coffee")
    public ResponseEntity<String> toggleCoffee(@PathVariable Long id) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");

        StudyActivity sa = studyActivityService.findById(id);
        if (sa == null) return ResponseEntity.notFound().build();

        Coffee coffee = coffeeService.findCoffeeByUserAndActivity(user, sa);
        if (coffee != null) {
            coffeeService.removeCoffee(user, sa);
            return ResponseEntity.ok("Coffee removed.");
        } else {
            coffeeService.giveCoffee(user, sa);
            return ResponseEntity.ok("Coffee added.");
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private StudyActivityDTO toDTO(StudyActivity sa, User user) {
        Coffee coffeeCheck = coffeeService.findCoffeeByUserAndActivity(user, sa);
        return new StudyActivityDTO(
                sa.getId(),
                sa.getUser().getId(),
                sa.getBuilding(),
                sa.getLocation(),
                sa.getDate(),
                sa.getFormattedDuration(),
                sa.getTitle(),
                sa.getDescription(),
                sa.getUser().getName(),
                sa.getSubjectID(),
                sa.getSubjectName(),
                user.getPrivacy(),
                sa.getCoffees().size(),
                coffeeCheck != null
        );
    }
}
