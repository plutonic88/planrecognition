package network;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	
	public int id;
	public int value;
	public int cost;
	public HashMap<Integer, Integer> nei = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> exploits = new HashMap<Integer, Integer>(); 
	
	//initially allow every exploits
	public ArrayList<Exploits> allowedtoexecute;
	
	
	
	
	public Node() {
		super();
	}




	public Node(int id, int value, int cost) {
		super();
		this.id = id;
		this.value = value;
		this.cost = cost;
	}
	
	public void addNeighbors(int[] nodeids)
	{
		int count = this.nei.size();
		for(int id: nodeids)
		{
			this.nei.put(count, id);
			count++;
		}
		
	}
	
	
	public void addExploits(int[] exploits)
	{
		int count = this.exploits.size();
		for(int id: exploits)
		{
			this.nei.put(count, id);
			count++;
		}
		
	}
	
	
	
	

}
