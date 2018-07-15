package planrecognition;

import java.util.HashMap;



import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;

public class PlanRecognition {

	public static void doExp1() {
		
		
		int[] goals = {26, 23, 25, 24};
		
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
		
		
		playGame(net, exploits, attackers, goals);
		
		
		
		
		
	}

	private static void playGame(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals) {
		
		
		/**
		 * choose an attacker
		 */
		
		int chisenaid = 0;
		
		Attacker chosenatt = attackers.get(chisenaid);
		System.out.println("chosen attacker "+ chosenatt.id);
		Logger.logit("Chosen attacker is "+ chosenatt.id);
		HashMap<Integer, Integer> policy = null;
		
		for(HashMap<Integer, Integer> p: chosenatt.fixedpolicy.values())
		{
			policy = p;
			break;
		}
		
		
		
		
		double pr = attackers.size();
		
		double priorsattackertype[] = new double[attackers.size()];
		
		//HashMap<Integer, Integer[]> policies = new HashMap<Integer, Integer[]>();
		
		for(int i=0; i<attackers.size(); i++)
		{
			priorsattackertype[i] = 1.0/pr;
			
		}
		
		double[][]priorforplang = priorForPlans(attackers, goals);
		
		Logger.logit("Priors for goals regarding plans...\n");
		
		for(int a=0; a<attackers.size(); a++)
		{
			Logger.logit("Attacker "+ a +": ");
			for(int i=0; i<priorforplang[a].length; i++)
			{
				Logger.logit(priorforplang[a][i]+" ");
			}
			Logger.logit("\n");
		}
		
		
		HashMap<Integer, Integer> oactions = new HashMap<Integer, Integer>();
		int round = 0;
		while(true)
		{
			Logger.logit("****** round "+round+" ***************");
			int observedaction = policy.get(round);
			oactions.put(round, observedaction);
			Logger.logit("Observed action "+ observedaction);
			
			double posteriorattackertype[] = new double[attackers.size()];
			
			
			
			Logger.logit("Computing Posterior probs...\n");
			
			System.out.println("round "+ round + " observed action "+ observedaction);
			
			/**
			 * posterior
			 */
			posteriorattackertype = computePosteriorAttackerType(oactions, attackers, net, priorsattackertype);
			
			
			
			
			
			System.out.println("Determining attacker plan given the priors");
			
			Logger.logit("Determining attacker plan given the priors \n");
			
			
			double posteriorplang[][] = new double[attackers.size()][goals.length];
			
			
			posteriorplang = posteriorPlang(attackers, net, priorforplang, oactions, goals, priorsattackertype);
			
			
			
			
			
			/**
			 * updating the priors with the posteriors
			 */
			for(int i=0; i<attackers.size(); i++)
			{
				System.out.println(" t "+ i + " posterior "+ posteriorattackertype[i]);
				priorsattackertype[i] = posteriorattackertype[i];
			}
			
			
			for(int a=0; a<attackers.size(); a++)
			{
				System.out.println("attacker "+ a);
				for(int g=0; g<goals.length; g++)
				{
					System.out.println(" g "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
					priorforplang[a][g] = posteriorplang[a][g];
				}
			}

			
			
			
			
			round++;
			
			
			/**
			 * if there are multiple goals, we need to check against all the possible goals
			 */
			if(observedaction==chosenatt.goals.get(0))
			{
				break;
			}
			
			
		}
			
	}

	private static double[][] posteriorPlang(HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net,
			double[][] priorforplang, HashMap<Integer, Integer> oactions, int[] goals, double[] priorsattackertype) {
		
		
		double[][] posteriors = new double[attackers.size()][goals.length];
		
		
		for(Integer attid: attackers.keySet())
		{
			Attacker att = attackers.get(attid);
			double priors[] = priorforplang[attid];
			
			Logger.logit("Attacker "+ attid+"\n");
			
			double[] posterior = new double[goals.length];
			
			int[] policygivengoalcounts = new int[goals.length];
			
			int[] observationmatchescounts = new int[goals.length];
			
			int[] likelihoods = new int[goals.length];
			
			
			
			
			double totallp=0;
			/**
			 * likelihood*prior
			 */
			double [] lp = new double[goals.length];
			
			
			
			
			for(int gindex = 0; gindex<goals.length; gindex++)
			{

				Logger.logit("**********goal "+ goals[gindex]+"***********\n ");
				for(HashMap<Integer, Integer> policy: att.fixedpolicy.values())
				{
					
					Logger.logit("policy : ");
					int plen = policy.size();
					
					
					for(int p: policy.values())
					{
						Logger.logit(p+" ");
						
					}
					Logger.logit("\n");
					
					if(policy.get(plen-1)==goals[gindex])
					{
						Logger.logit("poliicy has the goal "+ goals[gindex]+"\n");
						policygivengoalcounts[gindex]++;
					}
					else
					{
						Logger.logit("poliicy does not have the goal "+ goals[gindex]+"\n");
					}
					
					boolean seqmatches = true;
					/**
					 * run match for observed actions' length
					 */
					for(int oaindex: oactions.keySet())
					{
						
						if(policy.get(oaindex) != oactions.get(oaindex))
						{
							seqmatches = false;
							break;
						}
					}
					
					if(seqmatches)
					{
						
						observationmatchescounts[gindex]++;
						Logger.logit("Obsrvation matches policy, count "+ observationmatchescounts[gindex]+"\n");
					}
					else
					{
						Logger.logit("Obsrvation does not match policy, count "+ observationmatchescounts[gindex]+"\n");
					}
					
				
				}
				
				if(policygivengoalcounts[gindex]>0)
				{
					likelihoods[gindex] = observationmatchescounts[gindex]/policygivengoalcounts[gindex];
				}
				
				
				
				Logger.logit("Likelihood : "+ likelihoods[gindex]+"\n");
				
				lp[gindex] += likelihoods[gindex]*priors[gindex]*priorsattackertype[attid];
				
				totallp += lp[gindex];
				
				Logger.logit("lp : "+ lp[gindex] +"\n");
				
				Logger.logit("total lp : "+ totallp +"\n");
				
			
				
			}
			
			
			Logger.logit(" Posteriors : \n");
			for(int gindex = 0; gindex<goals.length; gindex++)
			{
				
				if(totallp>0)
				{
					posterior[gindex] = lp[gindex]/totallp;
				}
				else
				{
					posterior[gindex] = 0;
				}
					
				Logger.logit("goal "+ goals[gindex] +": "+posterior[gindex]+"\n");
				posteriors[attid][gindex] = posterior[gindex];
				
			}
			
		
		}
		
		
		return posteriors;
	}

	private static double[][] priorForPlans(HashMap<Integer, Attacker> attackers, int[] goals) {


		double[][] priors = new double[attackers.size()][goals.length];


		for(Integer attid: attackers.keySet())
		{
			Attacker att = attackers.get(attid);

			int policysize = att.fixedpolicy.size();

			for(Integer index: att.fixedpolicy.keySet())
			{
				HashMap<Integer, Integer> p = att.fixedpolicy.get(index);



				for(int i=0; i<goals.length; i++)
				{
					if(p.get(p.size()-1) == goals[i])
					{
						priors[attid][i]++;
					}
				}

			}

			for(int i=0; i<priors[attid].length; i++)
			{
				priors[attid][i] = priors[attid][i]/policysize;
			}

		}


		return priors;
	}
	
	

	private static double[] computePosteriorAttackerType(HashMap<Integer, Integer> observedactions,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net, double[] priors) {
		
		
		
		
		double[] posteriors = new double[attackers.size()];
		double[] likelihoods = new double[attackers.size()];
		double[] observationsgiventype = new double[attackers.size()];
		double totalobservations = 0.0;
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
			HashMap<Integer, Integer> policy = att.fixedpolicy.get(0);
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

	public static void constructAttackers(HashMap<Integer, Attacker> attackers) {
		
		int id = 0;
		
		//int goals[] = {26,23,25,24};
		
		Attacker a0  = new Attacker(id++);
		a0.goals.put(0, 26);
		a0.addExploits(new int[] {0, 4});
		a0.addPolicy(0, new int[] {0, 2, 5, 9, 15, 21, 26});
		//a0.addPolicy(1, new int[] {0, 2, 5, 9, 15, 21, 24});
		
		
		Attacker a1  = new Attacker(id++);
		a1.goals.put(0, 23);
		a1.addExploits(new int[] {0, 1});
		a1.addPolicy(0, new int[] {0, 2, 5, 9, 14, 19, 23});
		
		Attacker a2  = new Attacker(id++);
		a2.goals.put(0, 25);
		a2.addExploits(new int[] {2, 3});
		a2.addPolicy(0, new int[] {0, 2, 5, 10, 15, 20, 25});
		
		
		Attacker a3  = new Attacker(id++);
		a3.goals.put(0, 24);
		a3.addExploits(new int[] {0, 2, 4});
		a3.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 24});
		
		attackers.put(0, a0);
		attackers.put(1, a1);
		attackers.put(2, a2);
		attackers.put(3, a3);
		
		
		//return goals;
		
		
	}

}
