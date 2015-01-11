package vp.com.watchrooms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Floor extends BaseModel {

	@JsonProperty
	private String floorId;

	@JsonProperty
	private String buildingId;

	public Floor(String floorId, String buildingId, String name) {
		super();
		this.floorId = floorId;
		this.buildingId = buildingId;
		super.setName(name);
	}

	public Floor() {

	}

	public String getFloorId() {
		return floorId;
	}

	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	@Override
	public String toString() {
		return "Floor [floorId=" + floorId + ", buildingId=" + buildingId + ", name=" + getName() + ", getCreateDate()=" + getCreateDate() + ", getUpdateDate()=" + getUpdateDate() + "]";
	}

}
