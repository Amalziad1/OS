package application;

import java.util.Comparator;

public class IOComparator implements Comparator<process> {

	public int compare(process p1, process p2)
	{
		if(p1.IOBurst.get(0) < p2.IOBurst.get(0))
		{
			return -1;
		}else if(p1.IOBurst.get(0) > p2.IOBurst.get(0))
		{
			return 1;
		}
		return 0;
	}
}

