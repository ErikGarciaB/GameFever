package net.iessochoa.erikgarciabelen.gamefever.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class User implements Parcelable {
    private String Name;
    private Timestamp LastTimeConnected;

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
    }

    public User(){}

    public Timestamp getLastTimeConnected() {
        return LastTimeConnected;
    }

    public void setLastTimeConnected(Timestamp lastTimeConnected) {
        this.LastTimeConnected = lastTimeConnected;
    }

    protected User(Parcel in) {
        Name = in.readString();
        LastTimeConnected = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeValue(LastTimeConnected);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
