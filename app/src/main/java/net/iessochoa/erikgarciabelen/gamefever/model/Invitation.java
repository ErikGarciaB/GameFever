package net.iessochoa.erikgarciabelen.gamefever.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Invitation implements Parcelable{
    private User hostUser;
    private User invitedUser;

    public Invitation(){}

    public Invitation(User hostUser, User invitedUser) {
        this.hostUser = hostUser;
        this.invitedUser = invitedUser;
    }

    public User getHostUser() {
        return hostUser;
    }

    public void setHostUser(User hostUser) {
        this.hostUser = hostUser;
    }

    public User getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }


    protected Invitation(Parcel in) {
        hostUser = (User) in.readValue(User.class.getClassLoader());
        invitedUser = (User) in.readValue(User.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(hostUser);
        dest.writeValue(invitedUser);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Invitation> CREATOR = new Parcelable.Creator<Invitation>() {
        @Override
        public Invitation createFromParcel(Parcel in) {
            return new Invitation(in);
        }

        @Override
        public Invitation[] newArray(int size) {
            return new Invitation[size];
        }
    };

}
