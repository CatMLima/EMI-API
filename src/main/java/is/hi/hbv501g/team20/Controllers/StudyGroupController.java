package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.*;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import is.hi.hbv501g.team20.Persistence.Repository.StudyGroupRepository;
import is.hi.hbv501g.team20.Services.LoginService;
import is.hi.hbv501g.team20.Services.PostService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class StudyGroupController {

    private StudyGroupService studyGroupService;
    private PostService postService;

    public StudyGroupController(StudyGroupService studyGroupService, PostService postService) {
        this.studyGroupService = studyGroupService;
        this.postService = postService;
    }

    // Displays a page containing a list of the user's studygroups
    @RequestMapping(value="/studygroups-feed", method= RequestMethod.GET)
    public String getStudyGroupFeed(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<StudyGroup> isMemberStudyGroups = studyGroupService.findByUserId(user.getId());
            List<StudyGroup> notMemberStudyGroups = studyGroupService.findAllExceptUser(user.getId());
            model.addAttribute("isMemberStudyGroup", isMemberStudyGroups);
            model.addAttribute("notMemberStudyGroup", notMemberStudyGroups);
        }
        return "studygroups-feed";
    }

    // Displays the Create a studygroup page
    @RequestMapping(value = "/studygroup-create", method = RequestMethod.GET)
    public String createStudyGroupGet(Model model) {
        model.addAttribute("studygroup", new StudyGroup());
        return "studygroup-create";
    }

    //create a new StudyGroup
    @RequestMapping(value = "/api/studygroup-create", method = RequestMethod.POST)
    public String createStudyGroup(HttpSession httpSession, StudyGroup studyGroup, BindingResult result){

        //set user (that created the group) as admin
        User admin = (User) httpSession.getAttribute("user");
        studyGroup.setAdmin(admin);
        studyGroup.addMember(admin);
        studyGroup.addMemberCount();

        if(result.hasErrors()){
            return "studygroup-create";
        }
        studyGroupService.save(studyGroup);
        return "redirect:/studygroups-feed";
    }

    // allows user to join a studygroup
    @GetMapping("/studygroup-join/{id}")
    public String joinStudyGroup(@PathVariable Long id, HttpSession session){

        User user = (User) session.getAttribute("user");
        StudyGroup studyGroup = studyGroupService.findById(id);

        //if user is not in studygroup, user joins
        if (!studyGroup.getMembers().contains(user)) {
            studyGroup.addMember(user);
            studyGroup.addMemberCount();
            studyGroupService.save(studyGroup);
        }
        return "redirect:/studygroups-feed";
    }

    // Displays a page containing a list of the user's studygroups
    @RequestMapping(value="/studygroup-view/{id}", method= RequestMethod.GET)
    public String getStudyGroupViewPage(@PathVariable("id") long id, HttpSession httpSession, Model model) {
        StudyGroup studyGroup = studyGroupService.findById(id);
        List<Post> posts = postService.findByStudyGroup(studyGroup);

        //sort posts so the most recent post in at the top
        Collections.reverse(posts);
        model.addAttribute("studyGroup", studyGroup);

        User user = (User) httpSession.getAttribute("user");
        if (posts != null && user != null) {
            model.addAttribute("post", posts);
            model.addAttribute("user", user);
        }
        return "studygroup-view";
    }

    //update lookingForMembers attribute of a studygroup
    @RequestMapping(value = "/api/admin-join", method = RequestMethod.POST)
    public String changeLookingForMembers(@RequestParam("studyGroupId") long studyGroupId, @RequestParam("lookingForMembers") int lookingForMembers) {
        StudyGroup studyGroup = studyGroupService.findById(studyGroupId);
        studyGroup.setLookingForMembers(lookingForMembers);
        studyGroupService.save(studyGroup);
        return "redirect:/studygroup-view/" + studyGroupId;
    }

    // create a new post in a studygroup
    @RequestMapping(value = "/api/post-create", method = RequestMethod.POST)
    public String createPost(@RequestParam("studyGroupId") long studyGroupId, HttpSession httpSession, Post post) {
        User user = (User) httpSession.getAttribute("user");
        StudyGroup studyGroup = studyGroupService.findById(studyGroupId);
        post.setUser(user);
        post.setStudygroup(studyGroup);
        postService.save(post);
        return "redirect:/studygroup-view/" + studyGroupId;
    }
}
