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
		
		
		
		int goals[] = PlanRecognition.constructAttackers(attackers);
		
		System.out.println("Attacker construction... \ndone");
		
		printNetwork(net);
		System.out.println();
		printAttackers(attackers);
		
		
		playGame(net, exploits, attackers, goals);
		
		
		
		
		
	}

	private static void playGame(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals) {
		
		
		/**
		 * choose an attacker
		 */
		
		int chisenaid = 3;
		
		Attacker chosenatt = attackers.get(chisenaid);
		System.out.println("chosen attacker "+ chosenatt.id);
		Logger.logit("Chosen attacker is "+ chosenatt.id);
		HashMap<Integer, Integer> policy = null;
		
		for(HashMap<Integer, Integer> p: chosenatt.fixedpolicy.values())
		{
			policy = p;
			break;
		}
		
		
		
		
		
		boolean goalachieved = false;
		Node node = net.get(0);
		int round = 0;
		
		
		double pr = 1.0/attackers.size();
		
		double priors[] = new double[attackers.size()];
		
		//HashMap<Integer, Integer[]> policies = new HashMap<Integer, Integer[]>();
		
		for(int i=0; i<attackers.size(); i++)
		{
			priors[i] = 1/pr;
			
		}
		
		HashMap<Integer, Integer> oactions = new HashMap<Integer, Integer>();
		
		while(true)
		{
			Logger.logit("****** round "+round+" ***************");
			int observedaction = policy.get(round);
			oactions.put(round, observedaction);
			Logger.logit("Observed action "+ observedaction);
			
			double posterior[] = new double[attackers.size()];
			
			
			
			Logger.logit("Computing Posterior probs...\n");
			
			System.out.println("round "+ round + " observed action "+ observedaction);
			
			
			posterior = computePosterior(oactions, attackers, net, priors, goals);
			
			for(int i=0; i<attackers.size(); i++)
			{
				System.out.println(" t "+ i + " posterior "+ posterior[i]);
				priors[i] = posterior[i];
			}
			
			
			round++;
			
			if(observedaction==goals[chisenaid])
			{
				break;
			}
			
			
		}
		
		
		
		
	}

	private static double[] computePosterior(HashMap<Integer, Integer> observedactions,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net, double[] priors, int[] goals) {
		
		
		
		
		double[] posteriors = new double[attackers.size()];
		double[] likelihoods = new double[attackers.size()];
		double[] observationsgiventype = new double[attackers.size()];
		double totalobservations = 0;
		double[] probobservations = new double[attackers.size()];
		/**
		 *  compute in how many observations the sequence was observed
		 */
		
		Logger.logit("\n\nComputing posteriors...\n\n");
		
		for(Integer attindex: attackers.keySet())
		{
			Attacker att = attackers.get(attindex);
			
			Logger.logit("**********Attacker index "+ attindex +"*******\n");
			Logger.logit("Attacker type "+ att.id +"\n");
			Logger.logit("policy : ");
			HashMap<Integer, Integer> policy = att.fixedpolicy.get(goals[att.id]);
			for(int fp: policy.values())
			{
				Logger.logit(fp+" ");
			}
			Logger.logit("\nobserved actions : ");
			for(int oa: observedactions.values())
			{
				Logger.logit(oa+" ");
			}
			Logger.logit("\n");
			boolean matches = true;
			for(Integer round: observedactions.keySet())
			{
				int oa = observedactions.get(round);
				int fp = policy.get(round);
				if(oa != fp)
				{
					Logger.logit("does not matches...\n");
					matches = false;
					break;
				}
						
			}
			if(matches)
			{
				Logger.logit("matches...\n");

				totalobservations++;
				observationsgiventype[attindex]++;
				
				Logger.logit("total observations "+ totalobservations + "\n");
				Logger.logit("observ t0 "+ observationsgiventype[0] + " observ t1 "+observationsgiventype[1]+" observ t2 "+observationsgiventype[2]
						+" observ t3 "+observationsgiventype[3]+"\n");
			}
		}
		
		
		double sumprobtotalobservations = 0;
		Logger.logit("\n\nComputing likelihoods...\n\n");
		for(Attacker att: attackers.values())
		{
			Logger.logit("*******Attacker type "+ att.id +"*****\n");
			likelihoods[att.id] = observationsgiventype[att.id]/totalobservations;
			Logger.logit("likelihood "+ likelihoods[att.id]+"\n");
			Logger.logit("prior "+ priors[att.id]+"\n");
			probobservations[att.id] = likelihoods[att.id]*priors[att.id];
			sumprobtotalobservations += probobservations[att.id];
			Logger.logit("att "+ att.id + ", probovservations "+ probobservations[att.id]+"\n");
			Logger.logit("sum total observations "+ sumprobtotalobservations+"\n");
			
		}
		
		Logger.logit("\nposteriors...\n\n");
		
		for(Attacker att: attackers.values())
		{
			Logger.logit("********Attacker type "+ att.id +"*********\n");
			posteriors[att.id] = probobservations[att.id]/ sumprobtotalobservations;
			Logger.logit("posterior "+ posteriors[att.id] + "\n");
			
		}
		
		
		
		
		
		
		return posteriors;
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

	public static int[] constructAttackers(HashMap<Integer, Attacker> attackers) {
		
		int id = 0;
		
		int goals[] = {26,23,25,24};
		
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
		
		
		return goals;
		
		
	}

}
