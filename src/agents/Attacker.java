package agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


import network.Exploits;
import network.Node;
import planrecognition.Logger;

public class Attacker {

	public int id;
	public HashMap<Integer, Integer> exploits = new HashMap<Integer, Integer>();
	public HashMap<Integer, HashMap<Integer, Integer>> fixedpolicy = new HashMap<Integer, HashMap<Integer, Integer>>();
	public HashMap<Integer, HashMap<Integer, int[]>> fixedexploitpolicy = new HashMap<Integer, HashMap<Integer, int[]>>();
	
	public HashMap<Integer, Integer> goals = new HashMap<Integer, Integer>();

	
	public Attacker() {
		super();
		
	}
	

	public Attacker(int id) {
		super();
		this.id = id;
	}

	public void addExploits(int[] exploits)
	{
		int count = this.exploits.size();
		for(int id: exploits)
		{
			this.exploits.put(count, id);
			count++;
		}
	}

	public void addPolicy(int[] actions)
	{
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();

		int count = 0;
		for(int a: actions)
		{
			tmp.put(count++, a);

		}
		this.fixedpolicy.put(this.fixedpolicy.size(), tmp);

	}

	public void findFixedPolifyBFS(HashMap<Integer,Node> net, HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) {

		Node start = new Node(net.get(0));

		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		fringequeue.add(start);

		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);

