package is.hi.hbv501g.team20.dto;

import java.time.Duration;

public class OngoingResponse {

    private Long id;

    private String title;

    private String subjectId;

    private String subjectName;

    private String duration;

    public OngoingResponse(Long id, String title, String subjectId, String subjectName, String duration){
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getSubjectId(){
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDuration() {return duration;}

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subject_name) {
        this.subjectName = subjectName;
    }

    public void setDuration(String duration) {this.duration = duration;}
}