package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.*;
import is.hi.hbv501g.team20.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Users", description = "APIs for managing users.")
@RestController
@RequestMapping("/users")
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

    // POST /register — create a new account
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = new User(request.getName(), request.getEmail(), request.getPassword());
            User registered = userService.registerNewUser(user);
            UserDTO dto = new UserDTO(registered.getId(), registered.getIsActive(), registered.getPrivacy(),
                    registered.getStreak(), registered.getName(), registered.getEmail(),
                    "0h 0m", 0, "0h 0m", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /login — authenticate and receive JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User userForAuth = new User(null, request.getEmail(), request.getPassword());
            String token = userService.verify(userForAuth);
            if (token.equals("Failed")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userService.findByEmail(request.getEmail());
            LoginResponse response = new LoginResponse(token, user.getId(), user.getIsActive());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /me — get the authenticated user's profile
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        user = userService.updateUser(user);
        String totalTime = userService.totalTime(user);
        Integer totalSessions = userService.totalSessions(user);
        String averageTime = userService.average(user);
        String favouriteLocation = userService.favouriteLocation(user);

        UserDTO dto = new UserDTO(user.getId(), user.getIsActive(), user.getPrivacy(), user.getStreak(),
                user.getName(), user.getEmail(), totalTime, totalSessions, averageTime, favouriteLocation);
        return ResponseEntity.ok(dto);
    }

    // GET /{id} — get a user's public profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        String totalTime = userService.totalTime(user);
        Integer totalSessions = userService.totalSessions(user);
        String averageTime = userService.average(user);
        String favouriteLocation = userService.favouriteLocation(user);
        user = userService.updateUser(user);

        UserDTO dto = new UserDTO(user.getId(), user.getIsActive(), user.getPrivacy(), user.getStreak(),
                user.getName(), user.getEmail(), totalTime, totalSessions, averageTime, favouriteLocation);
        return ResponseEntity.ok(dto);
    }

    // PUT /me/privacy — update privacy setting
    @PutMapping("/me/privacy")
    public ResponseEntity<?> changePrivacy(@RequestBody Map<String, Integer> body) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Integer privacy = body.get("privacy");
        if (privacy == null) return ResponseEntity.badRequest().body("Missing 'privacy' field.");

        userService.updatePrivacy(user.getId(), privacy);
        return ResponseEntity.ok("Privacy updated.");
    }

    // PUT /me/password — change password
    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        if (!userService.checkOldPassword(user, request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password incorrect.");
        }
        if (!userService.checkNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords don't match or are invalid.");
        }

        userService.changePassword(user, request.getNewPassword());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Password changed successfully. Please log in again.");
    }

    // POST /me/picture — upload profile picture
    @PostMapping("/me/picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            user.setProfilePicture(file.getBytes());
            userService.save(user);
            return ResponseEntity.ok("Profile picture uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading profile picture.");
        }
    }

    // GET /me/picture — get own profile picture
    @GetMapping("/me/picture")
    public ResponseEntity<byte[]> getMyProfilePicture() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null || user.getProfilePicture() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    // GET /{id}/picture — get a user's profile picture by ID
    @GetMapping("/{id}/picture")
    public ResponseEntity<byte[]> getProfilePictureById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null || user.getProfilePicture() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getProfilePicture());
    }

    // DELETE /me — delete own account
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            postService.deletePostByUser(user);
            coffeeService.deleteCoffeesByUser(user);
            studyGroupService.removeUserFromStudyGroups(user);
            userService.deleteUser(user);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("Account deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting account.");
        }
    }
}
