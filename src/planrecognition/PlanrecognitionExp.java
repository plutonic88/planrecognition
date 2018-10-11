package planrecognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;
import solver.Solver;

public class PlanrecognitionExp {
	
	public static void doFixedPolicyWithDefenseExp1(boolean withdefense, int chosenattacker, int chosenpolicy, boolean minentropy, 
			boolean maxoverlap, boolean expoverlap, boolean mincost, boolean mincommonoverlap, boolean honeyedge) throws Exception {




		
		/*boolean withdefense = true;
		
		int chosenattacker = 1;
		int chosenpolicy = 0;
		
		boolean minentropy = false;*/
		
		
		
		//boolean mincommonoverlap = mincommonoverlap2;
		
		/*if(minentropy)
		{
			mincommonoverlap = false;
		}
		else
		{
			mincommonoverlap = true;
		}*/
		
		
		boolean samevalhp = false;
		boolean allexphp = true; // if false, this can lead to blocking action, attacker might not have any policy...
		
		
		
		
		
		

		
		//int[] goals = {10, 11};
		int nattackers = 3;
		int startnode = 0;
		int nnodes = 16;
		int nhoneypots = 3;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		boolean pickfromnet = true;
		
		

		int nexploits = 5;

		

		boolean singlegoal = true;
		boolean singlepath = false;
		int startnodeid = 0;
		int npath = 1;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		//Network.constructNetwork(net, exploits, nnodes, nexploits);
		
		
		
		
		int[] goals = new int[nattackers];
		
		for(int i=0; i<nattackers; i++)
		{
			goals[i] = nnodes - nattackers + i;
		}
		
		//Network.constructNetwork23(net, exploits, nnodes, nexploits);
		
		Network.constructNetwork13(net, exploits, nnodes, nexploits);
		
		
		//Network.constructNetwork10(net, exploits, nnodes, nexploits);
		
		//Network.constructNetwork10V2(net, exploits, nnodes, nexploits);

		System.out.println("Attacker construction... \ndone");

		PlanRecognition.printNetwork(net);
		System.out.println();
		
		//PlanRecognition.printNetwork(net);
		
		
		
		/**
		 * construct honeypots from real node configurations
		 * Imagine that attacker is naive
		 */
		
		if(!honeyedge)
		{
			Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc, samevalhp, allexphp, net, pickfromnet, goals);
		}
		
		
		System.out.println("*****************Honeypots******************");
		PlanRecognition.printNetwork(honeypots);
		//PlanRecognition.printNetwork(honeypots);
		
		testSolver(net, exploits, honeypots, chosenattacker, goals, nattackers);

		//System.out.println("Network construction... \ndone");
		
		
		
		/*
		


		if(singlegoal)
		{
			//PlanRecognition.constructAttackersSingleGoal(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
			
			PlanRecognition.constructAttackers(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap, nattackers, goals);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();
		
		

		
		
		PlanRecognition.playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap, mincost, honeyedge);



*/

	}
	
	
	public static void doFixedPolicyWithDefenseMILP(boolean withdefense, int chosenattacker, int chosenpolicy, boolean minentropy, 
			boolean maxoverlap, boolean expoverlap, boolean mincost, boolean mincommonoverlap, boolean honeyedge, boolean minmaxexpectedoverlap) throws Exception {




		
		boolean samevalhp = false;
		boolean allexphp = true; // if false, this can lead to blocking action, attacker might not have any policy...
		
		
		
		//int[] goals = {10, 11};
		int nattackers = 3;
		int startnode = 0;
		int nnodes = 16;
		int nhoneypots = 3;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		boolean pickfromnet = true;
		
		

		/**
		 * there should be a cost for honey edges and a cost limit at a single time
		 * or how many there should be a limit on how many exploits we can start at a single round in how many nodes
		 * can we repeatedly use an exploit to add in different nodes?
		 */
		int nexploits = 5;
		int honeyedgelimit = 2;
		

		

		boolean singlegoal = true;
		boolean singlepath = false;
		int startnodeid = 0;
		int npath = 1;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		//Network.constructNetwork(net, exploits, nnodes, nexploits);
		
		
		
		
		int[] goals = new int[nattackers];
		
		for(int i=0; i<nattackers; i++)
		{
			goals[i] = nnodes - nattackers + i;
		}
		
		//Network.constructNetwork23(net, exploits, nnodes, nexploits);
		
		Network.constructNetwork13(net, exploits, nnodes, nexploits);
		
		
		//Network.constructNetwork10(net, exploits, nnodes, nexploits);
		
		//Network.constructNetwork10V2(net, exploits, nnodes, nexploits);

		System.out.println("Attacker construction... \ndone");

		PlanRecognition.printNetwork(net);
		System.out.println();
		
		//PlanRecognition.printNetwork(net);
		
		
		
		/**
		 * construct honeypots from real node configurations
		 * Imagine that attacker is naive
		 */
		
		if(!honeyedge)
		{
			Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc, samevalhp, allexphp, net, pickfromnet, goals);
		}
		
		
		System.out.println("*****************Honeypots******************");
		PlanRecognition.printNetwork(honeypots);
		//PlanRecognition.printNetwork(honeypots);
		
		//testSolver(net, exploits, honeypots, chosenattacker, goals, nattackers);

		//System.out.println("Network construction... \ndone");
		
		
		
		
		


		if(singlegoal)
		{
			
			
			
			PlanRecognition.constructAttackersMILP(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap, nattackers, goals, honeypots);

			//PlanRecognition.constructAttackers(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap, nattackers, goals);
			
			
			//PlanRecognition.constructAttackersSingleGoal(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();
		
		
		
		
		
		/*PlanRecognition.playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap, mincost, honeyedge);
*/
		
		/**
		 * 1. honey edges
		 * 2. honeypot
		 * 
		 * For each defensive action the decision criteria can be: 
		 * 
		 * a. min max overlap
		 * b. min ax expected overlap
		 * c. min entropy
		 */

		PlanRecognition.playGameWithNaiveDefenseMILP(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap, mincost, honeyedge, honeyedgelimit, minmaxexpectedoverlap);



	}
	
	
	
