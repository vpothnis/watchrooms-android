package vp.com.watchrooms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SubscriptionList {

	@JsonProperty
	private Long count;

	@JsonProperty
	private List<Subscription> subscriptions = new ArrayList<Subscription>();

	public SubscriptionList() {
	}

	public SubscriptionList(Long count, List<Subscription> rooms) {
		super();
		this.count = count;
		this.subscriptions = rooms;
	}

	public void addSubscription(Subscription toAdd) {
		this.subscriptions.add(toAdd);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> rooms) {
		this.subscriptions = rooms;
	}

	@Override
	public String toString() {
		return "SubscriptionList [count=" + count + ", rooms=" + subscriptions + "]";
	}

}
