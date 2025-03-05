package is.hi.hbv501g.team20.dto;

import java.time.Duration;

public class OngoingResponse {

    private Long id;

    private String title;

    private String subjectId;

    private String subject_name;

    private Duration duration;

    public OngoingResponse(Long id, String title, String subjectId, String subject_name, Duration duration){
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subject_name = subject_name;
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

    public String getSubject_name() {
        return subject_name;
    }

    public Duration getDuration() {return duration;}

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public void setDuration(Duration duration) {this.duration = duration;}
}