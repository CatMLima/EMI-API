package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.Post;
import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class StudyGroupRestController {

    private final StudyGroupService studyGroupService;
    private final PostService postService;

    public StudyGroupRestController(StudyGroupService studyGroupService, PostService postService) {
        this.studyGroupService = studyGroupService;
        this.postService = postService;
    }

    @GetMapping("/studygroups-feed")
    public ResponseEntity<?> getStudyGroupFeed(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        List<StudyGroup> isMemberStudyGroups = studyGroupService.findByUserId(user.getId());
        List<StudyGroup> notMemberStudyGroups = studyGroupService.findAllExceptUser(user.getId());

        Map<String, List<StudyGroup>> response = new HashMap<>();
        response.put("isMemberStudyGroups", isMemberStudyGroups);
        response.put("notMemberStudyGroups", notMemberStudyGroups);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/studygroup-create")
    public ResponseEntity<StudyGroup> getStudyGroupTemplate(){
        StudyGroup studyGroup = new StudyGroup();
        return ResponseEntity.ok(studyGroup);
    }

    @PostMapping("/studygroup-create")
    public ResponseEntity<?> createStudyGroup(HttpSession session, @RequestBody StudyGroup studyGroup) {
        User admin = (User) session.getAttribute("user");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        studyGroup.setAdmin(admin);
        studyGroup.addMember(admin);
        studyGroup.addMemberCount();

        studyGroupService.save(studyGroup);

        return ResponseEntity.status(HttpStatus.CREATED).body(studyGroup);
    }

    @PostMapping("/studygroup-join/{id}")
    public ResponseEntity<?> joinStudyGroup(@PathVariable Long id, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        StudyGroup studyGroup = studyGroupService.findById(id);
        if (!studyGroup.getMembers().contains(user)) {
            studyGroup.addMember(user);
            studyGroup.addMemberCount();
            studyGroupService.save(studyGroup);
        }

        return ResponseEntity.ok().body("Joined study group successfully!");
    }

    @GetMapping("/studygroup-view/{id}")
    public ResponseEntity<?> viewStudyGroup(@PathVariable("id") Long id, HttpSession session) {
        StudyGroup studyGroup = studyGroupService.findById(id);
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Study group does not exist.");
        }

        User user = (User) session.getAttribute("user");
        List<Post> posts = postService.findByStudyGroup(studyGroup);
        Collections.reverse(posts);

        Map<String, List<Post>> response = new HashMap<>();
        response.put("posts", posts);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin-join")
    public ResponseEntity<?> changeLookingForMembers(@RequestParam("studyGroupId") long studyGroupId,
                                                     @RequestParam("lookingForMembers") int lookingForMembers) {
        StudyGroup studyGroup = studyGroupService.findById(studyGroupId);
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Study group does not exist.");
        }

        studyGroup.setLookingForMembers(lookingForMembers);
        studyGroupService.save(studyGroup);
        return ResponseEntity.ok().body("Updated looking for members status.");
    }

    @PostMapping("/post-create")
    public ResponseEntity<?> createPost(@RequestParam("studyGroupId") long studyGroupId, HttpSession session,
                                        @RequestBody Post post){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        StudyGroup studyGroup = studyGroupService.findById(studyGroupId);
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Study group does not exist.");
        }

        post.setUser(user);
        post.setStudygroup(studyGroup);
        postService.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }


}