			if(node.id==goal)
			{
				HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
				traversePolicy(node, path);
				this.fixedpolicy.put(this.fixedpolicy.size(), path);


				int numberofpaths = nPaths(this.fixedpolicy, goal);

				if(numberofpaths>npath)
				{
					break;
				}

				if(singlepath)
				{
					break;
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						tmp.parent = node;
						//if(!closed.contains(tmp.id))
						{
							fringequeue.add(tmp);
						}

					}
				}

			}



		}


	}

	public double findFixedPolicyMaxRewardMinCost(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		
		
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentreward = start.value - start.cost - exploitcost;

		fringequeue.add(start);

		double maxreward = Double.NEGATIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentreward > maxreward)
				{
					
					maxreward = node.currentreward;
					//maxgoalnode = node;
					
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					
					if(singlepath)
					{
						this.fixedpolicy.clear();
					}
					if(!this.fixedpolicy.isEmpty())
					{
						
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					
				}
				else if(node.currentreward==maxreward && singlepath==false)
				{
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					if(!this.fixedpolicy.isEmpty())
					{
						//this.fixedpolicy.clear();;
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentreward = node.currentreward + tmp.value - tmp.cost - exploitcost;
						tmp.parent = node;
						fringequeue.add(tmp);


					}
				}

			}



		}
		return maxreward;


	}
	
	
	public double findFixedPolicyMinCost(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		
		
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentcost +=  exploitcost;

		fringequeue.add(start);

		double mincost = Double.POSITIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentcost < mincost)
				{
					
					mincost = node.currentcost;
					//maxgoalnode = node;
					
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					
					if(singlepath)
					{
						this.fixedpolicy.clear();
					}
					if(!this.fixedpolicy.isEmpty())
					{
						
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					
				}
				else if(node.currentcost==mincost && singlepath==false)
				{
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					/*if(!this.fixedpolicy.isEmpty())
					{
						//this.fixedpolicy.clear();;
					}*/
					this.fixedpolicy.put(this.fixedpolicy.size(), path);
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentcost += /*node.currentreward + tmp.value - tmp.cost -*/node.currentcost + exploitcost;
						tmp.parent = node;
						fringequeue.add(tmp);


					}
				}

			}



		}
		return mincost;


	}
	
	
	
	
	
	public double findFixedPolicyShortestPath(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		
		
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentreward = start.value - start.cost - exploitcost;

		fringequeue.add(start);

		double mindist = Double.POSITIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);
			if(node.id==goal)
			{


				if(node.currentdistance < mindist)
				{
					
					mindist = node.currentdistance;
					//maxgoalnode = node;
					
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					
					if(singlepath)
					{
						this.fixedpolicy.clear();
					}
					if(!this.fixedpolicy.isEmpty())
					{
						
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					
				}
				else if(node.currentdistance==mindist && singlepath==false)
				{
					HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					if(!this.fixedpolicy.isEmpty())
					{
						//this.fixedpolicy.clear();;
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentreward = node.currentreward + tmp.value - tmp.cost - exploitcost;
						tmp.parent = node;
						tmp.currentdistance = node.currentdistance + 1;
						fringequeue.add(tmp);


					}
				}

			}



		}
		return mindist;


	}
	
	
	public double findFixedExploitPolicyMaxRewardMinCost(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		
		
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		double[] exploitcost = maxUtilityWithExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentreward = start.value - start.cost - exploitcost[1];
		start.usedexploit = (int)exploitcost[0];

		fringequeue.add(start);

		double maxreward = Double.NEGATIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentreward > maxreward)
				{
					
					maxreward = node.currentreward;
					//maxgoalnode = node;
					
					HashMap<Integer, int[]> path = new HashMap<Integer, int[]>();
					traversePolicyExploit(node, path);
					System.out.println();
					
					if(singlepath)
					{
						this.fixedexploitpolicy.clear();
					}
					if(!this.fixedexploitpolicy.isEmpty())
					{
						
					}
					this.fixedexploitpolicy.put(this.fixedexploitpolicy.size(), path);


					int numberofpaths = nExpltPaths(this.fixedexploitpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					
				}
				else if(node.currentreward==maxreward && singlepath==false)
				{
					HashMap<Integer, int[]> path = new HashMap<Integer, int[]>();
					traversePolicyExploit(node, path);
					System.out.println();
					if(!this.fixedexploitpolicy.isEmpty())
					{
						//this.fixedpolicy.clear();;
					}
					this.fixedexploitpolicy.put(this.fixedexploitpolicy.size(), path);
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(int neibor[]: orignode.neiwithexploits.values())
			{
				
				int nei = neibor[0];
				int neinodeexploit = neibor[1];
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				//for(Integer neinodeexploit: neinode.exploits.values())
				
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						//exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						int expcost = allexploits.get(neinodeexploit).cost;
						tmp.currentreward = node.currentreward + tmp.value - tmp.cost - expcost;
						tmp.parent = node;
						tmp.usedexploit = neinodeexploit;
						fringequeue.add(tmp);


					}
				

			}



		}
		return maxreward;


	}
	
	
	
	public HashMap<Integer, HashMap<Integer, Integer>> findOneFixedPolifyMaxReward(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		HashMap<Integer, HashMap<Integer, Integer>> paths = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
		
		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentreward = start.value - start.cost - exploitcost;

		fringequeue.add(start);

		double maxreward = Double.NEGATIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			
			//System.out.println("polled node  "+ node.id);
			
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentreward > maxreward)
				{
					
					maxreward = node.currentreward;
					//maxgoalnode = node;
					
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					
					
					
					if(singlepath)
					{
						paths.clear();
					}
					paths.put(paths.size(), path);
					
					/*if(!this.fixedpolicy.isEmpty())
					{
						this.fixedpolicy.remove(0);
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					if(singlepath)
					{
						//break;
					}*/
				}
				else if(node.currentreward==maxreward && singlepath==false)
				{
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					paths.put(paths.size(), path);
				}
				//break;
			}

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentreward = node.currentreward + tmp.value - tmp.cost - exploitcost;
						tmp.parent = node;
						fringequeue.add(tmp);


					}
				}

			}



		}
		
		
		return paths;


	}
	
	
	
	public HashMap<Integer, HashMap<Integer, Integer>> findPolicyShortestPath(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath) 
	{

		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		HashMap<Integer, HashMap<Integer, Integer>> paths = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
		
		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentreward = start.value - start.cost - exploitcost;

		fringequeue.add(start);

		double mindist = Double.POSITIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			
			//System.out.println("polled node  "+ node.id);
			
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentdistance < mindist)
				{
					
					mindist = node.currentdistance;
					//maxgoalnode = node;
					
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					//System.out.println();
					if(singlepath)
					{
						paths.clear();
					}
					paths.put(paths.size(), path);
					
					/*if(!this.fixedpolicy.isEmpty())
					{
						this.fixedpolicy.remove(0);
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					if(singlepath)
					{
						//break;
					}*/
				}
				else if(node.currentdistance==mindist && singlepath==false)
				{
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					paths.put(paths.size(), path);
				}
				//break;
			}	

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentreward = node.currentreward + tmp.value - tmp.cost - exploitcost;
						tmp.parent = node;
						tmp.currentdistance = node.currentdistance + 1;
						fringequeue.add(tmp);


					}
				}

			}



		}
		
		
		return paths;


	}
	
	
	public HashMap<Integer, HashMap<Integer, Integer>> findPolicyMinCostPath(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, boolean singlepath, int npath, HashMap<Integer,Double> costs, int attid) 
	{

		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		HashMap<Integer, HashMap<Integer, Integer>> paths = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
		
		double exploitcost = minCostExploit(net.get(startnodeid), this.exploits, allexploits);
		start.currentcost = /*start.value - start.cost -*/ exploitcost;

		fringequeue.add(start);

		double mincost = Double.POSITIVE_INFINITY;
		//Node maxgoalnode = null;


		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			
			//System.out.println("polled node  "+ node.id);
			
			closed.add(node.id);

			if(node.id==goal)
			{


				if(node.currentcost < mincost)
				{
					
					
					mincost = node.currentcost;
					
					costs.put(attid, mincost);
					//maxgoalnode = node;
					
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					System.out.println();
					//if(singlepath)
					{
						paths.clear();
					}
					paths.put(paths.size(), path);
					
					/*if(!this.fixedpolicy.isEmpty())
					{
						this.fixedpolicy.remove(0);
					}
					this.fixedpolicy.put(this.fixedpolicy.size(), path);


					int numberofpaths = nPaths(this.fixedpolicy, goal);

					if(numberofpaths>npath)
					{
						//break;
					}

					if(singlepath)
					{
						//break;
					}*/
				}
				else if(node.currentcost==mincost && singlepath==false)
				{
					path = new HashMap<Integer, Integer>();
					traversePolicy(node, path);
					System.out.println();
					paths.put(paths.size(), path);
				}
				//break;
			}	

			Node orignode = net.get(node.id);


			for(Integer nei: orignode.nei.values())
			{
				//canaccess = false;
				//Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					//Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						exploitcost = minCostExploit(neinode, this.exploits, allexploits);
						tmp.currentcost += /*node.currentreward + tmp.value - tmp.cost -*/node.currentcost + exploitcost;
						tmp.parent = node;
						tmp.currentdistance = node.currentdistance + 1;
						fringequeue.add(tmp);


					}
				}

			}



		}
		
		
		return paths;


	}




	public static double minCostExploit(Node start, HashMap<Integer, Integer> attackerexploits, HashMap<Integer,Exploits> allexploits) {
		
		double mincost = Double.POSITIVE_INFINITY;
		
		for(Integer eid: attackerexploits.values())
		{
			
			if(start.exploits.containsValue(eid))
			{
			
				Exploits exp = allexploits.get(eid);
				if(mincost>exp.cost)
				{
					mincost = exp.cost;
				}
			}
		}
		
		
		
		
		
		return mincost;
	}
	
	
