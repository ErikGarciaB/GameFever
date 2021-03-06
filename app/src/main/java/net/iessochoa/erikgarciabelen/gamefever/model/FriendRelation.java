package net.iessochoa.erikgarciabelen.gamefever.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendRelation implements Parcelable {
    private User user1;
    private User user2;

    private String id;

    public FriendRelation(){}

    public FriendRelation(User user1, User user2, String id) {
        this.user1 = user1;
        this.user2 = user2;
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected FriendRelation(Parcel in) {
        user1 = (User) in.readValue(User.class.getClassLoader());
        user2 = (User) in.readValue(User.class.getClassLoader());
        id = (String) in.readValue(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(user1);
        dest.writeValue(user2);
        dest.writeValue(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FriendRelation> CREATOR = new Parcelable.Creator<FriendRelation>() {
        @Override
        public FriendRelation createFromParcel(Parcel in) {
            return new FriendRelation(in);
        }

        @Override
        public FriendRelation[] newArray(int size) {
            return new FriendRelation[size];
        }
    };
}
