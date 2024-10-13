package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import is.hi.hbv501g.team20.Services.Implementations.LoginServiceImplementation;
import is.hi.hbv501g.team20.Services.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private LoginServiceImplementation loginService;

    @Autowired
    public UserController(LoginServiceImplementation loginService, UserRepository userRepository) {
        this.loginService = loginService;
        this.userRepository = userRepository;
    }

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
                loginService.save(user);
                model.addAttribute("user", user);
                model.addAttribute("picture", user.getProfilePicture());
                return "user";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error uploading profile picture";
            }
        }

        return "No picture uploaded";
    }

    /*
    Still working on this - Cat
     */
    @GetMapping("/user/{id}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id){
        User user = loginService.findById(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

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

}