private static void testSolver(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, HashMap<Integer,Node> honeypots, int chosenattacker, int[] goals2, int nattackers) throws Exception {
		
	
	int n= net.size();
	int e = exploits.size();
	
	
	/**
	 * create a map of nodes with exploits to id
	 */
	
	
	HashMap<String, Integer> nodeexpltmap = new HashMap<String, Integer>();
	HashMap<Integer, String> nodeexpltmapback = new HashMap<Integer, String>();
	HashMap<String, Integer> edgecost = new HashMap<String, Integer>();
	
	
	//HashMap<Integer,Node> newnet = new HashMap<Integer,Node>();
	
	
	//int[][] wt = buildCostMatrix(net, nodeexpltmap, nodeexpltmapback, edgecost, exploits);
	int [][][] w = build3DCostMatrix(net, nodeexpltmap, nodeexpltmapback, edgecost, exploits);
	
	int start = 0;
	int goal = goals2[0];
	//int[] goals = {8,9,10};
	
	
	ArrayList<Integer> starts = new ArrayList<Integer>();
	ArrayList<Integer> goals = new ArrayList<Integer>();
	
	
	for(String key: nodeexpltmap.keySet())
	{
		String s1 = key.split("-")[0];
		int id = Integer.parseInt(s1);
		if(id==start)
		{
			starts.add(nodeexpltmap.get(key));
		}
		if(id==goal)
		{
			goals.add(nodeexpltmap.get(key));
		}
	}
	
	for(int g: goals2)
	{
		goals.add(g);
	}
	
	
	
	
	
	
	
	
	// ArrayList<int[]> path1 = Solver.solve(wt, starts, goals);
	
	
	
	 
	 
	// ArrayList<int[]> path = Solver.solve3DCost(w, start, goal, exploits.size());
	
	//int nattakers = 3;
	
	//int n = net.size();
	
	int hpdeploylimit = 2;
	
	//int totalhp = 4;
	
	int attcurrentnodeid = 0;
	
	Node curnode = net.get(attcurrentnodeid);
	
	/**
	 * find in how many places HPs can be deployed
	 * 
	 * 1. find all nodes reachable from attacker's current position
	 * 2. Then use slot and HP combinations
	 */
	
	
	ArrayList<Integer> reachablesnodes = new ArrayList<Integer>(); //findReachableNodes(net, attcurrentnodeid, exploits, goals);
	
	PlanRecognition.printNetwork(net);
	
	/**
	 * 1. free HPS
	 * 2. total # of settings
	 * 3. For each settings set costs between two i j nodes. 
	 * 
	 * 4. need slot ids and Hps
	 * 5. need combinations
	 * 6. then set up costs 
	 */
	
	
	HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, goals, curnode);
	
	PlanRecognition.printSlots(placestoallocatehp);

	if(placestoallocatehp.size()==1)
	{
		System.out.println("One slot only");
	}


	/**
	 * Now compute which honeypots are free
	 * Which are currently not being used
	 */
	
	
	ArrayList<Integer> currenthps = new ArrayList<Integer>();
	
	System.out.println("current honeypots: ");
	
	for(Integer h: currenthps)
	{
		System.out.print(h+", ");
	}
	
	System.out.println();
	

	ArrayList<Integer> freehps = PlanRecognition.findFreeHP(currenthps, honeypots);
	
	
	System.out.println("free honeypots: ");
	
	for(Integer h: freehps)
	{
		System.out.print(h+", ");
	}
	
	System.out.println();
	
	

	int hplimit = hpdeploylimit - currenthps.size();

	if(hplimit>placestoallocatehp.size())
	{
		hplimit = placestoallocatehp.size();
	}
	
	System.out.println("total slots "+ placestoallocatehp.size());

	//System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");

	if(hplimit<=0)
	{
		System.out.println("We can deploy no honeypots");
		return;
	}


	int slotlimit = hplimit;
	System.out.println("#slotslimit "+ slotlimit);


	/**
	 * create combinations of placestoallocatehp
	 */
	HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
	HashMap<Integer, int[]> hpids = new HashMap<Integer, int[]>();

	System.out.println("*******Slot ids******");
	PlanRecognition.createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
	System.out.println("******HP ids: *******");
	PlanRecognition.createHPCombinations(hpids, hplimit, freehps.size());
	
	
	
	//int tmpconf = comb(totalhp, hpdeploylimit);
	
	
	//int nconf = (n*n)*tmpconf;
	
	
	int totalconf = slotids.size()*hpids.size();
	
	
	System.out.println("Total HP deloyment conf "+ totalconf);
	
	
	/**
	 * dimension need to include hps
	 * fill the costs to 100 for hps to other nodes
	 * the last dimnesions will be for hps
	 */
	
	
	int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
	
	
	
	
	
	
	buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
	
	/*totalconf = 50;
	
	int cnf = 14;
	
	hpdeploymentcost[cnf][1][11][0] = 1;
	hpdeploymentcost[cnf][11][8][0] = 1;
	
	
	hpdeploymentcost[cnf][1][11][0] = 1;
	hpdeploymentcost[cnf][11][9][0] = 1;
	
	
	hpdeploymentcost[cnf][1][11][0] = 1;
	hpdeploymentcost[cnf][11][10][0] = 1;
	
	int cnf2 = 12;
	
	hpdeploymentcost[cnf2][1][11][0] = 1;
	hpdeploymentcost[cnf2][11][8][0] = 1;
	
	
	hpdeploymentcost[cnf2][1][11][0] = 1;
	hpdeploymentcost[cnf2][11][9][0] = 1;
	
	
	hpdeploymentcost[cnf2][1][11][0] = 1;
	hpdeploymentcost[cnf2][11][10][0] = 1;*/
	
	
	
	
	
	
	
	verifyW(hpdeploymentcost);
	
	double[] priors = {0.333333, 0.333333, 0.3333333};
	
	
	long startTime = System.currentTimeMillis();
	
	
	
	
	
	double[] bfsconf = findMinCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
	
	
	
	//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
	 
	
	
	long endTime   =  System.currentTimeMillis();
	long bfstotalTime = endTime - startTime;
	System.out.println("BFS runtime: "+bfstotalTime);
	
	
	
	// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
	 
	
	startTime =  System.currentTimeMillis();
	
	//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
	
	//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
	
	ArrayList<ArrayList<double[]>> paths = Solver.solveDummy2(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
	
	
	//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
	
	
	
	
	endTime   =  System.currentTimeMillis();
	long milptotalTime = endTime - startTime;
	
	
	//printSolutionM(paths);
	
	
	/*System.out.println("#conf: "+totalconf);
	
	System.out.println("BFS conf: "+bfsconf[0]);
	System.out.println("MILP conf: "+milpconf[0]);
	
	
	System.out.println("BFS cost: "+bfsconf[1]);
	System.out.println("MILP cost: "+milpconf[1]);
	
	
	
	
	System.out.println("BFS runtime: "+bfstotalTime);
	System.out.println("MILP runtime: "+milptotalTime);
	*/
	
	
	
	
	//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
	 //printSolution(paths);
	
	
	 
	}


