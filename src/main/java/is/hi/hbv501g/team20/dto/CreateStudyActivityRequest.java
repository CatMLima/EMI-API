package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Enums.Building;

public class CreateStudyActivityRequest {
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    private String title;
    private String description;
    private String subjectID;
    private String subjectName;
    private Building building;

    public CreateStudyActivityRequest(){}

    public CreateStudyActivityRequest(String title, String description, String subjectID, String subjectName, Building building){
        this.title = title;
        this.description = description;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.building = building;
    }

}
