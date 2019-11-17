package main;

public class Auction {
	private String uuid;
	private String auctioneer;
	private String item_name;
	private long starting_bid;
	private long highest_bid_amount;
	private int item_count;
	private long start;
	private long end;
	private long timestamp;

	public Auction(String uuid, String auctioneer, long start, long end, long timestamp, String item_name, long highest_bid_amount,
			int item_count, long starting_bid) {
		super();
		this.setUuid(uuid);
		this.setAuctioneer(auctioneer);
		this.start = start;
		this.end = end;
		this.timestamp = timestamp;
		this.item_name = item_name;
		this.highest_bid_amount = highest_bid_amount;
		this.starting_bid = starting_bid;
		this.item_count = item_count;
	}

	public long getSeconds_left() {
		return ((end - timestamp) / 1000);
	}
	
	public double getNextBidAmount() {
		double b1 = getHighest_bid_amount();
		if (b1 == 0)
			return getStarting_bid();
		else
			return b1 * 1.15;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public long getHighest_bid_amount() {
		return highest_bid_amount;
	}

	public void setHighest_bid_amount(long highest_bid_amount) {
		this.highest_bid_amount = highest_bid_amount;
	}

	public int getItem_count() {
		return item_count;
	}

	public void setItem_count(int item_count) {
		this.item_count = item_count;
	}

	public long getStarting_bid() {
		return starting_bid;
	}

	public void setStarting_bid(long starting_bid) {
		this.starting_bid = starting_bid;
	}

	private String addSpaces(String in, int length) {
		String res = in;
		for (int i = in.length(); i < length; i++)
			res = " " + res;
		return res;
	}

	public long getSeconds_on() {
		return ((end - start) / 1000 / 60);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Auction))
			return false;
		return this.uuid.equalsIgnoreCase(((Auction) obj).getUuid());
	}

	@Override
	public String toString() {
		return addSpaces("" + item_count, 2) + "x " + addSpaces(item_name, 36) + " [ t="
				+ addSpaces("" + getSeconds_left(), 5) + " sec, s=" + addSpaces("" + starting_bid, 10) + " coins, h="
				+ addSpaces("" + highest_bid_amount, 10) + " coins, o=" + addSpaces("" + getSeconds_on() / 60, 10)
				+ " min ]";
	}

	public String getAuctioneer() {
		return auctioneer;
	}

	public void setAuctioneer(String auctioneer) {
		this.auctioneer = auctioneer;
	}

}
