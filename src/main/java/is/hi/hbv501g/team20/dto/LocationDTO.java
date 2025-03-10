package is.hi.hbv501g.team20.dto;

public class LocationDTO {

    private String building;
    private int userCount;

    public LocationDTO(String building, int userCount) {
        this.building = building;
        this.userCount = userCount;
    }

    public String getBuilding() {return building;}
    public int getUserCount() {return userCount;}
}
