package is.hi.hbv501g.team20.dto;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(){
    }

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password= password;
    }
}
