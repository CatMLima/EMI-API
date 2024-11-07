package is.hi.hbv501g.team20.Services.Implementations;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.StudyActivity;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.CoffeeRepository;
import is.hi.hbv501g.team20.Services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoffeeServiceImplementation implements CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepo;

    @Override
    public Coffee giveCoffee(User user, StudyActivity activity) {
        Coffee coffee = new Coffee(user, activity);
        return coffeeRepo.save(coffee);
    }


    @Override
    public void removeCoffee(User user, StudyActivity activity) {
        Optional<Coffee> optionalCoffee = coffeeRepo.findByUserAndActivity(user, activity);
        if (optionalCoffee.isPresent()) {
            coffeeRepo.delete(optionalCoffee.get());
        }
    }

    @Override
    public Coffee findCoffeeByUserAndActivity(User user, StudyActivity activity) {
        Optional<Coffee> coffee = coffeeRepo.findByUserAndActivity(user, activity);
        return coffee.orElse(null); // Return the Coffee if present, otherwise null
    }
    public long countCoffeesForActivity(StudyActivity activity) {
        return coffeeRepo.countByActivity(activity);
    }

}
