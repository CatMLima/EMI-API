package is.hi.hbv501g.team20.Controllers.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudyActivityRestControllerTest {

    @Mock private StudyActivityService studyActivityService;
    @Mock private UserService userService;
    @Mock private CoffeeService coffeeService;
    @Mock private UserAuthService userAuthService;

    @InjectMocks
    private StudyActivityRestController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private User otherUser;
    private StudyActivity testActivity;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // userAuthService uses @Autowired field injection; @InjectMocks skips fields after
        // constructor injection, so we set it manually.
        ReflectionTestUtils.setField(controller, "userAuthService", userAuthService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = new User("Test User", "test@test.com", "hashed");
        testUser.setId(1L);
        testUser.setIsActive(1);
        testUser.setPrivacy(0);

        otherUser = new User("Other User", "other@test.com", "hashed");
        otherUser.setId(2L);

        testLocation = new Location();
        testLocation.setBuilding(Building.ADAL);
        testLocation.setUserCount(1);

        testActivity = new StudyActivity();
        // StudyActivity.setId() has a bug (assigns field to itself), so use reflection
        ReflectionTestUtils.setField(testActivity, "id", 10L);
        testActivity.setUser(testUser);
        testActivity.setTitle("Study Session");
        testActivity.setDescription("Studying hard");
        testActivity.setSubjectID("CS101");
        testActivity.setSubjectName("Computer Science");
        testActivity.setBuilding(Building.ADAL);
        testActivity.setLocation(testLocation);
        testActivity.setIsActive(0);
        testActivity.setDate(new Date());
        testActivity.setStart(LocalTime.now().minusHours(1));
    }

    // -------------------------------------------------------------------------
    // POST /rest/activities — create session
    // -------------------------------------------------------------------------

    @Test
    void createActivity_authenticated_returns201() throws Exception {
        CreateStudyActivityRequest request = new CreateStudyActivityRequest(
                "Study Session", "Studying hard", "CS101", "Computer Science", Building.ADAL);

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.save(any(User.class))).thenReturn(testUser);
        when(studyActivityService.findByBuilding(Building.ADAL)).thenReturn(testLocation);
        when(studyActivityService.save(any(Location.class))).thenReturn(testLocation);
        when(studyActivityService.save(any(StudyActivity.class))).thenAnswer(inv -> {
            StudyActivity sa = inv.getArgument(0);
            ReflectionTestUtils.setField(sa, "id", 10L);
            return sa;
        });

        mockMvc.perform(post("/rest/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Study Session"));
    }

    @Test
    void createActivity_unauthenticated_returns401() throws Exception {
        CreateStudyActivityRequest request = new CreateStudyActivityRequest(
                "Session", "Desc", "ID", "Name", Building.ADAL);

        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(post("/rest/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /rest/activities — list sessions
    // -------------------------------------------------------------------------

    @Test
    void getMyActivities_returnsListOfDTOs() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findByUser(testUser)).thenReturn(List.of(testActivity));
        when(coffeeService.findCoffeeByUserAndActivity(eq(testUser), any())).thenReturn(null);

        mockMvc.perform(get("/rest/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Study Session"))
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void getMyActivities_unauthenticated_returns401() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/rest/activities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyActivities_noSessions_returnsEmptyList() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findByUser(testUser)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/rest/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /rest/activities/ongoing
    // -------------------------------------------------------------------------

    @Test
    void getOngoing_hasActiveSession_returns200() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.getOngoingId(testUser)).thenReturn(10L);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(get("/rest/activities/ongoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Study Session"));
    }

    @Test
    void getOngoing_noActiveSession_returns404() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(userService.getOngoingId(testUser)).thenReturn(null);

        mockMvc.perform(get("/rest/activities/ongoing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOngoing_unauthenticated_returns401() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/rest/activities/ongoing"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /rest/activities/{id}
    // -------------------------------------------------------------------------

    @Test
    void getActivity_found_returns200WithDTO() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);
        when(coffeeService.findCoffeeByUserAndActivity(testUser, testActivity)).thenReturn(null);

        mockMvc.perform(get("/rest/activities/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Study Session"))
                .andExpect(jsonPath("$.subjectID").value("CS101"));
    }

    @Test
    void getActivity_notFound_returns404() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/rest/activities/99"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /rest/activities/{id} — edit session
    // -------------------------------------------------------------------------

    @Test
    void editActivity_ownSession_returns200() throws Exception {
        EditActivityRequest request = new EditActivityRequest(
                "Updated Title", "Updated Desc", "CS102", "New Subject");

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(put("/rest/activities/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Study session updated."));

        verify(studyActivityService).save(argThat((StudyActivity sa) ->
                "Updated Title".equals(sa.getTitle()) &&
                "CS102".equals(sa.getSubjectID())
        ));
    }

    @Test
    void editActivity_notOwner_returns403() throws Exception {
        EditActivityRequest request = new EditActivityRequest("Title", "Desc", "ID", "Name");
        testActivity.setUser(otherUser);

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(put("/rest/activities/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void editActivity_notFound_returns404() throws Exception {
        EditActivityRequest request = new EditActivityRequest("Title", "Desc", "ID", "Name");

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/rest/activities/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PATCH /rest/activities/{id}/finish
    // -------------------------------------------------------------------------

    @Test
    void finishActivity_ownSession_returns200() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);
        when(userService.updateStreak(testUser)).thenReturn(testUser);
        when(userService.save(testUser)).thenReturn(testUser);

        mockMvc.perform(patch("/rest/activities/10/finish"))
                .andExpect(status().isOk())
                .andExpect(content().string("Study session finished."));

        verify(studyActivityService).save(argThat((StudyActivity sa) -> sa.getIsActive() == 1));
    }

    @Test
    void finishActivity_notOwner_returns403() throws Exception {
        testActivity.setUser(otherUser);

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(patch("/rest/activities/10/finish"))
                .andExpect(status().isForbidden());
    }

    @Test
    void finishActivity_notFound_returns404() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(99L)).thenReturn(null);

        mockMvc.perform(patch("/rest/activities/99/finish"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // DELETE /rest/activities/{id}
    // -------------------------------------------------------------------------

    @Test
    void deleteActivity_ownSession_returns200() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);
        doNothing().when(studyActivityService).delete(testActivity);

        mockMvc.perform(delete("/rest/activities/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Study session deleted."));

        verify(studyActivityService).delete(testActivity);
    }

    @Test
    void deleteActivity_notOwner_returns403() throws Exception {
        testActivity.setUser(otherUser);

        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(delete("/rest/activities/10"))
                .andExpect(status().isForbidden());

        verify(studyActivityService, never()).delete(any());
    }

    @Test
    void deleteActivity_notFound_returns404() throws Exception {
        when(userAuthService.getAuthenticatedUser()).thenReturn(testUser);
        when(studyActivityService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/rest/activities/99"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // GET /rest/activities/{id}/picture
    // -------------------------------------------------------------------------

    @Test
    void getActivityPicture_exists_returns200WithImage() throws Exception {
        testActivity.setActivityPicture(new byte[]{1, 2, 3});
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(get("/rest/activities/10/picture"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void getActivityPicture_noPicture_returns404() throws Exception {
        testActivity.setActivityPicture(null);
        when(studyActivityService.findById(10L)).thenReturn(testActivity);

        mockMvc.perform(get("/rest/activities/10/picture"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActivityPicture_activityNotFound_returns404() throws Exception {
        when(studyActivityService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/rest/activities/99/picture"))
                .andExpect(status().isNotFound());
    }
}
