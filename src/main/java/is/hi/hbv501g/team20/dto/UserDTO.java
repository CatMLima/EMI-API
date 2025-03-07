package is.hi.hbv501g.team20.dto;

public class UserDTO {

    private Long id;
    private Integer isActive;
    public Integer privacy;
    private Integer streak;
    private String name;
    private String email;

    public UserDTO(Long id, Integer isActive, Integer privacy, Integer streak, String name, String email) {
        this.id = id;
        this.isActive = isActive;
        this.privacy = privacy;
        this.streak = streak;
        this.name = name;
        this.email = email;
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

}
