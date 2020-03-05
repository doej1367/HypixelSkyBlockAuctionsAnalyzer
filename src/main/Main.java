package main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import org.json.*;

public class Main {

	public static String api_key = "";
	public static String ign = "";
	public static int max_pages = 1;
	public static ArrayList<Player> players;
	public static MainWindow mw;
	private static Profile currentProfile;
	private static Long kills_start = 0L;
	private static Long last_updated = 0L;
	private static Thread t1 = new Thread() {
		@Override
		public void run() {
			while (true) {
				mw.getLblLabel_lastUpdate().setText("" + (System.currentTimeMillis() - (long) last_updated) / 1000);
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
			}
		};
	};

	public static void main(String[] args) {
		try {
			File config = new File("config.txt");
			if (!config.exists())
				config.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(config));
			api_key = "" + br.readLine();
			ign = br.readLine();
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mw = new MainWindow();
					mw.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		players = new ArrayList<>();
	}

	public static void loadProfiles() {
		consoleOut("loading profiles ");
		URL url;
		String out = "";
		String player_name = mw.getTextField_IGN().getText();
		try {
			url = new URL("https://api.hypixel.net/player?key=" + api_key + "&name=" + player_name);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			out = getContent(con);
		} catch (MalformedURLException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		} catch (IOException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		}
		boolean success = false;
		try {
			JSONObject obj = new JSONObject(out);
			String uuid = obj.getJSONObject("player").getString("uuid");
			Player p = new Player(player_name, uuid);
			if (!players.contains(p)) {
				players.add(p);
				JSONObject player_profiles = obj.getJSONObject("player").getJSONObject("stats")
						.getJSONObject("SkyBlock").getJSONObject("profiles");
				mw.getComboBox_profile().removeAll();
				for (String profile_id : player_profiles.keySet()) {
					JSONObject profile = player_profiles.getJSONObject(profile_id);
					String id = profile.getString("profile_id");
					String name = profile.getString("cute_name");
					if (!p.contains(new Profile(id, name))) {
						p.addProfile(new Profile(id, name));
						mw.getComboBox_profile().addItem(name);
					}
				}
			}
			success = true;
		} catch (JSONException e) {
			e.printStackTrace();
			consoleOut(" [ FAILURE ] JSONException\n");
		}
		if (success) {
			setCurrentProfile();
			consoleOut(" [ OK ]\n");
		}
		updateConsoleOut();
	}

	public static void setCurrentProfile() {
		String name = (String) mw.getComboBox_profile().getSelectedItem();
		Player currentPlayer = null;
		for (Player p : players)
			if (p.getIgn().equalsIgnoreCase(mw.getTextField_IGN().getText()))
				currentPlayer = p;
		for (Profile p : currentPlayer.getProfiles())
			if (p.getCute_name().equalsIgnoreCase(name))
				currentProfile = p;
	}

	public static void sendRequest() {
		consoleOut("loading kill count ");

		loadZealotKillCount();
		if (!t1.isAlive())
			t1.start();

		consoleOut(" [ OK ]\n");
		updateConsoleOut();
	}

	private static void consoleOut(String s) {
		mw.getConsoleOut().append(s);
	}

	private static void updateConsoleOut() {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				mw.getSp().getVerticalScrollBar().setValue(mw.getSp().getVerticalScrollBar().getMaximum());
				mw.getSp().paint(mw.getSp().getGraphics());
				// mw.getBtnFilterButton().setEnabled(true);
			};
		};
		t.start();
	}

	private static void loadZealotKillCount() {
		URL url;
		String out = "";
		if (currentProfile == null) {
			consoleOut("[ FAILURE ] no profile selected!\n");
			return;
		}
		try {
			url = new URL(
					"https://api.hypixel.net/skyblock/profile?key=" + api_key + "&profile=" + currentProfile.getUuid());
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			out = getContent(con);
		} catch (MalformedURLException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		} catch (IOException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		}
		try {
			JSONObject obj = new JSONObject(out);
			JSONObject player_profile = obj.getJSONObject("profile").getJSONObject("members")
					.getJSONObject(currentProfile.getUuid());
			long last_save = player_profile.getLong("last_save");
			long kills_zealot_enderman = player_profile.getJSONObject("stats").getLong("kills_zealot_enderman");
			currentProfile.addZealotKillCount(last_save, kills_zealot_enderman);
			last_updated = last_save;
			mw.getLblNewLabel_killCount().setText("" + (kills_zealot_enderman - (long) kills_start));
			consoleOut("Timestamp: " + last_save + ", Kills:" + kills_zealot_enderman + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static String getContent(HttpsURLConnection con) {
		if (con != null) {
			try {
				String res = "";
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				while ((input = br.readLine()) != null) {
					sb.append(input);
				}
				res = sb.toString();
				br.close();
				return res;
			} catch (IOException e) {
				consoleOut("[ FAILURE ] Some internet connection problem!\n");
				e.printStackTrace();
			}
		}
		return "";
	}

	public static void setStart() {
		Set<Entry<Long, Long>> set = currentProfile.getZealotKillCounts().entrySet();
		if (set.size() > 0) {
			@SuppressWarnings("unchecked")
			Entry<Long, Long> lastValue = (Entry<Long, Long>) set.toArray()[set.size() - 1];
			kills_start = (long) lastValue.getValue();
			last_updated = (long) lastValue.getKey();
		}

	}
}