public static void printSolutionM(ArrayList<ArrayList<double[]>> paths, ArrayList<Integer> g) {
	
	
	// double[] winnerconf = {-1.0, -1.0};
	 
		 for(ArrayList<double[]> path: paths)
		 {
			 
			 System.out.println("****attacker "+ paths.indexOf(path)+"*****");

			 
			 int att = paths.indexOf(path);
			 
			 if(path==null)
			 {
				 System.out.println("Didn't find any solution");
			 }
			 else
			 {
				 for(double[] a: path)
				 {
					 /*String sid1 = nodeexpltmapback.get(a[0]).split("-")[0];
				 String sid2 = nodeexpltmapback.get(a[1]).split("-")[0];
				 String ex = String.valueOf(a[2]);*/


					 System.out.println(a[0] +"->"+ a[1] +"("+a[2]+")");
					 
					 if(a[1]==g.get(att))
					 {
						 break;
					 }

					 /*if(winnerconf[0]==-1)
					 {
						 winnerconf[0] = a[3];
						 winnerconf[1] = a[4];
					 }
*/


				 }

			 }
		 }
		 
		 
		 System.out.println();
		 
		// return winnerconf;
		 

	
}


public static void assignAttackerPolicy(ArrayList<ArrayList<double[]>> paths, ArrayList<Integer> g, ArrayList<Attacker> att2) {
	
	
	// double[] winnerconf = {-1.0, -1.0};
	 
		 for(ArrayList<double[]> path: paths)
		 {
			 
			 System.out.println("****attacker "+ paths.indexOf(path)+"*****");

			 
			 int att = paths.indexOf(path);
			 
			 if(path==null)
			 {
				 System.out.println("Didn't find any solution");
			 }
			 else
			 {
				 HashMap<Integer, Integer> pol = new HashMap<Integer, Integer>();
				 
				 for(double[] a: path)
				 {
					 /*String sid1 = nodeexpltmapback.get(a[0]).split("-")[0];
				 String sid2 = nodeexpltmapback.get(a[1]).split("-")[0];
				 String ex = String.valueOf(a[2]);*/


					 System.out.println(a[0] +"->"+ a[1] +"("+a[2]+")");
					 pol.put(pol.size(), (int)a[0]);
					 
					 if(a[1]==g.get(att))
					 {
						 pol.put(pol.size(), (int)a[1]);
						 att2.get(att).fixedpolicy.clear();
						 att2.get(att).fixedpolicy.put(att2.get(att).fixedpolicy.size(), pol);
						 break;
					 }

					 /*if(winnerconf[0]==-1)
					 {
						 winnerconf[0] = a[3];
						 winnerconf[1] = a[4];
					 }
*/


				 }

			 }
		 }
		 
		 
		 System.out.println();
		 
		// return winnerconf;
		 

	
}


