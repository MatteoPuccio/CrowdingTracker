package com.gpsreminder.model;

public class VenueBusynessLive {

	private VenueInformation venueInfo;
	private Double forecastedBusyness, liveBusyness;

	public VenueInformation getVenueInfo() {
		return venueInfo;
	}

	public void setVenueInfo(VenueInformation venueInfo) {
		this.venueInfo = venueInfo;
	}

	public Double getForecastedBusyness() {
		return forecastedBusyness;
	}

	public void setForecastedBusyness(Double forecastedBusyness) {
		this.forecastedBusyness = forecastedBusyness;
	}

	public Double getLiveBusyness() {
		return liveBusyness;
	}

	public void setLiveBusyness(Double liveBusyness) {
		this.liveBusyness = liveBusyness;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VenueBusynessLive other = (VenueBusynessLive) obj;
		if (forecastedBusyness == null) {
			if (other.forecastedBusyness != null)
				return false;
		} else if (!forecastedBusyness.equals(other.forecastedBusyness))
			return false;
		if (liveBusyness == null) {
			if (other.liveBusyness != null)
				return false;
		} else if (!liveBusyness.equals(other.liveBusyness))
			return false;
		if (venueInfo == null) {
			if (other.venueInfo != null)
				return false;
		} else if (!venueInfo.equals(other.venueInfo))
			return false;
		return true;
	}

}
