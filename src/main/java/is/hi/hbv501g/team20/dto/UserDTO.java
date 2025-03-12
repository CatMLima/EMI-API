package is.hi.hbv501g.team20.dto;

public class UserDTO {

    private Long id;
    private Integer isActive;
    public Integer privacy;
    private Integer streak;
    private String name;
    private String email;
    private String totalTime;
    private Integer numberActivities;
    private String averageTime;
    private String favoriteLocation;

    public UserDTO(Long id, Integer isActive, Integer privacy, Integer streak, String name, String email,
                   String totalTime, Integer numberActivities, String averageTime, String favoriteLocation) {
        this.id = id;
        this.isActive = isActive;
        this.privacy = privacy;
        this.streak = streak;
        this.name = name;
        this.email = email;
        this.totalTime = totalTime;
        this.numberActivities = numberActivities;
        this.averageTime = averageTime;
        this.favoriteLocation = favoriteLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public Integer getPrivacy() { return privacy; }
    public void setPrivacy(Integer privacy) { this.privacy = privacy; }
    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) { this.totalTime = totalTime; }
    public Integer getNumberActivities() { return numberActivities; }
    public void setNumberActivities(Integer numberActivities) { this.numberActivities = numberActivities; }
    public String getAverageTime() { return averageTime; }
    public void setAverageTime(String averageTime) { this.averageTime = averageTime; }
    public String getFavoriteLocation() { return favoriteLocation; }
    public void setFavoriteLocation(String favoriteLocation) { this.favoriteLocation = favoriteLocation; }

}
