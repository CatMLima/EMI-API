package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.CoffeeService;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.dto.ChangePasswordRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyGroupService studyGroupService;

    @Autowired
    private PostService postService;

    @Autowired
    private CoffeeService coffeeService;

    // Pre: User is not currently registered.
    // Post: New user account is created and their information displayed.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            User registeredUser = userService.registerNewUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pre: user exists, user password matches the password encrypted in the database
    // Post: token generated that can be used to validate the user's actions
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try {
            String token = userService.verify(user);
            if (token.equals("Failed")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pre: User must exist, token must be valid.
    // Post: Current signed in user's information is sent.
    @GetMapping("/get/user")
    public ResponseEntity<User> getCurrentUser(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userService.findByEmail(username);

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User userUpdated = userService.updateUser(user);

        return ResponseEntity.ok(userUpdated);
    }


    // Pre: user token must be valid, new privacy integer value must be provided, 0 <= privacy <= 1
    // Post: the user's privacy is changed, success message issued.
    @PostMapping("/settings/change_privacy")
    public ResponseEntity<?> changePrivacy(@RequestParam int privacy){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userService.findByEmail(username);

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        userService.updatePrivacy(user.getId(),privacy);

        return ResponseEntity.ok().body("Privacy changed successfully.");

    }

    // Pre: valid token, user's old password is correct, user's new password match
    // Post: user assigned new password and must log in again (the token will change)
    @PutMapping("/settings/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user.");
        }

        String username = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userService.findByEmail(username);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }


        if (!userService.checkOldPassword(user,request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password incorrect.");
        }

        if (!userService.checkNewPassword(request.getNewPassword(),request.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords don't match or are invalid.");
        }

        userService.changePassword(user,request.getNewPassword());

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Password changed successfully. Please log in again.");

    }




// THIS NEXT
    @PostMapping("/uploadProfilePicture")
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

    @GetMapping("/user/{id}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id){
        User user = userService.findById(id);
        if (user == null || user.getProfilePicture() == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    @DeleteMapping("/delete-account/{id}")
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





    /* These are all part of a testing thing.
     */
    private List<User> users = new ArrayList<>(List.of(
            new User("Cat","cms5@hi.is","somegoodpassowrd"),
            new User("josh", "josh@hi.is", "someotherpassword")
    ));

    @GetMapping("/userslist")
    public List<User> getUsers(){
        return users;
    }

    /* testing thing ends here */

}
