package is.hi.hbv501g.team20.Persistence.Entities;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;


@Entity
@Table(name = "\"studyactivity\"")
public class StudyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;

    //@Temporal(TemporalType.TIME)
    private LocalTime start; //eða Timer timer?

    //@Temporal(TemporalType.TIME)
    private LocalTime end;

    private String title;
    private String description;
    private String subjectID;
    private String subjectName;

    //not yet u horny bastard! <- THE WHHAT NOW?!

    //@OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Coffee> coffees;
    //private String ImageID; //or Image Image

    public StudyActivity(Date date,
                         LocalTime start,
                         LocalTime end,
                         String title,
                         String description,
                         String subjectID,
                         String subjectName) {
        this.date = date;
        this.start = start;
        this.end = end; //implement differently?
        this.title = title;
        this.description = description;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
    }

    public StudyActivity() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
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
}