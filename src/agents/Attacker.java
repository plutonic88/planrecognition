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
	
	public void addPolicy(int goal, int[] actions)
	{
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
		
		int count = 0;
		for(int a: actions)
		{
			tmp.put(count++, a);
			
		}
		this.fixedpolicy.put(goal, tmp);
		
	}
	
	public void findFixedPolifyBFS(HashMap<Integer,Node> net, HashMap<Integer,Exploits> allexploits) {
		
		Node start = new Node(net.get(0));
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		
		fringequeue.add(start);
		
		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			
			if(this.goals.containsValue(node.id))
			{
				HashMap<Integer, Integer> path = new HashMap<Integer, Integer>();
				addToFixedPolicy(node, path);
				this.fixedpolicy.put(this.fixedpolicy.size(), path);
				break;
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
						fringequeue.add(tmp);
						
					}
				}
				
			}
				
			
			
		}
		
		
	}
	
	

	private void addToFixedPolicy(Node node, HashMap<Integer,Integer> path) {
		
		
		
		
		if(node == null)
			return;
		
		addToFixedPolicy(node.parent, path);
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
	
	
	
	
	
}
