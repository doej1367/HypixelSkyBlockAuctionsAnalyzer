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

	private static Main m;
	private static MainWindow mw;
	private static int requests = 0;
	private static int loaded;
	private String api_key = "";
	private int max_pages = 1;
	private ArrayList<Auction> data;
	private int maxmax_pages = 0;
	private int[] synchronizer = new int[1];

	public static void main(String[] args) {
		m = new Main();
		try {
			File config = new File("config.txt");
			if (!config.exists())
				config.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(config));
			m.api_key = "" + br.readLine();
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		startRequestResetter();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mw = new MainWindow(m);
					mw.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		m.data = new ArrayList<Auction>();
	}

	public void sendRequest(boolean filterCT, String CT, boolean matchCase, boolean filterSL, int SL, boolean filterTT,
			int TT, boolean filterHB, int HB) {
		consoleOut("loading pages ... ");
		if (!mw.getChckbxKeepOldData().isSelected())
			m.data = new ArrayList<Auction>();
		mw.getProgressBar().setStringPainted(true);
		mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
		ArrayList<MyThread> threads = new ArrayList<>();
		loaded = 1;
		Thread t_loading = new Thread() {
			@Override
			public void run() {
				while (loaded > 0) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mw.getProgressBar().setValue(loaded);
					mw.getProgressBar().setMaximum(max_pages);
					mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
				}
			}
		};
		t_loading.start();
		MyThread t0 = new MyThread(0);
		t0.start();
		try {
			t0.join();
			mw.getProgressBar().setMaximum(max_pages);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (max_pages > maxmax_pages)
			maxmax_pages = max_pages;
		for (int i = 1; i < max_pages; i++) {
			MyThread t = new MyThread(i);
			threads.add(t);
			t.start();
			if (max_pages > maxmax_pages)
				maxmax_pages = max_pages;
		}

		try {
			for (MyThread t : threads)
				t.join();
			loaded = 0;
			t_loading.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mw.getProgressBar().setValue(0);
		mw.getProgressBar().setStringPainted(false);
		mw.getProgressBar().paint(mw.getProgressBar().getGraphics());
		consoleOut(" [ OK ]\n");
		updateConsoleOut();
	}

	class MyThread extends Thread {
		private int i;

		public MyThread(int i) {
			this.i = i;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			loadPage(i);
			loaded++;
		}
	}

	public void filterData(boolean filterCT, String CT, boolean matchCase, boolean filterSL, int SL, boolean filterTT,
			int TT, boolean filterHB, int HB) {
		mw.getBtnFilterButton().setEnabled(false);
		if (data.isEmpty()) {
			consoleOut(" [ FAILURE ] No data collected yet!\n");
			mw.getBtnFilterButton().setEnabled(true);
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
			consoleOut("Average: " + (sum / count) + " coins\n");
			consoleOut("Maximum: "
					+ (filterStream(data.stream(), filterCT, CT, matchCase, filterSL, SL, filterTT, TT, filterHB, HB)
							.mapToLong(a -> a.getHighest_bid_amount() / a.getItem_count()).max().getAsLong())
					+ " coins\n");
		} else
			consoleOut("No results!\n");
		updateConsoleOut();
	}

	private Stream<Auction> filterStream(Stream<Auction> s, boolean filterCT, String CT, boolean matchCase,
			boolean filterSL, int SL, boolean filterTT, int TT, boolean filterHB, int HB) {
		return s.filter(a -> !filterCT || (matchCase ? a.getItem_name().equalsIgnoreCase(CT)
				: a.getItem_name().toLowerCase().contains(CT.toLowerCase())))
				.filter(a -> !filterSL || a.getSeconds_left() < SL)
				.filter(a -> !filterTT || a.getSeconds_on() < 5 * 60 || a.getSeconds_on() > TT)
				.filter(a -> !filterHB || a.getHighest_bid_amount() > HB);
	}

	private void consoleOut(String s) {
		mw.getConsoleOut().append(s);

	}

	private void updateConsoleOut() {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				mw.getSp().getVerticalScrollBar().setValue(mw.getSp().getVerticalScrollBar().getMaximum());
				mw.getSp().paint(mw.getSp().getGraphics());
				mw.getBtnFilterButton().setEnabled(true);
			};
		};
		t.start();
	}

	private static void startRequestResetter() {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
				}
				requests = 0;
			};
		};
		t.start();
	}

	private void loadPage(int page) {
		URL url;
		String out = "";
		try {
			if (requests > 120 - maxmax_pages) {
				consoleOut("[ WARNING ] Wait a bit or you will exceed the request cap of 120 / min\n");
				return;
			}
			url = new URL("https://api.hypixel.net/skyblock/auctions?key=" + api_key + "&page=" + page);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			out = getContent(con);
			requests++;
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
		mw.getBtnFilterButton().setEnabled(false);
		synchronized (synchronizer) {
			for (Auction a : data) {
				a.setTimestamp(timestamp);
			}
		}
		JSONArray arr = obj.getJSONArray("auctions");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject auction = arr.getJSONObject(i);
			String item_name = auction.getString("item_name");
			String item_bytes = auction.getString("item_bytes");
			int item_count = itemCountFromItemBytes(item_bytes);
			String uuid = auction.getString("uuid");
			long start = auction.getLong("start");
			long end = auction.getLong("end");
			long highest_bid_amount = auction.getLong("highest_bid_amount");
			long starting_bid = auction.getLong("starting_bid");
			// TODO
			Auction addition = new Auction(uuid, start, end, timestamp, item_name, highest_bid_amount, item_count,
					starting_bid);
			synchronized (synchronizer) {
				int tmp = data.indexOf(addition);
				if (tmp < 0)
					data.add(addition);
				else {
					data.get(tmp).setTimestamp(timestamp);
					data.get(tmp).setEnd(end);
					data.get(tmp).setHighest_bid_amount(highest_bid_amount);
				}
			}
		}
		mw.getBtnFilterButton().setEnabled(true);
	}

	private int itemCountFromItemBytes(String s) {
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

	public String jsonCode(String inputString) throws IOException {
		String jsonFormatOutput = decompressGzip(decodeBase64String(inputString));
		return jsonFormatOutput;
	}

	public byte[] decodeBase64String(String string) {
		return Base64.getDecoder().decode(string);
	}

	public String decompressGzip(byte[] compressed) throws IOException {
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

	private String getContent(HttpsURLConnection con) {
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

	public String getApi_key() {
		return api_key;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
}