private static void printSolution(ArrayList<int[]> paths) {
	
	
	System.out.println("\nPath: ");
	 
	 /*for(int[] a: path)
	 {
		 String sid1 = nodeexpltmapback.get(a[0]).split("-")[0];
		 String sid2 = nodeexpltmapback.get(a[1]).split("-")[0];
		 String ex = String.valueOf(a[2]);
		 
		 
		 System.out.println(sid1 +"->"+ sid2 +"("+ex+")");
		 
		 
		 
	 }*/
	 
	 int winnerconf = -1;
	 
	// for(ArrayList<int[]> path: paths)
	 {
		 
		// System.out.println("****attacker "+ paths.indexOf(path)+"*****");

		// if(path==null)
		 {
			 //System.out.println("Didn't find any solution");
		 }
		// else
		 {
			 for(int[] a: paths)
			 {
				 /*String sid1 = nodeexpltmapback.get(a[0]).split("-")[0];
			 String sid2 = nodeexpltmapback.get(a[1]).split("-")[0];
			 String ex = String.valueOf(a[2]);*/


				 System.out.println(a[0] +"->"+ a[1] +"("+a[2]+")" + "("+a[3]+")");

				 if(winnerconf==-1)
				 {
					 winnerconf = a[3];
				 }



			 }

		 }
	 }
	 
	 
	 System.out.println();
	 
	
}


private static double[] findMinCostPath(int[][][][] hpdeploymentcost, ArrayList<Integer> goals, int totalconf, int startnode, int chosenattacker, double[] priors) throws Exception {
	
	
	double mincost = Double.POSITIVE_INFINITY;
	int minconf = -1;
	
	ArrayList<Integer> allmincostcofs = new ArrayList<Integer>();
	
	
	
	
	ArrayList<ArrayList<Integer>> minpath = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<ArrayList<Integer>>> minpaths = new ArrayList<ArrayList<ArrayList<Integer>>>();
	
	for(int c=0; c<totalconf; c++)
	{
		int[][][] confcost = hpdeploymentcost[c];
		
		/*mincost = Double.POSITIVE_INFINITY;
		minconf = -1;*/
		
		//System.out.println(confcost[0][1][0]);
		
		if(c==9)
		{
			//System.out.println("conf: "+c);
		}
		
		double sumcost = 0;
		ArrayList<ArrayList<Integer>> tmppaths = new ArrayList<ArrayList<Integer>>();
		for(int a=0; a<3; a++)
		{

			ArrayList<Integer> tmpminpath = new ArrayList<Integer>();

			double tmpcost = findMinCostP(tmpminpath, confcost, startnode, goals, a);
			
			sumcost += (tmpcost*priors[a]);
			tmppaths.add(tmpminpath);

			//System.out.println("conf: "+c+", cost "+ tmpcost);


			
		}
		
		if(sumcost<mincost)
		{
			allmincostcofs.clear();
			mincost = sumcost;
			minconf = c;
			allmincostcofs.add(minconf);
			minpath = tmppaths;
			System.out.println("mincost path: ");
			
			
			for(ArrayList<Integer> path: minpath)
			{
				for(int p: path)
				{
					System.out.print(p+"->");
				}


				System.out.print("\n");
			}
			minpaths.clear();
			minpaths.add(minpath);
		}
		else if(sumcost==mincost)
		{
			mincost = sumcost;
			minconf = c;
			minpath = tmppaths;
			
			
			
			allmincostcofs.add(minconf);
			
			System.out.println("mincost path: ");
			
			
			for(ArrayList<Integer> path: minpath)
			{
				for(int p: path)
				{
					System.out.print(p+"->");
				}


				System.out.print("\n");
			}
			minpaths.add(minpath);
		}
		
		/*if(sumcost==mincost)
		{
			mincost = sumcost;
			minconf = c;
			minpath = tmppaths;
			System.out.println("mincost path: ");
			
			
			for(ArrayList<Integer> path: minpath)
			{
				for(int p: path)
				{
					System.out.print(p+"->");
				}


				System.out.print("\n");
			}
		}*/
		
		//System.out.println("mincost "+ mincost + ", minconf "+ minconf);
		
		
		
	}
	
	System.out.println("mincost "+ mincost + ", minconf "+ minconf);
	
	System.out.println("mincost path: ");
	
	
	int ind = 0;
	
	for(ArrayList<ArrayList<Integer>> minpa: minpaths)
	{

		//int ind = minpaths.indexOf(minpa);
		
		System.out.println("mincost conf: "+ allmincostcofs.get(ind));
		System.out.println("mincost path: ");
		for(ArrayList<Integer> path: minpa)
		{
			for(int p: path)
			{
				System.out.print(p+"->");
			}


			System.out.print("\n");
		}
		ind++;
	}



	return new double[] {minconf, mincost};


	
}



