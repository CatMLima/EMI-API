package is.hi.hbv501g.team20.dto;

public class OngoingResponse {

    private Long id;

    private String title;

    private String subjectId;

    private String subject_name;

    private String start;

    public OngoingResponse(Long id, String title, String subjectId, String subject_name, String start){
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subject_name = subject_name;
        this.start = start;
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

    public String getStart() {return start;}

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

    public void setStart(String start) {this.start = start;}
}