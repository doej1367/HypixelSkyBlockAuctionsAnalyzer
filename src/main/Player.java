package main;

import java.util.ArrayList;

public class Player {
	private String ign;
	private String uuid;
	private ArrayList<Profile> profiles = new ArrayList<>();

	public Player(String ign, String uuid) {
		super();
		this.ign = ign;
		this.uuid = uuid;
	}

	public String getIgn() {
		return ign;
	}

	public void setIgn(String ign) {
		this.ign = ign;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ArrayList<Profile> getProfiles() {
		return profiles;
	}

	public void addProfile(Profile profile) {
		this.profiles.add(profile);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player))
			return false;
		return getUuid().equalsIgnoreCase(((Player) o).getUuid());
	}

	public boolean contains(Profile p) {
		return getProfiles().contains(p);
	}
}