private static double[] findMaxCostPath(int[][][][] hpdeploymentcost, ArrayList<Integer> goals, int totalconf, int startnode, int chosenattacker, double[] priors) throws Exception {
	
	
	double mincost = Double.POSITIVE_INFINITY;
	int minconf = -1;
	ArrayList<ArrayList<Integer>> minpath = new ArrayList<ArrayList<Integer>>();
	
	for(int c=0; c<totalconf; c++)
	{
		int[][][] confcost = hpdeploymentcost[c];
		
		/*mincost = Double.POSITIVE_INFINITY;
		minconf = -1;*/
		
		//System.out.println(confcost[0][1][0]);
		
		if(c==9)
		{
			//System.out.println("conf: "+c);
		}
		
		double sumcost = 0;
		ArrayList<ArrayList<Integer>> tmppaths = new ArrayList<ArrayList<Integer>>();
		for(int a=0; a<3; a++)
		{

			ArrayList<Integer> tmpminpath = new ArrayList<Integer>();

			double tmpcost = findMaxCostP(tmpminpath, confcost, startnode, goals, a);
			
			sumcost += (tmpcost*priors[a]);
			tmppaths.add(tmpminpath);

			//System.out.println("conf: "+c+", cost "+ tmpcost);


			
		}
		
		if(sumcost<mincost)
		{
			mincost = sumcost;
			minconf = c;
			minpath = tmppaths;
			System.out.println("mincost path: ");
			
			
			for(ArrayList<Integer> path: minpath)
			{
				for(int p: path)
				{
					System.out.print(p+"->");
				}


				System.out.print("\n");
			}
		}
		
		/*if(sumcost==mincost)
		{
			mincost = sumcost;
			minconf = c;
			minpath = tmppaths;
			System.out.println("mincost path: ");
			
			
			for(ArrayList<Integer> path: minpath)
			{
				for(int p: path)
				{
					System.out.print(p+"->");
				}


				System.out.print("\n");
			}
		}*/
		
		//System.out.println("mincost "+ mincost + ", minconf "+ minconf);
		
		
		
	}
	
	System.out.println("mincost "+ mincost + ", minconf "+ minconf);
	
	System.out.println("mincost path: ");
	
	
	for(ArrayList<Integer> path: minpath)
	{
		for(int p: path)
		{
			System.out.print(p+"->");
		}


		System.out.print("\n");
	}
	

	
	return new double[] {minconf, mincost};
	
	
	
}


public static void traversePolicy(Node node, ArrayList<Integer> path) {




	if(node == null)
		return;

	traversePolicy(node.parent, path);
	System.out.print(node.id+"("+node.currentcost+")"+"->");
	path.add(node.id);


}


private static double findMinCostP(ArrayList<Integer> path, int[][][] confcost, int startnode, ArrayList<Integer> goals, int attacker) throws Exception {
	
	
	double mincost = Double.POSITIVE_INFINITY;
	
	
	
	
	
	

	Queue<Node> fringequeue = new LinkedList<Node>();
	Queue<Integer> closed = new LinkedList<Integer>();

	Node start = new Node(startnode);
	

	
	

	fringequeue.add(start);

	

	while(!fringequeue.isEmpty())
	{
		Node node = fringequeue.poll();
		if(!closed.contains(node.id))
		{
			closed.add(node.id);
		}
		
		if(node.id==goals.get(attacker))
		{


			if(node.currentcost < mincost)
			{
				
				mincost = node.currentcost;
				//maxgoalnode = node;
				
				ArrayList<Integer> tmppath = new ArrayList<Integer>();
				traversePolicy(node, tmppath);
				//System.out.println();
				
				path.clear();
				
				for(int z: tmppath)
				{
					path.add(z);
				}
				
				
				
				
				
			}
			
			
		}
		else
		{
			for(int j=0; j<confcost[node.id].length; j++)
			{
				for(int k=0; k<confcost[node.id][j].length; k++)
				{
					if(confcost[node.id][j][k] != 100 && confcost[node.id][j][k] != 0)
					{
						Node tmp = new Node(j);
						tmp.currentcost = node.currentcost + confcost[node.id][j][k] ;
						
						if(tmp.currentcost < mincost && !(closed.contains(tmp.id)))
						{
							tmp.parent = node;
							fringequeue.add(tmp);
						}
						
						
					}
					if(confcost[node.id][j][k] == 0)
					{
						throw new Exception("0 cost found");
					}
				}
			}
		}

		


	}
	

	
	
	return mincost;
}


