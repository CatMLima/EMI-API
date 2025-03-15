package is.hi.hbv501g.team20.Controllers.Rest;

import is.hi.hbv501g.team20.Persistence.Entities.*;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import is.hi.hbv501g.team20.Services.UserAuthService;
import is.hi.hbv501g.team20.Services.UserService;
import is.hi.hbv501g.team20.dto.PostDTO;
import is.hi.hbv501g.team20.dto.StudyActivityDTO;
import is.hi.hbv501g.team20.dto.StudyGroupDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class StudyGroupRestController {

    @Autowired
    private UserAuthService userAuthService;

    private final StudyGroupService studyGroupService;
    private final UserService userService;
    private final PostService postService;

    public StudyGroupRestController(StudyGroupService studyGroupService, UserService userService, PostService postService) {
        this.studyGroupService = studyGroupService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/rest/studygroups-feed")
    public ResponseEntity<List<StudyGroupDTO>> getStudyGroupFeed() {
        User user = userAuthService.getAuthenticatedUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<StudyGroup> notMemberStudyGroups = studyGroupService.findAllExceptUser(user.getId());
        if (notMemberStudyGroups == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<StudyGroupDTO> dtoList = new ArrayList<>();
        for (StudyGroup sg : notMemberStudyGroups) {
            StudyGroupDTO dto = new StudyGroupDTO(sg.getId(), sg.getName(), sg.getDescription(),
                    sg.getSubjectId(), sg.getLookingForMembers(), sg.getMemberCount(), 0);
            dtoList.add(dto);
        }

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/rest/get/user/studygroups")
    public ResponseEntity<List<StudyGroupDTO>> getUserStudyGroup() {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<StudyGroup> isMemberStudyGroups = studyGroupService.findByUserId(user.getId());
        if (isMemberStudyGroups == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<StudyGroupDTO> dtoList = new ArrayList<>();
        for (StudyGroup sg : isMemberStudyGroups) {
            StudyGroupDTO dto = new StudyGroupDTO(sg.getId(), sg.getName(), sg.getDescription(),
                    sg.getSubjectId(), sg.getLookingForMembers(), sg.getMemberCount(), 1);
            dtoList.add(dto);
        }

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/rest/studygroup-create")
    public ResponseEntity<String> createStudyGroup(@RequestBody StudyGroupDTO studyGroupDTO) {
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } else if (studyGroupDTO.getName() == null || studyGroupDTO.getDescription() == null
                || studyGroupDTO.getSubjectId() == null || studyGroupDTO.getSubjectId() == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Missing parameters.");
        }

        StudyGroup studyGroup = new StudyGroup(studyGroupDTO.getName(), studyGroupDTO.getDescription(),
                studyGroupDTO.getSubjectId(), studyGroupDTO.getLookingForMembers());

        User admin = userService.findById(user.getId());
        studyGroup.setAdmin(admin);
        studyGroup.addMember(admin);

        studyGroupService.save(studyGroup);

        return ResponseEntity.status(HttpStatus.CREATED).body("StudyGroup created!");
    }

    @PostMapping("/rest/studygroup-join/{id}")
    public ResponseEntity<String> joinStudyGroup(@PathVariable Long id){
        User user = userAuthService.getAuthenticatedUser();
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

    @GetMapping("/rest/studygroup-view/{id}")
    public ResponseEntity<List<PostDTO>> viewStudyGroup(@PathVariable("id") Long id) {
        StudyGroup studyGroup = studyGroupService.findById(id);
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userAuthService.getAuthenticatedUser();
        List<Post> posts = postService.findByStudyGroup(studyGroup);
        Collections.reverse(posts);

        List<PostDTO> dtoList = new ArrayList<>();
        for (Post post : posts) {
            PostDTO dto = new PostDTO(post.getId(), studyGroup.getId(), post.getUser().getId(),
                    post.getUser().getName(), post.getTitle(), post.getContent());
            dtoList.add(dto);
        }

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/rest/admin-join")
    public ResponseEntity<String> changeLookingForMembers(@RequestParam("studyGroupId") long studyGroupId,
                                                     @RequestParam("lookingForMembers") int lookingForMembers) {
        StudyGroup studyGroup = studyGroupService.findById(studyGroupId);
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Study group does not exist.");
        }

        studyGroup.setLookingForMembers(lookingForMembers);
        studyGroupService.save(studyGroup);
        return ResponseEntity.ok().body("Updated looking for members status.");
    }

    @PostMapping("/rest/post-create")
    public ResponseEntity<String> createPost(@RequestBody PostDTO postDTO){
        User user = userAuthService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        StudyGroup studyGroup = studyGroupService.findById(postDTO.getStudyGroupId());
        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUser(user);
        post.setStudygroup(studyGroup);
        postService.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).body("post created!");
    }

}
