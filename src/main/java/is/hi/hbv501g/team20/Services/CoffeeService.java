package is.hi.hbv501g.team20.Services;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;

public interface CoffeeService {
    Coffee giveCoffee(User user, StudyActivity activity);
    Coffee findCoffeeByUserAndActivity(User user, StudyActivity activity);
    void removeCoffee(User user, StudyActivity activity);
    long countCoffeesForActivity(StudyActivity activity);
}