private static double findMaxCostP(ArrayList<Integer> path, int[][][] confcost, int startnode, ArrayList<Integer> goals, int attacker) throws Exception {
	
	
	double maxcost = Double.NEGATIVE_INFINITY;
	
	
	
	
	
	

	Queue<Node> fringequeue = new LinkedList<Node>();
	Queue<Integer> closed = new LinkedList<Integer>();

	Node start = new Node(startnode);
	

	
	

	fringequeue.add(start);

	

	while(!fringequeue.isEmpty())
	{
		Node node = fringequeue.poll();
		if(!closed.contains(node.id))
		{
			closed.add(node.id);
		}
		
		if(node.id==goals.get(attacker))
		{


			if(node.currentcost > maxcost)
			{
				
				maxcost = node.currentcost;
				//maxgoalnode = node;
				
				ArrayList<Integer> tmppath = new ArrayList<Integer>();
				traversePolicy(node, tmppath);
				//System.out.println();
				
				path.clear();
				
				for(int z: tmppath)
				{
					path.add(z);
				}
				
				
				
				
				
			}
			
			
		}
		else
		{
			for(int j=0; j<confcost[node.id].length; j++)
			{
				for(int k=0; k<confcost[node.id][j].length; k++)
				{
					if(confcost[node.id][j][k] != 100 && confcost[node.id][j][k] != 0)
					{
						Node tmp = new Node(j);
						tmp.currentcost = node.currentcost + confcost[node.id][j][k] ;
						
						if(tmp.currentcost > maxcost && !(closed.contains(tmp.id)))
						{
							tmp.parent = node;
							fringequeue.add(tmp);
						}
						
						
					}
					if(confcost[node.id][j][k] == 0)
					{
						throw new Exception("0 cost found");
					}
				}
			}
		}

		


	}
	

	
	
	return maxcost;
}


private static void verifyW(int[][][][] hpdeploymentcost) throws Exception {
	
	
	
	for(int c=0; c<hpdeploymentcost.length; c++)
	{
	
	for(int i=0; i<hpdeploymentcost[c].length; i++)
	{
		for(int j=0; j<hpdeploymentcost[c][i].length; j++)
		{
			for(int k=0; k<hpdeploymentcost[c][i][j].length; k++)
			{
				if(hpdeploymentcost[c][i][j][k] == 0)
				{
					
					throw new Exception("W 0");
				}
				
			}
			
			
		}
	}
	}
	
	
}


public static ArrayList<Integer> findReachableNodes(HashMap<Integer, Node> net, int startnodeid, HashMap<Integer,Exploits> allexploits) {
	
	
	ArrayList<Integer> rn = new ArrayList<Integer>();
	
	
	Queue<Node> fringequeue = new LinkedList<Node>();
	Queue<Integer> closed = new LinkedList<Integer>();

	Node start = new Node(net.get(startnodeid));
	


	fringequeue.add(start);

	

	while(!fringequeue.isEmpty())
	{
		Node node = fringequeue.poll();
		if(!rn.contains(node.id) && node.id != startnodeid)
		{
			//for()
			
			rn.add(node.id);
		}
		closed.add(node.id);
		
			

		Node orignode = net.get(node.id);


		for(Integer nei: orignode.nei.values())
		{
			
			Node neinode = net.get(nei);
			

			
					Node tmp = new Node(neinode);
					
					
					if(!closed.contains(tmp.id))
					{
						neinode.depth = orignode.depth +1;
						fringequeue.add(tmp);
					}


				
			}

		}


	
	return rn;
}


public static ArrayList<Integer> findReachableNodesMILP(HashMap<Integer, Node> net, int startnodeid, HashMap<Integer,Exploits> allexploits, ArrayList<Integer> g) {
	
	
	ArrayList<Integer> rn = new ArrayList<Integer>();
	
	
	Queue<Node> fringequeue = new LinkedList<Node>();
	Queue<Integer> closed = new LinkedList<Integer>();

	Node start = new Node(net.get(startnodeid));
	


	fringequeue.add(start);

	

	while(!fringequeue.isEmpty())
	{
		Node node = fringequeue.poll();
		if(!rn.contains(node.id) && (node.id != startnodeid) && !g.contains(node.id))
		{
			
			rn.add(node.id);
		}
		closed.add(node.id);
		
			

		Node orignode = net.get(node.id);


		for(Integer nei: orignode.nei.values())
		{
			
			Node neinode = net.get(nei);
			

			
					Node tmp = new Node(neinode);
					
					
					if(!closed.contains(tmp.id))
					{
						neinode.depth = orignode.depth +1;
						fringequeue.add(tmp);
					}


				
			}

		}


	
	return rn;
}



public static int comb(int n , int r)
{
	if( r== 0 || n == r)
		return 1;
	else
		return comb(n-1,r)+comb(n-1,r-1);
}


