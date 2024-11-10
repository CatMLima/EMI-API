package is.hi.hbv501g.team20.Persistence.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"studygroup\"")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private String subjectId;
    private int lookingForMembers;
    private int memberCount;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToMany
    @JoinTable(
            name = "studygroup_members",
            joinColumns = @JoinColumn(name = "studygroup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "studygroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public StudyGroup(String name, String description, String subjectId, int lookingForMembers) {
        this.name = name;
        this.description = description;
        this.subjectId = subjectId;
        this.lookingForMembers = lookingForMembers;
        this.memberCount = 1;
    }

    public StudyGroup() {}

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getSubjectId() {return subjectId;}

    public void setSubjectId(String subjectId) {this.subjectId = subjectId;}

    public int getLookingForMembers() {return lookingForMembers;}

    public void setLookingForMembers(int lookingForMembers) {this.lookingForMembers = lookingForMembers;}

    public int getMemberCount() {return memberCount;}

    public void addMemberCount() {this.memberCount++;}

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public User getAdmin() {
        return admin;
    }

    public List<User> getMembers() {return members;}

}
