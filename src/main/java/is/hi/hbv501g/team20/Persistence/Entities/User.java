package is.hi.hbv501g.team20.Persistence.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name= "\"user\"")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="profile_picture")
    private byte[] profilePicture;

    //@JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyActivity> activities = new ArrayList<>();

    //@JsonIgnore
    @ManyToMany(mappedBy = "members")
    private List<StudyGroup> studyGroupsMember = new ArrayList<>();

    //@JsonManagedReference
    @OneToMany(mappedBy = "admin")
    private List<StudyGroup> studyGroupsAdmin = new ArrayList<>();

    //@JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    //@JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Coffee> coffees = new ArrayList<>();

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

    public List<StudyGroup> getStudyGroupsAdmin() {
        return studyGroupsAdmin;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<StudyActivity> getActivities() {
        return activities;
    }

    public List<Coffee> getCoffees() {
        return coffees;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", profilePicture=" + Arrays.toString(profilePicture) +
                ", activities=" + activities +
                ", studyGroupsMember=" + studyGroupsMember +
                ", studyGroupsAdmin=" + studyGroupsAdmin +
                ", posts=" + posts +
                ", coffees=" + coffees +
                ", isActive=" + isActive +
                ", privacy=" + privacy +
                ", streak=" + streak +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
