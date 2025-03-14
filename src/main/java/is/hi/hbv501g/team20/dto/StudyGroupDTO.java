package is.hi.hbv501g.team20.dto;


public class StudyGroupDTO {
    private long id;

    private String name;
    private String description;
    private String subjectId;
    private int lookingForMembers;
    private int memberCount;
    private int hasUserJoined;

    public StudyGroupDTO(Long id, String name, String description, String subjectId,
                         int lookingForMembers, int memberCount, int hasUserJoined) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subjectId = subjectId;
        this.lookingForMembers = lookingForMembers;
        this.memberCount = memberCount;
        this.hasUserJoined = hasUserJoined;
    }

    public StudyGroupDTO(Long id, String name, String description, String subjectId, int lookingForMembers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subjectId = subjectId;
        this.lookingForMembers = lookingForMembers;
        this.memberCount = 1;
        this.hasUserJoined = 1;
    }

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

    public void setMemberCount(int memberCount) {this.memberCount = memberCount;}

    public int getHasUserJoined() { return hasUserJoined; }

    public void setHasUserJoined(int hasUserJoined) {this.hasUserJoined = hasUserJoined;}
}
