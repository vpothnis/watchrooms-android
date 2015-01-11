package vp.com.watchrooms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class BuildingList {

	@JsonProperty
	private Long count;

	@JsonProperty
	private List<Building> buildings = new ArrayList<Building>();

	public BuildingList() {
	}

	public BuildingList(Long count, List<Building> buildings) {
		super();
		this.count = count;
		this.buildings = buildings;
	}

	public void addBuilding(Building toAdd) {
		this.buildings.add(toAdd);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

	@Override
	public String toString() {
		return "BuildingList [count=" + count + ", buildings=" + buildings + "]";
	}

}
