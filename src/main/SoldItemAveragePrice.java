package main;

public class SoldItemAveragePrice implements Comparable<SoldItemAveragePrice> {
	String item_name;
	long price;
	long count;

	public SoldItemAveragePrice(String item_name, long price, long count) {
		super();
		this.item_name = item_name;
		this.price = price;
		this.count = count;
	}

	public String getItem_name() {
		return item_name;
	}

	public long getPrice() {
		return price;
	}

	public long getCount() {
		return count;
	}

	@Override
	public int compareTo(SoldItemAveragePrice o) {
		if (getPrice() == o.getPrice())
			return 0;
		return (getPrice() > o.getPrice()) ? -1 : 1;
	}

}
