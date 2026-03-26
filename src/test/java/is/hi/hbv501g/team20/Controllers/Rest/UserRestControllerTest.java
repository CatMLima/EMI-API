package is.hi.hbv501g.team20.Controllers.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.CoffeeService;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import is.hi.hbv501g.team20.Services.UserAuthService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.dto.ChangePasswordRequest;
import is.hi.hbv501g.team20.dto.LoginRequest;
import is.hi.hbv501g.team20.dto.LoginResponse;
import is.hi.hbv501g.team20.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock private UserService userService;
    @Mock private UserAuthService userAuthService;
    @Mock private StudyGroupService studyGroupService;
    @Mock private PostService postService;
    @Mock private CoffeeService coffeeService;

    @InjectMocks
    private UserRestController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = new User("Test User", "test@test.com", "hashedpassword");
        testUser.setId(1L);
        testUser.setIsActive(1);
        testUser.setPrivacy(0);
        testUser.setStreak(3);
    }

    // -------------------------------------------------------------------------
    // POST /rest/register
    // -------------------------------------------------------------------------

    @Test
    void register_validRequest_returns201WithUserDTO() throws Exception {
        RegisterRequest request = new RegisterRequest("New User", "new@test.com", "password123");

        User registered = new User("New User", "new@test.com", "hashed");
        registered.setId(2L);
        registered.setIsActive(1);
        registered.setPrivacy(0);
        registered.setStreak(0);

        when(userService.registerNewUser(any(User.class))).thenReturn(registered);

        mockMvc.perform(post("/rest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void register_serviceThrows_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("User", "dup@test.com", "pass");

        when(userService.registerNewUser(any(User.class))).thenThrow(new RuntimeException("Duplicate email"));

        mockMvc.perform(post("/rest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /rest/login
    // -------------------------------------------------------------------------

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");

        when(userService.verify(any(User.class))).thenReturn("jwt.token.here");
        when(userService.findByEmail("test@test.com")).thenReturn(testUser);

        mockMvc.perform(post("/rest/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "wrongpassword");

        when(userService.verify(any(User.class))).thenReturn("Failed");

        mockMvc.perform(post("/rest/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /rest/users/me
    // -------------------------------------------------------------------------

    @Test
    void getMe_authenticated_returns200WithUserDTO() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.updateUser(testUser)).thenReturn(testUser);
        when(userService.totalTime(testUser)).thenReturn("1h 30m");
        when(userService.totalSessions(testUser)).thenReturn(5);
        when(userService.average(testUser)).thenReturn("0h 18m");
        when(userService.favouriteLocation(testUser)).thenReturn("Adal");

        mockMvc.perform(get("/rest/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.totalTime").value("1h 30m"))
                .andExpect(jsonPath("$.numberActivities").value(5));
    }

    @Test
    void getMe_unauthenticated_returns401() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/rest/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /rest/users/{id}
    // -------------------------------------------------------------------------

    @Test
    void getUserById_existingUser_returns200WithUserDTO() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);
        when(userService.totalTime(testUser)).thenReturn("2h 0m");
        when(userService.totalSessions(testUser)).thenReturn(4);
        when(userService.average(testUser)).thenReturn("0h 30m");
        when(userService.favouriteLocation(testUser)).thenReturn("Gimli");
        when(userService.updateUser(testUser)).thenReturn(testUser);

        mockMvc.perform(get("/rest/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/rest/users/99"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /rest/users/me/privacy
    // -------------------------------------------------------------------------

    @Test
    void changePrivacy_authenticated_returns200() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.updatePrivacy(1L, 1)).thenReturn(testUser);

        mockMvc.perform(put("/rest/users/me/privacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"privacy\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Privacy updated."));
    }

    @Test
    void changePrivacy_unauthenticated_returns401() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(put("/rest/users/me/privacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"privacy\": 1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePrivacy_missingField_returns400() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);

        mockMvc.perform(put("/rest/users/me/privacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // PUT /rest/users/me/password
    // -------------------------------------------------------------------------

    @Test
    void changePassword_validRequest_returns200() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("newpass123");

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.checkOldPassword(testUser, "oldpass")).thenReturn(true);
        when(userService.checkNewPassword("newpass123", "newpass123")).thenReturn(true);
        when(userService.changePassword(eq(testUser), eq("newpass123"))).thenReturn(testUser);

        mockMvc.perform(put("/rest/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully. Please log in again."));
    }

    @Test
    void changePassword_wrongOldPassword_returns400() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongpass");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("newpass123");

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.checkOldPassword(testUser, "wrongpass")).thenReturn(false);

        mockMvc.perform(put("/rest/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Old password incorrect."));
    }

    @Test
    void changePassword_passwordMismatch_returns400() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("different");

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.checkOldPassword(testUser, "oldpass")).thenReturn(true);
        when(userService.checkNewPassword("newpass123", "different")).thenReturn(false);

        mockMvc.perform(put("/rest/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("New passwords don't match or are invalid."));
    }

    // -------------------------------------------------------------------------
    // DELETE /rest/users/me
    // -------------------------------------------------------------------------

    @Test
    void deleteAccount_authenticated_returns200() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        doNothing().when(postService).deletePostByUser(testUser);
        doNothing().when(coffeeService).deleteCoffeesByUser(testUser);
        doNothing().when(studyGroupService).removeUserFromStudyGroups(testUser);
        doNothing().when(userService).deleteUser(testUser);

        mockMvc.perform(delete("/rest/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted."));

        verify(userService).deleteUser(testUser);
    }

    @Test
    void deleteAccount_unauthenticated_returns401() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(delete("/rest/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /rest/users/me/picture and GET /rest/users/{id}/picture
    // -------------------------------------------------------------------------

    @Test
    void getMyPicture_noPictureSet_returns404() throws Exception {
        testUser.setProfilePicture(null);
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);

        mockMvc.perform(get("/rest/users/me/picture"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyPicture_pictureSet_returns200WithImage() throws Exception {
        testUser.setProfilePicture(new byte[]{1, 2, 3});
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);

        mockMvc.perform(get("/rest/users/me/picture"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void getPictureById_userNotFound_returns404() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/rest/users/99/picture"))
                .andExpect(status().isNotFound());
    }
}
