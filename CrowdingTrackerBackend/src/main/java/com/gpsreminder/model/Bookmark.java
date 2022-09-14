package com.gpsreminder.model;

import org.json.JSONObject;

public class Bookmark {
	private long id;
	private String name;
	private User user;
	private VenueInformation info;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bookmark other = (Bookmark) obj;
		if (id != other.id)
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bookmark [id=" + id + ", name=" + name + ", user=" + user + ", info=" + info + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("id", this.getId());
		obj.put("userId", this.getUser().getId());
		obj.put("name", this.getName());
		obj.put("latitude", this.getInfo().getLatitude());
		obj.put("longitude", this.getInfo().getLongitude());
		obj.put("address", this.getInfo().getAddress());
		return obj;
	}

	public VenueInformation getInfo() {
		return info;
	}

	public void setInfo(VenueInformation info) {
		this.info = info;
	}


}
