package is.hi.hbv501g.team20.Persistence.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name= "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] profilePicture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyActivity> activities;

    @ManyToMany(mappedBy = "members")
    private List<StudyGroup> studyGroupsMember = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    private List<StudyGroup> studyGroupsAdmin = new ArrayList<>();

    private Integer isActive;
    public Integer privacy;
    private Integer streak;
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.privacy = 0;
        this.lastActivityDate = null;
        this.streak = 0;
    }


    public User() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<StudyGroup> getStudyGroupsMember() {
        return studyGroupsMember;
    }

    public List<StudyActivity> getActivities() {
        return activities;
    }

    public Integer getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Integer privacy) {
        this.privacy = privacy;
    }

    public void changePrivacy(Integer privacy) {
        this.privacy = privacy;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getStreak() { return streak; }

    public void setStreak(Integer streak) { this.streak = streak; }

    private LocalDate lastActivityDate;

    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