private static void buildCostVar(int[][][][] hpdeploymentcost, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
		int nattakers, int e, int[][][] w, HashMap<Integer,int[]> slotids, HashMap<Integer,int[]> hpids, 
		HashMap<Integer,Node> honeypots, HashMap<Integer,int[]> placestoallocatehp, int hpdeploylimit, ArrayList<Integer> freehps, int hplimit) {
	
	
	/**
	 * 1. for a configuration 
	 * 2. set the usual costs for the oriignal network
	 * 3. then add the extra cost for slots of that config: need slot ids and hps, for the hps use the exploit's costs
	 * 
	 */
	
	int confcount =0; 
	
	for(Integer slt: slotids.keySet())
	{
		int[] slid = slotids.get(slt);
		
		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		for(int i=0; i<slid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(slid[i]);
			slots.put(slots.size(), slot1);
		}
		
		for(Integer hid: hpids.keySet())
		{
			int[] hpid = hpids.get(hid);

			int hp[] = new int[hpid.length];
			for(int i=0; i<hpid.length; i++)
			{
				hp[i] = freehps.get(hpid[i]);
			}
			
			
			
			/**
			 * fillup the usual costs first
			 */
			
			for(int i=0; i<w.length; i++)
			{
				for(int j=0; j<w[i].length; j++)
				{
					for(int k=0; k<w[i][j].length; k++)
					{
						hpdeploymentcost[confcount][i][j][k] = w[i][j][k];
					}
					
				}
				
			}
			
			/**
			 * fill up the costs for the hps to 100
			 */
			
			int start = hpdeploymentcost[confcount].length- hplimit;
			
			for(int i=start; i<hpdeploymentcost[confcount].length; i++)
			{
				for(int j=0; j<hpdeploymentcost[confcount][i].length; j++)
				{
					for(int k=0; k<e; k++)
					{
						hpdeploymentcost[confcount][i][j][k] = 100;
						hpdeploymentcost[confcount][j][i][k] = 100;
					}
					
					
				}
			}
			
			
			
			
			
			/**
			 * create hp map
			 */
			
			HashMap<Integer, Integer> hpmap = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> hpmapback = new HashMap<Integer, Integer>();
			
			int hpidstart = net.size();
			
			for(int h: hp)
			{
				hpmap.put(h, hpidstart);
				hpmapback.put(hpidstart++, h);
			}
			
			
			
			
			/**
			 * now add the new costs for adding hps
			 * 1. for particular slot ids and hpids add the costs
			 */
			
			for(int index: slots.keySet())
			{
			
				int[] placehp = slots.get(index);
				
				int n1 = placehp[0];
				int n2 = placehp[1];
				
				
				
				
				
				
				int hdi =  hp[index];
				
				Node hpnode  = honeypots.get(hdi);
				
				int hd = hpmap.get(hdi);
				
				
				
				//System.out.println("putting hp " +hdi+" between node "+ n1 + " and "+ n2);
				
				/**
				 * inserting hp into slots n1 -> hd , hd -> n2
				 * 1. 
				 */
				
				for(Integer exid: hpnode.exploits.values())
				{
					Exploits ex = exploits.get(exid);
					//int[] explts = hpdeploymentcost[confcount][n1][n2];
					
					/**
					 * only update if there is no existing exploit already
					 */
					
					if(hpdeploymentcost[confcount][n1][hd][exid] == 100)
					{
						hpdeploymentcost[confcount][n1][hd][exid] += (ex.cost-100);
					}
					
					//System.out.println("new value "+ hpdeploymentcost[confcount][n1][hd][exid]);
					
					
				}
				
				Node n2node = net.get(n2);
				
				for(Integer exid: n2node.exploits.values())
				{
					Exploits ex = exploits.get(exid);
					//int[] explts = hpdeploymentcost[confcount][n1][n2];
					
					/**
					 * only update if there is no existing exploit already
					 */
					
					if(hpdeploymentcost[confcount][hd][n2][exid] == 100)
					{
						hpdeploymentcost[confcount][hd][n2][exid] += (ex.cost-100);
					}
					
					//System.out.println("new value "+ hpdeploymentcost[confcount][hd][n2][exid]);
					
					
				}
				
				
				/*if(n1==1 && n2 ==10 && hdi==12)
				{
					System.out.println("putting hp " +hdi+" between node "+ n1 + " and "+ n2);
					
					for(int x: hpdeploymentcost[confcount][1][hd])
					{	
						System.out.println(x );
						if(x != 100)
						{
							//System.out.println(x );
						}
					}
					for(int x: hpdeploymentcost[confcount][hd][10])
					{	
						System.out.println(x );
						if(x != 100)
						{
							//System.out.println(x );
						}
					}
				}*/
				
				
				
				
				
			
			}
			
			
			/*if(confcount==3210)
			{
				System.out.println("x");
				
				//for(int[][][] c: hpdeploymentcost[confcount])
				{
					
					
					for(int x: hpdeploymentcost[confcount][1][12])
					{	
						System.out.println(x );
						if(x != 100)
						{
							System.out.println(x );
						}
					}
					for(int x: hpdeploymentcost[confcount][12][10])
					{	
						System.out.println(x );
						if(x != 100)
						{
							System.out.println(x );
						}
					}
					
					
				}
			}*/
			
			
			confcount++;
			
		}
		
	}
	
	
	
	
	
	
	
}



