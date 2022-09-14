package com.gpsreminder.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class VenueBusynessRaw {

	private String infoId;
	private Integer weekday;
	
	private List<Integer> busyness;

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public int getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	public List<Integer> getBusyness() {
		return busyness;
	}

	public void setBusyness(List<Integer> busyness) {
		this.busyness = busyness;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("infoId", this.getInfoId());
		obj.put("weekday", this.getWeekday());
		
		JSONArray busynesses = new JSONArray();
		for(Integer busyness: this.getBusyness()) {
			busynesses.put(busyness);
		}
		
		obj.put("busyness", busynesses);
		
		return obj;
	}

	@Override
	public String toString() {
		return "VenueBusynessRaw [infoId=" + infoId + ", weekday=" + weekday + ", busyness=" + busyness + "]";
	}
	
	
}
