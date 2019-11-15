package main;

import java.util.Comparator;

public class CompSecondsLeftDec implements Comparator<Auction> {

	@Override
	public int compare(Auction o1, Auction o2) {
		if (o1.getSeconds_left() == o2.getSeconds_left())
			return 0;
		return (o1.getSeconds_left() < o2.getSeconds_left()) ? 1 : -1;
	}
}