/**
 * see if incoming edges have cost to honey edge node
 * @param net
 * @param nodeexpltmap
 * @param nodeexpltmapback
 * @param edgecost
 * @param exploits
 * @return
 */
public static int[][][] build3DCostMatrix(HashMap<Integer, Node> net, HashMap<String, Integer> nodeexpltmap,
		HashMap<Integer, String> nodeexpltmapback, HashMap<String, Integer> edgecost,
		HashMap<Integer, Exploits> exploits) {


	int[][][] w = new int[net.size()][net.size()][exploits.size()];


	for(int i=0; i<w.length; i++)
	{
		for(int j=0; j<w[i].length; j++)
		{
			for(int k=0; k<w[i][j].length; k++)
			{
				Node a = net.get(i);
				Node b = net.get(j);


				/**
				 * need to update the condition when the a node in edge a->b will have rules
				 * right now we are allowing every exploits
				 */
				if(a.nei.containsValue(b.id) && b.exploits.containsValue(k))
				{

					Exploits ex2 = exploits.get(k);
					w[i][j][k] = ex2.cost;

				}
				else
				{
					w[i][j][k] = 100;
				}
			}


		}
	}


	//System.out.println(w[0][1][1]);

	return w;
}


private static int[][] buildCostMatrix(HashMap<Integer, Node> net, HashMap<String, Integer> nodeexpltmap,
		HashMap<Integer, String> nodeexpltmapback, HashMap<String, Integer> edgecost, HashMap<Integer,Exploits> exploits) {
	
	
	int count = 0;
	
	
	for(Node node: net.values())
	{

		for(Integer e1: node.exploits.values())
		{

			Exploits ex1 = exploits.get(e1);
			String id1 = node.id+"-"+ex1.id;
			
			nodeexpltmap.put(id1, count);
			nodeexpltmapback.put(count++, id1);

			
			
		}
	}
	
	System.out.println("Total #nodes "+ count);
	
	
	int[][] w = new int[count][count];
	
	
	for(int i=0; i<count; i++)
	{
		for(int j=0; j<count; j++)
		{
			w[i][j] = 100;
		}
	}
	
	
	for(Node node: net.values())
	{

		for(Integer e1: node.exploits.values())
		{

			Exploits ex1 = exploits.get(e1);
			String id1 = node.id+"-"+ex1.id;

			for(Integer neiid: node.nei.values())
			{
				Node neinode = net.get(neiid);

				for(Integer e2: neinode.exploits.values())
				{

					Exploits ex2 = exploits.get(e2);
					String id2 = neinode.id+"-"+ex2.id;
					
					
					edgecost.put(id1+" "+id2, ex2.cost);
					
					int i = nodeexpltmap.get(id1);
					int j = nodeexpltmap.get(id2);
					w[i][j] = ex2.cost;
					//w[j][i] = Integer.MAX_VALUE;
					

				}
			}
		}
	}
	
	
	
	
	System.out.println("Total #nodes "+ count);
	
	
	return w;
}


public static void doFixedExploitPolicyWithDefenseExp1(boolean withdefense, int chosenattacker, int chosenpolicy, boolean minentropy, boolean maxoverlap, boolean expoverlap) throws Exception {




		
		/*boolean withdefense = true;
		
		int chosenattacker = 1;
		int chosenpolicy = 0;
		
		boolean minentropy = false;*/
		
		
		
		boolean mincommonoverlap = !(minentropy);
		
		/*if(minentropy)
		{
			mincommonoverlap = false;
		}
		else
		{
			mincommonoverlap = true;
		}*/
		
		
		boolean samevalhp = false;
		boolean allexphp = true; // if false, this can lead to blocking action, attacker might not have any policy...
		
		
		

		int[] goals = {23, 24, 25, 26};
		int startnode = 0;
		int nnodes = 27;
		int nhoneypots = 8;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		boolean pickfromnet = true;
		
		

		int nexploits = 8;

		

		boolean singlegoal = true;
		boolean singlepath = false;
		int startnodeid = 0;
		int npath = 1;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		Network.constructNetworkWithExploits(net, exploits, nnodes, nexploits);

		
		/**
		 * construct honeypots from real node configurations
		 * Imagine that attacker is naive
		 */
		Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc, samevalhp, allexphp, net, pickfromnet, goals);

		System.out.println("Network construction... \ndone");
		
		
		System.out.println("Attacker construction... \ndone");

		PlanRecognition.printNetworkWithExploits(net);
		System.out.println();
		
		System.out.println("*****************Honeypots******************");
		PlanRecognition.printNetworkWithExploits(honeypots);


		if(singlegoal)
		{
			PlanRecognition.constructAttackersWithExploitsSingleGoal(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();
		
		

		
		
		/*PlanRecognition.playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap);
*/




	}

}
