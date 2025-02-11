package is.hi.hbv501g.team20.Controllers.Rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class HomeRestController {

    @GetMapping("/home")
    public String homeRestController(HttpServletRequest request) {
        return "Home endpoint." + request.getSession().getId();
    }

}
