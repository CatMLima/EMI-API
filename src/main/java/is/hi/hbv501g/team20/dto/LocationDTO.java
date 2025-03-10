package is.hi.hbv501g.team20.dto;

import is.hi.hbv501g.team20.Persistence.Enums.Building;

public class LocationDTO {

    private Building building;
    private int userCount;

    public LocationDTO(Building building, int userCount) {
        this.building = building;
        this.userCount = userCount;
    }

    public Building getBuilding() {return building;}
    public int getUserCount() {return userCount;}
}
