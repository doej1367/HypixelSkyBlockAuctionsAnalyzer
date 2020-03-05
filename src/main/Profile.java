package main;

import java.util.HashMap;

public class Profile {
	private String uuid;
	private String cute_name;
	private HashMap<Long, Long> zealotKillCounts = new HashMap<>();

	public Profile(String uuid, String cute_name) {
		this.uuid = uuid;
		this.cute_name = cute_name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCute_name() {
		return cute_name;
	}

	public void setCute_name(String cute_name) {
		this.cute_name = cute_name;
	}

	public HashMap<Long, Long> getZealotKillCounts() {
		return zealotKillCounts;
	}

	public void addZealotKillCount(Long timestamp, Long zealotKillCount) {
		zealotKillCounts.put(timestamp, zealotKillCount);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Profile))
			return false;
		return getUuid().equalsIgnoreCase(((Profile) o).getUuid());
	}
}
