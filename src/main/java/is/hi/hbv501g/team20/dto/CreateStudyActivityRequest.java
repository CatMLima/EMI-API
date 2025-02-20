package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Enums.Building;

public class CreateStudyActivityRequest {
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    private String title;
    private String description;
    private String subjectid;
    private String subject_name;
    private Building building;

    public CreateStudyActivityRequest(){}

    public CreateStudyActivityRequest(String title, String description, String subjectID, String subjectName, Building building){
        this.title = title;
        this.description = description;
        this.subjectid = subjectID;
        this.subject_name = subjectName;
        this.building = building;
    }

}
