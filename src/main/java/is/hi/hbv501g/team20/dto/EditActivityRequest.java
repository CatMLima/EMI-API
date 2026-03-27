package is.hi.hbv501g.team20.dto;

public class EditActivityRequest {
    private String title;
    private String description;
    private String subjectId;
    private String subjectName;

    public EditActivityRequest() {}

    public EditActivityRequest(String title, String description, String subjectId, String subjectName) {
        this.title = title;
        this.description = description;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
