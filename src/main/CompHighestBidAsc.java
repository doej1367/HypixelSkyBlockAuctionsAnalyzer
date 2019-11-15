package main;

import java.util.Comparator;

public class CompHighestBidAsc implements Comparator<Auction> {

	@Override
	public int compare(Auction o1, Auction o2) {
		if (o1.getHighest_bid_amount() == o2.getHighest_bid_amount())
			return 0;
		return (o1.getHighest_bid_amount() > o2.getHighest_bid_amount()) ? 1 : -1;
	}
}

