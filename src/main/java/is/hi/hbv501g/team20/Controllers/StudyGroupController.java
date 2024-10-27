package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.StudyGroup;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import is.hi.hbv501g.team20.Persistence.Repository.StudyGroupRepository;
import is.hi.hbv501g.team20.Services.LoginService;
import is.hi.hbv501g.team20.Services.StudyGroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Controller
public class StudyGroupController {

    private StudyGroupService studyGroupService;
    private LoginService loginService;

    public StudyGroupController(StudyGroupService studyGroupService, LoginService loginService) {
        this.studyGroupService = studyGroupService;
        this.loginService = loginService;
    }

    // Displays a page containing a list of the user's studyactivities
    @RequestMapping(value="/studygroups-feed", method= RequestMethod.GET)
    public String getStudyGroupfeed(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<StudyGroup> userStudyGroups = studyGroupService.findAll();
            model.addAttribute("studygroup", userStudyGroups);
        }
        return "studygroups-feed";
    }

    // Displays the Create a studyactivity page
    @RequestMapping(value = "/studygroup-create", method = RequestMethod.GET)
    public String createStudyGroupGet(Model model) {
        model.addAttribute("studygroup", new StudyGroup());
        return "studygroup-create";
    }

    @RequestMapping(value = "/api/studygroup-create", method = RequestMethod.POST)
    public String createStudyGroup(HttpSession httpSession, StudyGroup studyGroup, BindingResult result, Model model){

        User admin = (User) httpSession.getAttribute("user");
        studyGroup.setAdmin(admin);
        studyGroup.addMember(admin);

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

        if (!studyGroup.getMembers().contains(user)) {
            studyGroup.addMember(user);
            studyGroup.addMemberCount();
            studyGroupService.save(studyGroup);
        }
        return "redirect:/studygroups-feed";
    }
}
