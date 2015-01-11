package vp.com.watchrooms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class FloorList {

	@JsonProperty
	private Long count;

	@JsonProperty
	private List<Floor> floors = new ArrayList<Floor>();

	public FloorList() {
	}

	public FloorList(Long count, List<Floor> floors) {
		super();
		this.count = count;
		this.floors = floors;
	}

	public void addFloor(Floor toAdd) {
		this.floors.add(toAdd);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Floor> getFloors() {
		return floors;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}

	@Override
	public String toString() {
		return "FloorList [count=" + count + ", floors=" + floors + "]";
	}

}
