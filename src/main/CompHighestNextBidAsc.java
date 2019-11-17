package main;

import java.util.Comparator;

public class CompHighestNextBidAsc implements Comparator<Auction> {

	@Override
	public int compare(Auction o1, Auction o2) {
		double b1 = o1.getNextBidAmount();
		double b2 = o2.getNextBidAmount();
		if (Math.abs(b1 - b2) < 0.1)
			return 0;
		return (b1 > b2) ? 1 : -1;
	}
}
