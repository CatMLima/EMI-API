package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    @Autowired
    private UserService userService;

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)){
            return null;
        }

        String username = ((UserDetails) auth.getPrincipal()).getUsername();

        return userService.findByEmail(username);
    }
}
