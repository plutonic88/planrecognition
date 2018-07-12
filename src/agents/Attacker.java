package agents;

import java.util.HashMap;

public class Attacker {
	
	public int id;
	public HashMap<Integer, Integer> exploits = new HashMap<Integer, Integer>();
	public HashMap<Integer, HashMap<Integer, Integer>> fixedpolicy = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	
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
	
	
	
	
	
}
