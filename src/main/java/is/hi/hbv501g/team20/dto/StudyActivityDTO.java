package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Entities.Coffee;
import is.hi.hbv501g.team20.Persistence.Entities.Location;
import is.hi.hbv501g.team20.Persistence.Enums.Building;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyActivityDTO {
    private Long id;
    private Long userID;
    private byte[] activityPicture;
    private Building building;
    private Location location;
    private Date date;
    private String duration;
    private String title;
    private String description;
    private String userName;
    private String subjectID;
    private String subjectName;

    public StudyActivityDTO(Long id, Long userID, byte[] activityPicture, Building building,
                            Location location, Date date, String duration,  String title,
                            String description, String userName, String subjectID,
                            String subjectName) {
        this.id = id;
        this.userID = userID;
        this.activityPicture = activityPicture;
        this.building = building;
        this.location = location;
        this.date = date;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.userName = userName;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserID() { return userID; }
    public void setUserID(Long userID) { this.userID = userID; }
    public byte[] getActivityPicture() { return activityPicture; }
    public void setActivityPicture(byte[] activityPicture) { this.activityPicture = activityPicture; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) {this.userName = userName;}
    public String getSubjectID() { return subjectID; }
    public void setSubjectID(String subjectID) { this.subjectID = subjectID; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

}
