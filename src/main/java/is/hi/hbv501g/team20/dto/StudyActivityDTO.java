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
    private int coffeesCount;
    private boolean hasCoffeed;

    public StudyActivityDTO(Long id, Long userID, byte[] activityPicture, Building building,
                            Location location, Date date, String duration, String title,
                            String description, String userName, String subjectID,
                            String subjectName, int coffeesCount, boolean hasCoffeed) {
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
        this.coffeesCount = coffeesCount;
        this.hasCoffeed = hasCoffeed;
    }

    public StudyActivityDTO(long id, Long id1, byte[] activityPicture, Building building, Location location, Date date, String formattedDuration, String title, String description, String name, String subjectName, String subjectID) {
    }

    public long getId() { return id; }
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
    public int getCoffeesCount() { return coffeesCount; }
    public void setCoffeesCount(int coffeesCount) { this.coffeesCount = coffeesCount; }
    public boolean getHasCoffeed(){ return hasCoffeed; }
    public void setHasCoffeed(boolean value){ hasCoffeed = value; }

}
