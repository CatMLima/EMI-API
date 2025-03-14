package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Entities.User;

public class PostDTO {
    private long id;
    private long studyGroupId;
    private String userName;
    private String title;
    private String content;

    public PostDTO(long id, long studyGroupId, String userName, String title, String content) {
        this.id = id;
        this.studyGroupId = studyGroupId;
        this.userName = userName;
        this.title = title;
        this.content = content;
    }

    //getters and setters
    public long getId() {return this.id;}
    public long getStudyGroupId() {return this.studyGroupId;}
    public String getUserName() {return this.userName;}
    public String getTitle() {return this.title;}
    public String getContent() {return this.content;}
    public void setId(long id) {this.id = id;}
    public void setStudyGroupId(long studyGroupId) {this.studyGroupId = studyGroupId;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setTitle(String title) {this.title = title;}
    public void setContent(String content) {this.content = content;}

}
