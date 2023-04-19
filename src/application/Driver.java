package application;

import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Driver implements Initializable {
	public static int q1 = parametersController.q1;
	public static int q2 = parametersController.q2;
	public static double alpha = parametersController.alpha;
	public static int timer = 0; // a global clock for the scheduler
	public static boolean preemtion = false;

	public static ArrayList<String> Gantt = new ArrayList<String>();
	public static double utilization;
	public static double avgtime;

	public static ArrayList<Integer> waitTimeOfProcesses = new ArrayList<Integer>();

	// used in pause method (Stop method)
	static Queue<process> tempRRq1;
	static Queue<process> tempRRq2;
	static PriorityQueue<process> tempSRTF;
	static Queue<process> tempFCFS;
	static ArrayList<process> tempIO;
	static int tempCurrentQ;
	static int tempPID;
	public static boolean stop = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println(q1);
//		Scanner kb = new Scanner(System.in);
//		System.out.print("Please enter the input file's name: ");
//		String fileName = kb.nextLine();
//		System.out.print("Enter the number of processes please: ");
//		int numberOfProcesses = kb.nextInt();
		int numberOfProcesses = 10;
		String fileName;
		if (inputFileController.status == true) {
			fileName = inputFileController.fileName;
		} else {
			fileName = "data.txt";
		}

		// ideas i thought of for the processes in the system so I can check their
		// arrival time:
		// simply an array
		// linked list sorted by arrival time //how to check all arrival times?
		// a hash map with the arrival time as the key //arrival time isn't unique
		// LinkedList<process> processesInTheSystem = new LinkedList<>(); //list of
		// processes in the system
		// hash map of linked list with the key being arrival time and items of the list
		// processes with the same arrival time
		HashMap<Integer, LinkedList<process>> processesInTheSystem = new HashMap<Integer, LinkedList<process>>();
		// IO queue with priority to shortest (zero) waiting time; wrong idea
		// IO Array List (simple)
		ArrayList<process> IOQueue = new ArrayList<process>();

		// queues and lists
		Queue<process> RRq1 = new LinkedList<>(); // first RR queue with quantum q1
		Queue<process> RRq2 = new LinkedList<>(); // second RR queue with quantum q2
		PriorityQueue<process> SRTF = new PriorityQueue<process>(numberOfProcesses, new ProcessComparator());// shortest
																												// remaining
																												// time
																												// first
																												// queue
		Queue<process> FCFS = new LinkedList<>();// first come first serve queue
		// kb.close();

		try {
			File inputFile = new File(fileName);
			Scanner fileReader = new Scanner(inputFile);

			// read all the processes and add them to the first queue:RRq1
			String[] arguments;
			ArrayList<Integer> CPUBursts = new ArrayList<>();
			ArrayList<Integer> IOBursts = new ArrayList<>();
			while (fileReader.hasNextLine()) {
				String data = fileReader.nextLine();
				arguments = data.split("\\s");
				// arguments[0] = ID
				// arguments[1] = arrival time
				// arguments[2,4,6,...length-1] = CPU Burst
				// arguments[3,5,7,...length-2] = IO Burst
				CPUBursts = new ArrayList<>();
				IOBursts = new ArrayList<>();
				for (int i = 2; i < arguments.length - 1; i++) {
					// always add the first one as a CPU burst
					if (i % 2 == 0)// CPU Burst
					{
						if(arguments[arguments.length - 1]=="") {
							continue;
						}
						CPUBursts.add(Integer.parseInt(arguments[i]));
					} else // IO Burst
					{
						IOBursts.add(Integer.parseInt(arguments[i]));
					}
				}
				// always add the last one as a CPU burst
				if(arguments[arguments.length - 1]=="") {
					continue;
				}
				CPUBursts.add(Integer.parseInt(arguments[arguments.length - 1]));
				// create a new process and add it in the system processes linked list
				process temp = new process(Integer.valueOf(arguments[0]), Integer.valueOf(arguments[1]), CPUBursts,
						IOBursts);

				// we need to keep in mind the arrival time of the process when adding it to the
				// list
				System.out.println("processesInTheSystem=" + processesInTheSystem);
				System.out.println("temp=" + temp);
				addProcessToArrivalList(temp, processesInTheSystem);

				// processesInTheSystem.put(temp.getArrivalTime(),temp);
				// System.out.println(Arrays.toString(arguments));
				// System.out.println(temp.toString());
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.\nThe file " + fileName + " doesn't exist.");
		}

		// the ground rules are established. time for the simulation

		// it's actually at "run" time we add the processes when they arrive to the RRq1
		// queue
		// meaning after we define time and start interacting with it

		boolean isCPUbusy = false;
		process processInCPU = null;
		int lastTimeAprocessStarted = -1;
		int timeCPUnotWorking = 0;

		// add the processes that start at 0 to CPU and RRq1
		/*
		 * if (processesInTheSystem.containsKey(0)) { // add first process to CPU //
		 * processInCPU = processesInTheSystem.get(0).get(0); // isCPUbusy=true; //
		 * lastTimeAprocessStarted=timer; //=0 // add rest of the processes that start
		 * at 0 to RRq1 for (int i = 0; i < processesInTheSystem.get(0).size(); i++) {
		 * RRq1.add(processesInTheSystem.get(0).get(i)); } }
		 */

		ArrayList<process> GanttChart = new ArrayList<process>();

		while (!processesInTheSystem.isEmpty()) // as long as there's processes in the system
		{
			// System.out.println("lastTimeAprocessStarted"+lastTimeAprocessStarted);
			// shortest remaining time first
			System.out.println("timer= " + timer);
			// if a new process arrived at the system
			if (processesInTheSystem.containsKey(timer)) {
				System.out.println("new processes arrived to the system");
				for (int i = 0; i < processesInTheSystem.get(timer).size(); i++) {
					RRq1.add(processesInTheSystem.get(timer).get(i));
				}
				if (isCPUbusy) {
					if (processInCPU.currentQ != 0) {
						removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
								false);
						processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
						System.out.println("switched to new process");
						System.out.println(processInCPU);
						if (processInCPU == null)
							isCPUbusy = false;
						else {
							isCPUbusy = true;
							lastTimeAprocessStarted = timer;
							System.out.println("switched to process P" + processInCPU.getID());
							// GanttChart.add(processInCPU);
							// updateProcessesIOBurst(IOQueue,RRq1,RRq2,SRTF,FCFS);
							// timer++;
							// continue;
						}
					}
				} else {
					processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
					System.out.println("switched to new process");
					System.out.println(processInCPU);
					if (processInCPU == null)
						isCPUbusy = false;
					else {
						isCPUbusy = true;
						lastTimeAprocessStarted = timer;
						System.out.println("switched to process P" + processInCPU.getID());
						// GanttChart.add(processInCPU);
						// updateProcessesIOBurst(IOQueue,RRq1,RRq2,SRTF,FCFS);
						// timer++;
						// continue;
					}
				}

			}

			if (isCPUbusy) {
				if (processInCPU.CPUBurst.size() != 0) {

					processInCPU.CPUBurst.set(0, processInCPU.CPUBurst.get(0) - 1);
					// if a new process arrived at the system
					/*
					 * if(processesInTheSystem.containsKey(timer)) {
					 * System.out.println("new processes arrived to the system"); for(int
					 * i=0;i<processesInTheSystem.get(timer).size();i++) {
					 * RRq1.add(processesInTheSystem.get(timer).get(i)); }
					 * removeProcessFromCPU(processInCPU,RRq1,RRq2,SRTF,FCFS,
					 * lastTimeAprocessStarted, IOQueue,false);
					 * processInCPU=addProcessToCPU(RRq1,RRq2,SRTF,FCFS,lastTimeAprocessStarted);
					 * System.out.println("switched to new process");
					 * System.out.println(processInCPU); if(processInCPU == null) isCPUbusy=false;
					 * else { isCPUbusy=true; lastTimeAprocessStarted=timer;
					 * System.out.println("switched to process P"+processInCPU.getID()); } } else
					 */ if (processInCPU.CPUBurst.get(0) <= 0) {
						processInCPU.CPUBurst.remove(0);
						if (processInCPU.CPUBurst.size() == 0) {
							terminateProcess(processInCPU, processesInTheSystem);
							isCPUbusy = false;
						} else
							removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
									true);

						processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
						System.out.println(processInCPU);
						if (processInCPU == null)
							isCPUbusy = false;
						else {
							isCPUbusy = true;
							lastTimeAprocessStarted = timer;
							System.out.println("switched to process P" + processInCPU.getID());
						}
					} // end of if CPU burst finished
						// if q1 finished
					else if (processInCPU.currentQ == 0 && timer - lastTimeAprocessStarted >= q1) {
						removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
								false);
						processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
						System.out.println("q1 for process from RRq1 finished");
						System.out.println(processInCPU);
						if (processInCPU == null)
							isCPUbusy = false;
						else {
							isCPUbusy = true;
							lastTimeAprocessStarted = timer;
							System.out.println("switched to process P" + processInCPU.getID());
						}
					} // end of if q1 finished
						// if q2 finished
					else if (processInCPU.currentQ == 1 && timer - lastTimeAprocessStarted >= q2) {
						removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
								false);
						processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
						System.out.println("q2 for process from RRq2 finished");
						System.out.println(processInCPU);
						if (processInCPU == null)
							isCPUbusy = false;
						else {
							isCPUbusy = true;
							lastTimeAprocessStarted = timer;
							System.out.println("switched to process P" + processInCPU.getID());
						}
					} // end of if q2 ends
						// if a process entered a higher priority queue
					else if (preemtion) {
						boolean needToSwitch = false;
						switch (processInCPU.currentQ) {
						case 0:
							needToSwitch = true;
							break;
						case 1:
							if (!RRq1.isEmpty())
								needToSwitch = true;
							break;
						case 2:
							if (!RRq1.isEmpty() || !RRq2.isEmpty())
								needToSwitch = true;
							break;
						case 3:
							if (!RRq1.isEmpty() || !RRq2.isEmpty() || !SRTF.isEmpty())
								needToSwitch = true;
							break;
						}
						if (needToSwitch) {
							System.out.println("a process arrived at a higher priority q");

							removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
									false);
							processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
							System.out.println("switched to higher pririty process");
							System.out.println(processInCPU);
							if (processInCPU == null)
								isCPUbusy = false;
							else {
								isCPUbusy = true;
								lastTimeAprocessStarted = timer;
								System.out.println("switched to process P" + processInCPU.getID());
							}
							needToSwitch = false;
						}

						preemtion = false;
					} // end of if a process arrived at a higher priority q
