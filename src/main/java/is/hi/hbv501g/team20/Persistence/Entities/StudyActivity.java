package is.hi.hbv501g.team20.Persistence.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import is.hi.hbv501g.team20.Persistence.Enums.Building;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;


@Entity
@Table(name = "\"studyactivity\"")
public class StudyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coffee> coffees = new ArrayList<>();

    @Column(name = "activity_picture")
    private byte[] activityPicture;

    @Enumerated(EnumType.STRING)
    private Building building;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Temporal(TemporalType.TIME)
    private LocalTime start; //eða Timer timer?

    @Temporal(TemporalType.TIME)
    private LocalTime end_time;

    @Temporal(TemporalType.TIME)
    private Duration duration;

    private Integer isActive;
    private Integer privacy;
    private String title;
    private String description;
    private String subjectID;
    private String subjectName;

    public StudyActivity(Date date,
                         LocalTime start,
                         LocalTime end,
                         String title,
                         String description,
                         String subjectID,
                         String subjectName) {
        this.date = date;
        this.start = start;
        this.title = title;
        this.description = description;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.privacy = 0;
        this.location = null;
    }

    public StudyActivity() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer userPrivacy() {
        return user.getPrivacy();
    }

    public long getId() {
        return id;
    }

    public void setId(long ID) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalTime end) {
        this.end_time = end;
    }

    public List<Coffee> getCoffees() {
        return coffees;
    }

    public void setCoffees(List<Coffee> coffees) {
        this.coffees = coffees;
    }

    public void removeCoffee(Coffee coffee) {
        this.coffees.remove(coffee);
    }
    public Duration getDuration() {
        if (this.isActive == 0 ) {
            Duration durationTest = Duration.between(start, LocalTime.now());;
            if (durationTest.isNegative()){
                // Ensures that the time past is not negative in case of different dates
                return durationTest.plusHours(24);
            } else {
                return durationTest;
            }
        } else{
            return duration;
        }
    }

    public void setDuration(LocalTime start, LocalTime end) {
        Duration durationTest = Duration.between(start, Objects.requireNonNullElseGet(end, LocalTime::now));;
        if (durationTest.isNegative()){
            // Ensures that the time past is not negative in case of different dates
            this.duration = durationTest.plusHours(24);
        } else {
            this.duration = durationTest;
        }
    }

    public String getFormattedDuration() {
        if (start != null && end_time == null) {
            Duration currentDuration = getDuration();
            return formatDuration(currentDuration);
        } else if (start != null && duration != null) {
            return formatDuration(duration);
        }
        return "00:00:00";
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = (duration.toMinutes() % 60);
        long seconds = (duration.getSeconds() % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setActivityPicture(byte[] activityPicture) {
        this.activityPicture = activityPicture;
    }

    public byte[] getActivityPicture() {
        return activityPicture;
    }

    private Integer getPrivacy() {
        return privacy;
    }

    public void setPrivacy(User user) {
        this.privacy = userPrivacy();
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "StudyActivity{" +
                "id=" + id +
                "userID=" + user.getId()+
                ", coffees=" + coffees +
                ", activityPicture=" + Arrays.toString(activityPicture) +
                ", building=" + building +
                ", location=" + location +
                ", date=" + date +
                ", start=" + start +
                ", end_time=" + end_time +
                ", duration=" + duration +
                ", isActive=" + isActive +
                ", privacy=" + privacy +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subjectID='" + subjectID + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
