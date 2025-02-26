package is.hi.hbv501g.team20.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private Integer isActive;
    public Long ongoingId;

    public LoginResponse(String token, Long userId, Integer isActive, Long ongoingId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public Long getOngoingId() {
        return ongoingId;
    }
}