//					else if (processInCPU.currentQ == 2
//							|| processInCPU.remainingTime >= (timer - lastTimeAprocessStarted)) {
//						// update the process remaining time;
//						// processInCPU.setRemainingTime();
//
//						removeProcessFromCPU(processInCPU, RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted, IOQueue,
//								false);
//						processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
//						System.out.println("process from 3rd Q finished its allowed time");
//						System.out.println(processInCPU);
//						if (processInCPU == null)
//							isCPUbusy = false;
//						else {
//							isCPUbusy = true;
//							lastTimeAprocessStarted = timer;
//							System.out.println("switched to process P" + processInCPU.getID());
//						}
//					}
				} else // if the CPUBurst size =0 it means the process finished
				{
					terminateProcess(processInCPU, processesInTheSystem);
					processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
					System.out.println("process from 3rd Q finished its allowed time");
					System.out.println(processInCPU);
					if (processInCPU == null)
						isCPUbusy = false;
					else {
						isCPUbusy = true;
						lastTimeAprocessStarted = timer;
						System.out.println("switched to process P" + processInCPU.getID());
					}
				}

			} else // CPU is not busy
			{
				// if a new process arrived
				/*
				 * if(processesInTheSystem.containsKey(timer)) {
				 * System.out.println("new processes arrived to the system"); for(int
				 * i=0;i<processesInTheSystem.get(timer).size();i++) {
				 * RRq1.add(processesInTheSystem.get(timer).get(i)); }
				 * //removeProcessFromCPU(processInCPU,RRq1,RRq2,SRTF,FCFS,
				 * lastTimeAprocessStarted, IOQueue,false);
				 * processInCPU=addProcessToCPU(RRq1,RRq2,SRTF,FCFS,lastTimeAprocessStarted);
				 * System.out.println("switched to new process");
				 * System.out.println(processInCPU); if(processInCPU == null) isCPUbusy=false;
				 * else { isCPUbusy=true; lastTimeAprocessStarted=timer;
				 * System.out.println("switched to process P"+processInCPU.getID()); } }
				 */

				timeCPUnotWorking++;
				processInCPU = addProcessToCPU(RRq1, RRq2, SRTF, FCFS, lastTimeAprocessStarted);
				System.out.println(processInCPU);
				if (processInCPU == null)
					isCPUbusy = false;
				else {
					isCPUbusy = true;
					lastTimeAprocessStarted = timer;
					System.out.println("switched to process P" + processInCPU.getID());
				}
			}

			GanttChart.add(processInCPU);
			updateProcessesIOBurst(IOQueue, RRq1, RRq2, SRTF, FCFS);
			timer++;
			// if(!FCFS.isEmpty())
			// System.out.println("FCFS= "+FCFS);
			if (!RRq1.isEmpty())
				System.out.println("RRq1= " + RRq1);
			// if(timer>600)
			// break;
			// if(timer == 5)
			// stop=true;
			if (stop) {
				tempRRq1 = RRq1;
				tempRRq2 = RRq2;
				tempSRTF = SRTF;
				tempFCFS = FCFS;
				tempIO = IOQueue;
				tempCurrentQ = processInCPU.currentQ;
				tempPID = processInCPU.getID();
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} // end of while*/

		System.out.println(processesInTheSystem);
		System.out.println(RRq1);
		System.out.println(RRq2);
		System.out.println(SRTF);
		System.out.println(FCFS);
		System.out.println(IOQueue);
		System.out.println("CPU utilization = " + (float) (1 - (float) timeCPUnotWorking / timer));
		utilization = (float) (1 - (float) timeCPUnotWorking / timer);
		double sum = 0;
		for (int i = 0; i < waitTimeOfProcesses.size() - 1; i++) {
			sum += waitTimeOfProcesses.get(i);
		}
		System.out.println("avg waiting time= " + sum / waitTimeOfProcesses.size());
		avgtime = sum / waitTimeOfProcesses.size();
		System.out.print("Gantt chart: ");
		for (int i = 0; i < GanttChart.size() - 1; i++) {
			try {
				// if(GanttChart.get(i).getID()==6 || GanttChart.get(i).getID()==8)
				Gantt.add("P" + GanttChart.get(i).getID());
				System.out.print("P" + GanttChart.get(i).getID() + "  ");
			} catch (Exception e) {
				continue;
			}

		}
	}

	public static void addProcessToArrivalList(process temp, HashMap<Integer, LinkedList<process>> list) {
		// if the key is in to the hash map
		if (list.containsKey(temp.getArrivalTime())) {
			list.get(temp.getArrivalTime()).add(temp);
			System.out.println("containd key: arrival time" + temp.getArrivalTime() + " for process:" + temp.getID());
			System.out.println("====================================");
		} else {
			// the key is new
			LinkedList<process> listOfProcesses = new LinkedList<>();
			listOfProcesses.add(temp);
			list.put(temp.getArrivalTime(), listOfProcesses);
			System.out.println("new key: arrival time" + temp.getArrivalTime() + " for process:" + temp.getID());

		}
	}

	public static void removeProcessFromCPU(process temp, Queue<process> RRq1, Queue<process> RRq2,
			PriorityQueue<process> SRTF, Queue<process> FCFS, int lastTimeAprocessStarted, ArrayList<process> IOQ,
			boolean toIO) {
		// tested and working
		try {
			int queueNumber = temp.currentQ;
			int timeSpent = timer - lastTimeAprocessStarted;
			temp.numOfCPUvisits++; // Q3
			temp.timeSpentOnCPU += timeSpent;
			// temp.setRemainingTime(); //may not be needed
			switch (queueNumber) {
			case 0:// currently on RRq1

				// check if it should return to RRq1
				if (temp.timeSpentOnCPU >= 10 * q1) {
					if (toIO)
						IOQ.add(temp);
					else
						RRq2.add(temp);
					temp.currentQ = 1;
					temp.timeSpentOnCPU = 0; // give it a new chance in the new queue
				} else {
					if (toIO)
						IOQ.add(temp);
					else
						RRq1.add(temp);
				}
				break;
			case 1:// currently on RRq2

				// check if it should return to RRq2
				if (temp.timeSpentOnCPU >= 10 * q2) {
					if (toIO)
						IOQ.add(temp);
					else
						SRTF.add(temp);
					temp.currentQ = 2;
					temp.numOfCPUvisits = 0;// to count 3 times before moving it to the last queue
				} else {
					if (toIO)
						IOQ.add(temp);
					else
						RRq2.add(temp);
				}
				break;
			case 2:// currently on SRTF

				temp.setRemainingTime(timer-lastTimeAprocessStarted);
				System.out.println("new remaining time= " + temp.remainingTime);
				// check if it should return to RRq1
				if (temp.numOfCPUvisits >= 3) {
					if (toIO)
						IOQ.add(temp);
					else
						FCFS.add(temp);
					temp.currentQ = 3;
				} else {
					if (toIO)
						IOQ.add(temp);
					else
						FCFS.add(temp);
				}
				break;
			case 3:// currently on FCFS
					// in the last queue there's no choice but to go to IOQ
				if (toIO) {
					IOQ.add(temp);
				} else {
					FCFS.add(temp);
				}
				break;
			}

		} catch (Exception e) {
			System.out.println("something went wrong when trying to remove a process from the CPU");
		}
	}

	public static process addProcessToCPU(Queue<process> RRq1, Queue<process> RRq2, PriorityQueue<process> SRTF,
			Queue<process> FCFS, int lastTimeAprocessStarted) {

		// tested and working
		if (!RRq1.isEmpty()) {
			RRq1.peek().waitingTime += timer - lastTimeAprocessStarted;
			return RRq1.poll();
		}
		// no need for else because of the previous return
		if (!RRq2.isEmpty()) {
			RRq2.peek().waitingTime += timer - lastTimeAprocessStarted;
			return RRq2.poll();
		}
		if (!SRTF.isEmpty()) {
			SRTF.peek().waitingTime += timer - lastTimeAprocessStarted;
			return SRTF.poll();
		}
		if (!FCFS.isEmpty()) {
			FCFS.peek().waitingTime += timer - lastTimeAprocessStarted;
			return FCFS.poll();
		}
		return null;
	}

	public static void terminateProcess(process temp, HashMap<Integer, LinkedList<process>> map) {// tested and working
		waitTimeOfProcesses.add(temp.waitingTime);
		LinkedList<process> replacement = map.get(temp.getArrivalTime());
		if (replacement == null) {
			map.remove(temp.getArrivalTime());
			System.out.println("already removed");
			return;
		}

		int index = 0;
		if (replacement.size() == 1) // if its the only process in the list
		{
			map.remove(temp.getArrivalTime());
			System.out.println("P" + temp.getID() + " was the only process and it was removed");
			return;
		}
		// if it's not the only one
		while (replacement.get(index).getID() != temp.getID()) {
			index++;
			if (index >= replacement.size() - 1)
				break;
		}
		replacement.remove(index);

		map.replace(temp.getArrivalTime(), replacement);
		System.out.println("P" + temp.getID() + " was removed");
	}

	public static void updateProcessesIOBurst(ArrayList<process> IOQ, Queue<process> RRq1, Queue<process> RRq2,
			PriorityQueue<process> SRTF, Queue<process> FCFS) {
		// tested and working
		/*
		 * for (Map.Entry<Integer,LinkedList<process>> mapElement : map.entrySet()) {
		 * //for every list in the map LinkedList<process> list = mapElement.getValue();
		 * System.out.println(list); for(int i=0;i<list.size();i++)//for every process {
		 * list.get(i).IOBurst.set(i,list.get(i).IOBurst.get(i)- 1); } }
		 */
		/*
		 * for(int key: map.keySet()) { //System.out.println(map.get(key).size());
		 * LinkedList<process> list = map.get(key); for(int i=0;i<list.size();i++)//for
		 * every process { if(list.get(i).IOBurst.size()==0) continue;
		 * list.get(i).IOBurst.set(0,list.get(i).IOBurst.get(0)- 1);
		 * //System.out.println(i+": "+list.get(i).IOBurst); } }
		 */

		for (int i = 0; i < IOQ.size(); i++) {
			if (IOQ.get(i).IOBurst.size() != 0) {
				IOQ.get(i).IOBurst.set(0, IOQ.get(i).IOBurst.get(0) - 1);
				if (IOQ.get(i).IOBurst.get(0) <= 0) {
					removeProcessFromIOQ(IOQ.get(i), RRq1, RRq2, SRTF, FCFS);
					IOQ.remove(i);
				}
			} else {
				removeProcessFromIOQ(IOQ.get(i), RRq1, RRq2, SRTF, FCFS);
				IOQ.remove(i);
			}
		}
	}

	public static void removeProcessFromIOQ(process temp, Queue<process> RRq1, Queue<process> RRq2,
			PriorityQueue<process> SRTF, Queue<process> FCFS) {
		preemtion = true;
		switch (temp.currentQ) {
		case 0:
			RRq1.add(temp);
			break;
		case 1:
			RRq2.add(temp);
			break;
		case 2:
			SRTF.add(temp);
			break;
		case 3:
			FCFS.add(temp);
			break;
		}
	}

	@FXML
	private Text avg;

	@FXML
	private Button exit;

	@FXML
	private Button pause;

	@FXML
	private Button show;

	@FXML
	private Button start;

	@FXML
	private Text util;

	@FXML
	private Label msg;
	@FXML
	private Label tt;
	@FXML
	private Label ps;
	@FXML
	private Button b;

	public void Show() {
		double utili = utilization;
		double avgt = avgtime;
		ArrayList<String> temp = Gantt;
		ArrayList<String> p = new ArrayList<String>();
		ArrayList<Integer> time = new ArrayList<Integer>();
		String line1 = "";
		String line2 = "";
		int timer = 0;
		for (int i = 0; i < temp.size(); i++) {
			String element = temp.get(i).replaceAll("\\s", "");
			// System.out.println("===============" + element);
			if (i == 0) {
				// System.out.println("=============i=0 entered==");
				p.add(element);
				time.add(timer);
				// System.out.println("========size of p=======" + p.size());
			} else {
				int index = p.size() - 1;// last index
				// System.out.println("===============" + index);
				String str = p.get(index).replaceAll("\\s", "");
				if (!str.equals(element)) {
					p.add(element);
					time.add(timer);
				}
			}
			timer++;
		}
		time.add(timer);
		for (int i = 0; i < p.size(); i++) {
			line1 += (p.get(i) + " ");
		}
		// line += "\n";
		for (int i = 0; i < time.size(); i++) {
			line2 += (time.get(i) + "  ");
		}
		for (int i = 0; i < p.size(); i++) {
		}
		// System.out.println("\nMy line " + line);
		ps.setText(line1);
		tt.setText(line2);
		util.setText(String.valueOf(utili));
		avg.setText(String.valueOf(avgt));
	}

	public void back(ActionEvent event) {
		try {
			System.out.println("==================================");
			Parent root = FXMLLoader.load(getClass().getResource("parameters.fxml"));
			primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Stage primaryStage;
	private Scene scene;

	public void pause(ActionEvent event) {
		try {
			System.out.println("==================================");
			Parent root = FXMLLoader.load(getClass().getResource("pausing.fxml"));
			primaryStage = (Stage) (((Node) event.getSource())).getScene().getWindow();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		System.exit(1);
	}

	public static ArrayList<String> stopMethod() {
		stop = true;
		System.out.println("===================================");
		String line1 = "", line2 = "", line3 = "", line4 = "", line5 = "";
		System.out.println("System paused");
		process p1;
		int q1s = 0, q2s = 0, q3s = 0, q4s = 0, q5s = 0;
		if (tempRRq1 != null) {
			q1s = 1;
		} else {
			line1 = "Queue is empty";
		}
		if (tempRRq2 != null) {
			q2s = 1;
		} else {
			line2 = "Queue is empty";
		}
		if (tempSRTF != null) {
			q3s = 1;
		} else {
			line3 = "Queue is empty";
		}
		if (tempFCFS != null) {
			q4s = 1;
		} else {
			line4 = "Queue is empty";
		}
		if (tempIO != null) {
			q5s = 1;
		} else {
			line5 = "Queue is empty";
		}
		while ((p1 = tempRRq1.poll()) != null && q1s == 1) {
			line1 += ("P" + p1.getID() + " ");
		}
		while ((p1 = tempRRq2.poll()) != null && q2s == 1) {
			line2 += ("P" + p1.getID() + " ");
		}
		while ((p1 = tempSRTF.poll()) != null && q3s == 1) {
			line3 += ("P" + p1.getID() + " ");
		}
		while ((p1 = tempFCFS.poll()) != null && q4s == 1) {
			line4 += ("P" + p1.getID() + " ");
		}
		if (q5s == 1) {
			for (int i = 0; i < tempIO.size(); i++) {
				line5 += ("P" + tempIO.get(i).getID() + " ");
			}
		}
		String current = "";
		if (tempCurrentQ == 0) {
			current = "Queue 1";
		} else if (tempCurrentQ == 1) {
			current = "Queue 2";
		} else if (tempCurrentQ == 2) {
			current = "Queue 3";
		} else if (tempCurrentQ == 3) {
			current = "Queue 4";
		}
		System.out.println("Queue 1: " + line1);
		System.out.println("Queue 2: " + line2);
		System.out.println("Queue 3: " + line3);
		System.out.println("Queue 4: " + line4);
		System.out.println("I/O Queue : " + line5);
		System.out.println("Current queue of the running P" + tempPID + ": " + current);
		ArrayList<String> args = new ArrayList<String>();
		args.add(line1);
		args.add(line2);
		args.add(line3);
		args.add(line4);
		args.add(line5);
		args.add(String.valueOf(tempPID));
		args.add(current);

		return args;
	}

}