private double[] maxUtilityWithExploit(Node start, HashMap<Integer, Integer> attackerexploits, HashMap<Integer,Exploits> allexploits) {
		
		double mincost = Double.POSITIVE_INFINITY;
		double minex = -1;
		
		for(Integer eid: attackerexploits.values())
		{
			
			if(start.exploits.containsValue(eid))
			{
			
				Exploits exp = allexploits.get(eid);
				if(mincost>exp.cost)
				{
					mincost = exp.cost;
					minex = exp.id;
				}
			}
		}
		
		
		
		
		
		return new double[] {minex, mincost};
	}

	private int nPaths(HashMap<Integer, HashMap<Integer, Integer>> fixedpolicy, int goal) {


		this.removeDuplicatePolicies();

		int count = 0;

		for(HashMap<Integer, Integer> policy: fixedpolicy.values())
		{
			if(policy.get(policy.size()-1).equals(goal))
			{
				count++;
			}
		}


		return count;
	}
	
	
	private int nExpltPaths(HashMap<Integer, HashMap<Integer, int[]>> fixedpolicy, int goal) {


		this.removeDuplicatePolicies();

		int count = 0;

		for(HashMap<Integer, int[]> policy: fixedpolicy.values())
		{
			int[] p = policy.get(policy.size()-1);
			if(p[0]==goal)
			{
				count++;
			}
		}


		return count;
	}

	private void traversePolicy(Node node, HashMap<Integer,Integer> path) {




		if(node == null)
			return;

		traversePolicy(node.parent, path);
		//System.out.print(node.id+"("+node.currentcost+")"+"->");
		path.put(path.size(), node.id);


	}
	
	private void traversePolicyExploit(Node node, HashMap<Integer,int[]> path) {

		if(node == null)
			return;

		traversePolicyExploit(node.parent, path);
		//System.out.print(node.id+"("+node.currentreward+")"+"->");
		int[] p = {node.id, node.usedexploit};
		path.put(path.size(), p);


	}
	

	/**
	 * using simple traversing algo to find a fixed policy
	 * @param net
	 * @param allexploits2 
	 */
	public void findFixedPolify(HashMap<Integer,Node> net, HashMap<Integer,Exploits> allexploits) {

		Node start = net.get(0);
		Logger.logit("Start node "+ start.id+"\n");

		Node tmpnode = start;

		HashMap<Integer, Integer>  seq = new HashMap<Integer, Integer>();

		while(true)
		{
			Logger.logit("Current node "+ tmpnode.id+"\n");
			Logger.logit("Node"+ tmpnode.id + " exploits: ");
			for(Integer et: tmpnode.exploits.values())
			{
				Logger.logit(et+" ");
			}
			Logger.logit("\n");

			seq.put(this.fixedpolicy.size(), tmpnode.id);

			if(this.goals.containsValue(tmpnode.id))
			{

				Logger.logit("found goal "+ tmpnode.id);
				break;

			}


			Logger.logit("Successors: \n");

			/**
			 * how does the attacker decides?
			 * First check for which nodes he has the exploits
			 */
			double mincost = Double.POSITIVE_INFINITY;
			int minnode = -1;
			int minexp = -1;
			//boolean canaccess = false;



			for(Integer nei: tmpnode.nei.values())
			{



				//canaccess = false;
				Logger.logit(" Node "+ nei +" exploits: \n");

				Node neinode = net.get(nei);

				for(Integer neinodeexploit: neinode.exploits.values())
				{
					Logger.logit("exploit:"+neinodeexploit+"\n");
					if(this.exploits.containsValue(neinodeexploit))
					{
						//canaccess = true;
						Exploits exp = allexploits.get(neinodeexploit);
						Logger.logit("cost:"+exp.cost+"\n");
						//Logger.logit("cost:"+exp.cost+"\n");
						//double tmpcpst = exp.cost;
						if(mincost>exp.cost)
						{
							mincost = exp.cost;
							minnode = nei;
							minexp = neinodeexploit;
							Logger.logit("min exploit :"+exp.id+", cost "+exp.cost+"\n");
						}
					}
				}
				if(this.goals.containsValue(nei))
				{
					break;
				}
			}

			if((minnode != -1) && (minexp != -1))
			{
				Logger.logit("Next chosen node "+ minnode+", using exploit" +minexp +"\n");
				tmpnode = net.get(minnode);
			}
			else
			{

			}


		}
		this.fixedpolicy.put(this.fixedpolicy.size(), seq);


	}

	public void removeDuplicatePolicies() {

		HashMap<Integer, HashMap<Integer, Integer>> newfixpolicy = new HashMap<Integer, HashMap<Integer, Integer>>();


		for(int pid: this.fixedpolicy.keySet())
		{
			HashMap<Integer, Integer> policy = this.fixedpolicy.get(pid);


			boolean found = false;

			for(int pid2: newfixpolicy.keySet())
			{

				HashMap<Integer, Integer> policy2 = newfixpolicy.get(pid2);

				if(policy.equals(policy2))
				{
					found = true;
					break;
				}

			}
			if(!found)
			{
				newfixpolicy.put(newfixpolicy.size(), policy);
			}

		}

		this.fixedpolicy = newfixpolicy;




		//System.out.println("hi");

	}
	
	
	public void removeDuplicateExpltPolicies() {

		HashMap<Integer, HashMap<Integer, int[]>> newfixpolicy = new HashMap<Integer, HashMap<Integer, int[]>>();


		for(int pid: this.fixedexploitpolicy.keySet())
		{
			HashMap<Integer, int[]> policy = this.fixedexploitpolicy.get(pid);


			boolean found = false;

			for(int pid2: newfixpolicy.keySet())
			{

				HashMap<Integer, int[]> policy2 = newfixpolicy.get(pid2);

				
				boolean isequal = isEqual(policy, policy2);
				
				if(isequal)
				{
					found = true;
					break;
				}

			}
			if(!found)
			{
				newfixpolicy.put(newfixpolicy.size(), policy);
			}

		}

		this.fixedexploitpolicy = newfixpolicy;




		//System.out.println("hi");

	}


	private boolean isEqual(HashMap<Integer, int[]> policy, HashMap<Integer, int[]> policy2) {
		
		if(policy.size() == policy2.size())
		{
			boolean ok = true;
			for(int[] p: policy.values())
			{
				if(!policy2.containsValue(p))
				{
					ok= false;
					break;
				}
			}
			if(ok)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	
	
	





}
