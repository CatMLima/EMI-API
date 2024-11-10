package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import is.hi.hbv501g.team20.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
public class UserController {

    private UserService userService;
    private StudyGroupService studyGroupService;

    @Autowired
    public UserController(UserService userService, StudyGroupService studyGroupService) {
        this.userService = userService;
        this.studyGroupService = studyGroupService;
    }

    // Displays the login page
    @RequestMapping(value="/login", method= RequestMethod.GET)
    public String getLogInPage() { return "login"; }

    // Displays the sign up page
    @RequestMapping(value="/sign-up", method= RequestMethod.GET)
    public String getSignUpPage(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    // To move to the user page, control then passed to the UserController
    @RequestMapping(value="/user", method=RequestMethod.GET)
    public String getUserPage(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            // add attributes related to user stats
            model.addAttribute("totalActivityTime", userService.totalTime(user));
            model.addAttribute("activitiesCount", userService.totalSessions(user));
            model.addAttribute("averageTime", userService.average(user));
            model.addAttribute("favouriteLocation", userService.favouriteLocation(user));
            System.out.println("User in session: " + user.getName());
            return "user";
        }
        return "login";
    }

    // pull up the settings page
    @RequestMapping(value="/settings", method=RequestMethod.GET)
    public String getSettingsPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println("User in session: " + user.getName());
            return "settings";
        }
        return "user";
    }

    // Checks if the user is a new one before allowing them to create an account and saving it to the database.
    @RequestMapping(value="/api/signup", method= RequestMethod.POST)
    public String signupPOST(User user, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "redirect:/signup";
        }

        User alreadyExisting = userService.findByEmail(user.getEmail());
        if (alreadyExisting == null) {
            userService.save(user);
        }
        return "redirect:/login";

    }

    // When an existing user logs in, we add them as a session and model attribute while they are online.
    @RequestMapping(value="/api/login", method=RequestMethod.POST)
    public String loginPOST(@ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session){
        if (user.getPrivacy() == null) {
            user.setPrivacy(0);
        }

        if (result.hasErrors()) {
            return "login";
        }

        User existing = userService.findByEmail(user.getEmail());

        if (existing != null) {
            if (existing.getStreak() == null){
                existing = userService.initializeStreak(existing);
            }
            if (existing.getStreak() != 0){
                existing = userService.checkStreak(existing);
            }
            if(existing.getPrivacy() == null || existing.privacy != 0 && existing.privacy != 1 ) {
                existing = userService.updatePrivacy(existing.getId(), 0);
            }
            if (existing.getPassword().equals(user.getPassword())) {
                session.setAttribute("user", existing); // saves user to the session
                model.addAttribute("user", existing);
                return "redirect:/feed";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/settings/privacy")
    public String changePrivacy(@RequestParam int privacy, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user"); // Retrieve user from session

        if (user == null) {
            model.addAttribute("error", "User not logged in.");
            return "login";  // Redirect to login page
        }

        // Log the user and privacy values for debugging
        System.out.println("User before update: " + user.getName());
        System.out.println("Privacy before update: " + user.getPrivacy());

        // Update the user's privacy setting and save
        user = userService.updatePrivacy(user.getId(), privacy);

        // Log the updated user object
        System.out.println("User after update: " + user.getName());
        System.out.println("Privacy after update: " + user.getPrivacy());

        // Update session with the latest user data
        session.setAttribute("user", user);

        // Show the updated settings
        model.addAttribute("user", user);
        model.addAttribute("message", "Privacy setting updated successfully.");
        return "settings"; // Return to settings form with updated values
    }

    @GetMapping("/delete-account/{id}")
    public String deleteAccount(@PathVariable("id") long id, HttpSession session, Model model) {

        User user = userService.findById(id);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "redirect:/login"; // Or any other error page
        }

        studyGroupService.removeUserFromStudyGroups(user);
        userService.deleteUser(user);
        session.invalidate(); // Invalidate session after account deletion

        model.addAttribute("message", "Account deleted successfully.");
        // Redirect to home
        return "home";
    }

    // upload a new profile picture and save the changes to the database.
    @PostMapping("/api/uploadProfilePicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture,
                                       HttpSession session, Model model) {
        // Find the user by ID (you might use a service here to get the user)
        User user = (User) session.getAttribute("user");
        System.out.println(user.toString());

        if (!profilePicture.isEmpty()) {
            try{
                byte[] bytes = profilePicture.getBytes();
                user.setProfilePicture(bytes);
                userService.save(user);
                model.addAttribute("user", user);
                model.addAttribute("picture", user.getProfilePicture());
                return "settings";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error uploading profile picture";
            }
        }

        return "No picture uploaded";
    }

    // retrieve and display the user picture
    @GetMapping("/user/{id}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id){
        User user = userService.findById(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    // Changes the users password
    @GetMapping(value ="/change-password/{id}")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String newPassword2,
                                 HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !Objects.equals(user.getPassword(), oldPassword)) {
            model.addAttribute("message", "Current password is incorrect.");
            return "redirect:/settings";
        }

        if (!Objects.equals(newPassword, newPassword2)) {
            model.addAttribute("message", "New passwords do not match.");
            return "redirect:/settings";
        }

        user.setPassword(newPassword);
        userService.save(user);
        model.addAttribute("message", "Password changed successfully.");
        return "redirect:/settings";

    }
}
