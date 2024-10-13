package is.hi.hbv501g.team20.Controllers;

import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Services.LoginService;
import is.hi.hbv501g.team20.Services.StudyActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Controller
public class StudyActivityController {
    private StudyActivityService studyActivityService;
    private LoginService loginService;

    @Autowired
    public StudyActivityController(StudyActivityService studyActivityService, LoginService loginService) {
        this.studyActivityService = studyActivityService;
        this.loginService = loginService;
    }

    @RequestMapping(value = "/studyactivity-create", method = RequestMethod.GET)
    public String createStudyActivityGet(Model model) {
        model.addAttribute("studyactivity", new StudyActivity());
        return "studyactivity-create";
    }

    @RequestMapping(value = "/api/studyactivity-create", method = RequestMethod.POST)
    public String createStudyActivity(HttpSession httpSession, StudyActivity studyActivity, BindingResult result, Model model){

        User user = (User) httpSession.getAttribute("user");
        studyActivity.setUser(user);
        studyActivity.setDate(new Date());
        studyActivity.setStart(LocalTime.now());
        studyActivity.setEnd(LocalTime.now().plusHours(1));

        if(result.hasErrors()){
            return "studyactivity-create";
        }
        studyActivityService.save(studyActivity);
        return "redirect:/feed";
    }

    @RequestMapping(value="/studyactivity-details/{ID}", method= RequestMethod.GET)
    public String getStudyActivityDetailsPage(@PathVariable("ID") long ID, Model model){
        model.addAttribute("studyactivity", studyActivityService.findById(ID));
        return "studyactivity-details";
    }

    // Feed page stuff is here below
    // Displays feed page
    @RequestMapping("/feed")
    public String showFeed(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<StudyActivity> allStudyActivities = studyActivityService.findAll();
        model.addAttribute("studyactivity", allStudyActivities);
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "feed";
    }

    // Displays user profile page
    @RequestMapping("/profile")
    public String showProfile() {
        return "user";
    }

    // Create a study activity
    @RequestMapping("/studyactivity-create")
    public String startStudyActivity() {
        return "studyactivity-create";
    }

    //End of feed page stuff
}
