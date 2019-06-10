package br.com.arturbc.projetoiesb;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

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

    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        profileUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(profileUrl);
    }
}
