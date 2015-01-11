package vp.com.watchrooms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Subscription {

    @JsonProperty
    private String roomId;

    @JsonProperty
    private String userId;

    public Subscription() {
    }

    public Subscription(String roomId, String userId) {
        super();
        this.roomId = roomId;
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String floorId) {
        this.userId = floorId;
    }

    @Override
    public String toString() {
        return "Subscription [roomId=" + roomId + ", userId=" + userId + "]";
    }

}
