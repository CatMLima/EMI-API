package is.hi.hbv501g.team20.Persistence.Entities;

import is.hi.hbv501g.team20.Persistence.Enums.Building;
import jakarta.persistence.*;

@Entity
@Table(name = "\"location\"")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Location(Building building){
        this.building = building;
        this.userCount = 0;
    }

    public Location(){}

    @Enumerated(EnumType.STRING)
    private Building building;

    private int userCount;

    public void setId(Long id){ this.id = id;}
    public Long getId(){ return id; }
    public void setBuilding(Building building){ this.building = building; }
    public Building getBuilding(){ return building; }
    public void setUserCount(int userCount){ this.userCount = userCount; }
    public int getUserCount(){ return userCount; }

}
