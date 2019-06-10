package br.com.arturbc.projetoiesb;

public class User {

    private  String uid;
    private  String username;
    private  String profileUrl;

    public User(){

    }
    public User(String uid, String username, String profileUrl) {
        this.uid = uid;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }


}
