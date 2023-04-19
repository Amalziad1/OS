package application;
import java.util.ArrayList;

public class process {
	
	private int ID;
	private int arrivalTime;
	
	public ArrayList<Integer> CPUBurst;
	public ArrayList<Integer> IOBurst;
	
	public double remainingTime=0;
	public int numOfCPUvisits=0;
	public int timeSpentOnCPU=0; 
	public int waitingTime=0;
	public int currentQ = 0;
	//RRq1 -> currentQ=0
	//RRq2 -> currentQ=2
	//SRTF -> currentQ=3
	//FCFS -> currentQ=4
	
	public process() {}
	
	public process(int ID, int arrivalT, ArrayList<Integer> CPUBurst, ArrayList<Integer> IOBurst)
	{
		setID(ID);
		setArrivalTime(arrivalT);
		this.CPUBurst = new ArrayList<>(CPUBurst);
		this.IOBurst = new ArrayList<>(IOBurst);
		
	}

	public void setArrivalTime(int arrivalTime)
	{
		// TODO Auto-generated method stub
		this.arrivalTime=arrivalTime;
	}
	public int getArrivalTime()
	{
		return this.arrivalTime;
	}

	public void setID(int ID) 
	{
		// TODO Auto-generated method stub
		this.ID = ID;
	}
	public int getID()
	{
		return this.ID;
	}
	
	public void setRemainingTime(int tempSpent)
	{
		double alpha = Driver.alpha;
		this.remainingTime = alpha*tempSpent + (1-alpha)*this.remainingTime;
		/*int sumOFBursts = 0;
		for(int i=0;i<CPUBurst.size();i++)
		{
			sumOFBursts += CPUBurst.get(i);
		}
		this.remainingTime = sumOFBursts;*/
	}
	
	/*public int getRemainingTime()
	{
		return this.remainingTime;
	}*/
	
	public void finishCPUBurst()
	{
		this.remainingTime -= this.CPUBurst.get(0);
		this.CPUBurst.remove(0);
	}
	public int getCurrentCPUBurst()
	{
		return this.CPUBurst.get(0);
	}
	public void finishedIOBurst()
	{
		this.IOBurst.remove(0);
	}
	public int getCurrentIOBurst()
	{
		return this.IOBurst.get(0);
	}
	
	public String toString()
	{
		return "ID:"+this.ID+", arrivalTime "+this.arrivalTime+", number of CPU bursts:"+this.CPUBurst.size() + ", remaining time: "+this.remainingTime ;	}
}
