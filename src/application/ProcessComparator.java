package application;

import java.util.Comparator;

public class ProcessComparator implements Comparator<process> {

	public int compare(process p1, process p2)
	{
		if(p1.remainingTime < p2.remainingTime)
		{
			return -1;
		}else if(p1.remainingTime > p2.remainingTime)
		{
			return 1;
		}
		return 0;
	}
}

