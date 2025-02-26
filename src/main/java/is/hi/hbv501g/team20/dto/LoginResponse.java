package is.hi.hbv501g.team20.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private Long ongoingId;
    private Integer isActive;


    public LoginResponse(String token, Long userId, Long ongoingId, Integer isActive) {
        this.token = token;
        this.userId = userId;
        this.ongoingId = ongoingId;
        this.isActive = isActive;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOngoingId() {
        return ongoingId;
    }

    public Integer getIsActive() {return isActive;}
}
