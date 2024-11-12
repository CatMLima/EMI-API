package is.hi.hbv501g.team20.Controllers.Rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeRestController {

    @GetMapping("/rest/home")
    public String homeRestController() {
        return "Home endpoint.";
    }

}
