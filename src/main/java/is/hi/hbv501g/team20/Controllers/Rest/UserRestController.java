package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.CoffeeService;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import is.hi.hbv501g.team20.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserRestController {

    private final UserService userService;
    private final StudyGroupService studyGroupService;
    private final PostService postService;
    private final CoffeeService coffeeService;

    @Autowired
    public UserRestController(UserService userService, StudyGroupService studyGroupService, PostService postService, CoffeeService coffeeService) {
        this.userService = userService;
        this.studyGroupService = studyGroupService;
        this.postService = postService;
        this.coffeeService = coffeeService;
    }

    @GetMapping("/rest/login")
    public ResponseEntity<String> getLogInPage(){
        return ResponseEntity.ok("Please proceed to the login page.");
    }

    @GetMapping("/rest/sign-up")
    public ResponseEntity<User> getSignUpPage(){
        return ResponseEntity.ok(new User());
    }

    @GetMapping("/rest/user")
    public ResponseEntity<?> getUserPage(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in");
        }

        Map<String,Object> userData = new HashMap<>();
        userData.put("user",user);
        userData.put("totalActivityTime", userService.totalTime(user));
        userData.put("activitiesCount", userService.totalSessions(user));
        userData.put("averageTime", userService.average(user));
        userData.put("favouriteLocation", userService.favouriteLocation(user));

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/rest/settings")
    public ResponseEntity<?> getSettingsPage(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/rest/signup")
    public ResponseEntity<?> signUpUser(@RequestBody User user){
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User has been created.");
    }

    @PostMapping("/rest/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpSession session){
        User existingUser = userService.findByEmail(user.getEmail());

        if(existingUser == null || !existingUser.getPassword().equals(user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password or email.");
        }

        if(existingUser.getStreak() == null){
            existingUser = userService.initializeStreak(user);
        } else if (existingUser.getStreak() != 0){
            existingUser = userService.checkStreak(existingUser);
        }

        if (existingUser.getPrivacy() == null || (existingUser.getPrivacy() != 0 && existingUser.getPrivacy() != 1)){
            existingUser = userService.updatePrivacy(existingUser.getId(), 0);
        }

        session.setAttribute("user", existingUser);
        return ResponseEntity.ok("Logged in successfullly.");
    }

    @PostMapping("/rest/settings/privacy")
    public ResponseEntity<?> changePrivacy(@RequestParam int privacy, HttpSession session){
        User user = (User) session.getAttribute("user");

        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        user = userService.updatePrivacy(user.getId(), privacy);
        session.setAttribute("user", user);

        return ResponseEntity.ok("Privacy changed successfully.");
    }

    @DeleteMapping("/rest/delete-account/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") long id, HttpSession session){
        User user = userService.findById(id);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        postService.deletePostByUser(user);
        coffeeService.deleteCoffeesByUser(user);
        studyGroupService.removeUserFromStudyGroups(user);
        userService.deleteUser(user);

        session.invalidate();
        return ResponseEntity.ok("User has been deleted.");
    }

    @PostMapping("/rest/uploadProfilePicture")
    public ResponseEntity<?> uploadPicture(@RequestParam("profilePicture") MultipartFile profilePicture, HttpSession session){
        User user = (User) session.getAttribute("user");

        if (user == null || profilePicture.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not logged in or no picture provided.");
        }

        try{
            byte[] bytes = profilePicture.getBytes();
            user.setProfilePicture(bytes);
            userService.save(user);
            return ResponseEntity.ok("Profile picture uploaded successfully.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading profile picture.");
        }
    }

    @GetMapping("/rest/user/{id}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id){
        User user = userService.findById(id);
        if (user == null || user.getProfilePicture() == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    @PutMapping("/rest/change-password/{id}")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Long id, @RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String newPassword2, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null || !sessionUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Message", "Unauthorized access"));
        }

        User user = userService.findById(id);
        if (user == null || !user.getPassword().equals(oldPassword)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message", "Current password is incorrect."));
        }

        if (!newPassword.equals(newPassword2)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message", "Passwords do not match."));
        }

        user.setPassword(newPassword);
        userService.save(user);

        return ResponseEntity.ok(Map.of("Message", "Password changed successfully"));
    }

}
