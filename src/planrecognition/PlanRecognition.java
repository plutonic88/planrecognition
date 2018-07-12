package planrecognition;

import java.util.HashMap;

import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;

public class PlanRecognition {

	public static void doExp1() {
		
		
		
		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();
		
		
		Network.constructNetwork(net, exploits);
		
		System.out.println("Network construction... \ndone");
		
		
		
		PlanRecognition.constructAttackers(attackers);
		
		System.out.println("Attacker construction... \ndone");
		
		printNetwork(net);
		System.out.println();
		printAttackers(attackers);
		
		
		
		
		
	}

	private static void printAttackers(HashMap<Integer, Attacker> attackers) {
		
		for(Integer aid: attackers.keySet())
		{
			Attacker a = attackers.get(aid);
			System.out.println("******************** Attacker "+a.id+ " ***********************");
			Logger.logit("******************** Attacker "+a.id+ " ***********************"+"\n");
			
			
			
			System.out.print("exploits: ");
			Logger.logit("exploits: ");
			for(Integer exp: a.exploits.values())
			{
				System.out.print(exp+" ");
				Logger.logit(exp+" ");
			}
			System.out.println();
			Logger.logit(" "+"\n");
			
			System.out.print("policy: ");
			Logger.logit("policy: ");
			for(HashMap<Integer, Integer> p: a.fixedpolicy.values())
			{
				
				for(Integer s: p.values())
				{
					System.out.print(s+" ");
					Logger.logit(s+" ");
				}
				System.out.println();
				Logger.logit(" "+"\n");
				
			}
			System.out.println();
			Logger.logit(" "+"\n");
			
			
			
		}
		
	}

	

	private static void printNetwork(HashMap<Integer, Node> net) {
		
		
		for(Integer nodeid: net.keySet())
		{
			Node n = net.get(nodeid);
			System.out.println("******************** Node "+n.id+ " ***********************");
			Logger.logit("******************** Node "+n.id+ " ***********************\n");
			System.out.println("value: "+ n.value);
			Logger.logit("value: "+ n.value+"\n");
			System.out.println("cost: "+ n.cost);
			Logger.logit("cost: "+ n.cost+"\n");
			
			System.out.print("neighbors: ");
			Logger.logit("neighbors: ");
			for(Integer nei: n.nei.values())
			{
				System.out.print(nei+" ");
				Logger.logit(nei+" ");
			}
			System.out.println();
			Logger.logit(" "+"\n");
			
			System.out.print("exploits: "+"\n");
			Logger.logit("exploits: ");
			for(Integer exp: n.exploits.values())
			{
				System.out.print(exp+" ");
				Logger.logit(exp+" ");
			}
			System.out.println();
			Logger.logit(" "+"\n");
			
		}
		
		
	}

	public static void constructAttackers(HashMap<Integer, Attacker> attackers) {
		
		int id = 0;
		
		Attacker a0  = new Attacker(id++);
		a0.addExploits(new int[] {0, 4});
		a0.addPolicy(26, new int[] {0, 2, 5, 9, 15, 21, 26});
		
		
		Attacker a1  = new Attacker(id++);
		a1.addExploits(new int[] {0, 1});
		a1.addPolicy(23, new int[] {0, 2, 5, 9, 14, 19, 23});
		
		Attacker a2  = new Attacker(id++);
		a2.addExploits(new int[] {2, 3});
		a2.addPolicy(25, new int[] {0, 2, 5, 10, 15, 20, 25});
		
		
		Attacker a3  = new Attacker(id++);
		a3.addExploits(new int[] {0, 2, 4});
		a3.addPolicy(24, new int[] {0, 1, 3, 8, 14, 19, 24});
		
		attackers.put(0, a0);
		attackers.put(1, a1);
		attackers.put(2, a2);
		attackers.put(3, a3);
		
		
		
		
		
	}

}
