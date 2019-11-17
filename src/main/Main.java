package main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import org.json.*;

public class Main {

	public static String api_key = "";
	public static int max_pages = 1;
	public static ArrayList<Auction> data;
	public static MainWindow mw;

	public static void main(String[] args) {
		try {
			File config = new File("config.txt");
			if (!config.exists())
				config.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(config));
			api_key = "" + br.readLine();
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
		data = new ArrayList<Auction>();
	}

	public static void sendRequest(boolean filterCT, String CT, boolean matchCase, boolean filterSL, int SL,
			boolean filterTT, int TT, boolean filterHB, int HB) {
		consoleOut("loading pages ");
		if (!mw.getChckbxKeepOldData().isSelected())
			data = new ArrayList<Auction>();
		mw.getProgressBar().setStringPainted(true);
		mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
		for (int i = 0; i < max_pages; i++) {
			loadPage(i);
			mw.getProgressBar().setMaximum(max_pages);
			mw.getProgressBar().setValue(i + 1);
			mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
		}
		mw.getProgressBar().setValue(0);
		mw.getProgressBar().setStringPainted(false);
		mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
		consoleOut(" [ OK ]\n");
		updateConsoleOut();
	}

	public static void filterData(boolean filterCT, String CT, boolean matchCase, boolean filterSL, int SL,
			boolean filterTT, int TT, boolean filterHB, int HB) {
		//mw.getBtnFilterButton().setEnabled(false);
		if (data.isEmpty()) {
			consoleOut(" [ FAILURE ] No data collected yet!\n");
			//mw.getBtnFilterButton().setEnabled(true);
			return;
		}
		consoleOut("Filtering collected data ...\n");
		Comparator<Auction> comp = mw.getRdbtnSecondsleftdec().isSelected() ? new CompSecondsLeftDec()
				: mw.getRdbtnHighestbidasc().isSelected() ? new CompHighestBidAsc() : null;
		if (comp != null)
			filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB).sorted(comp)
					.collect(Collectors.toCollection(ArrayList::new)).forEach(a -> consoleOut(a + "\n"));
		else
			filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB)
					.collect(Collectors.toCollection(ArrayList::new)).forEach(a -> consoleOut(a + "\n"));
		long count = filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB)
				.count();
		long sum = filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB)
				.mapToLong(a -> a.getHighest_bid_amount() / a.getItem_count()).sum();
		if (count > 0) {
			printCheapest(3, filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB);
			consoleOut("Average: " + (sum / count) + " coins\n");
			consoleOut("Maximum: "
					+ (filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB)
							.mapToLong(a -> a.getHighest_bid_amount() / a.getItem_count()).max().getAsLong())
					+ " coins\n");
		} else
			consoleOut("No results!\n");
		updateConsoleOut();
	}

	private static void printCheapest(int topX, boolean filterCT, String CT, boolean matchCase, boolean filterSL,
			int SL, boolean filterTT, int TT, boolean filterHB, int HB) {
		long count = (filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB,
				HB).sorted(new CompHighestBidAsc())).filter(a -> a.getSeconds_left() > 5).count();
		for (int i = 0; i < topX && i < count; i++) {
			Auction min = (filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB,
					HB).sorted(new CompHighestBidAsc())).filter(a -> a.getSeconds_left() > 5).skip(i).findFirst().get();
			String cheapestAuctioneer = getPlayerFromUUID(min.getAuctioneer());
			consoleOut("Minimum " + (i + 1) + ": " + min.getHighest_bid_amount() + " coins" + " by "
					+ cheapestAuctioneer + " " + min.getSeconds_left() + "sec left" + "\n");
		}
	}

	private static Stream<Auction> filterStream(Stream<Auction> s, boolean filterCT, String CT, boolean matchCase,
			boolean filterSL, int SL, boolean filterTT, int TT, boolean filterHB, int HB) {
		return s.filter(a -> !filterCT || (matchCase ? a.getItem_name().equalsIgnoreCase(CT)
				: a.getItem_name().toLowerCase().contains(CT.toLowerCase())))
				.filter(a -> !filterSL || a.getSeconds_left() < SL)
				.filter(a -> !filterTT || a.getSeconds_on() < 5 * 60 || a.getSeconds_on() > TT)
				.filter(a -> !filterHB || a.getHighest_bid_amount() > HB);
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
				//mw.getBtnFilterButton().setEnabled(true);
			};
		};
		t.start();
	}

	private static void loadPage(int page) {
		URL url;
		String out = "";
		try {
			url = new URL("https://api.hypixel.net/skyblock/auctions?key=" + api_key + "&page=" + page);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			out = getContent(con);
		} catch (MalformedURLException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		} catch (IOException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		}
		JSONObject obj = new JSONObject(out);
		long timestamp = obj.getLong("lastUpdated");
		max_pages = obj.getInt("totalPages");
		//mw.getBtnFilterButton().setEnabled(false);
		for (Auction a : data) {
			a.setTimestamp(timestamp);
		}
		JSONArray arr = obj.getJSONArray("auctions");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject auction = arr.getJSONObject(i);
			String item_name = auction.getString("item_name");
			String item_bytes = auction.getString("item_bytes");
			int item_count = itemCountFromItemBytes(item_bytes);
			String uuid = auction.getString("uuid");
			String auctioneer = auction.getString("auctioneer");
			long start = auction.getLong("start");
			long end = auction.getLong("end");
			long highest_bid_amount = auction.getLong("highest_bid_amount");
			long starting_bid = auction.getLong("starting_bid");
			// TODO maybe add more detail to the auction objects
			Auction addition = new Auction(uuid, auctioneer, start, end, timestamp, item_name, highest_bid_amount,
					item_count, starting_bid);
			int tmp = data.indexOf(addition);
			if (tmp < 0)
				data.add(addition);
			else {
				data.get(tmp).setTimestamp(timestamp);
				data.get(tmp).setEnd(end);
				data.get(tmp).setHighest_bid_amount(highest_bid_amount);
			}
		}
		//mw.getBtnFilterButton().setEnabled(true);
	}

	private static String getPlayerFromUUID(String uuid) {
		URL url;
		String out = "";
		try {
			url = new URL("https://api.hypixel.net/player?key=" + api_key + "&uuid=" + uuid);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			out = getContent(con);
		} catch (MalformedURLException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		} catch (IOException e) {
			consoleOut("[ FAILURE ] Some internet connection problem!\n");
			e.printStackTrace();
		}
		JSONObject obj = new JSONObject(out);
		JSONObject player = obj.getJSONObject("player");
		return player.getString("playername");
	}

	private static int itemCountFromItemBytes(String s) {
		String jsonFormatOutput = "";
		try {
			jsonFormatOutput = jsonCode(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String out = jsonFormatOutput.split("Count")[1].split("\n")[0];
		if (out == null || out.isEmpty())
			return 1;
		return out.getBytes()[0];
	}

	public static String jsonCode(String inputString) throws IOException {
		String jsonFormatOutput = decompressGzip(decodeBase64String(inputString));
		return jsonFormatOutput;
	}

	public static byte[] decodeBase64String(String string) {
		return Base64.getDecoder().decode(string);
	}

	public static String decompressGzip(byte[] compressed) throws IOException {
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(compressed);
		GZIPInputStream gzipStream = new GZIPInputStream(byteArrayStream, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gzipStream.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gzipStream.close();
		byteArrayStream.close();
		return string.toString();
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
}
