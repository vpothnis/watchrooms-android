package vp.com.watchrooms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonSerialize
public class User extends BaseModel {

	@JsonProperty
	private String userId;

	@JsonProperty()
	private String emailId;

	@JsonProperty()
	private Boolean isFacility;

	@JsonProperty
	private List<Device> devices = new ArrayList<Device>();

	public User() {

	}

    public User(String emailId, String name, Boolean isFacility) {
        super();
        this.emailId = emailId;
        this.isFacility = isFacility;
        super.setName(name);
    }

	public User(String userId, String emailId, String name, Boolean isFacility) {
		super();
		this.userId = userId;
		this.emailId = emailId;
		this.isFacility = isFacility;
		super.setName(name);
	}

	public void addDevice(Device toAdd) {
		this.devices.add(toAdd);
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Boolean getIsFacility() {
		return isFacility;
	}

	public void setIsFacility(Boolean isFacility) {
		this.isFacility = isFacility;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", emailId=" + emailId + ", isFacility=" + isFacility + ", devices=" + devices + ", getName()=" + getName() + ", getCreateDate()=" + getCreateDate()
		        + ", getUpdateDate()=" + getUpdateDate() + "]";
	}

	/**
	 * remove the given device from the collection
	 * 
	 * @param inputDevice
	 */
	public void removeDevice(Device inputDevice) {
		for (Iterator<Device> iterator = devices.iterator(); iterator.hasNext();) {
			Device device = (Device) iterator.next();
			if (device.getDeviceId().equals(inputDevice.getDeviceId()) && device.getPlatform().equals(inputDevice.getPlatform())) {
				iterator.remove();
			}
		}
	}

}
