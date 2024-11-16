package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import is.hi.hbv501g.team20.Services.CoffeeService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.Services.StudyActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StudyActivityController {

    private StudyActivityService studyActivityService;
    private UserService userService;

    @Autowired
    public StudyActivityController(StudyActivityService studyActivityService, UserService userService) {
        this.studyActivityService = studyActivityService;
        this.userService = userService;
    }

    // Displays the Create a studyactivity page
    @RequestMapping(value = "/studyactivity-create", method = RequestMethod.GET)
    public String createStudyActivityGet(Model model) {
        model.addAttribute("studyactivity", new StudyActivity());
        return "studyactivity-create";
    }

    // Assigns a user to the studyactivity as well as a date
    // start and end variables are for a timer that will be implemented later
    // creates a studyactivity and saves it to the database
    @RequestMapping(value = "/api/studyactivity-create", method = RequestMethod.POST)
    public String createStudyActivity(HttpSession httpSession, StudyActivity studyActivity, BindingResult result, Model model){

        User user = (User) httpSession.getAttribute("user");
        user.setIsActive(0);
        userService.save(user);
        studyActivity.setUser(user);
        studyActivity.setPrivacy(user);
        studyActivity.setDate(new Date());
        studyActivity.setStart(LocalTime.now());
        studyActivity.setIsActive(0);
        studyActivity.setDuration(studyActivity.getStart(), null);

        Building building = studyActivity.getBuilding();
        Location location = studyActivityService.findByBuilding(building);

        // check if locaiton exists in the database, otherwise create it
        if (location == null){
            location = new Location();
            location.setBuilding(building);
            location.setUserCount(1);
            studyActivityService.save(location);
        } else {
            location.setUserCount(location.getUserCount() + 1);
            studyActivityService.save(location);
        }

        studyActivity.setLocation(location);

        if(result.hasErrors()){
            return "studyactivity-create";
        }
        studyActivityService.save(studyActivity);
        model.addAttribute("studyActivity", studyActivity);

        long id = studyActivity.getId();

        return "redirect:/studyactivity-active/" + id;
    }

    // Go to active study acvtivity page
    @RequestMapping(value = "/studyactivity-active/{id}", method = RequestMethod.GET)
    public String activeStudyActivity(Model model, @PathVariable("id") long id) {

        StudyActivity studyActivity = studyActivityService.findById(id);
        model.addAttribute("studyActivity", studyActivity);

        return "studyactivity-active";
    }

    // set the end time of the study activity and update the count of the location to -1 its current number.
    @RequestMapping(value = "/studyactivity-finish/{id}")
    public String finishStudyActivity(HttpSession httpSession, @PathVariable("id") long id, Model model) {

        User user = (User) httpSession.getAttribute("user");
        StudyActivity studyActivity = studyActivityService.findById(id);
        user.setIsActive(1);
        user = userService.updateStreak(user);
        userService.save(user);
        studyActivity.setEnd_time(LocalTime.now());
        studyActivity.setIsActive(1);
        studyActivity.setDuration(studyActivity.getStart(),studyActivity.getEnd_time());
        studyActivityService.save(studyActivity);
        Location location = studyActivity.getLocation();
        location.setUserCount(location.getUserCount() - 1);
        studyActivityService.save(location);

        return "redirect:/feed";

    }

    // deletes a selected studyactivity and removes it from the database
    @GetMapping("/studyactivity-delete/{id}")
    public String deleteStudyActicity (@PathVariable("id") long id, Model model){
        StudyActivity studyActivityToDelete = studyActivityService.findById(id);
        studyActivityService.delete(studyActivityToDelete);
        return "redirect:/studyactivity-list";
    }

    // displays study activity details
    @RequestMapping(value="/studyactivity-details/{id}", method= RequestMethod.GET)
    public String getStudyActivityDetailsPage(@PathVariable("id") long id, Model model) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        model.addAttribute("studyactivity", studyActivity);

        // To ensure that old study activity has an "updated" duration, other than null
        if (studyActivity.getDuration() == null) {
            studyActivity.setDuration(studyActivity.getStart(), studyActivity.getEnd_time());
            studyActivityService.save(studyActivity);
        }

        return "studyactivity-details";
    }

    // displays page for editing study activity
    @RequestMapping(value="/studyactivity-edit/{id}", method= RequestMethod.GET)
    public String getStudyActivityEditPage(@PathVariable("id") long id, Model model) {
        StudyActivity studyActivity = studyActivityService.findById(id);
        model.addAttribute("studyactivity", studyActivity);

        // To ensure that old study activity has an "updated" duration, other than null
        if (studyActivity.getDuration() == null) {
            studyActivity.setDuration(studyActivity.getStart(), studyActivity.getEnd_time());
            studyActivityService.save(studyActivity);
        }

        return "studyactivity-edit";
    }


    // Displays a page containing a list of the user's studyactivities
    @RequestMapping(value="/studyactivity-list", method= RequestMethod.GET)
    public String getStudyActivityDetailsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<StudyActivity> studyActivities = studyActivityService.findByUser(user);
            model.addAttribute("studyactivity", studyActivities);
        }
        return "studyactivity-list";
    }

    // Displays a page containing the list of the locations and the  number of *Active* study activities in them.
    @RequestMapping(value="/locations-list", method=RequestMethod.GET)
    public String getLocationsList(@RequestParam(required=false) Integer userCount, HttpSession session, Model model) {
        List<Location> locations = studyActivityService.findAllLocations();

        if (userCount != null){
            locations = studyActivityService.findByUserCountLessThanEqual(userCount);
        } else {
            locations = studyActivityService.findBuildingAlphabetically();
        }
        model.addAttribute("locations", locations);
        //model.addAttribute("userCount", userCount);

        return "locations-list";
    }

    // Feed page stuff is here below
    // Displays feed page
    @RequestMapping("/feed")
    public String showFeed(HttpSession session, Model model) {
        //gets the logged in user and all public and user study activities
        User user = (User) session.getAttribute("user");
        List<StudyActivity> allStudyActivities = studyActivityService.findAllPublicAndUserActivities(user);
        model.addAttribute("studyactivity", allStudyActivities);

        //gets the logged in user's active study activities
        Integer userActive = user.getIsActive();
        List<StudyActivity> activeStudyActivity = studyActivityService.findActiveStudyActivity(user);
        model.addAttribute("activeStudyActivity", activeStudyActivity);

        //code needed to display the right toggle coffee button
        Map<Long, Boolean> userHasGivenCoffee = new HashMap<>(); // Map to track user's coffee status
        model.addAttribute("userHasGivenCoffee", userHasGivenCoffee);
        for (StudyActivity activity : allStudyActivities) {
            // Check if the user has given coffee for this activity
            Coffee userCoffee = coffeeService.findCoffeeByUserAndActivity(user, activity);
            userHasGivenCoffee.put(activity.getId(), userCoffee != null);
        }

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("userActive", userActive);
        }
        return "feed";
    }

    // Displays user profile page
    @RequestMapping("/profile")
    public String showProfile() {
        return "user";
    }

    // Create a study activity
    @RequestMapping("/studyactivity-create")
    public String startStudyActivity() {
        return "studyactivity-create";
    }

    // Add a picture to a study activity
    @PostMapping("/uploadActivityPicture")
    public String uploadActivityPicture(@RequestParam("activityPicture") MultipartFile activityPicture, @RequestParam("activityId") Long activityId,
                                       HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        StudyActivity studyActivity = studyActivityService.findById(activityId);

        if (!activityPicture.isEmpty()) {
            try{
                byte[] bytes = activityPicture.getBytes();
                studyActivity.setActivityPicture(bytes);
                studyActivityService.save(studyActivity);
                model.addAttribute("user", user);
                model.addAttribute("activityPicture", studyActivity.getActivityPicture());
                model.addAttribute("studyActivity", studyActivity);
                return "redirect:/studyactivity-details/" + studyActivity.getId();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error uploading activity picture";
            }
        }

        return "No picture uploaded";
    }

    // Retrieve and display the study activity picture
    @GetMapping("/activity/{id}/activityPicture")
    public ResponseEntity<byte[]> getActivityPicture(@PathVariable Long id){
        StudyActivity activity = studyActivityService.findById(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(activity.getActivityPicture());
    }

    @RequestMapping(value = "/feed-search", method = RequestMethod.GET)
    public String searchStudyActivities(@RequestParam("query") String query, Model model, HttpSession session) {
        // Get the user from the session
        User sessionUser = (User) session.getAttribute("user");

        // Ensure the user is not null before querying
        if (sessionUser != null) {
            // Pass the managed User entity to the service
            List<StudyActivity> searchResults = studyActivityService.searchByTitleOrDescription(query, sessionUser);
            model.addAttribute("studyactivity", searchResults);

        } else {
            // Handle the case where the user is not logged in or session has expired
            return "redirect:/login";  // Redirect to login page if needed
        }

        return "search";
    }
    // Controller Method to toggle coffee for a study activity
    @Autowired CoffeeService coffeeService;
    @RequestMapping(value = "/studyactivity/{id}/toggle-coffee", method = RequestMethod.POST)
    public String toggleCoffee(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        StudyActivity activity = studyActivityService.findById(id); // Fetch the StudyActivity by ID

        if (user != null && activity != null) {
            Coffee existingCoffee = coffeeService.findCoffeeByUserAndActivity(user, activity);
            if (existingCoffee != null) {
                // If coffee exists, remove it
                coffeeService.removeCoffee(user, activity);
            } else {
                // If coffee does not exist, add it
                coffeeService.giveCoffee(user, activity);
            }
        }
        return "redirect:/feed"; // Redirect to the feed page after toggling coffee
    }
}
