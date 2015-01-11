package vp.com.watchrooms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class RoomList {

	@JsonProperty
	private Long count;

	@JsonProperty
	private List<Room> rooms = new ArrayList<Room>();

	public RoomList() {
	}

	public RoomList(Long count, List<Room> rooms) {
		super();
		this.count = count;
		this.rooms = rooms;
	}

	public void addRoom(Room toAdd) {
		this.rooms.add(toAdd);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	@Override
	public String toString() {
		return "RoomList [count=" + count + ", rooms=" + rooms + "]";
	}

}
