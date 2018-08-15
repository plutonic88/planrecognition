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
					System.out.println();
					
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
					System.out.println();
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



	private double minCostExploit(Node start, HashMap<Integer, Integer> attackerexploits, HashMap<Integer,Exploits> allexploits) {
		
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

	private void traversePolicy(Node node, HashMap<Integer,Integer> path) {




		if(node == null)
			return;

		traversePolicy(node.parent, path);
		//System.out.print(node.id+"("+node.currentreward+")"+"->");
		path.put(path.size(), node.id);


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
	
	
	





}
