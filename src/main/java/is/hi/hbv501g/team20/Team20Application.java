package is.hi.hbv501g.team20;

import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "is.hi.hbv501g.team20.Persistence.Repository")
public class Team20Application {

    public static void main(String[] args) {
        SpringApplication.run(Team20Application.class, args);
    }

    /*
    @Bean
    public CommandLineRunner run(UserRepository repository){
        return args -> {
            insertJavaAdvocates(repository);
            System.out.println(repository.findAll());
        };
    }
    **/

}
