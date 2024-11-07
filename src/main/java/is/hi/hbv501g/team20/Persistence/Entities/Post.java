package is.hi.hbv501g.team20.Persistence.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "\"post\"")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudyGroup studygroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String title;
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post() {}

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public StudyGroup getStudygroup() { return studygroup; }

    public void setStudygroup(StudyGroup studygroup) { this.studygroup = studygroup; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

}
