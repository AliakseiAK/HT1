package app.entities;

public class PhoneNumber {
    private int id;
    private int ownerId;
    private String phoneNumber;

    public PhoneNumber(int id, int ownerId, String phoneNumber) {
        this.id = id;
        this.ownerId = ownerId;
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
