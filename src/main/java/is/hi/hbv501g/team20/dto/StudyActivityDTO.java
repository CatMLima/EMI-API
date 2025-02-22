package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Entities.User;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyActivityDTO {
    private Long id;
    private Long userID;
    private List<Coffee> coffees = new ArrayList<>();
    private byte[] activityPicture;
    private Building building;
    private Location location;
    private Date date;
    private Duration duration;
    private String title;
    private String description;
    private String subjectID;
    private String subjectName;

    public StudyActivityDTO(Long id, Long userID, List<Coffee> coffees,
                            byte[] activityPicture, Building building, Location location,
                            Date date, Duration duration,  String title,
                            String description, String subjectID, String subjectName) {
        this.id = id;
        this.userID = userID;
        this.coffees = coffees;
        this.activityPicture = activityPicture;
        this.building = building;
        this.location = location;
        this.date = date;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
    }
}
