package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.*;
import is.hi.hbv501g.team20.dto.ChangePasswordRequest;
import is.hi.hbv501g.team20.dto.LoginResponse;
import is.hi.hbv501g.team20.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private UserAuthService userAuthService;


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

    // Pre: Receives email and password
    // Post: if valid information, a token is given which allows user to be authenticated in other method calls.

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try {
            // if I don't like it, change it to RequestBody User.
            String token = userService.verify(user);
            if (token.equals("Failed")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long userId = userService.findByEmail(user.getEmail()).getId();
//            Long ongoingId = userService.getOngoingId(user);
//            if (ongoingId == null){
//                ongoingId = 0L;
//            }
            Integer isActive = userService.findByEmail(user.getEmail()).getIsActive();
            LoginResponse response = new LoginResponse(token, userId, isActive);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pre: User must exist, token must be valid.
    // Post: Current signed in user's information is sent.
    @GetMapping("/get/user")
    public ResponseEntity<User> getCurrentUser(){

        User user = userAuthService.getAuthenticatedUser();

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User userUpdated = userService.updateUser(user);

        User smallerUser = new User(user.getName(),userUpdated.getEmail(), userUpdated.getPassword());

        return ResponseEntity.ok(smallerUser);
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){

        User user = userService.findById(id);
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UserDTO userDTO = new UserDTO(user.getId(), user.getIsActive(), user.getPrivacy(), user.getStreak(),
                user.getName(), user.getEmail());

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/set/user")
    public ResponseEntity<String> syncUser(@RequestBody UserDTO userDTO){
        if (userDTO != null) {
            User user = userService.findById(userDTO.getId());
            if (user == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

            user.setIsActive(userDTO.getIsActive());
            user.setPrivacy(userDTO.getPrivacy());
            user.setStreak(userDTO.getStreak());
            userService.save(user);

            return ResponseEntity.ok("Sync User successful.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    /*
    A bunch of GET MAPPINGS to get the information about the User.
     */

    @GetMapping("/get/isActive")
    public ResponseEntity<Integer> getIsActive() {
        User user = userAuthService.getAuthenticatedUser();

        if (user == null || user.getIsActive() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(user.getIsActive());
    }

    @GetMapping("/get/privacy")
    public ResponseEntity<Integer> getPrivacy() {
        User user = userAuthService.getAuthenticatedUser();

        if (user == null || user.getPrivacy() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(user.getPrivacy());
    }

    @GetMapping("/get/streak")
    public ResponseEntity<Integer> getStreak() {
        User user = userAuthService.getAuthenticatedUser();
        User streakCheckedUser = userService.checkStreak(user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(streakCheckedUser.getStreak());
    }

    @GetMapping("/get/name")
    public ResponseEntity<String> getName() {
        User user = userAuthService.getAuthenticatedUser();

        if (user == null || user.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(user.getName());
    }


    // Pre: user token must be valid, new privacy integer value must be provided, 0 <= privacy <= 1
    // Post: the user's privacy is changed, success message issued.
    @PostMapping("/settings/change_privacy")
    public ResponseEntity<?> changePrivacy(@RequestParam int privacy){
        User user = userAuthService.getAuthenticatedUser();

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

        User user = userAuthService.getAuthenticatedUser();

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

    @GetMapping("/get/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(){
        User user = userAuthService.getAuthenticatedUser();

        if (user == null || user.getProfilePicture() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    @GetMapping("/get/profile_pic_by_id/{id}")
    public ResponseEntity<byte[]> getProfilePicById(@PathVariable Long id){
        User user = userAuthService.getAuthenticatedUser();
        User searchedUser = userService.findById(id);

        if (user == null || searchedUser == null || searchedUser.getProfilePicture() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(searchedUser.getProfilePicture());
    }

    @PostMapping("/set/profilePicture")
    public ResponseEntity<String> uploadPicture(@RequestParam("file") MultipartFile profilePicture){
        User user = userAuthService.getAuthenticatedUser();

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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

    @DeleteMapping("/delete_account")
    public ResponseEntity<String> deleteAccount(){
        User user = userAuthService.getAuthenticatedUser();

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            postService.deletePostByUser(user);
            coffeeService.deleteCoffeesByUser(user);
            studyGroupService.removeUserFromStudyGroups(user);
            userService.deleteUser(user);

            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("User has been deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user.");
        }
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
