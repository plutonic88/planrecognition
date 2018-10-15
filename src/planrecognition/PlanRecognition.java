package planrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;
import solver.Solver;

public class PlanRecognition {

	
	public static int ST_LIMIT = 100;
	
	
	public static void doFixedPolicyExp1() {





		int[] goals = {23, 24, 25, 26};

		int chosenattacker = 2;
		int chosenpolicy = 1;

		boolean singlegoal = false;
		boolean singlepath = false;
		int startnodeid = 0; 
		int npath = 4;

		int nnodes = 27;
		int nhoneypots = 8;
		int nexploits = 8;



		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		Network.constructNetwork(net, exploits, nnodes, nexploits);

		System.out.println("Network construction... \ndone");


		if(singlegoal)
		{
			//PlanRecognition.constructAttackersSingleGoal(startnodeid, attackers, net, exploits, singlepath, npath, chosenattacker);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		System.out.println("Attacker construction... \ndone");

		printNetwork(net);
		System.out.println();
		printAttackers(attackers);

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();

		playGame(chosenattacker, chosenpolicy, net, exploits, attackers, goals);





	}


	

	private static void playGame(int chosenattacker, int chosenpolicy, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals) {


		/**
		 * choose an attacker
		 */

		int chisenaid = chosenattacker;

		Attacker chosenatt = attackers.get(chisenaid);
		System.out.println("*************chosen attacker "+ chosenatt.id);
		Logger.logit("*************Chosen attacker is "+ chosenatt.id+"***********\n");
		HashMap<Integer, Integer> policy = null;

		//for(HashMap<Integer, Integer> p: chosenatt.fixedpolicy.get(chosenpolicy))
		//{
		//policy = p;//chosenatt.fixedpolicy.get(chosenpolicy);
		//	break;
		//}

		policy = chosenatt.fixedpolicy.get(chosenpolicy);

		System.out.print("Chosen policy for the attacker: ");

		for(Integer p: policy.values())
		{
			System.out.print(p+" ");
			Logger.logit(p+" ");
		}
		System.out.println();
		Logger.logit("\n");



		double pr = attackers.size();

		double priorsattackertype[] = new double[attackers.size()];

		//HashMap<Integer, Integer[]> policies = new HashMap<Integer, Integer[]>();

		for(int i=0; i<attackers.size(); i++)
		{
			priorsattackertype[i] = 1.0/pr;

		}

		double[][]priorforplang = priorForPlans(attackers, goals);

		writeBUpdatesForAttackerType(priorsattackertype);
		writeBayesianUpdatesForPlan(priorforplang);


		HashMap<Integer, Integer> oactions = new HashMap<Integer, Integer>();
		int round = 0;
		while(true)
		{
			Logger.logit("\n***************** round "+round+" **********************\n");
			System.out.println("\n********************round "+round+"*********************");
			int observedaction = policy.get(round);
			oactions.put(round, observedaction);
			Logger.logit("******************Observed actions: ");
			System.out.print("****************Observed actions: ");

			for(int oa: oactions.values())
			{
				Logger.logit(oa+" ");
				System.out.print(oa+" ");
			}
			Logger.logit("\n\n");
			System.out.println("\n");

			double posteriorattackertype[] = new double[attackers.size()];

			for(int i=0; i<attackers.size(); i++)
			{
				//if(priorsattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " prior "+ priorsattackertype[i]);
					//Logger.logit(" attacker "+ i + " prior "+ priorsattackertype[i]+"\n");
				}
				//priorsattackertype[i] = posteriorattackertype[i];
			}


			Logger.logit("\nComputing Posterior probs...\n");
			System.out.println("\nComputing Posterior probs...\n");

			//System.out.println("round "+ round + " observed action "+ observedaction);

			/**
			 * posterior
			 */
			posteriorattackertype = computePosteriorAttackerType(oactions, attackers, net, priorsattackertype);


			/**
			 * updating the priors with the posteriors
			 */



			for(int i=0; i<attackers.size(); i++)
			{
				//if(posteriorattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " posterior "+ posteriorattackertype[i]);
					Logger.logit(" attacker "+ i + " posterior "+ posteriorattackertype[i]+"\n");
				}
				priorsattackertype[i] = posteriorattackertype[i];

			}





			double posteriorplang[][] = new double[attackers.size()][goals.length];



			Logger.logit("\nPriors for goals regarding plans...\n\n");
			System.out.println("\nPriors for goals regarding plans...\n");

			for(int a=0; a<attackers.size(); a++)
			{
				Logger.logit("Attacker "+ a +": "+"\n");
				System.out.println("Attacker "+ a +": ");
				for(int i=0; i<priorforplang[a].length; i++)
				{
					Logger.logit(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
					System.out.print(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
				}
				Logger.logit("\n");
				System.out.println();

			}


			System.out.println("Determining posterior on attacker plan given the priors");

			Logger.logit("Determining posterior attacker plan given the priors \n\n");

			posteriorplang = posteriorPlang(attackers, net, priorforplang, oactions, goals, priorsattackertype);


			writeBUpdatesForAttackerType(posteriorattackertype);
			writeBayesianUpdatesForPlan(posteriorplang);






			for(int a=0; a<attackers.size(); a++)
			{
				System.out.println("\nattacker "+ a);
				Logger.logit("\nattacker "+ a+"\n");
				for(int g=0; g<goals.length; g++)
				{
					System.out.println(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
					Logger.logit(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]+"\n");
					priorforplang[a][g] = posteriorplang[a][g];
				}
			}

			round++;


			removePolicies(attackers, oactions, net, goals);
			System.out.println("\nCurrent policies: ");
			printAttackers(attackers);
			System.out.println();




			/**
			 * if there are multiple goals, we need to check against all the possible goals
			 */
			if(chosenatt.goals.containsValue(observedaction))
			{
				break;
			}


		}

	}


	/**
	 * single goal, single path, naive attacker, naive defense
	 * @param chosenattacker
	 * @param chosenpolicy
	 * @param net
	 * @param exploits
	 * @param attackers
	 * @param goals
	 * @param hpdeploylimit 
	 * @param honeypots 
	 * @param withdefense 
	 * @param mincommonoverlap 
	 * @param minentropy 
	 * @param expoverlap 
	 * @param maxoverlap 
	 * @param honeyedge 
	 * @throws Exception 
	 */
	public static void playGameWithNaiveDefense(int chosenattacker, int chosenpolicy, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals, HashMap<Integer,Node> honeypots, int hpdeploylimit, 
			boolean singlepath, int npath, int startnode, boolean withdefense, boolean minentropy, boolean mincommonoverlap, 
			boolean maxoverlap, boolean expoverlap, boolean mincost, boolean honeyedge) throws Exception {


		/**
		 * choose an attacker
		 */

		int startnodeid = startnode;

		int chisenaid = chosenattacker;

		Attacker chosenatt = attackers.get(chisenaid);
		System.out.println("*************chosen attacker "+ chosenatt.id);
		Logger.logit("*************Chosen attacker is "+ chosenatt.id+"***********\n");
		HashMap<Integer, Integer> chosenattackerpolicy = null;

		//for(HashMap<Integer, Integer> p: chosenatt.fixedpolicy.get(chosenpolicy))
		//{
		//policy = p;//chosenatt.fixedpolicy.get(chosenpolicy);
		//	break;
		//}

		chosenattackerpolicy = chosenatt.fixedpolicy.get(chosenpolicy);

		System.out.print("Chosen policy for the attacker: ");

		for(Integer p: chosenattackerpolicy.values())
		{
			System.out.print(p+" ");
			Logger.logit(p+" ");
		}
		System.out.println();
		Logger.logit("\n");



		double pr = attackers.size();

		//double priorsattackertype[] = new double[attackers.size()];
		double[][]priorsplans = priorForPlans(attackers, goals);


		HashMap<Integer, Double> priorsattackertype = new HashMap<Integer, Double>();
		HashMap<Integer, HashMap<Integer, Double>> priorforplang = new HashMap<Integer, HashMap<Integer, Double>>();


		//HashMap<Integer, Integer[]> policies = new HashMap<Integer, Integer[]>();

		for(Attacker att: attackers.values())
		{
			double p = 1.0/pr;

			priorsattackertype.put(att.id, p);


			HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();

			for(int i=0; i<goals.length; i++)
			{
				tmp.put(i, p);
			}
			priorforplang.put(att.id, tmp);

		}



		writeBUpdatesForAttackerType(priorsattackertype);
		writeBayesianUpdatesForPlan(priorforplang);


		HashMap<Integer, Integer> oactions = new HashMap<Integer, Integer>();
		ArrayList<Integer> currenthps = new ArrayList<Integer>();
		HashMap<Integer, Integer> currenthdges = new HashMap<Integer, Integer>();
		

		int round = 0;

		oactions.put(oactions.size(), startnodeid);
		
		ArrayList<int[]> deceptionslots = new ArrayList<int[]>();
		ArrayList<int[]> hpsdeployments = new ArrayList<int[]>();
		ArrayList<int[]> hesdeployments = new ArrayList<int[]>();
		ArrayList<Integer> atobservedactions = new ArrayList<Integer>();
		
		

		while(true)
		{
			Logger.logit("\n***************** round "+round+" **********************\n");
			System.out.println("\n********************round "+round+"*********************");


			if(round>10)
			{
				System.out.println("something is wrong");
			}
			
			/**
			 * 1. defender observes the actions played by the attacker
			 * 2. defender updates his belief about attacker type and the plan of the attacker
			 */
			System.out.println("defender observing the attacker actions...");
			System.out.print("****************Observed actions: ");

			for(int oa: oactions.values())
			{

				System.out.print(oa+" ");
			}
			System.out.println("\n");
			
			int currentnodeid = oactions.get(oactions.size()-1);
			
			
			
			printAttackers(attackers);
			
			System.out.println("****************Attacker current position node: "+ currentnodeid);
			
			
			
			/**
			 * checking for prgram terminations
			 */
			
			
			/**
			 * We have to remove policy and remove the attackers with 0 posteriors or priors
			 */

			if(chosenatt.goals.containsValue(currentnodeid))
			{
				System.out.println("***********Attacker reached his goal "+ currentnodeid+"********************");
				//System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				//break;
			}

			

			//double posteriorattackertype[] = new double[attackers.size()];
			//double posteriorplang[][] = new double[attackers.size()][goals.length];


			HashMap<Integer, Double> posteriorattackertype = new HashMap<Integer, Double>();
			HashMap<Integer, HashMap<Integer, Double>> posteriorplang = new HashMap<Integer, HashMap<Integer, Double>>();
			
			
			
			


			if(oactions.size()!=0)
			{
				Node curnode = net.get(currentnodeid);
				//System.out.println("*******Attacker current position node "+ curnode.id + " round "+ round);

				printNetwork(net);

				/**
				 * first free the honeypots which are invalid because of the attacker actions
				 * The honeypots are not in the paths of the attacker
				 */
				if(currenthps.size()>0 && !honeyedge)
				{
					freeInvalidHoneypots(currenthps, curnode, net, honeypots, currentnodeid);
					//printNetwork(honeypots);
				}
				else if(honeyedge)
				{
					
				}

				//printNetwork(net);


				System.out.println("\nComputing Posterior probs for attacker types...\n");
				/**
				 * posterior
				 */
				
				doPosteriorAttType(oactions, attackers, net, priorsattackertype, posteriorattackertype);
				
				doPosteriorPlan(oactions, attackers, net, priorforplang, posteriorplang, goals, priorsattackertype, posteriorattackertype);
				
				
			}//end of else
			
			
			if(currenthps.contains(currentnodeid))
			{
				System.out.println("***********Attacker got caught in honeypot "+ currentnodeid+"********************");
				System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				/**
				 * write the probs
				 */
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				return;
			}


			
			int determinedattacker = checkIfAttackerIsDetermined(priorsattackertype, attackers);	

			if(determinedattacker!=-1)
			{
				System.out.println("***********Determined attacker type "+ determinedattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				return;
			}

			
			/**
			 * remove unnecessary attackers
			 */
			removeUnAttackers(attackers, priorsattackertype, priorforplang, chosenattacker);
			
			
			
			

			
			
			if(withdefense)
			{
				

				/*int determinedattacker = checkIfAttackerIsDetermined(priorsattackertype, attackers);	

				if(determinedattacker!=-1)
				{
					System.out.println("***********Determined attacker type "+ determinedattacker+"********************");
					System.out.println("***********round "+ round+"********************");
					return;
				}*/


				System.out.println("Now defender will deploy HP");



				if(minentropy)
				{
					
					
					/**
					 * 1. Maximize over the chosen attacker
					 */
					System.out.println("***********Using deployHPMinEntropy******************************");
					/*deployHPMinEntropy(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, hpslots, hpsdeployments );
					*/
					
					deployHPMinEntropyV2(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, deceptionslots, hpsdeployments, goals );
					
					
					
					
					
					
				}
				else if(mincommonoverlap)
				{
					System.out.println("***********Using deployHPMinCommonOverlap******************************");
					
					/**
					 * 1. include possible attacker scenario
					 * 2. Maximize over the chosen attacker
					 */
					/*deployHPMinCommonOverlap(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap );
					*/
					
					
					deployHPMinCommonOverlapV2(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, goals );
				}
				else if(mincost)
				{
					System.out.println("***********Using deployHPMinCost******************************");
					
					/**
					 * 1. include possible attacker scenario
					 * 2. Maximize over the chosen attacker
					 */
					/*deployHPMinCommonOverlap(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap );
					*/
					
					deceptionslots.clear();
					hpsdeployments.clear();
					deployHPMinCost(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, goals, deceptionslots, hpsdeployments );
				}
				else if(honeyedge)
				{
					
					System.out.println("***********Using deployHEMinEntropy******************************");
					
					
					deployHEMinEntropy(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, deceptionslots, hesdeployments, goals, currenthdges );
					
				}
				
				

				//System.out.println("Network after honeypot deployment: ");

				printNetwork(net);



				/**
				 * make attacker move
				 */

				
				//printNetwork(net);


			/*	if(round==3)
				{
					System.out.println("Chosen Attackers policy size 0 round "+ round);
					printNetwork(net);
				}*/
				
				
				
				boolean attackerok = computeAttackerPolicy(net, exploits, attackers, currentnodeid, singlepath, npath, priorsattackertype, priorforplang, chosenattacker, maxoverlap, expoverlap, round);
				
				
				
				if(!attackerok)
				{
					printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
					return;
				}
				
				
				
				System.out.println("Attackers after deploying HPs and refining policies");

				printAttackers(attackers);

			}
			
			
			int attaction = makeAttackerMove(chosenatt, withdefense, oactions, round, chosenpolicy, currentnodeid);
			atobservedactions.add(attaction);
			
			
			

			
			
			
			/*if(attaction==10)
			{
				System.out.println("round "+round+", chosen attacker action "+ attaction);
			}*/

			

			
			//printAttackers(attackers);
			if(honeyedge)
			{
				printInfos(deceptionslots, hesdeployments, atobservedactions, round);
			}
			else
			{
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
			}
			
			


			round++;

			System.out.println("Hii");

			//printNetwork(net);










			/************************************************END****************************************************/






			/*	

			int observedaction = chosenattackerpolicy.get(round);
			oactions.put(round, observedaction);
			Logger.logit("******************Observed actions: ");
			System.out.print("****************Observed actions: ");

			for(int oa: oactions.values())
			{
				Logger.logit(oa+" ");
				System.out.print(oa+" ");
			}
			Logger.logit("\n\n");
			System.out.println("\n");



			for(int i=0; i<attackers.size(); i++)
			{
				//if(priorsattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " prior "+ priorsattackertype[i]);
					Logger.logit(" attacker "+ i + " prior "+ priorsattackertype[i]+"\n");
				}
				//priorsattackertype[i] = posteriorattackertype[i];
			}


			Logger.logit("\nComputing Posterior probs...\n");
			System.out.println("\nComputing Posterior probs...\n");

			//System.out.println("round "+ round + " observed action "+ observedaction);

			 *//**
			 * posterior
			 *//*
			posteriorattackertype = computePosteriorAttackerType(oactions, attackers, net, priorsattackertype);


			  *//**
			  * updating the priors with the posteriors
			  *//*



			for(int i=0; i<attackers.size(); i++)
			{
				//if(posteriorattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " posterior "+ posteriorattackertype[i]);
					Logger.logit(" attacker "+ i + " posterior "+ posteriorattackertype[i]+"\n");
				}
				priorsattackertype[i] = posteriorattackertype[i];

			}





			//double posteriorplang[][] = new double[attackers.size()][goals.length];



			Logger.logit("\nPriors for goals regarding plans...\n\n");
			System.out.println("\nPriors for goals regarding plans...\n");

			for(int a=0; a<attackers.size(); a++)
			{
				Logger.logit("Attacker "+ a +": "+"\n");
				System.out.println("Attacker "+ a +": ");
				for(int i=0; i<priorforplang[a].length; i++)
				{
					Logger.logit(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
					System.out.print(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
				}
				Logger.logit("\n");
				System.out.println();

			}


			System.out.println("Determining posterior on attacker plan given the priors");

			Logger.logit("Determining posterior attacker plan given the priors \n\n");

			posteriorplang = posteriorPlang(attackers, net, priorforplang, oactions, goals, priorsattackertype);


			writeBUpdatesForAttackerType(posteriorattackertype);
			writeBayesianUpdatesForPlan(posteriorplang);






			for(int a=0; a<attackers.size(); a++)
			{
				System.out.println("\nattacker "+ a);
				Logger.logit("\nattacker "+ a+"\n");
				for(int g=0; g<goals.length; g++)
				{
					System.out.println(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
					Logger.logit(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]+"\n");
					priorforplang[a][g] = posteriorplang[a][g];
				}
			}

			round++;


			removePolicies(attackers, oactions, net, goals);
			System.out.println("\nCurrent policies: ");
			printAttackers(attackers);
			System.out.println();




			   *//**
			   * if there are multiple goals, we need to check against all the possible goals
			   *//*
			if(chosenatt.goals.containsValue(observedaction))
			{
				break;
			}*/


		}// while loop

	}
	
	
	public static void playGameWithNaiveDefenseMILP(int chosenattacker, int chosenpolicy, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals, HashMap<Integer,Node> honeypots, int hpdeploylimit, 
			boolean singlepath, int npath, int startnode, boolean withdefense, boolean minentropy, boolean mincommonoverlap, 
			boolean maxoverlap, boolean expoverlap, boolean mincost, boolean honeyedge, int honeyedgelimit, boolean minmaxexpectedoverlap, boolean honeypot) throws Exception {


		/**
		 * choose an attacker
		 */

		int startnodeid = startnode;

		int chisenaid = chosenattacker;

		Attacker chosenatt = attackers.get(chisenaid);
		System.out.println("*************chosen attacker "+ chosenatt.id);
		//Logger.logit("*************Chosen attacker is "+ chosenatt.id+"***********\n");
		HashMap<Integer, Integer> chosenattackerpolicy = null;

		

		chosenattackerpolicy = chosenatt.fixedpolicy.get(chosenpolicy);

		System.out.print("Chosen policy for the attacker: ");

		for(Integer p: chosenattackerpolicy.values())
		{
			System.out.print(p+" ");
			//Logger.logit(p+" ");
		}
		System.out.println();
		//Logger.logit("\n");



		double pr = attackers.size();

		//double priorsattackertype[] = new double[attackers.size()];
		//double[][]priorsplans = priorForPlans(attackers, goals);


		HashMap<Integer, Double> priorsattackertype = new HashMap<Integer, Double>();
		HashMap<Integer, HashMap<Integer, Double>> priorforplang = new HashMap<Integer, HashMap<Integer, Double>>();


		//HashMap<Integer, Integer[]> policies = new HashMap<Integer, Integer[]>();

		for(Attacker att: attackers.values())
		{
			double p = 1.0/pr;

			priorsattackertype.put(att.id, p);


			HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();

			for(int i=0; i<goals.length; i++)
			{
				tmp.put(i, p);
			}
			priorforplang.put(att.id, tmp);

		}



		writeBUpdatesForAttackerType(priorsattackertype);
		writeBayesianUpdatesForPlan(priorforplang);


		HashMap<Integer, Integer> oactions = new HashMap<Integer, Integer>();
		
		// honeypot ids
		ArrayList<Integer> currenthps = new ArrayList<Integer>();
		
		// node,honeyedge
		ArrayList<String> currenthoneydges = new ArrayList<String>();
		

		int round = 0;

		oactions.put(oactions.size(), startnodeid);
		
		ArrayList<int[]> deceptionslots = new ArrayList<int[]>();
		ArrayList<int[]> hpsdeployments = new ArrayList<int[]>();
		ArrayList<int[]> hesdeployments = new ArrayList<int[]>();
		ArrayList<Integer> atobservedactions = new ArrayList<Integer>();
		
		

		while(true)
		{
			//Logger.logit("\n***************** round "+round+" **********************\n");
			System.out.println("\n********************round "+round+"*********************");


			if(round>10)
			{
				System.out.println("something is wrong");
			}
			
			/**
			 * 1. defender observes the actions played by the attacker
			 * 2. defender updates his belief about attacker type and the plan of the attacker
			 */
			System.out.println("defender observing the attacker actions...");
			System.out.print("****************Observed actions: ");

			for(int oa: oactions.values())
			{
				System.out.print(oa+" ");
			}
			System.out.println("\n");
			
			int currentnodeid = oactions.get(oactions.size()-1);
			
			
			
			printAttackers(attackers);
			
			System.out.println("****************Attacker current position node: "+ currentnodeid);
			
			
			
			/**
			 * checking for prgram terminations
			 */
			
			
			/**
			 * We have to remove policy and remove the attackers with 0 posteriors or priors
			 */

			if(chosenatt.goals.containsValue(currentnodeid))
			{
				System.out.println("***********Attacker reached his goal "+ currentnodeid+"********************");
				//System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				//break;
			}

			

			//double posteriorattackertype[] = new double[attackers.size()];
			//double posteriorplang[][] = new double[attackers.size()][goals.length];


			HashMap<Integer, Double> posteriorattackertype = new HashMap<Integer, Double>();
			HashMap<Integer, HashMap<Integer, Double>> posteriorplang = new HashMap<Integer, HashMap<Integer, Double>>();
			
			
			
			


			if(oactions.size()!=0)
			{
				Node curnode = net.get(currentnodeid);
				//System.out.println("*******Attacker current position node "+ curnode.id + " round "+ round);

				printNetwork(net);

				/**
				 * first free the honeypots which are invalid because of the attacker actions
				 * The honeypots are not in the paths of the attacker
				 */
				if(currenthps.size()>0 && !honeyedge)
				{
					freeInvalidHoneypots(currenthps, curnode, net, honeypots, currentnodeid);
					printNetwork(honeypots);
				}
				else if(honeyedge)
				{
					removeUnHoneyEdges(currenthoneydges, curnode, net);
				}

				//printNetwork(net);


				System.out.println("\nComputing Posterior probs for attacker types...\n");
				/**
				 * posterior
				 */
				
				doPosteriorAttType(oactions, attackers, net, priorsattackertype, posteriorattackertype);
				
				doPosteriorPlan(oactions, attackers, net, priorforplang, posteriorplang, goals, priorsattackertype, posteriorattackertype);
				
				
			}//end of else
			
			
			if(currenthps.contains(currentnodeid))
			{
				System.out.println("***********Attacker got caught in honeypot "+ currentnodeid+"********************");
				System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				/**
				 * write the probs
				 */
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				return;
			}


			
			int determinedattacker = checkIfAttackerIsDetermined(priorsattackertype, attackers);	

			if(determinedattacker!=-1)
			{
				System.out.println("***********Determined attacker type "+ determinedattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
				return;
			}

			
			/**
			 * remove unnecessary attackers
			 */
			removeUnAttackers(attackers, priorsattackertype, priorforplang, chosenattacker);
			
			
			
			

			
			
			if(withdefense)
			{
				

				/*int determinedattacker = checkIfAttackerIsDetermined(priorsattackertype, attackers);	

				if(determinedattacker!=-1)
				{
					System.out.println("***********Determined attacker type "+ determinedattacker+"********************");
					System.out.println("***********round "+ round+"********************");
					return;
				}*/


				System.out.println("Now defender will deploy deception");



				/*if(minentropy)
				{
					
					
					*//**
					 * 1. Maximize over the chosen attacker
					 *//*
					System.out.println("***********Using deployHPMinEntropy******************************");
					deployHPMinEntropy(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, hpslots, hpsdeployments );
					
					
					deployHPMinEntropyV2(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, deceptionslots, hpsdeployments, goals );
					
					
					
					
					
					
				}*/
				
				int[][][] hpdepcosts = new int[net.size()][net.size()][exploits.size()];
				
					
				if(honeypot && mincommonoverlap)
				{
					System.out.println("***********Using deployHPMinOverlapMILP******************************");
					
					deceptionslots.clear();
					hpsdeployments.clear();
					
					hpdepcosts = deployHPMinMaxOverlapMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, 
							maxoverlap, expoverlap, deceptionslots, hpsdeployments, goals, currenthps, honeypots, hpdeploylimit );
					
				}
				else if(honeypot && minmaxexpectedoverlap)
				{
					System.out.println("***********Using deployHPMinMaxExpectedOverlapMILP******************************");
					
					deceptionslots.clear();
					hesdeployments.clear();
					
					hpdepcosts = deployHPMinMaxExpOverlapMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, 
							deceptionslots, hpsdeployments, goals, currenthps, honeypots, hpdeploylimit );
				}
				else if(honeypot && minentropy)
				{
					System.out.println("***********Using deployHPMinentropyMILP******************************");
					
					deceptionslots.clear();
					hesdeployments.clear();
					
					hpdepcosts = deployHPMinEntropyMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, 
							deceptionslots,hpsdeployments, goals, currenthps, honeypots, hpdeploylimit  );
					
				}
				else if(honeyedge && mincommonoverlap)
				{
					System.out.println("***********Using deployHEMinOverlapMILP******************************");
					
					deceptionslots.clear();
					hesdeployments.clear();
					
					deployHEMinMaxOverlapMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, deceptionslots, hesdeployments, goals, currenthoneydges );
					
				}
				else if(honeyedge && minmaxexpectedoverlap)
				{
					System.out.println("***********Using deployHEMinMaxExpectedOverlapMILP******************************");
					
					deceptionslots.clear();
					hesdeployments.clear();
					
					deployHEMinMaxExpOverlapMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, 
							deceptionslots, hesdeployments, goals, currenthoneydges, chosenpolicy );
					
				}
				else if(honeyedge && minentropy)
				{
					System.out.println("***********Using deployHEMinentropyMILP******************************");
					
					deceptionslots.clear();
					hesdeployments.clear();
					
					deployHEMinEntropyMILP(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, 
							deceptionslots, hesdeployments, goals, currenthoneydges, chosenpolicy );
					
				}
				else if(mincommonoverlap)
				{
					System.out.println("***********Using deployHPMinCommonOverlap******************************");
					
					/**
					 * 1. include possible attacker scenario
					 * 2. Maximize over the chosen attacker
					 */
					/*deployHPMinCommonOverlap(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap );
					*/
					
					
					deployHPMinCommonOverlapV2(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, goals );
				}
				else if(mincost)
				{
					System.out.println("***********Using deployHPMinCost******************************");
					
					/**
					 * 1. include possible attacker scenario
					 * 2. Maximize over the chosen attacker
					 */
					/*deployHPMinCommonOverlap(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap );
					*/
					
					deceptionslots.clear();
					hpsdeployments.clear();
					deployHPMinCost(net, honeypots, currenthps, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, goals, deceptionslots, hpsdeployments );
				}
				
				/*else if(honeyedge)
				{
					
					System.out.println("***********Using deployHEMinEntropy******************************");
					
					
					deployHEMinEntropy(net, oactions, attackers, chosenatt, chosenattackerpolicy, singlepath, 
							npath, priorsattackertype, priorforplang, round, hpdeploylimit, startnodeid, exploits, maxoverlap, expoverlap, deceptionslots, hesdeployments, goals, currenthoneydges );
					
				}*/
				
				
				

				//System.out.println("Network after honeypot deployment: ");

			//	printNetwork(net);



				/**
				 * make attacker move
				 */

				
				//printNetwork(net);


			/*	if(round==3)
				{
					System.out.println("Chosen Attackers policy size 0 round "+ round);
					printNetwork(net);
				}*/
				
				
				
				/*boolean attackerok = computeAttackerPolicy(net, exploits, attackers, currentnodeid, singlepath, npath, priorsattackertype, priorforplang, chosenattacker, maxoverlap, expoverlap, round);
				
				*/
				
				HashMap<Integer, Integer> mincosts = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincosts.put(a.id, minc);
					
				}
				

				boolean attackerok = constructAttackerPoliciesMILPRun(net, exploits, honeypots, chosenattacker, goals, attackers.size(), mincosts, attackers, currentnodeid, hpdepcosts);
				
				
				
				
				if(!attackerok)
				{
					printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
					return;
				}
				
				
				
				System.out.println("Attackers after deploying HPs and refining policies");

				printAttackers(attackers);

			}
			
			
			int attaction = makeAttackerMove(chosenatt, withdefense, oactions, round, chosenpolicy, currentnodeid);
			atobservedactions.add(attaction);
			
			
			

			
			
			
			/*if(attaction==10)
			{
				System.out.println("round "+round+", chosen attacker action "+ attaction);
			}*/

			

			
			//printAttackers(attackers);
			if(honeyedge)
			{
				printInfos(deceptionslots, hesdeployments, atobservedactions, round);
			}
			else
			{
				printInfos(deceptionslots, hpsdeployments, atobservedactions, round);
			}
			
			


			round++;

			System.out.println("Hii");

			//printNetwork(net);










			/************************************************END****************************************************/






			/*	

			int observedaction = chosenattackerpolicy.get(round);
			oactions.put(round, observedaction);
			Logger.logit("******************Observed actions: ");
			System.out.print("****************Observed actions: ");

			for(int oa: oactions.values())
			{
				Logger.logit(oa+" ");
				System.out.print(oa+" ");
			}
			Logger.logit("\n\n");
			System.out.println("\n");



			for(int i=0; i<attackers.size(); i++)
			{
				//if(priorsattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " prior "+ priorsattackertype[i]);
					Logger.logit(" attacker "+ i + " prior "+ priorsattackertype[i]+"\n");
				}
				//priorsattackertype[i] = posteriorattackertype[i];
			}


			Logger.logit("\nComputing Posterior probs...\n");
			System.out.println("\nComputing Posterior probs...\n");

			//System.out.println("round "+ round + " observed action "+ observedaction);

			 *//**
			 * posterior
			 *//*
			posteriorattackertype = computePosteriorAttackerType(oactions, attackers, net, priorsattackertype);


			  *//**
			  * updating the priors with the posteriors
			  *//*



			for(int i=0; i<attackers.size(); i++)
			{
				//if(posteriorattackertype[i]>0)
				{
					System.out.println(" attacker "+ i + " posterior "+ posteriorattackertype[i]);
					Logger.logit(" attacker "+ i + " posterior "+ posteriorattackertype[i]+"\n");
				}
				priorsattackertype[i] = posteriorattackertype[i];

			}





			//double posteriorplang[][] = new double[attackers.size()][goals.length];



			Logger.logit("\nPriors for goals regarding plans...\n\n");
			System.out.println("\nPriors for goals regarding plans...\n");

			for(int a=0; a<attackers.size(); a++)
			{
				Logger.logit("Attacker "+ a +": "+"\n");
				System.out.println("Attacker "+ a +": ");
				for(int i=0; i<priorforplang[a].length; i++)
				{
					Logger.logit(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
					System.out.print(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
				}
				Logger.logit("\n");
				System.out.println();

			}


			System.out.println("Determining posterior on attacker plan given the priors");

			Logger.logit("Determining posterior attacker plan given the priors \n\n");

			posteriorplang = posteriorPlang(attackers, net, priorforplang, oactions, goals, priorsattackertype);


			writeBUpdatesForAttackerType(posteriorattackertype);
			writeBayesianUpdatesForPlan(posteriorplang);






			for(int a=0; a<attackers.size(); a++)
			{
				System.out.println("\nattacker "+ a);
				Logger.logit("\nattacker "+ a+"\n");
				for(int g=0; g<goals.length; g++)
				{
					System.out.println(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
					Logger.logit(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]+"\n");
					priorforplang[a][g] = posteriorplang[a][g];
				}
			}

			round++;


			removePolicies(attackers, oactions, net, goals);
			System.out.println("\nCurrent policies: ");
			printAttackers(attackers);
			System.out.println();




			   *//**
			   * if there are multiple goals, we need to check against all the possible goals
			   *//*
			if(chosenatt.goals.containsValue(observedaction))
			{
				break;
			}*/


		}// while loop

	}


	private static void removeUnHoneyEdges(ArrayList<String> currenthoneydges, Node curnode,
			HashMap<Integer, Node> net) throws Exception {
		
		/** if the node where honeyedge was added is reachable from attacker's current position then we can keep the service,
		 * otherwise we have to stop it
		 */
		
		
		
		
		if(currenthoneydges.size()>2)
		{
			System.out.println("More than two HE existings ");
			throw new Exception("More than two HE existings");
		}


		//printNetwork(net);
		//Node curnode = net.get(curnode2);
		System.out.println("Attacker current position node "+ curnode.id);
		System.out.println("Current using HE: ");
		for(String s: currenthoneydges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int eid = Integer.parseInt(che[1]);
			
			System.out.println("node: "+nodeid+", honeyedge: "+eid);
		}
		System.out.println();

		ArrayList<Integer> unreachablenode = new ArrayList<Integer>();

		for(String s: currenthoneydges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int eid = Integer.parseInt(che[1]);
			
			System.out.print("node: "+nodeid+", honeyedge: "+eid);
			
			
			/**
			 * now check whether it's possible to reach the node from the current node
			 * 
			 */
			boolean reach = false;
			
			/**
			 * if the current node id is where the attacker arrived and where the he is added then keep the he. 
			 * later check if the attacker used the honeyedge
			 * use a bfs algo
			 */
			if(nodeid== curnode.id)
			{
				reach = true;
				//break;
			}
			else
			{
				// bfs
				Node honeyedgenode = net.get(nodeid);
				reach = isReachable(curnode, honeyedgenode, net);
			}
			


			if(reach)
			{
				System.out.println("Honeypot "+ nodeid +" is reachable from attacker current position node "+ curnode.id);
			}
			else
			{
				System.out.println("Honeypot "+ nodeid +" is not reachable from attacker current position node "+ curnode.id);
				unreachablenode.add(nodeid);

			}
		}
		
		
		ArrayList<String> newcurrenthoneydges = new ArrayList<String>();

		for(String s: currenthoneydges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int e = Integer.parseInt(che[1]);
			
			if(unreachablenode.contains(nodeid))
			{
				System.out.println("removing invalid HE: "+ e + " from node "+ nodeid);

				Node node = net.get(nodeid);

				if(node.honeyedge.containsValue(e) && node.exploits.containsValue(e))
				{
					node.exploits.remove(e); // key value same
					//currenthoneydges.remove(nodeid); // removing from current HE
				}
			}
			else
			{
				newcurrenthoneydges.add(s);
			}

		}
		
		
		currenthoneydges = newcurrenthoneydges;
		
		System.out.println("After removing invalid, current using HE: ");
		for(String s: currenthoneydges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int e = Integer.parseInt(che[1]);
			System.out.println("node: "+nodeid+", honeyedge: "+e);
		}
		System.out.println();
		//printNetwork(net);
		

		
	}




	private static boolean isReachable(Node curnode, Node honeynode, HashMap<Integer, Node> net) {
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(curnode.id));
		fringequeue.add(start);

		while(!fringequeue.isEmpty())
		{
			Node node = fringequeue.poll();
			closed.add(node.id);

			if(node.id==honeynode.id)
			{
				return true;
			}
			
			Node orignode = net.get(node.id);
			for(Integer nei: orignode.nei.values())
			{
				Node neinode = net.get(nei);
				//Logger.logit("exploit:"+neinodeexploit+"\n");
				Node tmp = new Node(neinode);
				tmp.parent = node;
				fringequeue.add(tmp);
			}
		}
		return false;
	}




	private static void printInfos(ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments,
			ArrayList<Integer> atobservedactions, int round) {
		
		
		System.out.println("***********round "+ round+"********************");
		System.out.println("deployed HP slots: ");
		for(int[] slts: hpslots)
		{
			System.out.print("[");
			for(int s: slts)
			{
				System.out.print(s+" ");
			}
			System.out.print("] ");
		}
		System.out.println("\ndeployed Deception: ");
		for(int[] slts: hpsdeployments)
		{
			System.out.print("[");
			for(int s: slts)
			{
				System.out.print(s+" ");
			}
			System.out.println("]");
		}
		
		System.out.print("Attacker actions: ");
		
			
			for(int s: atobservedactions)
			{
				System.out.print(s+" ");
			}
			System.out.println();
		
		
		
	}




	private static void doPosteriorPlan(HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers,
			HashMap<Integer, Node> net, HashMap<Integer, HashMap<Integer, Double>> priorforplang,
			HashMap<Integer, HashMap<Integer, Double>> posteriorplang, int[] goals, HashMap<Integer,Double> priorsattackertype, HashMap<Integer,Double> posteriorattackertype) {
		
		
		System.out.println("\nPriors for goals regarding plans...\n");

		for(Attacker att: attackers.values())
		{
			System.out.println("Attacker "+ att.id +": ");
			HashMap<Integer, Double> prob = priorforplang.get(att.id);
			int gi = 0;
			for(double d: prob.values())
			{

				System.out.print(" goal "+ goals[gi]+" prior: "+d+"\n");
				gi++;
			}
			System.out.println();

		}

		System.out.println("computing posterior on attacker plan given the priors");
		posteriorplang = posteriorPlangWithHashMap(attackers, net, priorforplang, oactions, goals, priorsattackertype);

		writeBUpdatesForAttackerType(posteriorattackertype);
		writeBayesianUpdatesForPlan(posteriorplang);



		for(Attacker att: attackers.values())
		{
			System.out.println("Attacker "+ att.id +": ");
			HashMap<Integer, Double> prob = posteriorplang.get(att.id);
			int gi=0;
			for(double d: prob.values())
			{

				System.out.print(" goal "+ goals[gi]+" prior: "+d+"\n");
				gi++;
			}
			priorforplang.put(att.id, posteriorplang.get(att.id));
			System.out.println();

		}
		
	}




	private static void doPosteriorAttType(HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers,
			HashMap<Integer, Node> net, HashMap<Integer, Double> priorsattackertype, HashMap<Integer,Double> posteriorattackertype) {
		
		posteriorattackertype = computePosteriorAttackerTypeWithHashMap(oactions, attackers, net, priorsattackertype);
		for(Attacker att: attackers.values())
		{
			System.out.println(" attacker "+ att.id + " prior "+ priorsattackertype.get(att.id));

		}
		for(Attacker att: attackers.values())
		{
			System.out.println(" attacker "+ att.id + " posterior "+ posteriorattackertype.get(att.id));
			priorsattackertype.put(att.id, posteriorattackertype.get(att.id));
		}
		
		
	}




	private static void removeUnAttackers(HashMap<Integer, Attacker> attackers,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer, HashMap<Integer, Double>> priorforplang, int chosenattacker) {
		
		
		/**
		 * remove attacker with 0 posterior
		 */
		ArrayList<Integer> tobermvedatt = new ArrayList<Integer>();


		for(Integer attid: priorsattackertype.keySet())
		{
			double p = priorsattackertype.get(attid);
			if(p==0)
			{
				tobermvedatt.add(attid);
				if(attid == chosenattacker)
				{
					System.out.println("********Attacker "+ attid +" will be removed which is the chosen attacker********");
					return;
				}

			}
		}


		for(Integer a: tobermvedatt)
		{

			attackers.remove(a);
			priorsattackertype.remove(a);
			priorforplang.remove(a);

			System.out.println("Attacker "+ a +" is removed...");


		}
		
	}




	private static int makeAttackerMove(Attacker chosenatt, boolean withdefense, HashMap<Integer, Integer> oactions,
			int round, int chosenpolicy, int currentnodeid) {
		
		if(chosenatt.fixedpolicy.get(chosenpolicy).size()==0)
		{
			System.out.println("***********Attacker got stuck no vulnerabilities to exploit********************");
			//System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
			System.out.println("***********round "+ round+"********************");
			return -1;
		}


		int attaction = -1;
		
		
		
		if(withdefense)
		{
			attaction = chosenatt.fixedpolicy.get(chosenpolicy).get(1);
			oactions.clear();
			oactions.put(oactions.size(), currentnodeid);
			oactions.put(oactions.size(), attaction);
		}
		else
		{
			attaction = chosenatt.fixedpolicy.get(chosenpolicy).get(round+1);
			oactions.put(oactions.size(), attaction);
		}

		System.out.println("round "+round+", chosen attacker action "+ attaction);
		
		return attaction;

		
	}




	private static boolean computeAttackerPolicy(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int currentnodeid, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer, HashMap<Integer, Double>> priorforplang,
			int chosenattacker, boolean maxoverlap, boolean expoverlap, int round) throws Exception {
		
		//ArrayList<Integer> toberm = new ArrayList<Integer>();
		
		HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
		

		for(Attacker att: attackers.values())
		{

			HashMap<Integer,HashMap<Integer,Integer>> policy = computeSingleAttackPolicy(net, exploits, att, currentnodeid, singlepath, npath, costs);
			
			if(policy.size()==0)
			{
				//System.out.println("*******Attacker "+att.id+" has no policy, round "+ round +", will be removed *******");
				
				throw new Exception("*******Attacker "+att.id+" has no policy, round "+ round +"*******");
				//toberm.add(att.id);
				//return;
			}
			else if(policy.size()>0)
			{
				att.fixedpolicy.clear();
			}
			
			if(policy.size()==0 && att.id == chosenattacker)
			{
				/*System.out.println("***********Attacker got stuck no policies to exploit********************");
				System.out.println("***********Determined attacker type "+ chosenattacker+"********************");
				System.out.println("***********round "+ round+"********************");
				return false;*/
				
				throw new Exception("*******Attacker "+att.id+" has no policy, round "+ round +" *******");
			}
			
			

			for(HashMap<Integer,Integer> po: policy.values())
			{
				att.fixedpolicy.put(att.fixedpolicy.size(), po);
			}
			
			//att.removeDuplicatePolicies();
		}
		
		/*for(Integer a: toberm)
		{

			attackers.remove(a);
			priorsattackertype.remove(a);
			priorforplang.remove(a);

			System.out.println("Attacker "+ a +" is removed...");


		}
		
		System.out.println("Attackers policies before refining ");*/
		
		printAttackers(attackers);
		
		if(maxoverlap)
		{
		
			System.out.println("refining using max overlapping");
			refinePoliciesInit(attackers, chosenattacker);
		}
		else if(expoverlap)
		{
			System.out.println("refining using max exp overlapping");
			refinePoliciesInitMaxExpOverlap(attackers, chosenattacker);
		}
		return true;
		
	}




	private static int checkIfAttackerIsDetermined(HashMap<Integer, Double> priorsattackertype,
			HashMap<Integer, Attacker> attackers) {



		for(Integer atid: priorsattackertype.keySet())
		{
			double prob = priorsattackertype.get(atid);

			if(prob==1)
			{
				System.out.println("Attacker type: "+ atid);
				return atid;
			}
		}



		return -1;
	}


	private static void deployHPMinEntropy(HashMap<Integer, Node> net, HashMap<Integer, Node> honeypots,
			ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, int hpdeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");

		HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");


		
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

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());


		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minentropy = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int hp[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					hp[i] = freehps.get(hpid[i]);
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}
				
				System.out.println("HP: ");

				System.out.print("[");
				for(int s: hp)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				

				for(Integer atidconsidered: attackers.keySet()) // defender considers atid
				{


					double overlaplen = measureDefenseMovesForEntropy(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, round, slots, hp, 
							posteriorlibrary, settingsid, atidconsidered, maxoverlap, expoverlap );


					double entropy = computeEntropy(posteriorlibrary.get(settingsid), atidconsidered);

					/**
					 * if the entropy is zero then
					 * define some other criteria to choose the settings
					 */

					if(entropy==Double.NaN)
					{
						System.out.println("Found entropy NaN");
					}
					else if(entropy==0)
					{


						System.out.println("Found entopy 0");
					}

					System.out.println("Considering Attacker "+ atidconsidered + " entropy "+ entropy);

					double attprior = priorsattackertype.get(atidconsidered);


					/**
					 * measure the max overlap length
					 * work in favor of the attacker
					 */

					if((minentropy>entropy) /*&& (maxprior<attprior)*/)
					{
						minentropy = entropy;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;
						maxprior = attprior;
						minslots = slots;

					}
					else if(minentropy==entropy)
					{
						if(maxoverlaplen<overlaplen)
						{
							maxoverlaplen = overlaplen;
							minentropy = entropy;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
							maxprior = attprior;
							minslots = slots;
						}

					}

					System.out.println("Min entropy "+ minentropy + ", minsettings "+ minsettings);
					settingsid++;
				}

				//printPosteriors(posteriorlibrary, settingsid);

				

				//System.out.println("Hii");


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			insertHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		//printNetwork(net);




	}
	
	/*
	 * does not recompute the attackers policies repeatedly 
	 */
	private static void deployHPMinEntropyV2(HashMap<Integer, Node> net, HashMap<Integer, Node> honeypots,
			ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hpdeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments, int[] goals) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		System.out.println("Attacker current position "+ currentnodeid);
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		
		
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodes(net, currentnodeid, exploits);
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");


		
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

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());


		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minentropy = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int hp[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					hp[i] = freehps.get(hpid[i]);
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HP: ");

				System.out.print("[");
				for(int s: hp)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];
					
					addHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				
				/**
				 * compute attacker policies
				 */
				
				
				
				
				/*if(settingsid==506 && currentnodeid==4)
				{
					System.out.println("Attacker current position "+ currentnodeid);
					printNetwork(net);
				}*/


				/**
				 * attackpolicies for each attacker for a particular settings of honeypot
				 * attids--> polcicies
				 */
				//System.out.println("*********Attacker policies before adding honeypots*********");
				//printAttackers(attackers);


				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				
				//printAttackersPolicy(attackpolicies);
				
				

				for(Integer atidconsidered: attackers.keySet()) // defender considers atid
				{


					/*double overlaplen = measureDefenseMovesForEntropy(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, round, slots, hp, 
							posteriorlibrary, settingsid, atidconsidered, maxoverlap, expoverlap );

					*/
					double overlaplen = measureDefenseMovesForEntropyV2(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, round, slots, hp, 
							posteriorlibrary, settingsid, atidconsidered, maxoverlap, expoverlap, attackpolicies, currentnodeid );

					

					double entropy = computeEntropy(posteriorlibrary.get(settingsid), atidconsidered);

					/**
					 * if the entropy is zero then
					 * define some other criteria to choose the settings
					 */

					if(entropy==Double.NaN)
					{
						System.out.println("Found entropy NaN");
					}
					else if(entropy==0)
					{


						System.out.println("Found entopy 0");
					}

					System.out.println("Considering Attacker "+ atidconsidered + " entropy "+ entropy);

					double attprior = priorsattackertype.get(atidconsidered);


					/**
					 * measure the max overlap length
					 * work in favor of the attacker
					 */

					if((minentropy>entropy) /*&& (maxprior<attprior)*/)
					{
						minentropy = entropy;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;
						maxprior = attprior;
						minslots = slots;

					}
					else if(minentropy==entropy)
					{
						if(maxoverlaplen<overlaplen)
						{
							maxoverlaplen = overlaplen;
							minentropy = entropy;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
							maxprior = attprior;
							minslots = slots;
						}

					}

					System.out.println("Min entropy "+ minentropy + ", minsettings "+ minsettings);
					settingsid++;
				}
				
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];

					eliminateHoneyPot(nodepair, h, net, honeypots);
					//removeHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}

				//printPosteriors(posteriorlibrary, settingsid);

				

				//System.out.println("Hii");


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		//printNetwork(net);




	}
	
	
	
	/*
	 * does not recompute the attackers policies repeatedly 
	 * deploys honeyedge
	 */
	private static void deployHEMinEntropy(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, ArrayList<int[]> hpslots, ArrayList<int[]> hesdeployments, int[] goals, HashMap<Integer,Integer> currenthdges) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		System.out.println("Attacker current position "+ currentnodeid);
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		
		
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodes(net, currentnodeid, exploits);
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		//ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int helimit = hedeploylimit - currenthdges.size();

		if(helimit>placestoallocatehp.size())
		{
			helimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ helimit +" honeyedges from "+ exploits.size() + " exploits");


		
		if(helimit<=0)
		{
			System.out.println("We can deploy no honeyedges");
			return;
		}
		

		int slotlimit = helimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> heids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HE ids: ");
		createHPCombinations(heids, helimit, exploits.size());


		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minentropy = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: heids.keySet())
			{



				int[] heid = heids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = exploits.get(heid[i]).id;
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
					//addHoneyPot(nodepair, h, net, honeypots);
					
					addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				printNetwork(net);
				
				
				/**
				 * compute attacker policies
				 */
				
				
				
				
				/*if(settingsid==506 && currentnodeid==4)
				{
					System.out.println("Attacker current position "+ currentnodeid);
					printNetwork(net);
				}*/


				/**
				 * attackpolicies for each attacker for a particular settings of honeypot
				 * attids--> polcicies
				 */
				//System.out.println("*********Attacker policies before adding honeypots*********");
				//printAttackers(attackers);


				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				
				printAttackersPolicy(attackpolicies);
				
				

				for(Integer atidconsidered: attackers.keySet()) // defender considers atid
				{


					/*double overlaplen = measureDefenseMovesForEntropy(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, round, slots, hp, 
							posteriorlibrary, settingsid, atidconsidered, maxoverlap, expoverlap );

					*/
					
					
					
					double overlaplen = measureHEMovesForEntropy(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, 
							 net, exploits, attackers, oactions, singlepath, npath, round, slots, he, 
							posteriorlibrary, settingsid, atidconsidered, maxoverlap, expoverlap, attackpolicies, currentnodeid );

					

					double entropy = computeEntropy(posteriorlibrary.get(settingsid), atidconsidered);

					/**
					 * if the entropy is zero then
					 * define some other criteria to choose the settings
					 */

					if(entropy==Double.NaN)
					{
						System.out.println("Found entropy NaN");
					}
					else if(entropy==0)
					{


						System.out.println("Found entopy 0");
					}

					System.out.println("Considering Attacker "+ atidconsidered + " entropy "+ entropy);

					double attprior = priorsattackertype.get(atidconsidered);


					/**
					 * measure the max overlap length
					 * work in favor of the attacker
					 */

					if((minentropy>entropy) /*&& (maxprior<attprior)*/)
					{
						minentropy = entropy;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;
						maxprior = attprior;
						minslots = slots;

					}
					else if(minentropy==entropy)
					{
						if(maxoverlaplen<overlaplen)
						{
							maxoverlaplen = overlaplen;
							minentropy = entropy;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
							maxprior = attprior;
							minslots = slots;
						}

					}

					System.out.println("Min entropy "+ minentropy + ", minsettings "+ minsettings);
					settingsid++;
				}
				
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];

					eliminateHoneyEdge(nodepair, e, net, exploits);
					//removeHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				//printNetwork(net);

				//printPosteriors(posteriorlibrary, settingsid);

				

				//System.out.println("Hii");


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = heids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int he[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			he[i] = exploits.get(hpid[i]).id;
		}
		
		hesdeployments.add(he);


		for(int s: he)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int e = he[slid];

			//addHoneyPot(nodepair, h, net, honeypots);
			boolean ok = addHoneyEdge(nodepair, e, net, exploits);
			if(ok)
			{
				currenthdges.put(nodepair[1], e);
			}
			//insertHoneyPot(slot2, hp2, net, honeypots);
		}
		
		System.out.println("Selected HPs: ");

		/**
		 * fix it
		 */
		
		
		he = new int[currenthdges.size()];
		for(int i=0; i<hpid.length; i++)
		{
			he[i] = exploits.get(hpid[i]).id;
		}
		
		hesdeployments.add(he);
		

		//printNetwork(net);




	}
	
	
	
	public static double findMinCost(int startnodeid, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> allexploits, int goal, Attacker a) 
	{

		
		
		
		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(net.get(startnodeid));
		

		//double exploitcost = Attacker.minCostExploit(net.get(startnodeid), a.exploits, allexploits);
		start.currentcost +=  0;//exploitcost;

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
				}
				
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
					if(a.exploits.containsValue(neinodeexploit))
					{
						Node tmp = new Node(neinode);
						double exploitcost = Attacker.minCostExploit(neinode, a.exploits, allexploits);
						tmp.currentcost += /*node.currentreward + tmp.value - tmp.cost -*/node.currentcost + exploitcost;
						tmp.parent = node;
						fringequeue.add(tmp);


					}
				}

			}



		}
		return mincost;


	}
	
	
	
	
	/*
	 * does not recompute the attackers policies repeatedly 
	 * deploys honeyedge
	 */
	private static void deployHEMinMaxOverlapMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> hpslots, ArrayList<int[]> hesdeployments, int[] goals, ArrayList<String> currenthdges) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehe = PlanRecognition.computePlacesToAllocateHE(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehe);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehe.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		//ArrayList<Integer> freehe = findFreeHE(currenthdges, exploits);

		int helimit = hedeploylimit - currenthdges.size();

		if(helimit>placestoallocatehe.size())
		{
			helimit = placestoallocatehe.size();
		}

		System.out.println("We can deploy "+ helimit +" honeyedges from "+ exploits.size() + " exploits");


		
		if(helimit<=0)
		{
			System.out.println("We can deploy no honeyedges");
			return;
		}
		

		int slotlimit = helimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> heids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehe.size());
		System.out.println("HE ids: ");
		createHPCombinations(heids, helimit, exploits.size());

		
		System.out.println("total deployment settings: "+ slotids.size()*heids.size());

		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minheid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehe.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: heids.keySet())
			{



				int[] heid = heids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = exploits.get(heid[i]).id;
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
					//addHoneyPot(nodepair, h, net, honeypots);
					
					addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				

				int overlaplen = buildAttPolMaxOverlapHEMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id);
				
				printAttackersPolicy(attackpolicies);
				System.out.println("min costs: ");
				for(int a: mincost.keySet())
				{
					int c = mincost.get(a);
					
					System.out.print("a: "+ c+", ");
				}
				System.out.println();
				
				
				System.out.println("overlap length "+ overlaplen);

				if(minoverlap>overlaplen)
				{
					minoverlap = overlaplen;
					minsettings = settingsid;
					minslotid = s1;
					minheid = h1;
					minslots = slots;
					

				}
				System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);
				
				settingsid++;

				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];

					eliminateHoneyEdge(nodepair, e, net, exploits);
					

				}
				
				//printNetwork(net);

				
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int heid[] = heids.get(minheid);


		System.out.println("Selected slots: ");


		//HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehe.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HEs: ");

		int he[] = new int[heid.length];
		for(int i=0; i<heid.length; i++)
		{
			he[i] = exploits.get(heid[i]).id;
		}
		
		//hesdeployments.add(he);


		for(int s: he)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: minslots.keySet())
		{
			int[] nodepair = minslots.get(slid);
			int e = he[slid];

			//addHoneyPot(nodepair, h, net, honeypots);
			boolean ok = addHoneyEdge(nodepair, e, net, exploits);
			if(ok)
			{
				currenthdges.add(nodepair[1]+","+ e);
			}
			//insertHoneyPot(slot2, hp2, net, honeypots);
		}
		
		System.out.println("Selected HPs: ");

		
		
		he = new int[currenthdges.size()];
		int j=0;
		for(String s: currenthdges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int e = Integer.parseInt(che[1]);
			he[j++] = e;
		}
		
		hesdeployments.add(he);
		

		printNetwork(net);




	}
	
	
	
	
	/*
	 * does not recompute the attackers policies repeatedly 
	 * deploys honeyedge
	 */
	private static int[][][] deployHPMinMaxOverlapMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments, int[] goals, ArrayList<Integer> currenthps, HashMap<Integer,Node> honeypots, int hpdeploylimit) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		
		
		
		PlanRecognition.printSlots(placestoallocatehp);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		
		
		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);
		
		
		
		/**
		 * testing
		 */
		/*placestoallocatehp.clear();
		
		int p1[] = {1,7};
		int p2[] = {2,10};
		
		placestoallocatehp.put(0, p1);
		placestoallocatehp.put(1, p2);
		
		freehps.clear();
		
		freehps.add(15);
		freehps.add(16);*/
		

		
		

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");

		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		if(hplimit<=0)
		{
			System.out.println("We can deploy no honeypots");
			return w;
		}
		

		int slotlimit = hplimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> hpids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());

		int totalconf = slotids.size()*hpids.size();
		
		System.out.println("total deployment settings: "+ totalconf);

		
		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */

		
		

		int[][][][] hpdeploymentcost = new int[totalconf][net.size()+hplimit][net.size()+hplimit][exploits.size()];
		
		
		
		
		
		
		PlanrecognitionExp.buildCostVar(hpdeploymentcost, net, exploits, attackers.size(), exploits.size(), w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		



		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		int minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int h[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					h[i] = freehps.get(hpid[i]);
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HP: ");

				System.out.print("[");
				for(int s: h)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int hp = h[sid];
					
					addHoneyPot(nodepair, hp, net, honeypots);
					
					//addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				

				int overlaplen = buildAttPolMaxOverlapHPMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id, hpdeploymentcost[settingsid]);
				
				printAttackersPolicy(attackpolicies);
				System.out.println("min costs: ");
				for(int a: mincost.keySet())
				{
					int c = mincost.get(a);
					
					System.out.print("a: "+ c+", ");
				}
				System.out.println();
				
				
				System.out.println("overlap length "+ overlaplen);

				if(minoverlap>overlaplen)
				{
					minoverlap = overlaplen;
					minsettings = settingsid;
					minslotid = s1;
					minhpid = h1;
					minslots = slots;
					

				}
				System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);
				
				settingsid++;

				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int hp = h[sid];

					eliminateHoneyPot(nodepair, hp, net, honeypots);
					//removeHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				if(settingsid>450)
				{
					break;
				}

				//printNetwork(net);

				
			}
			if(settingsid>450)
			{
				break;
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
		

		printNetwork(net);
		
		return hpdeploymentcost[minsettings];




	}
	
	
	
	private static void deployHEMinMaxExpOverlapMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> heslots, ArrayList<int[]> hesdeployments, int[] goals, ArrayList<String> currenthdges, int chosenpolicy) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehe = PlanRecognition.computePlacesToAllocateHE(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehe);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehe.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		//ArrayList<Integer> freehe = findFreeHE(currenthdges, exploits);

		int helimit = hedeploylimit - currenthdges.size();

		if(helimit>placestoallocatehe.size())
		{
			helimit = placestoallocatehe.size();
		}

		System.out.println("We can deploy "+ helimit +" honeyedges from "+ exploits.size() + " exploits");


		
		if(helimit<=0)
		{
			System.out.println("We can deploy no honeyedges");
			return;
		}
		

		int slotlimit = helimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> heids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehe.size());
		System.out.println("HE ids: ");
		createHPCombinations(heids, helimit, exploits.size());

		
		System.out.println("total deployment settings: "+ slotids.size()*heids.size());

		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */


		HashMap<Integer, Integer> atmap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> atmapback = new HashMap<Integer, Integer>();
		int ind = 0;
		for(Attacker a: attackers.values())
		{
			atmap.put(a.id, ind);
			atmapback.put(ind++, a.id);
		}


		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minheid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehe.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: heids.keySet())
			{



				int[] heid = heids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = exploits.get(heid[i]).id;
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
					//addHoneyPot(nodepair, h, net, honeypots);
					
					addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				/**
				 * consider a chosen attacker 
				 * then use prior
				 */
				
				

				
				
				
				
				
				
					double[][] tmpoverlap = buildAttPolMaxExpOverlapHEMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id, atmap, atmapback);
					
					
					printAttackersPolicy(attackpolicies);
					System.out.println("min costs: ");
					for(int a: mincost.keySet())
					{
						int c = mincost.get(a);
						
						System.out.print("a: "+ c+", ");
					}
					System.out.println();
					
					
					//HashMap<Integer, Integer> chosenpol = attackpolicies.get(chosenatt.id).get(chosenpolicy);
					
					
					for(int i=0; i<tmpoverlap.length; i++)
					{
						double overlaplen = 0;
						int attpollen = attackpolicies.get(atmapback.get(i)).get(0).size(); // 1 policy only
						for(int j=0; j<tmpoverlap.length; j++)
						{
							
							if(i != j)
							{
							
								overlaplen += tmpoverlap[i][j]/attpollen;
							}
							
						}
						System.out.println("overlap length "+ overlaplen);

						if(minoverlap>overlaplen)
						{
							minoverlap = overlaplen;
							minsettings = settingsid;
							minslotid = s1;
							minheid = h1;
							minslots = slots;
							

						}
						System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);
						
					}
					
					
					
					settingsid++;
				
				
				
				/*
				for(int i=0; i<tmpoverlap.length-1; i++)
				{
					for(int j=i+1; j<tmpoverlap.length; j++)
					{
						overlaplen += (priorsattackertype.get(atmapback.get(i))*tmpoverlap[i][j]);
					}
				}*/
				
				
				

				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];

					eliminateHoneyEdge(nodepair, e, net, exploits);
					

				}
				
				//printNetwork(net);

				
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int heid[] = heids.get(minheid);


		System.out.println("Selected slots: ");


		//HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehe.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HEs: ");

		int he[] = new int[heid.length];
		for(int i=0; i<heid.length; i++)
		{
			he[i] = exploits.get(heid[i]).id;
		}
		
		//hesdeployments.add(he);


		for(int s: he)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: minslots.keySet())
		{
			int[] nodepair = minslots.get(slid);
			int e = he[slid];

			//addHoneyPot(nodepair, h, net, honeypots);
			boolean ok = addHoneyEdge(nodepair, e, net, exploits);
			if(ok)
			{
				heslots.add(nodepair);
				currenthdges.add(nodepair[1]+","+ e);
			}
			//insertHoneyPot(slot2, hp2, net, honeypots);
		}
		
		System.out.println("Selected HE: ");

		
		
		he = new int[currenthdges.size()];
		int j=0;
		for(String s: currenthdges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int e = Integer.parseInt(che[1]);
			he[j++] = e;
		}
		
		hesdeployments.add(he);
		

		printNetwork(net);




	}
	
	
	private static int[][][] deployHPMinMaxExpOverlapMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments, int[] goals, ArrayList<Integer> currenthps, HashMap<Integer,Node> honeypots, int hpdeploylimit) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		
		

		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		
		
		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");

		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		if(hplimit<=0)
		{
			System.out.println("We can deploy no honeypots");
			return w;
		}
		

		int slotlimit = hplimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> hpids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());

		int totalconf = slotids.size()*hpids.size();
		
		System.out.println("total deployment settings: "+ totalconf);

		
		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */

		
		

		int[][][][] hpdeploymentcost = new int[totalconf][net.size()+hplimit][net.size()+hplimit][exploits.size()];
		
		
		
		
		
		
		PlanrecognitionExp.buildCostVar(hpdeploymentcost, net, exploits, attackers.size(), exploits.size(), w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */


		HashMap<Integer, Integer> atmap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> atmapback = new HashMap<Integer, Integer>();
		int ind = 0;
		for(Attacker a: attackers.values())
		{
			atmap.put(a.id, ind);
			atmapback.put(ind++, a.id);
		}


		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		int minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: hpids.keySet())
			{



				int[] heid = hpids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = freehps.get(heid[i]);
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
				
					
					addHoneyPot(nodepair, e, net, honeypots);
					
					//addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				/**
				 * consider a chosen attacker 
				 * then use prior
				 */
				
				double[][] tmpoverlap = buildAttPolMaxExpOverlapHPMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id, atmap, 
							atmapback, hpdeploymentcost[settingsid]);
					
					
					printAttackersPolicy(attackpolicies);
					System.out.println("min costs: ");
					for(int a: mincost.keySet())
					{
						int c = mincost.get(a);
						
						System.out.print("a: "+ c+", ");
					}
					System.out.println();
					
					
					//HashMap<Integer, Integer> chosenpol = attackpolicies.get(chosenatt.id).get(chosenpolicy);
					
					
					for(int i=0; i<tmpoverlap.length; i++)
					{
						double overlaplen = 0;
						int attpollen = attackpolicies.get(atmapback.get(i)).get(0).size(); // 1 policy only
						for(int j=0; j<tmpoverlap.length; j++)
						{
							
							if(i != j)
							{
							
								overlaplen += tmpoverlap[i][j]/attpollen;
							}
							
						}
						System.out.println("overlap length "+ overlaplen);

						if(minoverlap>overlaplen)
						{
							minoverlap = overlaplen;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
							minslots = slots;
							

						}
						System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);
						
					}
					
					
					
					settingsid++;
					
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					eliminateHoneyPot(nodepair, e, net, honeypots);

				}
				
				//printNetwork(net);
				if(settingsid==ST_LIMIT)
				{
					break;
				}

				
			}
			if(settingsid==ST_LIMIT)
			{
				break;
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		
		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
		

		printNetwork(net);
		
		return hpdeploymentcost[minsettings];



	}
	
	
	private static void deployHEMinEntropyMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> heslots, ArrayList<int[]> hesdeployments, int[] goals, ArrayList<String> currenthdges, int chosenpolicy) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehe = PlanRecognition.computePlacesToAllocateHE(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehe);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehe.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		//ArrayList<Integer> freehe = findFreeHE(currenthdges, exploits);

		int helimit = hedeploylimit - currenthdges.size();

		if(helimit>placestoallocatehe.size())
		{
			helimit = placestoallocatehe.size();
		}

		System.out.println("We can deploy "+ helimit +" honeyedges from "+ exploits.size() + " exploits");


		
		if(helimit<=0)
		{
			System.out.println("We can deploy no honeyedges");
			return;
		}
		

		int slotlimit = helimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> heids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehe.size());
		System.out.println("HE ids: ");
		createHPCombinations(heids, helimit, exploits.size());

		
		System.out.println("total deployment settings: "+ slotids.size()*heids.size());

		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */


		HashMap<Integer, Integer> atmap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> atmapback = new HashMap<Integer, Integer>();
		int ind = 0;
		for(Attacker a: attackers.values())
		{
			atmap.put(a.id, ind);
			atmapback.put(ind++, a.id);
		}


		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minentropy = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minheid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehe.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: heids.keySet())
			{



				int[] heid = heids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = exploits.get(heid[i]).id;
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
					//addHoneyPot(nodepair, h, net, honeypots);
					
					addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				/**
				 * consider a chosen attacker 
				 * then use prior
				 */
				
				

				
				
				
				
				
				
					double[][] tmpoverlap = buildAttPolMaxExpOverlapHEMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id, atmap, atmapback);
					
					
					printAttackersPolicy(attackpolicies);
					System.out.println("min costs: ");
					for(int a: mincost.keySet())
					{
						int c = mincost.get(a);
						
						System.out.print("a: "+ c+", ");
					}
					System.out.println();
					
					
					//HashMap<Integer, Integer> chosenpol = attackpolicies.get(chosenatt.id).get(chosenpolicy);
					
					
					for(int consideredatt: attackers.keySet())
					{
						double tmpentropy = entropy(attackpolicies, consideredatt, currentnodeid, attackers, priorsattackertype, net);
						
						
						
						
						System.out.println("entropy "+ tmpentropy);

						if(minentropy>tmpentropy)
						{
							
							minentropy = tmpentropy;
							minsettings = settingsid;
							minslotid = s1;
							minheid = h1;
							minslots = slots;
							

						}
						System.out.println("Min entropy "+ minentropy + ", minsettings "+ minsettings);
						
					}
					
					
					
					settingsid++;
				
				
				
				/*
				for(int i=0; i<tmpoverlap.length-1; i++)
				{
					for(int j=i+1; j<tmpoverlap.length; j++)
					{
						overlaplen += (priorsattackertype.get(atmapback.get(i))*tmpoverlap[i][j]);
					}
				}*/
				
				
				

				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];

					eliminateHoneyEdge(nodepair, e, net, exploits);
					

				}
				
				//printNetwork(net);

				
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int heid[] = heids.get(minheid);


		System.out.println("Selected slots: ");


		//HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehe.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HEs: ");

		int he[] = new int[heid.length];
		for(int i=0; i<heid.length; i++)
		{
			he[i] = exploits.get(heid[i]).id;
		}
		
		//hesdeployments.add(he);


		for(int s: he)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: minslots.keySet())
		{
			int[] nodepair = minslots.get(slid);
			int e = he[slid];

			//addHoneyPot(nodepair, h, net, honeypots);
			boolean ok = addHoneyEdge(nodepair, e, net, exploits);
			if(ok)
			{
				heslots.add(nodepair);
				currenthdges.add(nodepair[1]+","+ e);
			}
			//insertHoneyPot(slot2, hp2, net, honeypots);
		}
		
		System.out.println("Selected HPs: ");

		
		
		he = new int[currenthdges.size()];
		int j=0;
		for(String s: currenthdges)
		{
			String[] che = s.split(",");
			
			int nodeid = Integer.parseInt(che[0]);
			int e = Integer.parseInt(che[1]);
			he[j++] = e;
		}
		
		hesdeployments.add(he);
		

		printNetwork(net);




	}
	
	
	
	private static int[][][] deployHPMinEntropyMILP(HashMap<Integer, Node> net, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int round, 
			int hedeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, 
			ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments, int[] goals, ArrayList<Integer> currenthps, HashMap<Integer,Node> honeypots, int hpdeploylimit) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		
		
		


		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current position "+ currentnodeid);
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int gl: goals)
		{
			g.add(gl);
		}
		
		System.out.print("reachable nodes : ");
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodesMILP(net, currentnodeid, exploits, g);
		
		for(int n: reachablesnodes)
		{
			System.out.print(n +" ");
		}
		
		System.out.println();
		
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		
		
		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

		System.out.println("We can deploy "+ hplimit +" honeypots from "+ freehps.size() + " honeypots");


		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		
		if(hplimit<=0)
		{
			System.out.println("We can deploy no honeypots");
			return w;
		}
		

		int slotlimit = hplimit;
		System.out.println("#slotslimit "+ slotlimit);


		/**
		 * create combinations of placestoallocatehp
		 */
		HashMap<Integer, int[]> slotids = new HashMap<Integer, int[]>();
		HashMap<Integer, int[]> hpids = new HashMap<Integer, int[]>();

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());

		int totalconf = slotids.size()*hpids.size();
		
		System.out.println("total deployment settings: "+ totalconf);

		
		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */


		int[][][][] hpdeploymentcost = new int[totalconf][net.size()+hplimit][net.size()+hplimit][exploits.size()];
		
		
		
		
		
		
		PlanrecognitionExp.buildCostVar(hpdeploymentcost, net, exploits, attackers.size(), exploits.size(), w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		//refineSlots(slotids, placestoallocatehp);


		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */


		HashMap<Integer, Integer> atmap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> atmapback = new HashMap<Integer, Integer>();
		int ind = 0;
		for(Attacker a: attackers.values())
		{
			atmap.put(a.id, ind);
			atmapback.put(ind++, a.id);
		}

		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */
		
		double maxoverlaplen = -1;

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minentropy = Double.POSITIVE_INFINITY;
		double maxprior = Double.NEGATIVE_INFINITY;
		int minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		HashMap<Integer, int[]> minslots = new HashMap<Integer, int[]>();
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
			
			//slots = refineSlots(slots);
			
			System.out.println("sss");
			
			//slots = refineSlots(slots);
			
			if(slots.size()==0)
			{
				settingsid++;
				continue;
			}


			for(int h1: hpids.keySet())
			{



				int[] heid = hpids.get(h1);

				int he[] = new int[heid.length];
				for(int i=0; i<heid.length; i++)
				{
					he[i] = freehps.get(heid[i]);
				}




				System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}

				
				/*
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}*/
				
				System.out.println("HE: ");

				System.out.print("[");
				for(int s: he)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */

				
				/**
				 * deploy the honeypots
				 */
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];
					
					addHoneyPot(nodepair, e, net, honeypots);
					
					//addHoneyEdge(nodepair, e, net, exploits);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				
				//printNetwork(net);
				
				

				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				
				/*HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				printAttackersPolicy(attackpolicies);*/
				
				HashMap<Integer, Integer> mincost = new HashMap<Integer, Integer>();
				
				
				for(Attacker a: attackers.values())
				{
					int minc = (int)findMinCost(currentnodeid, net, exploits, a.goals.get(0), a);
					mincost.put(a.id, minc);
					
				}
				
				
				/**
				 * consider a chosen attacker 
				 * then use prior
				 */
				double[][] tmpoverlap = buildAttPolMaxExpOverlapHPMILP(net, exploits, goals, attackers, mincost, attackpolicies, priorsattackertype, curnode.id, atmap, 
						atmapback, hpdeploymentcost[settingsid]);
					
					
					printAttackersPolicy(attackpolicies);
					System.out.println("min costs: ");
					for(int a: mincost.keySet())
					{
						int c = mincost.get(a);
						
						System.out.print("a: "+ c+", ");
					}
					System.out.println();
					
					
					//HashMap<Integer, Integer> chosenpol = attackpolicies.get(chosenatt.id).get(chosenpolicy);
					
					
					for(int consideredatt: attackers.keySet())
					{
						double tmpentropy = entropy(attackpolicies, consideredatt, currentnodeid, attackers, priorsattackertype, net);
						
						
						
						
						System.out.println("entropy "+ tmpentropy);

						if(minentropy>tmpentropy)
						{
							
							minentropy = tmpentropy;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
							minslots = slots;
							

						}
						System.out.println("Min entropy "+ minentropy + ", minsettings "+ minsettings);
						
					}
					
					
					
					settingsid++;
					
					
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int e = he[sid];

					eliminateHoneyPot(nodepair, e, net, honeypots);
					//eliminateHoneyEdge(nodepair, e, net, exploits);
					

				}
				
				if(settingsid==ST_LIMIT)
				{
					break;
				}
				
				//printNetwork(net);

				
			}
			
			if(settingsid==ST_LIMIT)
			{
				break;
			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */


		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("Selected slots: ");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>(minslots);
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			//slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}




		System.out.println("Selected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
		

		printNetwork(net);

		return hpdeploymentcost[minsettings];


	}
	
	
	
	
	
	
	private static double entropy(HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies,
			int atidconsidered, int currentnodeid, HashMap<Integer,Attacker> attackers, HashMap<Integer,Double> priorsattackertype, HashMap<Integer,Node> net) throws Exception {
		
		
		
		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Integer> tmpoactions = new HashMap<Integer, Integer>();



		//for(Integer attid: attackpolicies.keySet())
		Integer attid = atidconsidered;
		
			System.out.println("Considering attacker "+ attid +" as the attacker to compute posteriors.\npolicy:..");
			HashMap<Integer, Integer> tmppolicy = attackpolicies.get(attid).get(0);

			
			

				for(Integer p: tmppolicy.values())
				{
					System.out.print(p+" ");
				}

				System.out.println();

				tmpoactions = new HashMap<Integer, Integer>();
				tmpoactions.put(tmpoactions.size(), currentnodeid);

				if(tmppolicy.get(1)==null)
				{

					throw new Exception("Found null in policy");
				}

				tmpoactions.put(tmpoactions.size(), tmppolicy.get(1));

				System.out.println("Attacker "+ attid +" next round "+(1)+" move: "+ tmppolicy.get(1));

				

				/**
				 * this method supports only single policy per attacker
				 */
				HashMap<Integer, Double> tmpposteriors = computePosteriorAttTypeWithPolicy(tmpoactions, attackers, net, priorsattackertype, attackpolicies);



				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();

				System.out.println("Posteriors: ");
				for(Integer in: tmpposteriors.keySet())
				{

					//tmppost[in] = tmpposteriors.get(in);

					tmppost.put(in, tmpposteriors.get(in));

					System.out.println("Att "+ in + " posterior : "+ tmppost.get(in));
				}
				posteriors.put(attid, tmppost);


				//Integer key = settingsid;

				//posteriorlibrary.put(settingsid, posteriors);


				double entropy = computeEntropy(posteriors, atidconsidered);
				
				return entropy;

			




		
	}




	private static void refineSlots(HashMap<Integer, int[]> slotids, HashMap<Integer, int[]> placestoallocatehp) 
	{
		
		
		
		
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}
		}
		
	}




	private static HashMap<Integer, int[]> refineSlots(HashMap<Integer, int[]> slots) {



		if(slots.size()==1)
			return slots;


		HashMap<Integer, int[]> s = new HashMap<Integer, int[]>();


		int[] sl1 = slots.get(0);
		int[] sl2 = slots.get(1);
		
		int t = 1; // traingle defense
		
		
		if(t==1)
		{

			if(sl1[0] == sl2[0])
			{
				s.put(0, sl1);
				s.put(1, sl2);
				//return s;
			}
		}
		else
		{
			if(sl1[1] == sl2[0] || sl1[0] == sl2[1])
			{
				s.put(0, sl1);
			}
			else
			{
				s.put(0, sl1);
				s.put(1, sl2);
			}
		}
		

		



		return s;


	}




	private static void deployHPMinCommonOverlap(HashMap<Integer, Node> net, HashMap<Integer, Node> honeypots,
			ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, 
			int round, int hpdeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");

		HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

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

		//System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		//System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());





		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int hp[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					hp[i] = freehps.get(hpid[i]);
				}




				/*System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}


				System.out.println("HP: ");

				System.out.print("[");
				for(int s: hp)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");*/



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */
				
				/*if(settingsid==341)
				{
					System.out.println("hi2");
				}
*/
				
				
				/** Need attacker loop
				 * 
				 */
				
				
				
				
				
				
				for(Integer possibleatid: attackers.keySet())
				{	

					double overlaplength = measureDefenseMovesForOverlap(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, 
							round, slots, hp, posteriorlibrary, settingsid, possibleatid, maxoverlap, expoverlap);

					System.out.println("overlap length "+ overlaplength);

					if(minoverlap>overlaplength)
					{
						minoverlap = overlaplength;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;

					}
					System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);


					//printPosteriors(posteriorlibrary, settingsid);

					settingsid++;

					//System.out.println("Hii");
				}


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("*************Round "+round+"\nSelected slots: **************");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			slots.put(slots.size(), slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}


		/*printNetwork(net);
		System.out.println("\n******* HPs: ");
		printNetwork(honeypots);*/


		System.out.println("\nSelected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			insertHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		//printNetwork(net);
		System.out.print("********Honeypot dployed*************");




	}
	
	
	/**
	 * does not compute attacker polciies repeatedly 
	 * @param net
	 * @param honeypots
	 * @param currenthps
	 * @param oactions
	 * @param attackers
	 * @param chosenatt
	 * @param chosenattackerpolicy
	 * @param singlepath
	 * @param npath
	 * @param priorsattackertype
	 * @param priorforplang
	 * @param round
	 * @param hpdeploylimit
	 * @param startnodeid
	 * @param exploits
	 * @param maxoverlap
	 * @param expoverlap
	 * @param goals 
	 * @throws Exception
	 */
	private static void deployHPMinCommonOverlapV2(HashMap<Integer, Node> net, HashMap<Integer, Node> honeypots,
			ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, 
			int round, int hpdeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, int[] goals) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		System.out.println("Attacker current position "+ currentnodeid);
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		
		
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodes(net, currentnodeid, exploits);
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);
		

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

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

		//System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		//System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());





		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int hp[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					hp[i] = freehps.get(hpid[i]);
				}




				/*System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}


				System.out.println("HP: ");

				System.out.print("[");
				for(int s: hp)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");*/



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */
				
				/*if(settingsid==341)
				{
					System.out.println("hi2");
				}
*/
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];

					addHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				

				//System.out.println("Attacker current position "+ currentnodeid);


				/**
				 * attackpolicies for each attacker for a particular settings of honeypot
				 * attids--> polcicies
				 */
				//System.out.println("*********Attacker policies before adding honeypots*********");
				//printAttackers(attackers);


				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				//double overlaplength = -1;
				HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				
				

				
				
				for(Integer possibleatid: attackers.keySet())
				{	

					double overlaplength = measureDefenseMovesForOverlapV2(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, 
							round, slots, hp, posteriorlibrary, settingsid, possibleatid, maxoverlap, expoverlap, attackpolicies, currentnodeid);

					System.out.println("overlap length "+ overlaplength);

					if(minoverlap>overlaplength)
					{
						minoverlap = overlaplength;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;

					}
					System.out.println("Min overlap "+ minoverlap + ", minsettings "+ minsettings);


					//printPosteriors(posteriorlibrary, settingsid);

					settingsid++;

					//System.out.println("Hii");
				}
				
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];

					eliminateHoneyPot(nodepair, h, net, honeypots);
					//removeHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("*************Round "+round+"\nSelected slots: **************");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			slots.put(slots.size(), slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}


		/*printNetwork(net);
		System.out.println("\n******* HPs: ");
		printNetwork(honeypots);*/


		System.out.println("\nSelected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			//insertHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		//printNetwork(net);
		System.out.print("********Honeypot dployed*************");




	}
	
	private static void deployHPMinCost(HashMap<Integer, Node> net, HashMap<Integer, Node> honeypots,
			ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions, HashMap<Integer, Attacker> attackers, Attacker chosenatt, 
			HashMap<Integer,Integer> chosenattackerpolicy, boolean singlepath, int npath,
			HashMap<Integer, Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, 
			int round, int hpdeploylimit, int startnodeid, HashMap<Integer,Exploits> exploits, boolean maxoverlap, boolean expoverlap, int[] goals,
			ArrayList<int[]> hpslots, ArrayList<int[]> hpsdeployments) throws Exception {



		/**
		 * Now defender makes a defensive move naively
		 * 1. See which slots are free
		 * 2. see which honeypots are free
		 * 3. compute posterior for every combination of hps and networks for each attacker
		 * 4. Then deploy the hp where the prob for type increases most
		 * 5. If the prob does not increase, see if the goal increases
		 */

		System.out.println("Finding placecs where honetpots can be placed");
		
		
		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		System.out.println("Attacker current position "+ currentnodeid);
		
		
		
		
		
		Node curnode = net.get(currentnodeid);
		
		System.out.println("Attacker current node depth "+ curnode.depth);
		
		
		
		ArrayList<Integer> reachablesnodes = PlanrecognitionExp.findReachableNodes(net, currentnodeid, exploits);
		
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
		
		ArrayList<Integer> gls = new ArrayList<Integer>();
		
		for(int i=0; i<goals.length; i++)
		{
			gls.add(goals[i]);
		}
		
		
		
		
		HashMap<Integer, int[]> placestoallocatehp = PlanRecognition.computePlacesToAllocateHP(reachablesnodes, net, gls, curnode);

		
		PlanRecognition.printSlots(placestoallocatehp);
		

		//HashMap<Integer, int[]> placestoallocatehp = computePlacesToAllocateHP(net, honeypots, currenthps, oactions);

		if(placestoallocatehp.size()==1)
		{
			System.out.println("One slot only");
		}


		/**
		 * Now compute which honeypots are free
		 * Which are currently not being used
		 */

		ArrayList<Integer> freehps = findFreeHP(currenthps, honeypots);

		int hplimit = hpdeploylimit - currenthps.size();

		if(hplimit>placestoallocatehp.size())
		{
			hplimit = placestoallocatehp.size();
		}

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

		System.out.println("Slot ids: ");
		createSlotCombinations(slotids, slotlimit, placestoallocatehp.size());
		System.out.println("HP ids: ");
		createHPCombinations(hpids, hplimit, freehps.size());





		/**
		 * create all the settings
		 * then iterate over them
		 * 
		 * Slots
		 * Honeypots: freehps
		 */





		/**
		 * For each settings we need to compute the posteriors
		 * Then pick a setting which has the max posterior
		 */

		int settingsid = 0;
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> posteriorlibrary = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		double minoverlap = Double.POSITIVE_INFINITY;
		double mincost = Double.POSITIVE_INFINITY;
		double minsettings = -1;
		int minslotid = -1;
		int minhpid = -1;
		for(int s1: slotids.keySet())
		{


			int[] slid = slotids.get(s1);

			HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
			for(int i=0; i<slid.length; i++)
			{
				int[] slot1 = placestoallocatehp.get(slid[i]);
				slots.put(slots.size(), slot1);
			}


			for(int h1: hpids.keySet())
			{



				int[] hpid = hpids.get(h1);

				int hp[] = new int[hpid.length];
				for(int i=0; i<hpid.length; i++)
				{
					hp[i] = freehps.get(hpid[i]);
				}




				/*System.out.println("\nSettings "+ settingsid +": \nslots: ");
				for(int[] sl: slots.values())
				{
					System.out.print("[");
					for(int s: sl)
					{
						System.out.print(s+", ");
					}
					System.out.println("]");
				}


				System.out.println("HP: ");

				System.out.print("[");
				for(int s: hp)
				{
					System.out.print(s+", ");
				}
				System.out.println("]");*/



				/*if(settingsid==1)
			{

				printNetwork(net);
				printNetwork(honeypots);
			}
				 */
				
				/*if(settingsid==341)
				{
					System.out.println("hi2");
				}
*/
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];

					addHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
			//	printNetwork(net);
				
				

				//System.out.println("Attacker current position "+ currentnodeid);


				/**
				 * attackpolicies for each attacker for a particular settings of honeypot
				 * attids--> polcicies
				 */
				//System.out.println("*********Attacker policies before adding honeypots*********");
				//printAttackers(attackers);


				/**
				 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
				 */
				HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
				//double overlaplength = -1;
				HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
				computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
				
				
				

				
				
				for(Integer possibleatid: attackers.keySet())
				{	

					double overlaplength = measureDefenseMovesForMinCost(priorsattackertype, priorforplang, startnodeid,placestoallocatehp, freehps, 
							honeypots, net, exploits, attackers, oactions, singlepath, npath, 
							round, slots, hp, posteriorlibrary, settingsid, possibleatid, maxoverlap, expoverlap, attackpolicies, currentnodeid);

					
					double tmpmincost = 0;
					
					for(Integer a: priorsattackertype.keySet())
					{
						
						tmpmincost+= (priorsattackertype.get(a)*costs.get(a));
						
					}
					
					//costs.get(possibleatid);
					System.out.println("tmpcost "+ tmpmincost);
					System.out.println("overlap length "+ overlaplength);

					
					
					if(mincost>tmpmincost)
					{
						mincost = tmpmincost;
						minoverlap = overlaplength;
						minsettings = settingsid;
						minslotid = s1;
						minhpid = h1;

					}
					else if(mincost == tmpmincost)
					{
						if(minoverlap<overlaplength)
						{
							mincost = tmpmincost;
							minoverlap = overlaplength;
							minsettings = settingsid;
							minslotid = s1;
							minhpid = h1;
						}
					}
					System.out.println("mincost "+mincost+", Min overlap "+ minoverlap + ", minsettings "+ minsettings);


					//printPosteriors(posteriorlibrary, settingsid);

					settingsid++;

					//System.out.println("Hii");
				}
				
				
				for(Integer sid: slots.keySet())
				{
					int[] nodepair = slots.get(sid);
					int h = hp[sid];

					eliminateHoneyPot(nodepair, h, net, honeypots);
					//removeHoneyPot(nodepair, h, net, honeypots);
					//insertHoneyPot(slot2, hp2, net, honeypots);


				}
				
				//printNetwork(net);


			}

		}// end of for loop


		//printNetwork(net);

		/**
		 * deploy the HP with min entropy
		 * update the currenthps
		 */

		int sltid[] = slotids.get(minslotid);
		int hpid[] = hpids.get(minhpid);


		System.out.println("*************Round "+round+"\nSelected slots: **************");


		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		for(int i=0; i<sltid.length; i++)
		{
			int[] slot1 = placestoallocatehp.get(sltid[i]);
			slots.put(slots.size(), slot1);
			
			hpslots.add(slot1);

			System.out.print("[");
			for(int s: slot1)
			{
				System.out.print(s+" ");
			}
			System.out.print("]");

		}


		/*printNetwork(net);
		System.out.println("\n******* HPs: ");
		printNetwork(honeypots);*/


		System.out.println("\nSelected HPs: ");

		int hp[] = new int[hpid.length];
		for(int i=0; i<hpid.length; i++)
		{
			hp[i] = freehps.get(hpid[i]);
		}
		
		hpsdeployments.add(hp);


		for(int s: hp)
		{
			System.out.print(s+" ");
		}
		System.out.println("");

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int h = hp[slid];

			addHoneyPot(nodepair, h, net, honeypots);
			//insertHoneyPot(nodepair, h, net, honeypots);
			currenthps.add(h);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		//printNetwork(net);
		System.out.print("********Honeypot dployed*************");




	}
	
	


	/**
	 * posteriors considering different attacker id for the settings
	 * @param atid 
	 * @param posterior
	 * @return
	 */
	private static double computeEntropy(HashMap<Integer,HashMap<Integer,Double>> posterior, Integer atid) {

		
		
		
		/**
		 * check if the sum is zero
		 */
		
		
		double sum = 0;

		/*if(hashMap.containsKey(atid))
		{

			HashMap<Integer, Double> p = hashMap.get(atid);

			for(double d: p.values())
			{
				if(d==0)
				{
					sum +=d;
					//break;
				}
				
			}
		}
		
		if(sum==0)
		{
			return 999;
		}*/
		
		

		sum = 0;

		if(posterior.containsKey(atid))
		{

			HashMap<Integer, Double> p = posterior.get(atid);

			for(double d: p.values())
			{
				if(d==0)
				{
					sum +=0;
					//break;
				}
				else
				{
					sum += -(d*(Math.log(d)/Math.log(2.0)));
					System.out.println("entropy sum "+ sum);
				}
			}
		}
		else
		{
			return 5;
		}

		//sum = -sum;

		return sum;
	}


	private static void printPosteriors(HashMap<Integer, HashMap<Integer, double[]>> posteriorlibrary, int settingsid) {


		System.out.println("Posteriors: "+ settingsid);




	}


	public static void createSlotCombinations(HashMap<Integer, int[]> slotids, int slotlimit, int numberofslots) {


		Integer[] input = new Integer[numberofslots];
		int[] branch = new int[slotlimit];//{0,0};//new char[k];

		for(int i=0; i<input.length; i++)
		{
			input[i] = i;
		}

		HashSet jSet=new HashSet();
		jSet=combine(input, slotlimit, 0, branch, 0, jSet);
		List<ArrayList<Integer>> jset = new ArrayList<ArrayList<Integer>>(jSet);
		//printSlotIdPairs(jset);
		/**
		 * now insert the id pairs in hashmap
		 */


		for(int i=0; i<jset.size(); i++)
		{
			//System.out.print("[");
			int[] slotpairs = new int[slotlimit];
			for(int j=0; j<jset.get(i).size(); j++)
			{
				//System.out.print(jset.get(i).get(j)+" ");
				slotpairs[j] = jset.get(i).get(j);
			}
			slotids.put(slotids.size(), slotpairs);
			//System.out.println("]");
		}



		if(slotlimit>1)
		{

			HashMap<Integer, int[]> tmpslotids = new HashMap<Integer, int[]>();
			for(int pair[]: slotids.values())
			{
				int[] rev = revArray(pair);
				tmpslotids.put(tmpslotids.size(), rev);
			}


			for(int pair[]: tmpslotids.values())
			{

				slotids.put(slotids.size(), pair);
			}
		}

		printSlotidPairs(slotids);
		//System.out.println("x");



	}


	public static void createHPCombinations(HashMap<Integer, int[]> slotids, int slotlimit, int numberofslots) {


		Integer[] input = new Integer[numberofslots];
		int[] branch = new int[slotlimit];//{0,0};//new char[k];

		for(int i=0; i<input.length; i++)
		{
			input[i] = i;
		}

		HashSet jSet=new HashSet();
		jSet=combine(input, slotlimit, 0, branch, 0, jSet);
		List<ArrayList<Integer>> jset = new ArrayList<ArrayList<Integer>>(jSet);
		//printSlotIdPairs(jset);
		/**
		 * now insert the id pairs in hashmap
		 */


		for(int i=0; i<jset.size(); i++)
		{
			//System.out.print("[");
			int[] slotpairs = new int[slotlimit];
			for(int j=0; j<jset.get(i).size(); j++)
			{
				//System.out.print(jset.get(i).get(j)+" ");
				slotpairs[j] = jset.get(i).get(j);
			}
			slotids.put(slotids.size(), slotpairs);
			//System.out.println("]");
		}



		/*if(slotlimit>1)
		{

			HashMap<Integer, int[]> tmpslotids = new HashMap<Integer, int[]>();
			for(int pair[]: slotids.values())
			{
				int[] rev = revArray(pair);
				tmpslotids.put(tmpslotids.size(), rev);
			}


			for(int pair[]: tmpslotids.values())
			{

				slotids.put(slotids.size(), pair);
			}
		}*/

		printSlotidPairs(slotids);



	}

	private static void printSlotidPairs(HashMap<Integer, int[]> slotids) {


		//System.out.println("slots: ");
		for(int pair[]: slotids.values())
		{
			System.out.print("[");
			for(int i: pair)
			{
				System.out.print(i+",");
			}
			System.out.println("]");
		}

	}


	private static int[] revArray(int[] pair) {

		int rev[] = new int[pair.length];


		for(int i=0; i<pair.length; i++)
		{
			rev[i] = pair[rev.length-i-1];
		}


		return rev;
	}


	public static void printSlotIdPairs(List<ArrayList<Integer>> jset) {

		System.out.println();
		for(int i=0; i<jset.size(); i++)
		{
			System.out.print("[");
			for(int j=0; j<jset.get(i).size(); j++)
			{
				System.out.print(jset.get(i).get(j)+",");
			}
			System.out.println("]");
		}
		System.out.println();


	}

	public static HashSet combine(Integer[] arr, int k, int startId, int[] branch, int numElem,HashSet arrSet)
	{
		if (numElem == k)
		{
			//System.out.println("k: "+k+(Arrays.toString(branch)));
			ArrayList<Integer> mySet = new ArrayList<Integer>();
			for(int i=0;i<branch.length;i++)
			{
				mySet.add(branch[i]);
			}
			arrSet.add(mySet);
			return arrSet;
		}

		for (int i = startId; i < arr.length; ++i)
		{
			branch[numElem++]=arr[i];
			combine(arr, k, ++startId, branch, numElem, arrSet);
			--numElem;
		}
		return arrSet;
	}
	
	public static HashSet combineHash(Integer[] arr, int k, int startId, int[] branch, int numElem,HashSet arrSet, HashMap<Integer,Integer> pathlimits)
	{
		if (numElem == k)
		{
			//System.out.println("k: "+k+(Arrays.toString(branch)));
			ArrayList<Integer> mySet = new ArrayList<Integer>();
			for(int i=0;i<branch.length;i++)
			{
				mySet.add(branch[i]);
			}
			arrSet.add(mySet);
			return arrSet;
		}

		//int[] arr1 = new int
		
		
		for (int i = startId; i < arr.length; ++i)
		{
			
			//if(arr[i]<pathlimits.get(i))
			
			branch[numElem++]=arr[i];
			combine(arr, k, ++startId, branch, numElem, arrSet);
			--numElem;
		}
		return arrSet;
	}


	private static void freeInvalidHoneypots(ArrayList<Integer> currenthps, Node curnode, HashMap<Integer,Node> net, HashMap<Integer,Node> honeypots, int currentnodeid) throws Exception {

		if(currenthps.size()>2)
		{
			System.out.println("More than two HP existings ");
			throw new Exception("More than two HP existings");
		}


		//printNetwork(net);
		//Node curnode = net.get(curnode2);
		//System.out.println("Attacker current position node "+ attcurposid);
		System.out.println("Current using HP: ");
		for(Integer hpid: currenthps)
		{
			System.out.print(hpid+" ");
		}
		System.out.println();

		ArrayList<Integer> unreachablehp = new ArrayList<Integer>();

		for(Integer hpid: currenthps)
		{
			Node hp = net.get(hpid);
			System.out.println("Honeypot "+ hpid);
			
			
			/**
			 * now check whether it's possible to reach the honeypot from the current node
			 * within 2 steps
			 */
			boolean reach = false;
			
			if(hp.id== currentnodeid)
			{
				reach = true;
				break;
			}
			



			for(Integer neiid: curnode.nei.values())
			{
				if(neiid.equals(hp.id))
				{
					reach = true;
					break;
				}
				Node neinode = net.get(neiid);
				for(Integer nextnei: neinode.nei.values())
				{
					if(nextnei.equals(hp.id))
					{
						reach = true;
						break;
					}
				}
			}



			if(reach)
			{
				System.out.println("Honeypot "+ hpid +" is reachable from attacker current position node "+ curnode.id);
			}
			else
			{
				System.out.println("Honeypot "+ hpid +" is not reachable from attacker current position node "+ curnode.id);
				unreachablehp.add(hpid);

			}
		}

		for(Integer hpid: unreachablehp)
		{
			System.out.println("removing invalid HP: "+ hpid);
			currenthps.remove(hpid);
			Node h= honeypots.get(hpid);

			Node prev = h.parent;
			if(h.nei.size()>1)
			{
				
				System.out.println("MOre than 1 neighbor");
				throw new Exception("MOre than 1 neighbor");
			}
			Node next = null;
			//h.nei.get(key) // should have only one neighbor
			for(Integer nid: h.nei.values())
			{
				next = net.get(nid);
			}

			prev.nei.remove(hpid);
			next.parent = prev;
			prev.nei.put(next.id, next.id);
			h.parent=null;
			h.nei.clear();
			net.remove(hpid);

		}
		System.out.println("After removing invalid, current using HP: ");
		for(Integer hpid: currenthps)
		{
			System.out.print(hpid+" ");
		}
		System.out.println();
		//printNetwork(net);
		

	}


	/**
	 * this method iterates over all the possible actions and choose the action which increases the probability of revealing the type of the attacker
	 * @param priorsattackertype
	 * @param priorforplang
	 * @param startnodeid
	 * @param placestoallocatehp
	 * @param freehps
	 * @param honeypots
	 * @param net
	 * @param exploits
	 * @param attackers
	 * @param oactions
	 * @param chosenatt
	 * @param chosenattackerpolicy
	 * @param singlepath
	 * @param npath
	 * @param posteriorlibrary 
	 * @param settingsid 
	 * @param atidconsidered 
	 * @param expoverlap 
	 * @param maxoverlap 
	 * @throws Exception 
	 */

	private static double measureDefenseMovesForEntropy(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp, ArrayList<Integer> freehps,
			HashMap<Integer, Node> honeypots, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer atidconsidered, boolean maxoverlap, boolean expoverlap) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/
		
		double maxoverlaplength = -1;
		

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}


		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}
*/



		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		System.out.println("Attacker current position "+ currentnodeid);
		
		if(settingsid==506 && currentnodeid==4)
		{
			System.out.println("Attacker current position "+ currentnodeid);
			printNetwork(net);
		}


		/**
		 * attackpolicies for each attacker for a particular settings of honeypot
		 * attids--> polcicies
		 */
		//System.out.println("*********Attacker policies before adding honeypots*********");
		//printAttackers(attackers);


		/**
		 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
		 */
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
		
		HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
		computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
		
		
		
		printAttackersPolicy(attackpolicies);
		
		
		
		
		if(attackpolicies.size() != attackers.size())
		{
			throw new Exception("Attacker doesnt have policy");
		}
		else
		{
			if(maxoverlap)
			{
				attackpolicies = refinePoliciesMeasure(attackpolicies, attackers, atidconsidered);
				maxoverlaplength = maxOverlapLength(attackpolicies, atidconsidered);
			}
			else if(expoverlap)
			{
				maxoverlaplength = refinePoliciesMaxExpOverlap(attackers, attackpolicies, atidconsidered);
			}
			
			
			if(maxoverlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("maxoverlaplength==Double.NEGATIVE_INFINITY");
				//printNetwork(net);
				//maxoverlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * compute max overlap length
		 */
		
		//maxoverlaplength = maxOverlapLength(attackpolicies);
		
		
		//System.out.println("********Attacker policies after adding honeypots*********");
		//printAttackers(attackers);
		printAttackersPolicy(attackpolicies);

		/**
		 * For all the settings: all slots, all honeypots and for all the attackers
		 * compute posterior considering each attacker's next move
		 *
		 */

		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Integer> tmpoactions = new HashMap<Integer, Integer>();



		//for(Integer attid: attackpolicies.keySet())
		Integer attid = atidconsidered;
		{
			System.out.println("Considering attacker "+ attid +" as the attacker to compute posteriors.\npolicy:..");
			HashMap<Integer, Integer> tmppolicy = attackpolicies.get(attid).get(0);

			
			
			/*if(tmppolicy==null)
			{
				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();
				
				for(Attacker at: attackers.values())
				{

					tmppost.put(at.id, 0.0);
					
				}

				posteriors.put(attid, tmppost);
				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}
			else */
			if(tmppolicy.size()>0)
			{

				for(Integer p: tmppolicy.values())
				{
					System.out.print(p+" ");
				}

				System.out.println();

				tmpoactions = new HashMap<Integer, Integer>();
				tmpoactions.put(tmpoactions.size(), currentnodeid);

				if(tmppolicy.get(1)==null)
				{
					throw new Exception("Found null in policy");
				}

				tmpoactions.put(tmpoactions.size(), tmppolicy.get(1));

				System.out.println("Attacker "+ attid +" next round "+(1)+" move: "+ tmppolicy.get(1));

				/*//System.out.print("observed action seq: ");

			for(int a: tmpoactions.values())
			{
				System.out.print(a+" ");
			}
			System.out.println();*/

				/**
				 * this method supports only single policy per attacker
				 */
				HashMap<Integer, Double> tmpposteriors = computePosteriorAttTypeWithPolicy(tmpoactions, attackers, net, priorsattackertype, attackpolicies);



				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();

				System.out.println("Posteriors: ");
				for(Integer in: tmpposteriors.keySet())
				{

					//tmppost[in] = tmpposteriors.get(in);

					tmppost.put(in, tmpposteriors.get(in));

					System.out.println("Att "+ in + " posterior : "+ tmppost.get(in));
				}
				posteriors.put(attid, tmppost);


				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}


		}


		/**
		 * remove the inserted honeypots
		 * update the net
		 */


		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			removeHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");
		
		return maxoverlaplength;




	}
	
	
	/**
	 * does not recompute the attacker policies
	 * @param priorsattackertype
	 * @param priorforplang
	 * @param startnodeid
	 * @param placestoallocatehp
	 * @param freehps
	 * @param honeypots
	 * @param net
	 * @param exploits
	 * @param attackers
	 * @param oactions
	 * @param singlepath
	 * @param npath
	 * @param round
	 * @param slots
	 * @param hps
	 * @param posteriorlibrary
	 * @param settingsid
	 * @param atidconsidered
	 * @param maxoverlap
	 * @param expoverlap
	 * @param attackpolicies 
	 * @param currentnodeid 
	 * @return
	 * @throws Exception
	 */
	private static double measureDefenseMovesForEntropyV2(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp, ArrayList<Integer> freehps,
			HashMap<Integer, Node> honeypots, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer atidconsidered, boolean maxoverlap, boolean expoverlap, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, int currentnodeid) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/
		
		double maxoverlaplength = Double.NEGATIVE_INFINITY;
		

		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/


		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}
*/



		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		
		
		printAttackersPolicy(attackpolicies);
		
		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> tmpattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
		
		
		for(Integer at: attackpolicies.keySet())
		{
			HashMap<Integer,HashMap<Integer,Integer>> t = new HashMap<Integer,HashMap<Integer,Integer>>();
			
			t = attackpolicies.get(at);
			
			tmpattackpolicies.put(at, t);
		}
		
		
		if(tmpattackpolicies.size() != attackers.size())
		{
			throw new Exception("Attacker doesnt have policy");
		}
		else
		{
			if(maxoverlap)
			{
				tmpattackpolicies = refinePoliciesMeasure(tmpattackpolicies, attackers, atidconsidered);
				maxoverlaplength = maxOverlapLength(tmpattackpolicies, atidconsidered);
			}
			else if(expoverlap)
			{
				maxoverlaplength = refinePoliciesMaxExpOverlap(attackers, tmpattackpolicies, atidconsidered);
			}
			
			
			if(maxoverlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("maxoverlaplen==Double.NEGATIVE_INFINITY");
				//printNetwork(net);
				//maxoverlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * compute max overlap length
		 */
		
		//maxoverlaplength = maxOverlapLength(attackpolicies);
		
		
		//System.out.println("********Attacker policies after adding honeypots*********");
		//printAttackers(attackers);
		printAttackersPolicy(tmpattackpolicies);

		/**
		 * For all the settings: all slots, all honeypots and for all the attackers
		 * compute posterior considering each attacker's next move
		 *
		 */

		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Integer> tmpoactions = new HashMap<Integer, Integer>();



		//for(Integer attid: attackpolicies.keySet())
		Integer attid = atidconsidered;
		
			System.out.println("Considering attacker "+ attid +" as the attacker to compute posteriors.\npolicy:..");
			HashMap<Integer, Integer> tmppolicy = tmpattackpolicies.get(attid).get(0);

			
			
			if(tmppolicy.size()>0)
			{

				for(Integer p: tmppolicy.values())
				{
					System.out.print(p+" ");
				}

				System.out.println();

				tmpoactions = new HashMap<Integer, Integer>();
				tmpoactions.put(tmpoactions.size(), currentnodeid);

				if(tmppolicy.get(1)==null)
				{
					throw new Exception("Found null in policy");
				}

				tmpoactions.put(tmpoactions.size(), tmppolicy.get(1));

				System.out.println("Attacker "+ attid +" next round "+(1)+" move: "+ tmppolicy.get(1));

				/*//System.out.print("observed action seq: ");

			for(int a: tmpoactions.values())
			{
				System.out.print(a+" ");
			}
			System.out.println();*/

				/**
				 * this method supports only single policy per attacker
				 */
				HashMap<Integer, Double> tmpposteriors = computePosteriorAttTypeWithPolicy(tmpoactions, attackers, net, priorsattackertype, tmpattackpolicies);



				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();

				System.out.println("Posteriors: ");
				for(Integer in: tmpposteriors.keySet())
				{

					//tmppost[in] = tmpposteriors.get(in);

					tmppost.put(in, tmpposteriors.get(in));

					System.out.println("Att "+ in + " posterior : "+ tmppost.get(in));
				}
				posteriors.put(attid, tmppost);


				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}


		


		/**
		 * remove the inserted honeypots
		 * update the net
		 */


		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			removeHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");
		
		return maxoverlaplength;




	}
	
	
	private static double measureHEMovesForEntropy(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp,
			 HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer atidconsidered, boolean maxoverlap, boolean expoverlap, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, int currentnodeid) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/
		
		double maxoverlaplength = Double.NEGATIVE_INFINITY;
		

		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/


		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}
*/



		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		
		
		printAttackersPolicy(attackpolicies);
		
		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> tmpattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
		
		
		for(Integer at: attackpolicies.keySet())
		{
			HashMap<Integer,HashMap<Integer,Integer>> t = new HashMap<Integer,HashMap<Integer,Integer>>();
			
			t = attackpolicies.get(at);
			
			tmpattackpolicies.put(at, t);
		}
		
		
		if(tmpattackpolicies.size() != attackers.size())
		{
			throw new Exception("Attacker doesnt have policy");
		}
		else
		{
			if(maxoverlap)
			{
				tmpattackpolicies = refinePoliciesMeasure(tmpattackpolicies, attackers, atidconsidered);
				maxoverlaplength = maxOverlapLength(tmpattackpolicies, atidconsidered);
			}
			else if(expoverlap)
			{
				maxoverlaplength = refinePoliciesMaxExpOverlap(attackers, tmpattackpolicies, atidconsidered);
			}
			
			
			if(maxoverlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("maxoverlaplen==Double.NEGATIVE_INFINITY");
				//printNetwork(net);
				//maxoverlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * compute max overlap length
		 */
		
		//maxoverlaplength = maxOverlapLength(attackpolicies);
		
		
		//System.out.println("********Attacker policies after adding honeypots*********");
		//printAttackers(attackers);
		printAttackersPolicy(tmpattackpolicies);

		/**
		 * For all the settings: all slots, all honeypots and for all the attackers
		 * compute posterior considering each attacker's next move
		 *
		 */

		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Integer> tmpoactions = new HashMap<Integer, Integer>();



		//for(Integer attid: attackpolicies.keySet())
		Integer attid = atidconsidered;
		{
			System.out.println("Considering attacker "+ attid +" as the attacker to compute posteriors.\npolicy:..");
			HashMap<Integer, Integer> tmppolicy = tmpattackpolicies.get(attid).get(0);

			
			
			/*if(tmppolicy==null)
			{
				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();
				
				for(Attacker at: attackers.values())
				{

					tmppost.put(at.id, 0.0);
					
				}

				posteriors.put(attid, tmppost);
				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}
			else */
			if(tmppolicy.size()>0)
			{

				for(Integer p: tmppolicy.values())
				{
					System.out.print(p+" ");
				}

				System.out.println();

				tmpoactions = new HashMap<Integer, Integer>();
				tmpoactions.put(tmpoactions.size(), currentnodeid);

				if(tmppolicy.get(1)==null)
				{
					throw new Exception("Found null in policy");
				}

				tmpoactions.put(tmpoactions.size(), tmppolicy.get(1));

				System.out.println("Attacker "+ attid +" next round "+(1)+" move: "+ tmppolicy.get(1));

				/*//System.out.print("observed action seq: ");

			for(int a: tmpoactions.values())
			{
				System.out.print(a+" ");
			}
			System.out.println();*/

				/**
				 * this method supports only single policy per attacker
				 */
				HashMap<Integer, Double> tmpposteriors = computePosteriorAttTypeWithPolicy(tmpoactions, attackers, net, priorsattackertype, tmpattackpolicies);



				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();

				System.out.println("Posteriors: ");
				for(Integer in: tmpposteriors.keySet())
				{

					//tmppost[in] = tmpposteriors.get(in);

					tmppost.put(in, tmpposteriors.get(in));

					System.out.println("Att "+ in + " posterior : "+ tmppost.get(in));
				}
				posteriors.put(attid, tmppost);


				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}


		}


		/**
		 * remove the inserted honeypots
		 * update the net
		 */


		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			removeHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");
		
		return maxoverlaplength;




	}
	
	
	private static double measureHEMovesForMinMaxOverlap(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp,
			 HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer atidconsidered, boolean maxoverlap, boolean expoverlap, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, int currentnodeid) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/
		
		double maxoverlaplength = Double.NEGATIVE_INFINITY;
		

		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/


		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}
*/



		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		
		
		printAttackersPolicy(attackpolicies);
		
		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> tmpattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
		
		
		for(Integer at: attackpolicies.keySet())
		{
			HashMap<Integer,HashMap<Integer,Integer>> t = new HashMap<Integer,HashMap<Integer,Integer>>();
			
			t = attackpolicies.get(at);
			
			tmpattackpolicies.put(at, t);
		}
		
		
		if(tmpattackpolicies.size() != attackers.size())
		{
			throw new Exception("Attacker doesnt have policy");
		}
		else
		{
			if(maxoverlap)
			{
				tmpattackpolicies = refinePoliciesMeasure(tmpattackpolicies, attackers, atidconsidered);
				maxoverlaplength = maxOverlapLength(tmpattackpolicies, atidconsidered);
			}
			else if(expoverlap)
			{
				maxoverlaplength = refinePoliciesMaxExpOverlap(attackers, tmpattackpolicies, atidconsidered);
			}
			
			
			if(maxoverlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("maxoverlaplen==Double.NEGATIVE_INFINITY");
				//printNetwork(net);
				//maxoverlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * compute max overlap length
		 */
		
		//maxoverlaplength = maxOverlapLength(attackpolicies);
		
		
		//System.out.println("********Attacker policies after adding honeypots*********");
		//printAttackers(attackers);
		printAttackersPolicy(tmpattackpolicies);

		/**
		 * For all the settings: all slots, all honeypots and for all the attackers
		 * compute posterior considering each attacker's next move
		 *
		 */

		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Integer> tmpoactions = new HashMap<Integer, Integer>();



		//for(Integer attid: attackpolicies.keySet())
		Integer attid = atidconsidered;
		{
			System.out.println("Considering attacker "+ attid +" as the attacker to compute posteriors.\npolicy:..");
			HashMap<Integer, Integer> tmppolicy = tmpattackpolicies.get(attid).get(0);

			
			
			/*if(tmppolicy==null)
			{
				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();
				
				for(Attacker at: attackers.values())
				{

					tmppost.put(at.id, 0.0);
					
				}

				posteriors.put(attid, tmppost);
				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}
			else */
			if(tmppolicy.size()>0)
			{

				for(Integer p: tmppolicy.values())
				{
					System.out.print(p+" ");
				}

				System.out.println();

				tmpoactions = new HashMap<Integer, Integer>();
				tmpoactions.put(tmpoactions.size(), currentnodeid);

				if(tmppolicy.get(1)==null)
				{
					throw new Exception("Found null in policy");
				}

				tmpoactions.put(tmpoactions.size(), tmppolicy.get(1));

				System.out.println("Attacker "+ attid +" next round "+(1)+" move: "+ tmppolicy.get(1));

				/*//System.out.print("observed action seq: ");

			for(int a: tmpoactions.values())
			{
				System.out.print(a+" ");
			}
			System.out.println();*/

				/**
				 * this method supports only single policy per attacker
				 */
				HashMap<Integer, Double> tmpposteriors = computePosteriorAttTypeWithPolicy(tmpoactions, attackers, net, priorsattackertype, tmpattackpolicies);



				HashMap<Integer, Double> tmppost = new HashMap<Integer, Double>();

				System.out.println("Posteriors: ");
				for(Integer in: tmpposteriors.keySet())
				{

					//tmppost[in] = tmpposteriors.get(in);

					tmppost.put(in, tmpposteriors.get(in));

					System.out.println("Att "+ in + " posterior : "+ tmppost.get(in));
				}
				posteriors.put(attid, tmppost);


				//Integer key = settingsid;

				posteriorlibrary.put(settingsid, posteriors);
			}


		}


		/**
		 * remove the inserted honeypots
		 * update the net
		 */


		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			removeHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}*/

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");
		
		return maxoverlaplength;




	}
	
	
	
	
	private static double measureDefenseMovesForOverlap(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp, ArrayList<Integer> freehps,
			HashMap<Integer, Node> honeypots, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer consideredattid, boolean maxoverlap, boolean expoverlap) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/

		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}


		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/




		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		int currentnodeid = startnodeid;
		if(oactions.size()>0)
		{
			currentnodeid = oactions.get(oactions.size()-1);
		}

		//System.out.println("Attacker current position "+ currentnodeid);


		/**
		 * attackpolicies for each attacker for a particular settings of honeypot
		 * attids--> polcicies
		 */
		//System.out.println("*********Attacker policies before adding honeypots*********");
		//printAttackers(attackers);


		/**
		 * Need to change the attackpolicies if  we want to adapt multiple policies by the attacker
		 */
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
		double overlaplength = -1;
		HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
		computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath, costs);
		
		
		
		
		
		//testRefinePolicyMeasure();
		
		printAttackersPolicy(attackpolicies);
		
		
		/**
		 * what to do if the acting attacker or only one attacker has policy
		 * 
		 * 1. If the considered attacker has no policy?
		 * 2. If all the other attackers except the considered attacker has no policy?
		 */
		System.out.println("s");
		
		
		if(attackpolicies.size() != attackers.size())
		{
			throw new Exception("attackpolicies.size()==0");
		
		}
		else
		{
			if(maxoverlap)
			{
				attackpolicies = refinePoliciesMeasure(attackpolicies, attackers, consideredattid);
				overlaplength = maxOverlapLength(attackpolicies, consideredattid);
			}
			else if(expoverlap)
			{
				overlaplength = refinePoliciesMaxExpOverlap(attackers, attackpolicies, consideredattid);
			}
			
			
			if(overlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("what!");
				//printNetwork(net);
				//overlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
	//	printAttackersPolicy(attackpolicies);
		
	//	printNetwork(net);
		
		
		/**
		 * remove the inserted honeypots
		 * update the net
		 */
		
		for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			removeHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
		//printNetwork(net);

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");


		return overlaplength;

	}
	
	
	private static double measureDefenseMovesForOverlapV2(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp, ArrayList<Integer> freehps,
			HashMap<Integer, Node> honeypots, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer consideredattid, boolean maxoverlap, boolean expoverlap, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, int currentnodeid) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/

		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
*/

		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/




		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> tmpattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>(attackpolicies);
		
		
		//testRefinePolicyMeasure();
		
		printAttackersPolicy(tmpattackpolicies);
		
		
		/**
		 * what to do if the acting attacker or only one attacker has policy
		 * 
		 * 1. If the considered attacker has no policy?
		 * 2. If all the other attackers except the considered attacker has no policy?
		 */
		System.out.println("s");
		
		double overlaplength = Double.NEGATIVE_INFINITY;
		
		if(tmpattackpolicies.size() != attackers.size())
		{
			throw new Exception("attackpolicies.size()==0");
		
		}
		else
		{
			if(maxoverlap)
			{
				tmpattackpolicies = refinePoliciesMeasure(tmpattackpolicies, attackers, consideredattid);
				overlaplength = maxOverlapLength(tmpattackpolicies, consideredattid);
			}
			else if(expoverlap)
			{
				overlaplength = refinePoliciesMaxExpOverlap(attackers, tmpattackpolicies, consideredattid);
			}
			
			
			if(overlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("what!");
				//printNetwork(net);
				//overlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
	//	printAttackersPolicy(attackpolicies);
		
	//	printNetwork(net);
		
		
		/**
		 * remove the inserted honeypots
		 * update the net
		 */
		
		
		//printNetwork(net);

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");


		return overlaplength;

	}
	
	
	
	private static double measureDefenseMovesForMinCost(HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,Double>> priorforplang, int startnodeid, 
			HashMap<Integer, int[]> placestoallocatehp, ArrayList<Integer> freehps,
			HashMap<Integer, Node> honeypots, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> oactions, boolean singlepath, int npath, int round,
			HashMap<Integer, int[]> slots, int[] hps, HashMap<Integer,HashMap<Integer,HashMap<Integer, Double>>> posteriorlibrary, 
			int settingsid, Integer consideredattid, boolean maxoverlap, boolean expoverlap, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, int currentnodeid) throws Exception {


		/**
		 * For a single slot find the policies of each attacker from current 
		 * position and using the previous history
		 * and previous cost
		 */

		/*int[] slot1 = placestoallocatehp.get(0);
		int slot2[] = placestoallocatehp.get(2);

		int hp1 = freehps.get(0);
		int hp2 = freehps.get(1);*/

		/**
		 * insert hp1 into slot1 and so on...
		 */
		
		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/

		/*for(Integer slid: slots.keySet())
		{
			int[] nodepair = slots.get(slid);
			int hp = hps[slid];

			insertHoneyPot(nodepair, hp, net, honeypots);
			//insertHoneyPot(slot2, hp2, net, honeypots);


		}
*/

		/*if(round==2)
		{
			printNetwork(net);
			printNetwork(honeypots);
		}*/




		/**
		 * now find each attacker's policy from current node
		 * Need current cost for each attacker
		 * ACtually we don't need current cost.
		 * All we need is to minimize the future cost and maximize rewards
		 * 
		 */


		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> tmpattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>(attackpolicies);
		
		
		//testRefinePolicyMeasure();
		
		printAttackersPolicy(tmpattackpolicies);
		
		
		/**
		 * what to do if the acting attacker or only one attacker has policy
		 * 
		 * 1. If the considered attacker has no policy?
		 * 2. If all the other attackers except the considered attacker has no policy?
		 */
		System.out.println("s");
		
		double overlaplength = Double.NEGATIVE_INFINITY;
		
		if(tmpattackpolicies.size() != attackers.size())
		{
			throw new Exception("attackpolicies.size()==0");
		
		}
		else
		{
			if(maxoverlap)
			{
				tmpattackpolicies = refinePoliciesMeasure(tmpattackpolicies, attackers, consideredattid);
				overlaplength = maxOverlapLength(tmpattackpolicies, consideredattid);
			}
			else if(expoverlap)
			{
				overlaplength = refinePoliciesMaxExpOverlap(attackers, tmpattackpolicies, consideredattid);
			}
			
			
			if(overlaplength==Double.NEGATIVE_INFINITY)
			{
				throw new Exception("what!");
				//printNetwork(net);
				//overlaplength = 99;
				
				
			}
			
		}
		
		
		
		
		
	//	printAttackersPolicy(attackpolicies);
		
	//	printNetwork(net);
		
		
		/**
		 * remove the inserted honeypots
		 * update the net
		 */
		
		
		//printNetwork(net);

		/*printNetwork(net);
	printNetwork(honeypots);
		 */


		//System.out.println("hp");


		return overlaplength;

	}
	
	


	private static void testRefinePolicyMeasure(int chosenattacker, HashMap<Integer, Attacker> attackers) {
		
		
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
		//computeSingleAttackPolicies(attackpolicies, net, exploits, attackers, currentnodeid, singlepath, npath);
		
		
		//testing
		
		HashMap<Integer, Integer> tmp1_1 = new HashMap<Integer, Integer>();
		tmp1_1.put(0, 1);
		tmp1_1.put(1, 5);
		tmp1_1.put(2, 4);
		tmp1_1.put(3, 1);
		tmp1_1.put(4, 1);
		tmp1_1.put(5, 26);
		
		HashMap<Integer, Integer> tmp1_2 = new HashMap<Integer, Integer>();
		tmp1_2.put(0, 1);
		tmp1_2.put(1, 5);
		tmp1_2.put(2, 10);
		tmp1_2.put(3, 1);
		tmp1_2.put(4, 1);
		tmp1_2.put(5, 26);
		
		HashMap<Integer, HashMap<Integer, Integer>> tmp_p = new HashMap<Integer, HashMap<Integer, Integer>>();
		
		tmp_p.put(0, tmp1_1);
		tmp_p.put(1, tmp1_2);
		
		
		
		HashMap<Integer, Integer> tmp2_1 = new HashMap<Integer, Integer>();
		tmp2_1.put(0, 1);
		tmp2_1.put(1, 2);
		tmp2_1.put(2, 4);
		tmp2_1.put(3, 1);
		tmp2_1.put(4, 1);
		tmp2_1.put(5, 25);
		
		HashMap<Integer, Integer> tmp2_2 = new HashMap<Integer, Integer>();
		tmp2_2.put(0, 1);
		tmp2_2.put(1, 5);
		tmp2_2.put(2, 10);
		tmp2_2.put(3, 2);
		tmp2_2.put(4, 1);
		tmp2_2.put(5, 25);
		
		HashMap<Integer, HashMap<Integer, Integer>> tmp2_p = new HashMap<Integer, HashMap<Integer, Integer>>();
		
		tmp2_p.put(1, tmp2_1);
		tmp2_p.put(0, tmp2_2);
		
		
		
		HashMap<Integer, Integer> tmp3_1 = new HashMap<Integer, Integer>();
		tmp3_1.put(0, 1);
		tmp3_1.put(1, 3);
		tmp3_1.put(2, 4);
		tmp3_1.put(3, 1);
		tmp3_1.put(4, 1);
		tmp3_1.put(5, 24);
		
		HashMap<Integer, Integer> tmp3_2 = new HashMap<Integer, Integer>();
		tmp3_2.put(0, 1);
		tmp3_2.put(1, 5);
		tmp3_2.put(2, 10);
		tmp3_2.put(3, 2);
		tmp3_2.put(4, 5);
		tmp3_2.put(5, 24);
		
		HashMap<Integer, HashMap<Integer, Integer>> tmp3_p = new HashMap<Integer, HashMap<Integer, Integer>>();
		
		tmp3_p.put(1, tmp3_1);
		tmp3_p.put(0, tmp3_2);
		
		attackpolicies.put(0, tmp_p);
		attackpolicies.put(1, tmp2_p);
		attackpolicies.put(2, tmp3_p);
		
		//attackpolicies = refinePoliciesMeasure(attackpolicies, attackers, chosenattacker);
		
		
		System.out.println("x");
		
		 
		
		
		
	}


	/**
	 * copmutes max overlap length between strategies
	 * @param attackpolicies
	 * @param consideredattid 
	 * @return
	 */
	private static double maxOverlapLength(HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackpolicies, Integer consideredattid) {
		
		
		
		double maxlen = Double.NEGATIVE_INFINITY;
		
		
		ArrayList<int[]> donepair = new ArrayList<int[]>();

		Integer atid = consideredattid;

		for(Integer atid2: attackpolicies.keySet())
		{
			if(atid != atid2)
			{


				boolean isdone = isDone(atid, atid2, donepair);


				if(!isdone)
				{
					int pair [] = {atid, atid2};
					donepair.add(pair);

					for(Integer pid: attackpolicies.get(atid).keySet())
					{
						for(Integer pid2: attackpolicies.get(atid2).keySet())
						{
							HashMap<Integer, Integer> p1 = attackpolicies.get(atid).get(pid);
							HashMap<Integer, Integer> p2 = attackpolicies.get(atid2).get(pid2);

							/*System.out.print("p1: ");

								for(int i: p1.values())
								{
									System.out.print(i+" ");
								}

								System.out.print("\np2: ");

								for(int i: p2.values())
								{
									System.out.print(i+" ");
								}
								System.out.println();*/

							int len = commonLen(p1, p2);

							/*if(len==2)
							{
								System.out.println("what!");
							}*/

							//System.out.println("common len "+ len);

							if(maxlen<len)
							{
								maxlen = len;
							}
							//System.out.println("max common len "+ maxlen);

						}
					}
				}
			}
		}


		
		
		return maxlen;
	}


	private static boolean isDone(Integer atid, Integer atid2, ArrayList<int[]> donepair) {
		
		
		
		for(int pair[]: donepair)
		{
			if((atid==pair[0] && atid2==pair[1]) || (atid==pair[1] && atid2==pair[0]))
			{
				return true;
			}
		}
		return false;
	}


	private static int commonLen(HashMap<Integer, Integer> p1, HashMap<Integer, Integer> p2) {
		
		int count = 0;
		
		int l1 = p1.size();
		int l2 = p2.size();
		
		int limit = (l1<l2)?l1:l2;
		
		for(int i=0; i<limit; i++)
		{
			if(p1.get(i) != p2.get(i))
			{
				return count;
			}
			count++;
		}
		return count;
	}
	
private static int commonLenExplt(HashMap<Integer, int[]> p1, HashMap<Integer, int[]> p2) {
		
		int count = 0;
		
		int l1 = p1.size();
		int l2 = p2.size();
		
		int limit = (l1<l2)?l1:l2;
		
		for(int i=0; i<limit; i++)
		{
			int x1[] = p1.get(i);
			int x2[] = p2.get(i);
			
			if(x1[0]==x2[0] && x1[1] == x2[1])
			{
				 count++;
			}
			else
			{
				return count;
			}
			
		}
		return count;
	}
	
	
private static int commonLen(String p1, String p2) {
		
		int count = 0;
		
		int l1 = p1.length();
		int l2 = p2.length();
		
		int limit = (l1<l2)?l1:l2;
		
		for(int i=0; i<limit; i++)
		{
			if(p1.charAt(i) != p2.charAt(i))
			{
				return count;
			}
			count++;
		}
		return count;
	}


	private static void computeSingleAttackPolicies(HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers,
			int currentnodeid, boolean singlepath, int npath, HashMap<Integer,Double> costs) throws Exception {


		for(Attacker att: attackers.values())
		{
			int goal = att.goals.get(0);


			//System.out.println("computing attacker "+ att.id+ " policy for goal "+ goal);

			//printNetwork(net);

			HashMap<Integer,HashMap<Integer,Integer>> p = new HashMap<Integer,HashMap<Integer,Integer>>();
					
			//p =	att.findOneFixedPolifyMaxReward(currentnodeid, net, exploits, goal, singlepath, npath);
			
			p =	att.findPolicyMinCostPath(currentnodeid, net, exploits, goal, singlepath, npath, costs, att.id);
			
			
			p= removeDuplicatePoliciesMeasure(p);
			
			/**
			 * keep the existing policy if there is no new policy
			 */
			if(p.size()>0)
			{
				attackpolicies.put(att.id, p);
			}
			else
			{
				
				throw new Exception("Attacker "+ att.id +" has no policy");
			}

		}


	}


	
	public static HashMap<Integer, HashMap<Integer, Integer>> removeDuplicatePoliciesMeasure(HashMap<Integer, HashMap<Integer, Integer>> policies) {

		HashMap<Integer, HashMap<Integer, Integer>> newfixpolicy = new HashMap<Integer, HashMap<Integer, Integer>>();


		for(int pid: policies.keySet())
		{
			HashMap<Integer, Integer> policy = policies.get(pid);


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

		return newfixpolicy;




		//System.out.println("hi");

	}

	private static HashMap<Integer,HashMap<Integer,Integer>> computeSingleAttackPolicy(HashMap<Integer, Node> net, 
			HashMap<Integer, Exploits> exploits, Attacker att, int currentnodeid, boolean singlepath, int npath, HashMap<Integer,Double> costs) 

	{

		int goal = att.goals.get(0);
		//HashMap<Integer,HashMap<Integer,Integer>> p = att.findOneFixedPolifyMaxReward(currentnodeid, net, exploits, goal, singlepath, npath);
		HashMap<Integer,HashMap<Integer,Integer>> p = att.findPolicyMinCostPath(currentnodeid, net, exploits, goal, singlepath, npath, costs, att.id);
		
		p = removeDuplicatePoliciesMeasure(p);
		return p;

	}


	/**
	 * inserts honeypot in the middle of the slot
	 * @param slot1
	 * @param hp1
	 * @param net
	 * @param honeypots 
	 */
	private static void insertHoneyPot(int[] slot, int hp, HashMap<Integer, Node> net, HashMap<Integer,Node> honeypots) {

		Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Node hpnode = honeypots.get(hp);

		//System.out.println("inserting hp "+ hp + " in the middle of slot ["+node1.id + ","+node2.id+"]");

		/**
		 * 1. remove 2 from 1's nei
		 * 2. insert hp into 1's nei
		 * 3. assign hp as 2's parent
		 * 4. assign 1 as hp's parent
		 * 5. insert 2 as hp's nei
		 */

		node1.nei.remove(node2.id);
		node1.nei.put(hp, hp);
		node2.parent = hpnode;
		hpnode.parent = node1;
		hpnode.nei.put(node2.id, node2.id);
		net.put(hpnode.id, hpnode);



	}
	
	
	private static void addHoneyPot(int[] slot, int hp, HashMap<Integer, Node> net, HashMap<Integer,Node> honeypots) {

		Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Node hpnode = honeypots.get(hp);

		System.out.println("adding hp "+ hp + " in the middle of slot ["+node1.id + ","+node2.id+"]");

		/**
		 * 1. remove 2 from 1's nei
		 * 2. insert hp into 1's nei
		 * 3. assign hp as 2's parent
		 * 4. assign 1 as hp's parent
		 * 5. insert 2 as hp's nei
		 */

		//node1.nei.remove(node2.id);
		node1.nei.put(hp, hp);
		//node2.parent = hpnode;
		hpnode.parent = node1;
		
		hpnode.depth = node1.depth+1;
		hpnode.nei.put(node2.id, node2.id);
		net.put(hpnode.id, hpnode);



	}
	
	
	private static boolean addHoneyEdge(int[] slot, int he, HashMap<Integer, Node> net, HashMap<Integer,Exploits> honeyedges) {

		Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Exploits ex = honeyedges.get(he);

		

		/**
		 * 1. remove 2 from 1's nei
		 * 2. insert hp into 1's nei
		 * 3. assign hp as 2's parent
		 * 4. assign 1 as hp's parent
		 * 5. insert 2 as hp's nei
		 */

		//node1.nei.remove(node2.id);
		/*node1.nei.put(he, he);
		//node2.parent = hpnode;
		hpnode.parent = node1;
		
		hpnode.depth = node1.depth+1;
		hpnode.nei.put(node2.id, node2.id);
		net.put(hpnode.id, hpnode);*/
		
		if(!node2.exploits.containsValue(ex.id))
		{
			node2.exploits.put(ex.id, ex.id);
			node2.honeyedge.put(ex.id, ex.id);
			System.out.println("adding he "+ ex.id + " in the middle of slot ["+node1.id + ","+node2.id+"]");
			return true;
		}
		else
		{
			System.out.println("could not add he "+ ex.id + " in the middle of slot ["+node1.id + ","+node2.id+"]");
			return false;
		}
		
		



	}



	private static void removeHoneyPot(int[] slot, int hp, HashMap<Integer, Node> net, HashMap<Integer,Node> honeypots) {

		Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Node hpnode = honeypots.get(hp);



		node1.nei.put(node2.id, node2.id);
		node1.nei.remove(hpnode.id);

		node2.parent = node1;

		hpnode.parent = null;
		hpnode.nei.clear();

		net.remove(hpnode.id);



	}
	
	
	private static void eliminateHoneyPot(int[] slot, int hp, HashMap<Integer, Node> net, HashMap<Integer,Node> honeypots) {

		Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Node hpnode = honeypots.get(hp);



		//node1.nei.put(node2.id, node2.id);
		node1.nei.remove(hpnode.id);

		//node2.parent = node1;

		hpnode.parent = null;
		hpnode.nei.clear();

		net.remove(hpnode.id);



	}
	
	private static void eliminateHoneyEdge(int[] slot, int he, HashMap<Integer, Node> net, HashMap<Integer,Exploits> exploits) {

		//Node node1 = net.get(slot[0]);
		Node node2 = net.get(slot[1]);
		Exploits hpnode = exploits.get(he);



		//node1.nei.put(node2.id, node2.id);
		
		
		
		if(node2.honeyedge.containsKey(hpnode.id))
		{
			node2.exploits.remove(hpnode.id);
			System.out.println("Removing honey edge "+ he + " from node "+ node2.id);
			node2.honeyedge.remove(hpnode.id);
			
		}
		else
		{
			System.out.println("already exists, was not added honey edge "+ he + " in node "+ node2.id +", so not need to remove");
		}

		//node2.parent = node1;

		/*hpnode.parent = null;
		hpnode.nei.clear();

		net.remove(hpnode.id);*/



	}


	public static ArrayList<Integer> findFreeHP(ArrayList<Integer> currenthps, HashMap<Integer, Node> honeypots) {


		ArrayList<Integer> freehps = new ArrayList<Integer>();

		for(Node hp: honeypots.values())
		{
			if(!currenthps.contains(hp.id))
			{
				freehps.add(hp.id);
				System.out.println("Honeypot "+ hp.id + " is free to use");
			}
		}


		return freehps;
	}
	
	
	/*public static ArrayList<Integer> findFreeHE(HashMap<Integer,Integer> currenthes, HashMap<Integer,Exploits> exploits) {


		ArrayList<Integer> freehps = new ArrayList<Integer>();

		for(Exploits hp: exploits.values())
		{
			if(!currenthes)
			{
				freehps.add(hp.id);
				System.out.println("Honeyedge "+ hp.id + " is free to use");
			}
		}


		return freehps;
	}*/


	public static HashMap<Integer, int[]> computePlacesToAllocateHP(HashMap<Integer, Node> net,
			HashMap<Integer, Node> honeypots, ArrayList<Integer> currenthps, HashMap<Integer, Integer> oactions) {

		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();


		int currentposition = -1;//oactions.get(oactions.size()-1);

		if(oactions.size()>0)
		{
			currentposition = oactions.get(oactions.size()-1);
		}
		else
		{
			currentposition = 0;
		}

		System.out.println("Attacker current position node "+ currentposition);




		Node currentnode = net.get(currentposition);

		/**
		 * NOw find neighbor nodes if they are not honeypot
		 */

		HashMap<Integer, Integer> onehopnodes = new HashMap<Integer, Integer>();

		for(Integer nei: currentnode.nei.values())
		{
			if(!currenthps.contains(nei))
			{
				System.out.println("Neighbor node "+ nei + " is not a honeypot\nIncludine node "+ nei + " in one hop node lists");
				onehopnodes.put(onehopnodes.size(), nei);
			}
			else
			{
				System.out.println("Neighbor node "+ nei + " is a honeypot");
			}
		}

		/**
		 * For each of the one hop nodes include its neighbor
		 */

		for(Integer onehopnode: onehopnodes.values())
		{
			Node ohopnode = net.get(onehopnode);
			System.out.println("NOde "+ ohopnode.id);
			System.out.println("Neighobors: ");
			for(Integer nei: ohopnode.nei.values())
			{
				System.out.println("Node "+ nei);
				int[] sl = {onehopnode, nei};
				System.out.println("including slot  ["+ sl[0] + ","+sl[1]+"] in slots");
				slots.put(slots.size(), sl);
			}
		}



		return slots;
	}


	private static void removePolicies(HashMap<Integer, Attacker> attackers, HashMap<Integer, Integer> observedactions,
			HashMap<Integer, Node> net, int[] goals) {

		//printAttackers(attackers);

		Logger.logit("\n\nComputing posteriors...\n\n");

		for(Integer attindex: attackers.keySet())
		{
			Attacker att = attackers.get(attindex);



			ArrayList<Integer> toberemovedpolicyies = new ArrayList<Integer>();


			for(Integer policyindex  : att.fixedpolicy.keySet())
			{

				HashMap<Integer, Integer> policy = att.fixedpolicy.get(policyindex);


				boolean matches = true;
				for(Integer round: observedactions.keySet())
				{

					/**
					 * 
					 * what if the length of the oservations is more than the length of the policy???
					 * 
					 */
					int oa = observedactions.get(round);
					int fp = -19999;
					if(round<policy.size())
					{
						fp = policy.get(round);
					}

					if(oa != fp)
					{
						Logger.logit("does not matches...\n");
						toberemovedpolicyies.add(policyindex);
						matches = false;
						break;
					}

				}


			}
			/**
			 * Now remove the policies
			 */
			for(Integer pno: toberemovedpolicyies)
			{
				Logger.logit("Removing attacker "+ att.id + "'s policy "+ pno+"\n");
				//System.out
				att.fixedpolicy.remove(pno);
			}

		}


	}

	private static void writeBayesianUpdatesForPlan(double[][] priorforplang) {



		PrintWriter pw;
		try 
		{
			pw = new PrintWriter(new FileOutputStream(new File("planrcg.csv"),true));
			for(int i=0; i<priorforplang.length; i++)
			{

				for(int j=0; j<priorforplang[i].length; j++)
				{
					pw.append(priorforplang[i][j]+",");
					/*if(i!=priorforplang.length-1)
					{
						pw.append(",");
					}*/
				}
				pw.append(", , ,");

			}
			pw.append("\n");
			pw.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}
	
	
	private static void writeBayesianUpdatesForPlan(HashMap<Integer, HashMap<Integer, Double>> priorforplang) {



		PrintWriter pw;
		try 
		{
			pw = new PrintWriter(new FileOutputStream(new File("planrcg.csv"),true));
			for(int attid: priorforplang.keySet())
			{

				
				for(int g: priorforplang.get(attid).keySet())
				{
					double p = priorforplang.get(attid).get(g);
					pw.append(p+",");
					/*if(i!=priorforplang.length-1)
					{
						pw.append(",");
					}*/
				}
				pw.append(", , ,");

			}
			pw.append("\n");
			pw.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	private static void writeBUpdatesForAttackerType(double[] priorsattackertype) {

		try 
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("attype.csv"),true));

			for(int i=0; i<priorsattackertype.length; i++)
			{
				pw.append(priorsattackertype[i]+"");
				if(i!=priorsattackertype.length-1)
				{
					pw.append(",");
				}
			}
			pw.append("\n");
			pw.close();

		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//pw.append("deftype,cluster,#users,llv,w1,w2,w3,w4,score,mscore,nscore,pscore"+ "\n");


	}
	
	
	private static void writeBUpdatesForAttackerType(HashMap<Integer, Double>  priorsattackertype) {

		try 
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("attype.csv"),true));

			int i=0;
			pw.append("\n");
			for(int attid=0; attid<4; attid++)
			{
				if(priorsattackertype.containsKey(attid))
				{
				
					double p = priorsattackertype.get(attid);
					pw.append(p+",");
				}
				else
				{
					pw.append(0+",");
				}
				if(i!=priorsattackertype.size()-1)
				{
					//pw.append(",");
				}
				i++;
			}
			pw.append("\n");
			pw.close();

		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//pw.append("deftype,cluster,#users,llv,w1,w2,w3,w4,score,mscore,nscore,pscore"+ "\n");


	}

	private static double[][] posteriorPlang(HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net,
			double[][] priorforplang, HashMap<Integer, Integer> oactions, int[] goals, double[] priorsattackertype) {


		double[][] posteriors = new double[attackers.size()][goals.length];


		//printAttackers(attackers);


		for(Integer attid: attackers.keySet())
		{
			Attacker att = attackers.get(attid);
			double priors[] = priorforplang[attid];

			Logger.logit("Attacker "+ attid+"\n");

			double[] posterior = new double[goals.length];

			double[] policygivengoalcounts = new double[goals.length];

			double[] observationmatchescounts = new double[goals.length];

			double[] likelihoods = new double[goals.length];




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


					/**
					 * match the sequences if the sequence has the goal
					 * otherwise don't
					 */

					if(policy.get(plen-1).equals(goals[gindex])) 
					{
						Logger.logit("poliicy has the goal "+ goals[gindex]+"\n");
						policygivengoalcounts[gindex]++;


						boolean seqmatches = true;
						/**
						 * run match for observed actions' length
						 */
						for(int oaindex: oactions.keySet())
						{

							int fp = -19999;

							if(oaindex<policy.size())
							{
								fp = policy.get(oaindex);
							}

							if(fp != oactions.get(oaindex))
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
					else
					{
						Logger.logit("poliicy does not have the goal "+ goals[gindex]+"\n");
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


	private static HashMap<Integer, HashMap<Integer, Double>> posteriorPlangWithHashMap(HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net,
			HashMap<Integer, HashMap<Integer, Double>> priorforplang, HashMap<Integer, Integer> oactions, int[] goals, HashMap<Integer,Double> priorsattackertype) {


		HashMap<Integer, HashMap<Integer, Double>> posteriors = new HashMap<Integer, HashMap<Integer, Double>>();


		//printAttackers(attackers);


		for(Integer attid: attackers.keySet())
		{
			Attacker att = attackers.get(attid);
			HashMap<Integer, Double> priors = priorforplang.get(attid);

			Logger.logit("Attacker "+ attid+"\n");

			HashMap<Integer, Double> posterior = new HashMap<Integer, Double>();

			HashMap<Integer, Double> policygivengoalcounts = new HashMap<Integer, Double>();

			HashMap<Integer, Double> observationmatchescounts = new HashMap<Integer, Double>();

			HashMap<Integer, Double> likelihoods = new HashMap<Integer, Double>();

			HashMap<Integer, Double> lp = new HashMap<Integer, Double>();


			for(int i=0; i<goals.length; i++)
			{
				posterior.put(i, 0.0);
				policygivengoalcounts.put(i, 0.0);
				observationmatchescounts.put(i, 0.0);
				likelihoods.put(i, 0.0);
				lp.put(i, 0.0);

			}




			double totallp=0;
			/**
			 * likelihood*prior
			 */





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


					/**
					 * match the sequences if the sequence has the goal
					 * otherwise don't
					 */

					if(policy.size()==0 || policy.get(plen-1).equals(goals[gindex])) 
					{
						Logger.logit("poliicy has the goal "+ goals[gindex]+"\n");

						double pgc = policygivengoalcounts.get(gindex);
						policygivengoalcounts.put(gindex, pgc+1);


						boolean seqmatches = true;
						/**
						 * run match for observed actions' length
						 */
						for(int oaindex: oactions.keySet())
						{

							int fp = -19999;

							if(oaindex<policy.size())
							{
								fp = policy.get(oaindex);
							}

							if(fp != oactions.get(oaindex))
							{
								seqmatches = false;
								break;
							}
						}

						if(seqmatches)
						{

							//observationmatchescounts[gindex]++;


							double oc = observationmatchescounts.get(gindex);
							observationmatchescounts.put(gindex, oc+1);

							//Logger.logit("Obsrvation matches policy, count "+ observationmatchescounts[gindex]+"\n");
						}
						else
						{
							//Logger.logit("Obsrvation does not match policy, count "+ observationmatchescounts[gindex]+"\n");
						}

					}
					else
					{
						Logger.logit("poliicy does not have the goal "+ goals[gindex]+"\n");
					}




				}

				if(policygivengoalcounts.get(gindex)>0)
				{
					double lh = observationmatchescounts.get(gindex)/policygivengoalcounts.get(gindex);
					likelihoods.put(gindex, lh);

				}



				//Logger.logit("Likelihood : "+ likelihoods[gindex]+"\n");

				double tp= likelihoods.get(gindex)*priors.get(gindex)*priorsattackertype.get(attid);

				lp.put(gindex, lp.get(gindex)+tp);

				totallp += lp.get(gindex);


				/*double lh = observationmatchescounts.get(gindex)/policygivengoalcounts.get(gindex);
				likelihoods.put(gindex, lh);*/





			}


			Logger.logit(" Posteriors : \n");
			for(int gindex = 0; gindex<goals.length; gindex++)
			{

				if(totallp>0)
				{
					//posterior[gindex] = lp[gindex]/totallp;

					posterior.put(gindex, lp.get(gindex)/totallp);


				}
				else
				{
					posterior.put(gindex, 0.0);
				}

				//Logger.logit("goal "+ goals[gindex] +": "+posterior[gindex]+"\n");
				//posteriors[attid][gindex] = posterior[gindex];

			}

			posteriors.put(attid, posterior);


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
		double[] totalobservations = new double[attackers.size()];
		double[] probobservations = new double[attackers.size()];
		/**
		 *  compute in how many observations the sequence was observed
		 */

		//printAttackers(attackers);

		Logger.logit("\n\nComputing posteriors...\n\n");

		for(Integer attindex: attackers.keySet())
		{
			Attacker att = attackers.get(attindex);

			Logger.logit("**********Attacker index "+ attindex +"*******\n");
			Logger.logit("Attacker type "+ att.id +"\n");
			Logger.logit("policy : ");

			//ArrayList<Integer> toberemovedpolicyies = new ArrayList<Integer>();


			for(Integer policyindex  : att.fixedpolicy.keySet())
			{

				HashMap<Integer, Integer> policy = att.fixedpolicy.get(policyindex);

				totalobservations[attindex]++;
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

					/**
					 * 
					 * what if the length of the oservations is more than the length of the policy???
					 * 
					 */
					int oa = observedactions.get(round);
					int fp = -19999;
					if(round<policy.size())
					{
						fp = policy.get(round);
					}

					if(oa != fp)
					{
						Logger.logit("does not matches...\n");
						//toberemovedpolicyies.add(policyindex);
						matches = false;
						break;
					}

				}
				if(matches)
				{
					Logger.logit("matches...\n");
					observationsgiventype[attindex]++;

					//Logger.logit("total observations "+ totalobservations + "\n");
					/*Logger.logit("observ t0 "+ observationsgiventype[0] + " observ t1 "+observationsgiventype[1]+" observ t2 "+observationsgiventype[2]
							+" observ t3 "+observationsgiventype[3]+"\n");*/
				}

			}
			/**
			 * Now remove the policies
			 */
			/*for(Integer pno: toberemovedpolicyies)
			{
				Logger.logit("Removing attacker "+ att.id + "'s policy "+ pno+"\n");
				//System.out
				att.fixedpolicy.remove(pno);
			}*/

		}


		double sumprobtotalobservations = 0;
		Logger.logit("\n\nComputing likelihoods...\n\n");
		for(Attacker att: attackers.values())
		{
			if(totalobservations[att.id]>0)
			{
				Logger.logit("*******Attacker type "+ att.id +"*****\n");
				likelihoods[att.id] = observationsgiventype[att.id]/totalobservations[att.id];
			}
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


	private static HashMap<Integer, Double> computePosteriorAttackerTypeWithHashMap(HashMap<Integer, Integer> observedactions,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net, HashMap<Integer,Double> priorsattackertype) {




		HashMap<Integer, Double> posteriors = new HashMap<Integer, Double>();

		HashMap<Integer,Double> likelihoods = new HashMap<Integer,Double>();
		HashMap<Integer,Double> observationsgiventype = new HashMap<Integer,Double>();
		HashMap<Integer,Double> totalobservations = new HashMap<Integer,Double>();
		HashMap<Integer,Double> probobservations = new HashMap<Integer,Double>();


		for(Attacker att: attackers.values())
		{
			likelihoods.put(att.id, 0.0);
			observationsgiventype.put(att.id, 0.0);
			totalobservations.put(att.id, 0.0);
			probobservations.put(att.id, 0.0);
		}



		/**
		 *  compute in how many observations the sequence was observed
		 */

		//printAttackers(attackers);

		Logger.logit("\n\nComputing posteriors...\n\n");

		for(Integer attindex: attackers.keySet())
		{
			Attacker att = attackers.get(attindex);

			Logger.logit("**********Attacker index "+ attindex +"*******\n");
			Logger.logit("Attacker type "+ att.id +"\n");
			Logger.logit("policy : ");

			//ArrayList<Integer> toberemovedpolicyies = new ArrayList<Integer>();


			for(Integer policyindex  : att.fixedpolicy.keySet())
			{

				HashMap<Integer, Integer> policy = att.fixedpolicy.get(policyindex);

				double to = totalobservations.get(attindex);
				totalobservations.put(attindex, to+1);

				//totalobservations[attindex]++;
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

					/**
					 * 
					 * what if the length of the oservations is more than the length of the policy???
					 * 
					 */
					int oa = observedactions.get(round);
					int fp = -19999;
					if(round<policy.size())
					{
						fp = policy.get(round);
					}

					if(oa != fp)
					{
						Logger.logit("does not matches...\n");
						//toberemovedpolicyies.add(policyindex);
						matches = false;
						break;
					}

				}
				if(matches)
				{
					Logger.logit("matches...\n");
					//observationsgiventype[attindex]++;


					to = observationsgiventype.get(attindex);
					observationsgiventype.put(attindex, to+1);

					//Logger.logit("total observations "+ totalobservations + "\n");
					/*Logger.logit("observ t0 "+ observationsgiventype[0] + " observ t1 "+observationsgiventype[1]+" observ t2 "+observationsgiventype[2]
							+" observ t3 "+observationsgiventype[3]+"\n");*/
				}

			}
			/**
			 * Now remove the policies
			 */
			/*for(Integer pno: toberemovedpolicyies)
			{
				Logger.logit("Removing attacker "+ att.id + "'s policy "+ pno+"\n");
				//System.out
				att.fixedpolicy.remove(pno);
			}*/

		}


		double sumprobtotalobservations = 0;
		Logger.logit("\n\nComputing likelihoods...\n\n");
		for(Attacker att: attackers.values())
		{
			if(totalobservations.get(att.id)>0)
			{
				Logger.logit("*******Attacker type "+ att.id +"*****\n");
				double tmplh = observationsgiventype.get(att.id)/totalobservations.get(att.id);
				likelihoods.put(att.id, tmplh);
			}
			//Logger.logit("likelihood "+ likelihoods[att.id]+"\n");
			//Logger.logit("prior "+ priorsattackertype[att.id]+"\n");

			double po = likelihoods.get(att.id)*priorsattackertype.get(att.id);
			probobservations.put(att.id, po);

			sumprobtotalobservations += probobservations.get(att.id);
			/*Logger.logit("att "+ att.id + ", probovservations "+ probobservations[att.id]+"\n");
			Logger.logit("sum total observations "+ sumprobtotalobservations+"\n");
			 */
		}

		Logger.logit("\nposteriors...\n\n");

		for(Attacker att: attackers.values())
		{
			Logger.logit("********Attacker type "+ att.id +"*********\n");
			double d = probobservations.get(att.id)/ sumprobtotalobservations;
			posteriors.put(att.id, d);
			//Logger.logit("posterior "+ posteriors[att.id] + "\n");

		}


		return posteriors;
	}


	/**
	 * computes posterior by considering different attackers
	 * supports single policy
	 * @param observedactions
	 * @param attackers
	 * @param net
	 * @param priorsattackertype
	 * @param attackpolicies 
	 * @return
	 */
	private static HashMap<Integer, Double> computePosteriorAttTypeWithPolicy(HashMap<Integer, Integer> observedactions,
			HashMap<Integer, Attacker> attackers, HashMap<Integer, Node> net, 
			HashMap<Integer,Double> priorsattackertype, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies) {




		HashMap<Integer, Double> posteriors = new HashMap<Integer, Double>();

		/*double[] likelihoods = new double[attackers.size()];
		double[] observationsgiventype = new double[attackers.size()];
		double[] totalobservations = new double[attackers.size()];
		double[] probobservations = new double[attackers.size()];*/



		//HashMap<Integer, Double> posteriors = new HashMap<Integer, Double>();

		HashMap<Integer,Double> likelihoods = new HashMap<Integer,Double>();
		HashMap<Integer,Double> observationsgiventype = new HashMap<Integer,Double>();
		HashMap<Integer,Double> totalobservations = new HashMap<Integer,Double>();
		HashMap<Integer,Double> probobservations = new HashMap<Integer,Double>();


		for(Attacker att: attackers.values())
		{
			likelihoods.put(att.id, 0.0);
			observationsgiventype.put(att.id, 0.0);
			totalobservations.put(att.id, 0.0);
			probobservations.put(att.id, 0.0);
		}





		/**
		 *  compute in how many observations the sequence was observed
		 */

		//printAttackers(attackers);

		//Logger.logit("\n\nComputing posteriors...\n\n");

		for(Integer attindex: attackers.keySet())
		{
			Attacker att = attackers.get(attindex);

			/*Logger.logit("**********Attacker index "+ attindex +"*******\n");
			Logger.logit("Attacker type "+ att.id +"\n");
			Logger.logit("policy : ");*/

			//ArrayList<Integer> toberemovedpolicyies = new ArrayList<Integer>();


			HashMap<Integer,HashMap<Integer,Integer>> policies = attackpolicies.get(att.id);

			
			if(policies != null)
			{

				for(Integer policyindex  : policies.keySet())
				{

					HashMap<Integer, Integer> policy = policies.get(policyindex);

					//totalobservations[attindex]++;

					double tmp = totalobservations.get(attindex);

					totalobservations.put(attindex, tmp+1);




					/*for(int fp: policy.values())
				{
					Logger.logit(fp+" ");
				}
				Logger.logit("\nobserved actions : ");
				for(int oa: observedactions.values())
				{
					Logger.logit(oa+" ");
				}
				Logger.logit("\n");*/
					boolean matches = true;
					for(Integer round: observedactions.keySet())
					{

						/**
						 * 
						 * what if the length of the oservations is more than the length of the policy???
						 * 
						 */
						int oa = observedactions.get(round);
						int fp = -19999;
						if(round<policy.size())
						{
							fp = policy.get(round);
						}

						if(oa != fp)
						{
							//Logger.logit("does not matches...\n");
							//toberemovedpolicyies.add(policyindex);
							matches = false;
							break;
						}

					}
					if(matches)
					{
						//Logger.logit("matches...\n");
						//observationsgiventype[attindex]++;


						tmp = observationsgiventype.get(attindex);

						observationsgiventype.put(attindex, tmp+1);

						//Logger.logit("total observations "+ totalobservations + "\n");
						/*Logger.logit("observ t0 "+ observationsgiventype[0] + " observ t1 "+observationsgiventype[1]+" observ t2 "+observationsgiventype[2]
							+" observ t3 "+observationsgiventype[3]+"\n");*/
					}

				}
			}
			/**
			 * Now remove the policies
			 */
			/*for(Integer pno: toberemovedpolicyies)
			{
				Logger.logit("Removing attacker "+ att.id + "'s policy "+ pno+"\n");
				//System.out
				att.fixedpolicy.remove(pno);
			}*/

		}


		double sumprobtotalobservations = 0;
		//Logger.logit("\n\nComputing likelihoods...\n\n");
		for(Attacker att: attackers.values())
		{
			if(totalobservations.get(att.id)>0)
			{
				//Logger.logit("*******Attacker type "+ att.id +"*****\n");
				//likelihoods[att.id] = observationsgiventype[att.id]/totalobservations[att.id];

				double lh = observationsgiventype.get(att.id)/totalobservations.get(att.id);
				likelihoods.put(att.id, lh);


			}
			//Logger.logit("likelihood "+ likelihoods[att.id]+"\n");
			//Logger.logit("prior "+ priorsattackertype[att.id]+"\n");
			probobservations.put(att.id, likelihoods.get(att.id)*priorsattackertype.get(att.id));
			sumprobtotalobservations += probobservations.get(att.id);





			//Logger.logit("att "+ att.id + ", probovservations "+ probobservations[att.id]+"\n");
			//Logger.logit("sum total observations "+ sumprobtotalobservations+"\n");

		}

		//Logger.logit("\nposteriors...\n\n");

		for(Attacker att: attackers.values())
		{
			//Logger.logit("********Attacker type "+ att.id +"*********\n");
			double d = probobservations.get(att.id)/ sumprobtotalobservations;
			posteriors.put(att.id, d);
			//Logger.logit("posterior "+ posteriors[att.id] + "\n");

		}


		return posteriors;
	}






	public static void printAttackers(HashMap<Integer, Attacker> attackers) {

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

			System.out.print("policies: \n");
			Logger.logit("policies: \n");
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
	
	public static void printAttackersExploit(HashMap<Integer, Attacker> attackers) {

		for(Integer aid: attackers.keySet())
		{
			Attacker a = attackers.get(aid);
			System.out.println("******************** Attacker "+a.id+ " ***********************");
			//Logger.logit("******************** Attacker "+a.id+ " ***********************"+"\n");



			System.out.print("exploits: ");
			//Logger.logit("exploits: ");
			for(Integer exp: a.exploits.values())
			{
				System.out.print(exp+" ");
				//Logger.logit(exp+" ");
			}
			System.out.println();
			//Logger.logit(" "+"\n");

			System.out.print("policies: \n");
			//Logger.logit("policies: \n");
			for(HashMap<Integer, int[]> p: a.fixedexploitpolicy.values())
			{

				for(int[] s: p.values())
				{
					System.out.print(s[0]+"("+s[1]+")->");
					//Logger.logit(s+" ");
				}
				System.out.println();
				//Logger.logit(" "+"\n");

			}
			System.out.println();
			//Logger.logit(" "+"\n");



		}

	}


	private static void printAttackersPolicy(HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attackers) {

		for(Integer aid: attackers.keySet())
		{
			//Attacker a = attackers.get(aid);
			System.out.println("******************** Attacker "+aid+ " ***********************");
			System.out.print("policies: \n");
			//Logger.logit("policies: \n");


			for(HashMap<Integer, Integer> s: attackers.get(aid).values())
			{

				for(Integer p: s.values())
				{
					System.out.print(p+" ");
				}
				System.out.println();
				//Logger.logit(s+" ");
			}
			System.out.println();
			//Logger.logit(" "+"\n");


			System.out.println();
			//Logger.logit(" "+"\n");



		}

	}



	public static void printNetwork(HashMap<Integer, Node> net) {


		for(Integer nodeid: net.keySet())
		{
			Node n = net.get(nodeid);
			System.out.println("******************** Node "+n.id+ " ***********************");
			//Logger.logit("******************** Node "+n.id+ " ***********************\n");
			System.out.println("value: "+ n.value);
			//Logger.logit("value: "+ n.value+"\n");
			System.out.println("cost: "+ n.cost);
			
			System.out.println("depth: "+ n.depth);
			//Logger.logit("cost: "+ n.cost+"\n");
			
			
			System.out.print("parent: ");
			if(n.parent!=null)
			{
				System.out.print(n.parent.id);
			}

			System.out.print("\nneighbors: ");
			//Logger.logit("neighbors: ");
			for(Integer nei: n.nei.values())
			{
				System.out.print(nei+" ");
				//Logger.logit(nei+" ");
			}
			System.out.println();
			//Logger.logit(" "+"\n");

			System.out.print("exploits: "+"");
			//Logger.logit("exploits: ");
			for(Integer exp: n.exploits.values())
			{
				System.out.print(exp+" ");
				//Logger.logit(exp+" ");
			}
			System.out.println();
			//Logger.logit(" "+"\n");

		}


	}
	
	
	public static void printNetworkWithExploits(HashMap<Integer, Node> net) {


		for(Integer nodeid: net.keySet())
		{
			Node n = net.get(nodeid);
			System.out.println("******************** Node "+n.id+ " ***********************");
			//Logger.logit("******************** Node "+n.id+ " ***********************\n");
			System.out.println("value: "+ n.value);
			//Logger.logit("value: "+ n.value+"\n");
			System.out.println("cost: "+ n.cost);
			//Logger.logit("cost: "+ n.cost+"\n");
			
			
			System.out.print("parent: ");
			if(n.parent!=null)
			{
				System.out.print(n.parent.id);
			}

			System.out.print("\nneighbors: ");
			//Logger.logit("neighbors: ");
			for(int[] nei: n.neiwithexploits.values())
			{
				System.out.println("Node: "+nei[0]+", explt: "+nei[1]);
				//Logger.logit(nei+" ");
			}
			System.out.println();
			//Logger.logit(" "+"\n");

			/*System.out.print("exploits: "+"");
			//Logger.logit("exploits: ");
			for(Integer exp: n.exploits.values())
			{
				System.out.print(exp+" ");
				//Logger.logit(exp+" ");
			}
			System.out.println();*/
			//Logger.logit(" "+"\n");

		}


	}

	public static void constructAttackersMultGoal(HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, boolean singlepath, int npath) {

		int id = 0;

		//int goals[] = {26,23,25,24};



		Attacker a0  = new Attacker(id++);
		//a0.goals.put(0, 23);
		a0.goals.put(0, 26);
		a0.goals.put(1, 25);
		a0.addExploits(new int[] {0, 1});
		//a0.findFixedPolifyBFS(net, exploits, 23);
		//a0.findFixedPolifyBFS(net, exploits, 24);
		a0.findFixedPolifyBFS(net, exploits, 26, singlepath, npath);
		a0.findFixedPolifyBFS(net, exploits, 25, singlepath, npath);
		a0.removeDuplicatePolicies();
		//a0.addPolicy(0, new int[] {0, 2, 5, 10, 16, 21, 26});
		//a0.addPolicy(1, new int[] {0, 2, 6, 10, 15, 20, 24});



		Attacker a1  = new Attacker(id++);
		a1.goals.put(0, 23);
		//a1.goals.put(1, 24);
		a1.goals.put(1, 24);
		a1.addExploits(new int[] {2,3});
		a1.findFixedPolifyBFS(net, exploits, 23, singlepath, npath);
		a1.findFixedPolifyBFS(net, exploits, 24, singlepath, npath);
		a1.removeDuplicatePolicies();
		//a1.findFixedPolifyBFS(net, exploits, 25);
		//a1.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 23});
		//a1.addPolicy(1, new int[] {0, 2, 5, 14, 20, 25});

		Attacker a2  = new Attacker(id++);
		//a2.goals.put(0, 23);
		a2.goals.put(0, 24);
		a2.goals.put(1, 25);
		//a2.goals.put(3, 26);

		a2.addExploits(new int[] {4,5});
		a2.findFixedPolifyBFS(net, exploits, 24, singlepath, npath);
		a2.findFixedPolifyBFS(net, exploits, 25, singlepath, npath);
		a2.removeDuplicatePolicies();
		//a2.findFixedPolifyBFS(net, exploits, 26);

		/*a2.addPolicy(0, new int[] {0, 1, 3, 7, 13, 18, 23});
		a2.addPolicy(1, new int[] {0, 2, 5, 14, 20, 24});
		a2.addPolicy(2, new int[] {0, 2, 5, 10, 15, 20, 25});
		a2.addPolicy(3, new int[] {0, 2, 6, 10, 16, 21, 26});*/


		Attacker a3  = new Attacker(id++);
		a3.goals.put(0, 23);
		a3.goals.put(1, 25);
		a3.addExploits(new int[] {6, 7});
		a3.findFixedPolifyBFS(net, exploits, 23, singlepath, npath);
		a3.findFixedPolifyBFS(net, exploits, 25,singlepath, npath);
		a3.removeDuplicatePolicies();




		/*Attacker a4  = new Attacker(id++);
		a4.goals.put(0, 24);
		a4.goals.put(1, 25);
		a4.addExploits(new int[] {1, 2});
		a4.findFixedPolifyBFS(net, exploits, 24);
		a4.findFixedPolifyBFS(net, exploits, 25);



		Attacker a5  = new Attacker(id++);
		//a5.goals.put(0, 23);
		a5.goals.put(0, 26);
		a5.addExploits(new int[] {0, 3});
		//a5.findFixedPolifyBFS(net, exploits, 23);
		a5.findFixedPolifyBFS(net, exploits, 26);*/

		//a3.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 24});

		/*attackers.put(0, a0);
		attackers.put(1, a1);
		attackers.put(2, a2);
		attackers.put(3, a3);*/

		attackers.put(a0.id, a0);
		attackers.put(a1.id, a1);
		attackers.put(a2.id, a2);
		attackers.put(a3.id, a3);
		/*attackers.put(3, a3);
		attackers.put(4, a4);
		attackers.put(5, a5);*/


		//return goals;


	}


	public static void constructAttackersSingleGoal(int startnodeid, HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> exploits, boolean singlepath, int npath, int chosenattacker, boolean maxoverlap, boolean expoverlap) {

		int id = 0;

		//int goals[] = {26,23,25,24};

		/*boolean singlepath = false;
		int npath = 3;*/

		Attacker a0  = new Attacker(id++);
		//a0.goals.put(0, 23);
		a0.goals.put(0, 23);
		//	a0.goals.put(1, 24);
		a0.addExploits(new int[] {0,1,2,3,4,5,6,7});
		//a0.findFixedPolifyBFS(net, exploits, 23);
		//a0.findFixedPolifyBFS(net, exploits, 24);
		//a0.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 23, singlepath, npath);
		
		a0.findFixedPolicyMinCost(startnodeid, net, exploits, 23, singlepath, npath);
		
		//a0.findFixedPolicyShortestPath(startnodeid, net, exploits, 23, singlepath, npath);
		a0.removeDuplicatePolicies();
		
		
		//a0.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 26});
		//a0.addPolicy(new int[] {0, 1, 3, 1, 1, 1, 26});
		
		//a0.addPolicy(0, new int[] {0, 2, 5, 10, 16, 21, 26});
		//a0.addPolicy(1, new int[] {0, 1, 4, 15, 21, 26});
		//a0.addPolicy(1, new int[] {0, 2, 6, 10, 15, 20, 24});



		Attacker a1  = new Attacker(id++);
		a1.goals.put(0, 24);
		//a1.goals.put(1, 24);
		//a1.goals.put(1, 25);
		a1.addExploits(new int[] {0,1,2,3,4,5,6,7});
		//a1.findFixedPolifyBFS(net, exploits, 23, singlepath, npath);
		//a1.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 24, singlepath, npath);
		
		a1.findFixedPolicyMinCost(startnodeid, net, exploits, 24, singlepath, npath);
		
		a1.removeDuplicatePolicies();
		
		//a1.findFixedPolicyShortestPath(startnodeid, net, exploits, 24, singlepath, npath);
		
		//a1.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 23});
		//a1.addPolicy(new int[] {0, 1, 5, 4, 1, 1, 23});
		
		/*
		 * a1.findFixedPolifyBFS(net, exploits, 24);
		a1.findFixedPolifyBFS(net, exploits, 25);*/
		//a1.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 23});
		//a1.addPolicy(1, new int[] {0, 2, 5, 14, 20, 25});

		Attacker a2  = new Attacker(id++);
		//a2.goals.put(0, 23);
		a2.goals.put(0, 25);
		//a2.goals.put(2, 25);
		//a2.goals.put(3, 26);

		a2.addExploits(new int[] {0, 1,2,3,4,5,6,7});
		//a2.findFixedPolifyBFS(net, exploits, 24, singlepath, npath);
		//a2.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 25, singlepath, npath);
		//a2.findFixedPolicyShortestPath(startnodeid, net, exploits, 25, singlepath, npath);
		
		a2.findFixedPolicyMinCost(startnodeid, net, exploits, 25, singlepath, npath);
		
		a2.removeDuplicatePolicies();
		
		
		//a2.addPolicy(new int[] {0, 1, 7, 2, 1, 1, 24});
		//a2.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 24});
		
		//a2.findFixedPolifyBFS(net, exploits, 25);
		//a2.findFixedPolifyBFS(net, exploits, 26);

		//a2.addPolicy(0, new int[] {0, 1, 3, 7, 13, 18, 23});
		//a2.addPolicy(0, new int[] {0, 2, 5, 14, 20, 24});
		//a2.addPolicy(2, new int[] {0, 2, 5, 10, 15, 20, 25});
		//a2.addPolicy(3, new int[] {0, 2, 6, 10, 16, 21, 26});


		Attacker a3  = new Attacker(id++);
		a3.goals.put(0, 26);
		//a3.goals.put(1, 24);
		a3.addExploits(new int[] {0, 1,2,3,4,5,6,7});
		//a3.findFixedPolifyBFS(net, exploits, 23);
		//a3.findFixedPolifyBFS(net, exploits, 24);
		//a3.findFixedPolifyBFS(net, exploits, 25, singlepath, npath);
		//a3.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 26, singlepath, npath);
		//a3.findFixedPolicyShortestPath(startnodeid, net, exploits, 26, singlepath, npath);
		
		a3.findFixedPolicyMinCost(startnodeid, net, exploits, 26, singlepath, npath);
		
		
		//a3.addPolicy(new int[] {0, 1, 5, 7, 1, 1, 25});
		//a3.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 25});
		a3.removeDuplicatePolicies();
		//a3.addPolicy(0, new int[] {0, 2, 5, 10, 15, 20, 25});




		/*Attacker a4  = new Attacker(id++);
		a4.goals.put(0, 24);
		a4.goals.put(1, 25);
		a4.addExploits(new int[] {1, 2});
		a4.findFixedPolifyBFS(net, exploits, 24);
		a4.findFixedPolifyBFS(net, exploits, 25);



		Attacker a5  = new Attacker(id++);
		//a5.goals.put(0, 23);
		a5.goals.put(0, 26);
		a5.addExploits(new int[] {0, 3});
		//a5.findFixedPolifyBFS(net, exploits, 23);
		a5.findFixedPolifyBFS(net, exploits, 26);*/

		//a3.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 24});
		
		
		
		

		attackers.put(a0.id, a0);
		attackers.put(a1.id, a1);
		attackers.put(a2.id, a2);
		attackers.put(a3.id, a3);
		
		
		//HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
		
		
		printAttackers(attackers);
		
		
		
		if(maxoverlap && !singlepath)
		{
			refinePoliciesInit(attackers, chosenattacker);
		}
		else if(expoverlap && !singlepath)
		{
			//refinePoliciesInitExpOverlap(attackers, chosenattacker);
			refinePoliciesInitMaxExpOverlap(attackers, chosenattacker);
		}
		
		
		
		printAttackers(attackers);
		
		
		//refinePoliciesMeasure(attackpolicies);
		
		System.out.println("here I am ");
		
		
		
		/*
		attackers.put(4, a4);
		attackers.put(5, a5);*/


		//return goals;

	}
	
	
	public static void constructAttackers(int startnodeid, HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> exploits, boolean singlepath, int npath, int chosenattacker, boolean maxoverlap, boolean expoverlap, int nattackers, int[] goals) {

		
		double mincosts[] = new double[nattackers];
		
		for(int id=0; id<nattackers; id++)
		{

			
			Attacker a0  = new Attacker(id);
			
			a0.goals.put(0, goals[id]);
			
			for(Exploits e: exploits.values())
			{
				a0.addExploits(new int[] {e.id});
			}
			
			
			//a0.findFixedPolifyBFS(net, exploits, 23);
			//a0.findFixedPolifyBFS(net, exploits, 24);
			//a0.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 23, singlepath, npath);

			//a0.findFixedPolicyShortestPath(startnodeid, net, exploits, 8, singlepath, npath);

			double minc = a0.findFixedPolicyMinCost(startnodeid, net, exploits, goals[id], singlepath, npath);
			
			mincosts[id] = minc;


			//a0.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 26});
			//a0.addPolicy(new int[] {0, 1, 3, 1, 1, 1, 26});
			a0.removeDuplicatePolicies();
			attackers.put(a0.id, a0);


		}
		
		
		printAttackers(attackers);
		
		if(maxoverlap && !singlepath)
		{
			refinePoliciesInit(attackers, chosenattacker);
		}
		else if(expoverlap && !singlepath)
		{
			//refinePoliciesInitExpOverlap(attackers, chosenattacker);
			refinePoliciesInitMaxExpOverlap(attackers, chosenattacker);
		}
		
		
		
		printAttackers(attackers);
		
		
		//refinePoliciesMeasure(attackpolicies);
		
		System.out.println("here I am ");
		
		
		
		

	}
	
	
	
	public static void constructAttackersMILP(int startnodeid, HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> exploits, boolean singlepath, int npath, int chosenattacker, 
			boolean maxoverlap, boolean expoverlap, int nattackers, int[] goals, HashMap<Integer,Node> honeypots) {

		
		HashMap<Integer, Integer> mincosts = new HashMap<Integer, Integer>();
		
		for(int id=0; id<nattackers; id++)
		{

			
			Attacker a0  = new Attacker(id);
			
			a0.goals.put(0, goals[id]);
			
			for(Exploits e: exploits.values())
			{
				a0.addExploits(new int[] {e.id});
			}
			
			
			//a0.findFixedPolifyBFS(net, exploits, 23);
			//a0.findFixedPolifyBFS(net, exploits, 24);
			//a0.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 23, singlepath, npath);

			//a0.findFixedPolicyShortestPath(startnodeid, net, exploits, 8, singlepath, npath);

			int minc = (int)a0.findFixedPolicyMinCost(startnodeid, net, exploits, goals[id], singlepath, npath);
			//int minc = (int)a0.findMinCost(startnodeid, net, exploits, goals[id], singlepath, npath);
			
			
			
			mincosts.put(id, minc);


			//a0.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 26});
			//a0.addPolicy(new int[] {0, 1, 3, 1, 1, 1, 26});
			a0.removeDuplicatePolicies();
			attackers.put(a0.id, a0);


		}
		
		int start = 0;
		
		constructAttackerPoliciesMILP(net, exploits, honeypots, chosenattacker, goals, nattackers, mincosts, attackers, start);
		
		
		
		printAttackers(attackers);
		
		
		/*if(maxoverlap && !singlepath)
		{
			refinePoliciesInit(attackers, chosenattacker);
		}
		else if(expoverlap && !singlepath)
		{
			//refinePoliciesInitExpOverlap(attackers, chosenattacker);
			refinePoliciesInitMaxExpOverlap(attackers, chosenattacker);
		}
		
		*/
		
		//printAttackers(attackers);
		
		
		//refinePoliciesMeasure(attackpolicies);
		
		System.out.println("here I am ");
		
		
		
		

	}
	
	
	private static boolean constructAttackerPoliciesMILP(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Node> honeypots, int chosenattacker, int[] goals, int nattackers, HashMap<Integer, Integer> mincosts, HashMap<Integer,Attacker> attackers, int start) {
		
		int n= net.size();
		int e = exploits.size();
		
		
		int[] minc = new int[mincosts.size()];
		
		int i=0;
		for(int a: attackers.keySet())
		{
			minc[i++] = mincosts.get(a);
		}
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		//HashMap<Integer,Node> newnet = new HashMap<Integer,Node>();
		
		
		//int[][] wt = buildCostMatrix(net, nodeexpltmap, nodeexpltmapback, edgecost, exploits);
		//w[node1][node2][exploits]
		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		//int start = 0;
		
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		double[] priors = new double[nattackers];
		
		for(int a: attackers.keySet())
		{
			priors[a] = 1.0/ nattackers;
		}
		
		
		long startTime = System.currentTimeMillis();
		
		
		
		
		
		//double[] bfsconf = findMinCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		ArrayList<ArrayList<double[]>> paths = Solver.attackerPolicyInItMILP(w, start, g, exploits.size(), nattackers, priors, minc, net.size(), chosenattacker);
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		if(paths == null)
		{
			return false;
		}
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		PlanrecognitionExp.assignAttackerPolicy(paths, g, att);
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		
		return true;
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		
		
	}
	
	
	private static boolean constructAttackerPoliciesMILPRun(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Node> honeypots, int chosenattacker, int[] goals, int nattackers, HashMap<Integer, Integer> mincosts, HashMap<Integer,Attacker> attackers, int start, int[][][] hpdepcosts) {
		
		int n= net.size();
		int e = exploits.size();
		
		
		int[] minc = new int[mincosts.size()];
		
		int i=0;
		for(int a: attackers.keySet())
		{
			minc[i++] = mincosts.get(a);
		}
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		//HashMap<Integer,Node> newnet = new HashMap<Integer,Node>();
		
		
		//int[][] wt = buildCostMatrix(net, nodeexpltmap, nodeexpltmapback, edgecost, exploits);
		//w[node1][node2][exploits]
		int [][][] w = hpdepcosts;//PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		//int start = 0;
		
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		double[] priors = new double[nattackers];
		
		for(int a: attackers.keySet())
		{
			priors[a] = 1.0/ nattackers;
		}
		
		
		long startTime = System.currentTimeMillis();
		
		
		
		
		
		//double[] bfsconf = findMinCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		ArrayList<ArrayList<double[]>> paths = Solver.attackerPolicyInItMILP(w, start, g, exploits.size(), nattackers, priors, minc, net.size(), chosenattacker);
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		if(paths == null)
		{
			return false;
		}
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		PlanrecognitionExp.assignAttackerPolicy(paths, g, att);
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		
		return true;
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		
		
	}
	
	
	
	
	private static int buildAttPolMaxOverlapHEMILP(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			int[] goals, HashMap<Integer,Attacker> attackers, HashMap<Integer,Integer> mincost, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer,Double> priorsattackertype, int start) throws Exception {
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		//int start = 0;
		
		int totalconf = 1;
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		int nattackers = attackers.size();
		
		
		
		
		long startTime = System.currentTimeMillis();
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		
		
		/*double[] bfsconf = findMinCostPath(w, g, totalconf, start);
		
		System.out.println("minc costs ");
		
		for(double c: bfsconf)
		{
			System.out.print(c+", ");
		}
		System.out.println();*/
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		ArrayList<ArrayList<double[]>> paths = Solver.solveMaxOvelapMILPV2(w, start, g, exploits.size(), nattackers, priorsattackertype, mincost, net.size());
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		
		/**
		 * assign the policy to the attackers
		 */
		
		 int ovrlaplen = assignAttackerPolicyV2(paths, g, att, attackpolicies);
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		/*
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		*/
		
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		 
		 return ovrlaplen;
		
		
	}
	
	
	
	private static int buildAttPolMaxOverlapHPMILP(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			int[] goals, HashMap<Integer,Attacker> attackers, HashMap<Integer,Integer> mincost, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer,Double> priorsattackertype, int start, int[][][] w) throws Exception {
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		
		//int start = 0;
		
		int totalconf = 1;
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		int nattackers = attackers.size();
		
		
		
		
		long startTime = System.currentTimeMillis();
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		
		
		/*double[] bfsconf = findMinCostPath(w, g, totalconf, start);
		
		System.out.println("minc costs ");
		
		for(double c: bfsconf)
		{
			System.out.print(c+", ");
		}
		System.out.println();*/
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		ArrayList<ArrayList<double[]>> paths = Solver.solveMaxOvelapMILPV2(w, start, g, exploits.size(), nattackers, priorsattackertype, mincost, net.size());
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		
		/**
		 * assign the policy to the attackers
		 */
		
		 int ovrlaplen = assignAttackerPolicyV2(paths, g, att, attackpolicies);
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		/*
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		*/
		
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		 
		 return ovrlaplen;
		
		
	}
	
	
	
	private static double[][] buildAttPolMaxExpOverlapHEMILP(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			int[] goals, HashMap<Integer,Attacker> attackers, HashMap<Integer,Integer> mincost, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer,Double> priorsattackertype, 
			int start, HashMap<Integer,Integer> atmap, HashMap<Integer,Integer> atmapback) throws Exception {
		
		int n= net.size();
		int e = exploits.size();
		
		
		double[][] overlap = new double[attackers.size()][attackers.size()];
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		//int start = 0;
		
		int totalconf = 1;
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		int nattackers = attackers.size();
		
		
		
		
		long startTime = System.currentTimeMillis();
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		
		
		/*double[] bfsconf = findMinCostPath(w, g, totalconf, start);
		
		System.out.println("minc costs ");
		
		for(double c: bfsconf)
		{
			System.out.print(c+", ");
		}
		System.out.println();*/
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		ArrayList<ArrayList<double[]>> paths = Solver.solveMaxExpOvelapMILPV2(w, start, g, exploits.size(), nattackers, priorsattackertype, mincost, net.size(), atmap, atmapback, overlap);
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		
		/**
		 * assign the policy to the attackers
		 */
		
		 int ovrlaplen = assignAttackerPolicyV2(paths, g, att, attackpolicies);
		 
		 
		 
		
		 
		 
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		/*
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		*/
		
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		 
		 return overlap;
		
		
	}
	
	
	
	private static double[][] buildAttPolMaxExpOverlapHPMILP(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			int[] goals, HashMap<Integer,Attacker> attackers, HashMap<Integer,Integer> mincost, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer,Double> priorsattackertype, 
			int start, HashMap<Integer,Integer> atmap, HashMap<Integer,Integer> atmapback, int[][][] w) throws Exception {
		
		int n= net.size();
		int e = exploits.size();
		
		
		double[][] overlap = new double[attackers.size()][attackers.size()];
		
		
		/**
		 * create a map of nodes with exploits to id
		 */
		
		
		//int [][][] w = PlanrecognitionExp.build3DCostMatrix(net, exploits);
		
		//int start = 0;
		
		int totalconf = 1;
		
		/*int[][][][] hpdeploymentcost = new int[totalconf][n+hplimit][n+hplimit][e];
		
		
		
		
		
		
		buildCostVar(hpdeploymentcost, net, exploits, nattackers, e, w, slotids, hpids, honeypots, placestoallocatehp, hpdeploylimit, freehps, hplimit);
		
		
		*//*totalconf = 50;
		
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
		
		
		
		
		
		
		
		//verifyW(hpdeploymentcost);
		
		int nattackers = attackers.size();
		
		
		
		
		long startTime = System.currentTimeMillis();
		
		ArrayList<Integer> g = new ArrayList<Integer>();
		
		for(int a: goals)
		{
			g.add(a);
		}
		
		
		
		/*double[] bfsconf = findMinCostPath(w, g, totalconf, start);
		
		System.out.println("minc costs ");
		
		for(double c: bfsconf)
		{
			System.out.print(c+", ");
		}
		System.out.println();*/
		
		
		
		//double[] bfsconf1 = findMaxCostPath(hpdeploymentcost, goals, totalconf, start, chosenattacker, priors);
		 
		
		
		long endTime   =  System.currentTimeMillis();
		long bfstotalTime = endTime - startTime;
		System.out.println("BFS runtime: "+bfstotalTime);
		
		
		
		// ArrayList<int[]> path = Solver.solve3DCostWithHP(w, start, goal, exploits.size(), nattackers, hpdeploymentcost);
		 
		
		startTime =  System.currentTimeMillis();
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttacker(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerCommPath(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		ArrayList<ArrayList<double[]>> paths = Solver.solveMaxExpOvelapMILPV2(w, start, g, exploits.size(), nattackers, priorsattackertype, mincost, net.size(), atmap, atmapback, overlap);
		
		
		//ArrayList<ArrayList<double[]>> paths = Solver.solveHPDeploymentMultAttackerWorstCase(w, start, goals, exploits.size(), nattackers, hpdeploymentcost, totalconf, priors);
		
		
		
		
		endTime   =  System.currentTimeMillis();
		long milptotalTime = endTime - startTime;
		
		
		ArrayList<Attacker> att = new ArrayList<Attacker>();
		
		
		for(Attacker a: attackers.values())
		{
			att.add(a);
		}
		
		
		
		/**
		 * assign the policy to the attackers
		 */
		
		 int ovrlaplen = assignAttackerPolicyV2(paths, g, att, attackpolicies);
		 
		 
		 
		
		 
		 
		
		//printAttackers(attackers);
		
		
		/*System.out.println("#conf: "+totalconf);
		
		System.out.println("BFS conf: "+bfsconf[0]);
		System.out.println("MILP conf: "+milpconf[0]);
		
		
		System.out.println("BFS cost: "+bfsconf[1]);
		System.out.println("MILP cost: "+milpconf[1]);*/
		
		
		
		/*
		System.out.println("BFS runtime: "+bfstotalTime);
		System.out.println("MILP runtime: "+milptotalTime);
		
		*/
		
		
		
		//ArrayList<int[]> paths = Solver.solveHPDeploymentSingleAttacker(w, start, goal, exploits.size(), nattakers, hpdeploymentcost, totalconf);
		 //printSolution(paths);
		 
		 return overlap;
		
		
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
	
	
	private static double findMinCostP(ArrayList<Integer> path, int[][][] confcost, int startnode, ArrayList<Integer> goals, int attacker) throws Exception {
		
		
		double mincost = Double.POSITIVE_INFINITY;
		
		
		
		
		
		

		Queue<Node> fringequeue = new LinkedList<Node>();
		Queue<Integer> closed = new LinkedList<Integer>();

		Node start = new Node(startnode);
		

		//start.currentcost = minCostExploit(start, attackerexploits, allexploits)
		

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
					PlanrecognitionExp.traversePolicy(node, tmppath);
					System.out.println();
					
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
	
	private static double[] findMinCostPath(int[][][] hpdeploymentcost, ArrayList<Integer> goals, int totalconf, int startnode) throws Exception {
		
		
		double mincost = Double.POSITIVE_INFINITY;
		int minconf = -1;
		
		ArrayList<Integer> allmincostcofs = new ArrayList<Integer>();
		
		
		double [] minc = new double[goals.size()];
		
		ArrayList<ArrayList<Integer>> minpath = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<ArrayList<Integer>>> minpaths = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		for(int c=0; c<totalconf; c++)
		{
			int[][][] confcost = hpdeploymentcost;
			
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
				minc[a] = tmpcost;
				sumcost += (tmpcost/**priors[a]*/);
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
				/*System.out.println("mincost path: ");
				
				
				for(ArrayList<Integer> path: minpath)
				{
					for(int p: path)
					{
						System.out.print(p+"->");
					}


					System.out.print("\n");
				}*/
				minpaths.clear();
				minpaths.add(minpath);
			}
			else if(sumcost==mincost)
			{
				mincost = sumcost;
				minconf = c;
				minpath = tmppaths;
				
				
				
				allmincostcofs.add(minconf);
				
				/*System.out.println("mincost path: ");
				
				
				for(ArrayList<Integer> path: minpath)
				{
					for(int p: path)
					{
						System.out.print(p+"->");
					}


					System.out.print("\n");
				}*/
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



		return minc;


		
	}
	
	
	public static int assignAttackerPolicyV2(ArrayList<ArrayList<double[]>> paths, ArrayList<Integer> g, ArrayList<Attacker> att2, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies) {
		
		
		int overlaplen = 1000000;
		// double[] winnerconf = {-1.0, -1.0};
		 
			 for(ArrayList<double[]> path: paths)
			 {
				 
				// System.out.println("****attacker "+ paths.indexOf(path)+"*****");

				 
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

						 overlaplen = (int)a[a.length-1];

						// System.out.println(a[0] +"->"+ a[1] +"("+a[2]+")");
						 pol.put(pol.size(), (int)a[0]);
						 
						 if(a[1]==g.get(att))
						 {
							 HashMap<Integer,HashMap<Integer,Integer>> pols = new HashMap<Integer,HashMap<Integer,Integer>>();
							 pol.put(pol.size(), (int)a[1]);
							 int attid = att2.get(att).id;
							// att2.get(att).fixedpolicy.clear();
							 //att2.get(att).fixedpolicy.put(att2.get(att).fixedpolicy.size(), pol);
							 pols.put(pols.size(), pol);
							 attackpolicies.put(attid, pols);
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
			 
			 
			// System.out.println();
			 
			// return winnerconf;
			 
			 return overlaplen;
		
	}
	
	
	public static int assignAttackerPolicyV3(ArrayList<ArrayList<double[]>> paths, ArrayList<Integer> g, ArrayList<Attacker> att2, 
			HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, HashMap<Integer,Integer> atmapback) {
		
		
		int overlaplen = 1000000;
		// double[] winnerconf = {-1.0, -1.0};
		 
			 for(ArrayList<double[]> path: paths)
			 {
				 
				// System.out.println("****attacker "+ paths.indexOf(path)+"*****");

				 
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

						 overlaplen = (int)a[a.length-1];

						// System.out.println(a[0] +"->"+ a[1] +"("+a[2]+")");
						 pol.put(pol.size(), (int)a[0]);
						 
						 if(a[1]==g.get(att))
						 {
							 HashMap<Integer,HashMap<Integer,Integer>> pols = new HashMap<Integer,HashMap<Integer,Integer>>();
							 pol.put(pol.size(), (int)a[1]);
							 int attid = att2.get(att).id;
							// att2.get(att).fixedpolicy.clear();
							 //att2.get(att).fixedpolicy.put(att2.get(att).fixedpolicy.size(), pol);
							 pols.put(pols.size(), pol);
							 attackpolicies.put(attid, pols);
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
			 
			 
			// System.out.println();
			 
			// return winnerconf;
			 
			 return overlaplen;
		
	}





	public static void constructAttackersWithExploitsSingleGoal(int startnodeid, HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, 
			HashMap<Integer,Exploits> exploits, boolean singlepath, int npath, int chosenattacker, boolean maxoverlap, boolean expoverlap) {

		int id = 0;

		//int goals[] = {26,23,25,24};

		/*boolean singlepath = false;
		int npath = 3;*/

		Attacker a0  = new Attacker(id++);
		//a0.goals.put(0, 23);
		a0.goals.put(0, 23);
		//	a0.goals.put(1, 24);
		a0.addExploits(new int[] {0, 1});
		//a0.findFixedPolifyBFS(net, exploits, 23);
		//a0.findFixedPolifyBFS(net, exploits, 24);
		//a0.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 23, singlepath, npath);
		
		a0.findFixedExploitPolicyMaxRewardMinCost(startnodeid, net, exploits, 23, singlepath, npath);
		
		//a0.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 26});
		//a0.addPolicy(new int[] {0, 1, 3, 1, 1, 1, 26});
		a0.removeDuplicateExpltPolicies();
		//a0.addPolicy(0, new int[] {0, 2, 5, 10, 16, 21, 26});
		//a0.addPolicy(1, new int[] {0, 1, 4, 15, 21, 26});
		//a0.addPolicy(1, new int[] {0, 2, 6, 10, 15, 20, 24});



		Attacker a1  = new Attacker(id++);
		a1.goals.put(0, 24);
		//a1.goals.put(1, 24);
		//a1.goals.put(1, 25);
		a1.addExploits(new int[] {2, 3});
		//a1.findFixedPolifyBFS(net, exploits, 23, singlepath, npath);
		//a1.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 24, singlepath, npath);
		
		
		
		a1.findFixedExploitPolicyMaxRewardMinCost(startnodeid, net, exploits, 24, singlepath, npath);
		
		
		//a1.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 23});
		//a1.addPolicy(new int[] {0, 1, 5, 4, 1, 1, 23});
		a1.removeDuplicateExpltPolicies();
		/*
		 * a1.findFixedPolifyBFS(net, exploits, 24);
		a1.findFixedPolifyBFS(net, exploits, 25);*/
		//a1.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 23});
		//a1.addPolicy(1, new int[] {0, 2, 5, 14, 20, 25});

		Attacker a2  = new Attacker(id++);
		//a2.goals.put(0, 23);
		a2.goals.put(0, 25);
		//a2.goals.put(2, 25);
		//a2.goals.put(3, 26);

		a2.addExploits(new int[] {4,5});
		//a2.findFixedPolifyBFS(net, exploits, 24, singlepath, npath);
		//a2.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 25, singlepath, npath);
		
		a2.findFixedExploitPolicyMaxRewardMinCost(startnodeid, net, exploits, 25, singlepath, npath);
		
		//a2.addPolicy(new int[] {0, 1, 7, 2, 1, 1, 24});
		//a2.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 24});
		a2.removeDuplicateExpltPolicies();
		//a2.findFixedPolifyBFS(net, exploits, 25);
		//a2.findFixedPolifyBFS(net, exploits, 26);

		//a2.addPolicy(0, new int[] {0, 1, 3, 7, 13, 18, 23});
		//a2.addPolicy(0, new int[] {0, 2, 5, 14, 20, 24});
		//a2.addPolicy(2, new int[] {0, 2, 5, 10, 15, 20, 25});
		//a2.addPolicy(3, new int[] {0, 2, 6, 10, 16, 21, 26});


		Attacker a3  = new Attacker(id++);
		a3.goals.put(0, 26);
		//a3.goals.put(1, 24);
		a3.addExploits(new int[] {6, 7});
		//a3.findFixedPolifyBFS(net, exploits, 23);
		//a3.findFixedPolifyBFS(net, exploits, 24);
		//a3.findFixedPolifyBFS(net, exploits, 25, singlepath, npath);
		//a3.findFixedPolicyMaxRewardMinCost(startnodeid, net, exploits, 26, singlepath, npath);
		
		a3.findFixedExploitPolicyMaxRewardMinCost(startnodeid, net, exploits, 26, singlepath, npath);
		
		//a3.addPolicy(new int[] {0, 1, 5, 7, 1, 1, 25});
		//a3.addPolicy(new int[] {0, 1, 5, 7, 2, 1, 1, 1, 25});
		a3.removeDuplicateExpltPolicies();
		//a3.addPolicy(0, new int[] {0, 2, 5, 10, 15, 20, 25});




		/*Attacker a4  = new Attacker(id++);
		a4.goals.put(0, 24);
		a4.goals.put(1, 25);
		a4.addExploits(new int[] {1, 2});
		a4.findFixedPolifyBFS(net, exploits, 24);
		a4.findFixedPolifyBFS(net, exploits, 25);



		Attacker a5  = new Attacker(id++);
		//a5.goals.put(0, 23);
		a5.goals.put(0, 26);
		a5.addExploits(new int[] {0, 3});
		//a5.findFixedPolifyBFS(net, exploits, 23);
		a5.findFixedPolifyBFS(net, exploits, 26);*/

		//a3.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 24});
		
		
		
		

		attackers.put(a0.id, a0);
		attackers.put(a1.id, a1);
		attackers.put(a2.id, a2);
		attackers.put(a3.id, a3);
		
		
		//HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
		
		
		printAttackersExploit(attackers);
		
		if(maxoverlap)
		{
			//refinePoliciesInit(attackers, chosenattacker);
			refineExpltPoliciesInit(attackers, chosenattacker);
		}
		else if(expoverlap)
		{
			//refinePoliciesInitExpOverlap(attackers, chosenattacker);
			refineExploitPoliciesInitMaxExpOverlap(attackers, chosenattacker);
		}
		
		
		
		printAttackersExploit(attackers);
		
		
		//refinePoliciesMeasure(attackpolicies);
		
		System.out.println("here I am ");
		
		
		
		/*
		attackers.put(4, a4);
		attackers.put(5, a5);*/


		//return goals;

	}


	private static void refinePoliciesInit(HashMap<Integer, Attacker> attackers, int chosenattacker) {
		
		
		HashMap<Integer, HashMap<Integer, Integer>> attpolicies = new HashMap<Integer, HashMap<Integer, Integer>>();
		
		ArrayList<int[]> done = new ArrayList<int[]>();
		
		HashMap<Integer, Integer> maxindex= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> maxlengths= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> opmaxlengths= new HashMap<Integer, Integer>();
		
		int maxlen = -1;
		
		Attacker a0 = attackers.get(chosenattacker);
		
		//for(Attacker a0: attackers.values())
		{

			maxlen = -1;
			maxlengths.put(a0.id, -1);
			int opmaxlen = -1;
			for(Attacker a1: attackers.values())
			{

				opmaxlen = -1;
				opmaxlengths.put(a1.id, -1);
				if(a0.id != a1.id)
				{
					int[] arr = {a0.id, a1.id};
					boolean isdone = isDoneIt(done, arr);
					if(!isdone)
					{

						done.add(arr);
						for(Integer pid0: a0.fixedpolicy.keySet())
						{
							for(Integer pid1: a1.fixedpolicy.keySet())
							{

								

								HashMap<Integer, Integer> p0 = a0.fixedpolicy.get(pid0);
								HashMap<Integer, Integer> p1 = a1.fixedpolicy.get(pid1);



								int len = commonLen(p0, p1);

								if(maxlen<len && len>0)
								{
									maxlen = len;
									maxlengths.put(a0.id, maxlen);
									maxindex.put(a0.id, pid0);
									attpolicies.put(a0.id, p0);
									
								}
								
								if(opmaxlen<len && len>0)
								{
									opmaxlen = len;
									opmaxlengths.put(a1.id, opmaxlen);
									attpolicies.put(a1.id, p1);
								}
								

								

							}

						}

					}
				}
			}
			System.out.println("Max len for attacker "+ a0.id + " is "+ maxlen + ", maxindex "+ maxindex.get(a0.id));
			//System.out.println("Max len for attacker "+ a1.id + " is "+ maxlen + ", maxindex "+ maxindex.get(a0.id));
		}
		
		
		for(Attacker att: attackers.values())
		{
			int tmplen = -1;
			
			if(att.id == chosenattacker)
			{
				tmplen = maxlengths.get(att.id);
			}
			else
			{
				tmplen = opmaxlengths.get(att.id);
			}
			
			if(tmplen==-1 && att.fixedpolicy.size()>0) // multiple strategy but no match with chosen attacker
			{
				HashMap<Integer, Integer> p0 = att.fixedpolicy.get(0);
				att.fixedpolicy.clear();
				att.fixedpolicy.put(att.fixedpolicy.size(), p0);
			}
			
			if(tmplen != -1) // if attacker's max len strategy found
			{
				att.fixedpolicy.clear();
				att.fixedpolicy.put(att.fixedpolicy.size(), attpolicies.get(att.id));
			}
		}



		
		
	}
	
	
private static void refineExpltPoliciesInit(HashMap<Integer, Attacker> attackers, int chosenattacker) {
		
		
		HashMap<Integer, HashMap<Integer, int[]>> attpolicies = new HashMap<Integer, HashMap<Integer, int[]>>();
		
		ArrayList<int[]> done = new ArrayList<int[]>();
		
		HashMap<Integer, Integer> maxindex= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> maxlengths= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> opmaxlengths= new HashMap<Integer, Integer>();
		
		int maxlen = -1;
		
		Attacker a0 = attackers.get(chosenattacker);
		
		//for(Attacker a0: attackers.values())
		{

			maxlen = -1;
			maxlengths.put(a0.id, -1);
			int opmaxlen = -1;
			for(Attacker a1: attackers.values())
			{

				opmaxlen = -1;
				opmaxlengths.put(a1.id, -1);
				if(a0.id != a1.id)
				{
					int[] arr = {a0.id, a1.id};
					boolean isdone = isDoneIt(done, arr);
					if(!isdone)
					{

						done.add(arr);
						for(Integer pid0: a0.fixedexploitpolicy.keySet())
						{
							for(Integer pid1: a1.fixedexploitpolicy.keySet())
							{

								

								HashMap<Integer, int[]> p0 = a0.fixedexploitpolicy.get(pid0);
								HashMap<Integer, int[]> p1 = a1.fixedexploitpolicy.get(pid1);



								int len = commonLenExplt(p0, p1);

								if(maxlen<len && len>0)
								{
									maxlen = len;
									maxlengths.put(a0.id, maxlen);
									maxindex.put(a0.id, pid0);
									attpolicies.put(a0.id, p0);
									
								}
								
								if(opmaxlen<len && len>0)
								{
									opmaxlen = len;
									opmaxlengths.put(a1.id, opmaxlen);
									attpolicies.put(a1.id, p1);
								}
								

								

							}

						}

					}
				}
			}
			System.out.println("Max len for attacker "+ a0.id + " is "+ maxlen + ", maxindex "+ maxindex.get(a0.id));
			//System.out.println("Max len for attacker "+ a1.id + " is "+ maxlen + ", maxindex "+ maxindex.get(a0.id));
		}
		
		
		for(Attacker att: attackers.values())
		{
			int tmplen = -1;
			
			if(att.id == chosenattacker)
			{
				tmplen = maxlengths.get(att.id);
			}
			else
			{
				tmplen = opmaxlengths.get(att.id);
			}
			
			if(tmplen==-1 && att.fixedpolicy.size()>0) // multiple strategy but no match with chosen attacker
			{
				HashMap<Integer, int[]> p0 = att.fixedexploitpolicy.get(0);
				att.fixedexploitpolicy.clear();
				att.fixedexploitpolicy.put(att.fixedexploitpolicy.size(), p0);
			}
			
			if(tmplen != -1) // if attacker's max len strategy found
			{
				att.fixedexploitpolicy.clear();
				att.fixedexploitpolicy.put(att.fixedexploitpolicy.size(), attpolicies.get(att.id));
			}
		}



		
		
	}
	
	
	private static void refinePoliciesInitExpOverlap(HashMap<Integer, Attacker> attackers, int chosenattacker) 
	{


		





	}

	
	
	
	private static void refinePoliciesInitMaxExpOverlap(HashMap<Integer, Attacker> attackers, int chosenattacker) 
	{

		ArrayList<HashMap<Integer, Integer>> permsofpathindexes = getPathPerms(attackers);

		//printComb(permsofpathindexes);


		HashMap<Integer, HashMap<Integer, Integer>> maxoverlappolicies = getMaxExpOverlapPolicies(attackers, permsofpathindexes, chosenattacker);


		for(Attacker att: attackers.values())
		{
			att.fixedpolicy.clear();
			att.fixedpolicy.put(att.fixedpolicy.size(), maxoverlappolicies.get(att.id));
		}



	}
	
	
	private static void refineExploitPoliciesInitMaxExpOverlap(HashMap<Integer, Attacker> attackers, int chosenattacker) 
	{

		ArrayList<HashMap<Integer, Integer>> permsofpathindexes = getPathPermsExploit(attackers);

		//printComb(permsofpathindexes);


		HashMap<Integer, HashMap<Integer, int[]>> maxoverlappolicies = getMaxExpOverlapExploitPolicies(attackers, permsofpathindexes, chosenattacker);


		for(Attacker att: attackers.values())
		{
			att.fixedexploitpolicy.clear();
			att.fixedexploitpolicy.put(att.fixedexploitpolicy.size(), maxoverlappolicies.get(att.id));
		}



	}
	
	
	private static double refinePoliciesMaxExpOverlap(HashMap<Integer, Attacker> attackers, HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> attpolicies, int chosenattacker) 
	{

		ArrayList<HashMap<Integer, Integer>> permsofpathindexes = getPathPermsV2(attackers, attpolicies);

		//printComb(permsofpathindexes);


		double maxexpoverlap = getMaxExpOverlapPoliciesV2(attackers, permsofpathindexes, chosenattacker, attpolicies);


		/*for(Attacker att: attackers.values())
		{
			att.fixedpolicy.clear();
			att.fixedpolicy.put(att.fixedpolicy.size(), maxoverlappolicies.get(att.id));
		}

*/
		return maxexpoverlap;

	}

	
	
	
private static HashMap<Integer, HashMap<Integer, Integer>> getMaxExpOverlapPolicies(
		HashMap<Integer, Attacker> attackers, ArrayList<HashMap<Integer, Integer>> permsofpathindexes, int chosenattacker) {
	
	
	System.out.println("********Determining max overlapping policies********");
	
	double maxexpoverlap = Double.NEGATIVE_INFINITY;
	HashMap<Integer, Integer> maxsettings = new HashMap<Integer, Integer>();
	
	for(HashMap<Integer, Integer> pathindex: permsofpathindexes)
	{
		HashMap<Integer, HashMap<Integer, Integer>> tmppolicies = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> chosenattpolicy = new HashMap<Integer, Integer>();
		for(Integer atindex: pathindex.keySet())
		{
			int pindex = pathindex.get(atindex);
			//System.out.println("att "+ atindex +" pathindex "+ pindex);
			tmppolicies.put(atindex, attackers.get(atindex).fixedpolicy.get(pindex));
			if(atindex==chosenattacker)
			{
				chosenattpolicy = attackers.get(atindex).fixedpolicy.get(pindex);
			}
			
		}
		
		//System.out.println("\nTmp Policies ");
		
		
		
		double policylen = chosenattpolicy.size();
		
		double sumexpoverlap = 0.0;
		
		for(int at: tmppolicies.keySet())
		{
			
			if(at != chosenattacker)
			{
				HashMap<Integer, Integer> policy = tmppolicies.get(at);
				
				double overlap = commonLen(chosenattpolicy, policy);
				
				sumexpoverlap += (overlap/policylen);
			}
		}
		//System.out.println("sum exp common len: "+ sumexpoverlap);
		if(maxexpoverlap<sumexpoverlap)
		{
			maxexpoverlap = sumexpoverlap;
			maxsettings = pathindex;
		}
		//System.out.println("max exp common len: "+ maxexpoverlap);
		
		
	}
	
	
	HashMap<Integer, HashMap<Integer, Integer>> finalpolicies = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	
	for(Integer atindex: maxsettings.keySet())
	{
		int pindex = maxsettings.get(atindex);
		//System.out.println("att "+ atindex +" pathindex "+ pindex);
		finalpolicies.put(atindex, attackers.get(atindex).fixedpolicy.get(pindex));
		
		
	}
	
	
	return finalpolicies;
}



private static HashMap<Integer, HashMap<Integer, int[]>> getMaxExpOverlapExploitPolicies(
		HashMap<Integer, Attacker> attackers, ArrayList<HashMap<Integer, Integer>> permsofpathindexes, int chosenattacker) {
	
	
	System.out.println("********Determining max overlapping policies********");
	
	double maxexpoverlap = Double.NEGATIVE_INFINITY;
	HashMap<Integer, Integer> maxsettings = new HashMap<Integer, Integer>();
	
	for(HashMap<Integer, Integer> pathindex: permsofpathindexes)
	{
		HashMap<Integer, HashMap<Integer, int[]>> tmppolicies = new HashMap<Integer, HashMap<Integer, int[]>>();
		HashMap<Integer, int[]> chosenattpolicy = new HashMap<Integer, int[]>();
		for(Integer atindex: pathindex.keySet())
		{
			int pindex = pathindex.get(atindex);
			//System.out.println("att "+ atindex +" pathindex "+ pindex);
			tmppolicies.put(atindex, attackers.get(atindex).fixedexploitpolicy.get(pindex));
			if(atindex==chosenattacker)
			{
				chosenattpolicy = attackers.get(atindex).fixedexploitpolicy.get(pindex);
			}
			
		}
		
		//System.out.println("\nTmp Policies ");
		
		
		
		double policylen = chosenattpolicy.size();
		
		double sumexpoverlap = 0.0;
		
		for(int at: tmppolicies.keySet())
		{
			
			if(at != chosenattacker)
			{
				HashMap<Integer, int[]> policy = tmppolicies.get(at);
				
				double overlap = commonLenExplt(chosenattpolicy, policy);
				
				sumexpoverlap += (overlap/policylen);
			}
		}
		//System.out.println("sum exp common len: "+ sumexpoverlap);
		if(maxexpoverlap<sumexpoverlap)
		{
			maxexpoverlap = sumexpoverlap;
			maxsettings = pathindex;
		}
		//System.out.println("max exp common len: "+ maxexpoverlap);
		
		
	}
	
	
	HashMap<Integer, HashMap<Integer, int[]>> finalpolicies = new HashMap<Integer, HashMap<Integer, int[]>>();
	
	
	for(Integer atindex: maxsettings.keySet())
	{
		int pindex = maxsettings.get(atindex);
		//System.out.println("att "+ atindex +" pathindex "+ pindex);
		finalpolicies.put(atindex, attackers.get(atindex).fixedexploitpolicy.get(pindex));
		
		
	}
	
	
	return finalpolicies;
}



private static double getMaxExpOverlapPoliciesV2(
		HashMap<Integer, Attacker> attackers, ArrayList<HashMap<Integer, Integer>> permsofpathindexes, int chosenattacker, 
		HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attpolicies) {
	
	
	System.out.println("********Determining max overlapping policies********");
	
	double maxexpoverlap = Double.NEGATIVE_INFINITY;
	HashMap<Integer, Integer> maxsettings = new HashMap<Integer, Integer>();
	
	for(HashMap<Integer, Integer> pathindex: permsofpathindexes)
	{
		HashMap<Integer, HashMap<Integer, Integer>> tmppolicies = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> chosenattpolicy = new HashMap<Integer, Integer>();
		for(Integer atindex: pathindex.keySet())
		{
			int pindex = pathindex.get(atindex);
			//System.out.println("att "+ atindex +" pathindex "+ pindex);
			tmppolicies.put(atindex, attpolicies.get(atindex).get(pindex));
			if(atindex==chosenattacker)
			{
				chosenattpolicy = attpolicies.get(atindex).get(pindex);
			}
			
		}
		
		//System.out.println("\nTmp Policies ");
		
		
		
		double policylen = chosenattpolicy.size();
		
		double sumexpoverlap = 0.0;
		
		for(int at: tmppolicies.keySet())
		{
			
			if(at != chosenattacker)
			{
				HashMap<Integer, Integer> policy = tmppolicies.get(at);
				
				double overlap = commonLen(chosenattpolicy, policy);
				
				sumexpoverlap += (overlap/policylen);
			}
		}
		//System.out.println("sum exp common len: "+ sumexpoverlap);
		if(maxexpoverlap<sumexpoverlap)
		{
			maxexpoverlap = sumexpoverlap;
			maxsettings = pathindex;
		}
		//System.out.println("max exp common len: "+ maxexpoverlap);
		
		
	}
	
	
	//HashMap<Integer, HashMap<Integer, Integer>> finalpolicies = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	
	for(Integer atindex: maxsettings.keySet())
	{
		int pindex = maxsettings.get(atindex);
		//System.out.println("att "+ atindex +" pathindex "+ pindex);
		//finalpolicies.put(atindex, attpolicies.get(atindex).get(pindex));
		
		HashMap<Integer, Integer> tmp = attpolicies.get(atindex).get(pindex);
		attpolicies.get(atindex).clear();
		attpolicies.get(atindex).put(attpolicies.get(atindex).size(), tmp);
		
		
		
	}
	
	
	return maxexpoverlap;
}




private static ArrayList<HashMap<Integer, Integer>> getPathPerms(HashMap<Integer, Attacker> attackers) {
	
	
	int maxnpaths = -1;
	HashMap<Integer, Integer> pathlimits = new HashMap<Integer, Integer>();
	int[] attmapindex = new int[attackers.size()];

	int ind = 0;
	for(Attacker att: attackers.values())
	{
		if(maxnpaths<att.fixedpolicy.size())
		{
			maxnpaths = att.fixedpolicy.size();
		}
		pathlimits.put(att.id, att.fixedpolicy.size());
		//System.out.println("Att "+ att.id+" pathlimit "+ att.fixedpolicy.size());
		attmapindex[ind++] = att.id;
	}
	
	//System.out.println("max n path "+ maxnpaths);
	
	int[] pathindexes = new int[maxnpaths];
	
	for(int i=0; i<maxnpaths; i++)
	{
		pathindexes[i] = i;
	}
	
	int slotlimit = attackers.size();
	
	ArrayList<HashMap<Integer, Integer>> perms = new ArrayList<HashMap<Integer, Integer>>();
	
	
	
	
	if(maxnpaths==1)
	{
		HashMap<Integer, Integer> perm = new HashMap<Integer, Integer>();
		
		for(Integer at: attackers.keySet())
		{
			perm.put(at, 0);
		}
		perms.add(perm);
		return perms;
		
	}
	
	
	if (slotlimit < 1 || slotlimit > pathindexes.length)
	 {
	      // throw new IllegalArgumentException("Illegal number of positions.");
	      //  System.out.println("Illegal number of positions.");
	 }
	
	if(/*pathindexes.length==2 && */(slotlimit>pathindexes.length) )
	{
		
		pathindexes = new int[slotlimit];
		
		for(int i=0; i<slotlimit; i++)
		{
			pathindexes[i] = i;
		}
	}
	
	
	
	
	permute2(pathindexes, slotlimit, perms, pathlimits, attmapindex);
	
	//System.out.println("Pathindex permuations with repetitions: ");
	
	
	
	 // alws make sure the path limits are in sorted order of the attackers
	
	
	return perms;
}


private static ArrayList<HashMap<Integer, Integer>> getPathPermsExploit(HashMap<Integer, Attacker> attackers) {
	
	
	int maxnpaths = -1;
	HashMap<Integer, Integer> pathlimits = new HashMap<Integer, Integer>();
	int[] attmapindex = new int[attackers.size()];

	int ind = 0;
	for(Attacker att: attackers.values())
	{
		if(maxnpaths<att.fixedexploitpolicy.size())
		{
			maxnpaths = att.fixedexploitpolicy.size();
		}
		pathlimits.put(att.id, att.fixedexploitpolicy.size());
		//System.out.println("Att "+ att.id+" pathlimit "+ att.fixedpolicy.size());
		attmapindex[ind++] = att.id;
	}
	
	//System.out.println("max n path "+ maxnpaths);
	
	int[] pathindexes = new int[maxnpaths];
	
	for(int i=0; i<maxnpaths; i++)
	{
		pathindexes[i] = i;
	}
	
	int slotlimit = attackers.size();
	
	ArrayList<HashMap<Integer, Integer>> perms = new ArrayList<HashMap<Integer, Integer>>();
	
	
	
	
	permute2(pathindexes, slotlimit, perms, pathlimits, attmapindex);
	
	//System.out.println("Pathindex permuations with repetitions: ");
	
	
	
	 // alws make sure the path limits are in sorted order of the attackers
	
	
	return perms;
}


private static ArrayList<HashMap<Integer, Integer>> getPathPermsV2(HashMap<Integer, Attacker> attackers, HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attpolicies) {
	
	
	int maxnpaths = -1;
	HashMap<Integer, Integer> pathlimits = new HashMap<Integer, Integer>();
	int[] attmapindex = new int[attackers.size()];

	int ind = 0;
	for(Integer att: attpolicies.keySet())
	{
		if(maxnpaths<attpolicies.get(att).size())
		{
			maxnpaths = attpolicies.get(att).size();
		}
		pathlimits.put(att, attpolicies.get(att).size());
		//System.out.println("Att "+ att.id+" pathlimit "+ att.fixedpolicy.size());
		attmapindex[ind++] = att;
	}
	
	ArrayList<HashMap<Integer, Integer>> perms = new ArrayList<HashMap<Integer, Integer>>();
	
	if(maxnpaths==1)
	{
		HashMap<Integer, Integer> perm = new HashMap<Integer, Integer>();
		
		for(Integer at: attpolicies.keySet())
		{
			perm.put(at, 0);
		}
		perms.add(perm);
		return perms;
		
	}
	
	
	//System.out.println("max n path "+ maxnpaths);
	
	int[] pathindexes = new int[maxnpaths];
	
	for(int i=0; i<maxnpaths; i++)
	{
		pathindexes[i] = i;
	}
	
	int slotlimit = attackers.size();
	
	
	
	
	 if (slotlimit < 1 || slotlimit > pathindexes.length)
	 {
	       throw new IllegalArgumentException("Illegal number of positions.");
	      //  System.out.println("Illegal number of positions.");
	 }
	
	permute2(pathindexes, slotlimit, perms, pathlimits, attmapindex);
	
	//System.out.println("Pathindex permuations with repetitions: ");
	
	
	
	 // alws make sure the path limits are in sorted order of the attackers
	
	
	return perms;
}

private static void printComb(ArrayList<HashMap<Integer, Integer>> comb) {
	
	
	
	for(HashMap<Integer, Integer> x: comb)
	{
		for(int y: x.values())
		{
			System.out.print(y+" ");
		}
		System.out.println();
	}
	
}

static void permute2(int[] a, int k, ArrayList<HashMap<Integer, Integer>> perms, HashMap<Integer, Integer> pathlimits, int[] attmapindex) 
{
    int n = a.length;
    if (k < 1 || k > n)
    {
       throw new IllegalArgumentException("Illegal number of positions.");
        //System.out.println("Illegal number of positions.");
    }
    int[] indexes = new int[n];
    int total = (int) Math.pow(n, k);

    while (total-- > 0) 
    {
    	HashMap<Integer,Integer> arr = new HashMap<Integer,Integer>();
    	
    	boolean ok = true;
    	
        for (int i = 0; i < n - (n - k); i++)
        {
           // System.out.print(a[indexes[i]]);
        	
        	int plindex = attmapindex[i];
        	
            arr.put(plindex, a[indexes[i]]);
            if(a[indexes[i]]>=pathlimits.get(plindex))
            {
            	ok = false;
            	break;
            }
            
        }
       // System.out.println();
        if(ok)
        {
        	perms.add(arr);
        }

        /*if (decider.test(indexes))
        {
           // break;
        }*/

        for (int i = 0; i < n; i++)
        {
            if (indexes[i] >= n - 1) 
            {
                indexes[i] = 0;
            } 
            else 
            {
                indexes[i]++;
                break;
            }
        }
    }
}




private static HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> refinePoliciesMeasure(HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> attackpolicies, 
		HashMap<Integer,Attacker> attackers, Integer atidconsidered) {
		
		
		
	HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> newattackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();

	HashMap<Integer, HashMap<Integer, Integer>> attpolicies = new HashMap<Integer, HashMap<Integer, Integer>>();

	ArrayList<int[]> done = new ArrayList<int[]>();

	HashMap<Integer, Integer> maxindex= new HashMap<Integer, Integer>();

	int maxlen = -1;

	int a0 = atidconsidered;
	//for(Integer a0: attackpolicies.keySet())
	{

		maxlen = -1;
		int opmaxlen  = -1;

		for(Integer a1: attackpolicies.keySet())
		{

			opmaxlen  = -1;
			if(a0 != a1)
			{
				int[] arr = {a0, a1};
				boolean isdone = isDoneIt(done, arr);
				if(!isdone)
				{

					done.add(arr);
					for(Integer pid0: attackpolicies.get(a0).keySet())
					{
						for(Integer pid1: attackpolicies.get(a1).keySet())
						{



							HashMap<Integer, Integer> p0 = attackpolicies.get(a0).get(pid0);
							HashMap<Integer, Integer> p1 = attackpolicies.get(a1).get(pid1);



							int len = commonLen(p0, p1);

							if(maxlen<len && len>0)
							{
								maxlen = len;
								maxindex.put(a0, pid0);
								attpolicies.put(a0, p0);
								//attpolicies.put(a1, p1);

							}
							
							if(opmaxlen<len && len>0)
							{
								opmaxlen = len;
								//maxlengths.put(a1.id, opmaxlen);
								attpolicies.put(a1, p1);
							}



						}

					}


				}

			}
		}
		//System.out.println("Max len for attacker "+ a0 + " is "+ maxlen + ", maxindex "+ maxindex.get(a0));

		
	}
	
	
	//HashMap<Integer, HashMap<Integer, Integer>> tmp = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	
	for(Integer a: attpolicies.keySet())
	{	
		HashMap<Integer, HashMap<Integer, Integer>> tmp = new HashMap<Integer, HashMap<Integer, Integer>>();
		tmp.put(tmp.size(), attpolicies.get(a));
		newattackpolicies.put(a, tmp);
	}
	
	//attackpolicies = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>(newattackpolicies);
	
	
	/*for(Integer at: newattackpolicies.keySet())
	{
		attackpolicies.get(at).clear();
		attackpolicies.put(at, newattackpolicies.get(at));
	}
	*/
	
	return newattackpolicies;

		
		


		
		
	}



	


	private static boolean isDoneIt(ArrayList<int[]> done, int[] arr) {
		
		
		
		for(int pair[]: done)
		{
			if((arr[0]==pair[0] && arr[1]==pair[1]) || (arr[1]==pair[1] && arr[0]==pair[0]))
			{
				return true;
			}
		}
		return false;
		
		
		
		/*boolean d = true;
		
		for(int[] a: done)
		{
			d = true;
			for(int i=0; i<a.length; i++)
			{
				if(a[i] != arr[i])
				{
					d = false;
					break;
				}
			}
			if(d)
			{
				return true;
			}
			
		}
		return false;*/
	}




	public static HashMap<Integer, int[]> computePlacesToAllocateHP(ArrayList<Integer> reachablesnodes,
			HashMap<Integer,Node> net, ArrayList<Integer> goals, Node curnode) {
		
		
		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		
		
		for(Integer a: reachablesnodes)
		{
			for(Integer b: reachablesnodes)
			{
				
				
								
				Node anode = net.get(a);
				Node bnode = net.get(b);
				
				
				//if(anode.nei.containsValue(b))
				{
				
				int dif = bnode.depth - anode.depth;
				
				
				if(a != b && a<b && !goals.contains(a) && (anode.depth != bnode.depth) && dif>1 && ((anode.depth-curnode.depth)==1))
				{
					int pair [] = {a,b};
					slots.put(slots.size(), pair);
				}
				}
			}
		}
		
		
		return slots;
	}
	
	
	public static HashMap<Integer, int[]> computePlacesToAllocateHE(ArrayList<Integer> reachablesnodes,
			HashMap<Integer,Node> net, ArrayList<Integer> goals, Node curnode) {
		
		
		HashMap<Integer, int[]> slots = new HashMap<Integer, int[]>();
		
		
		for(Integer a: reachablesnodes)
		{
			for(Integer b: reachablesnodes)
			{



				Node anode = net.get(a);
				Node bnode = net.get(b);


				if(anode.nei.containsValue(b))
				{

					int dif = bnode.depth - anode.depth;


					if(a != b && a<b && !goals.contains(a) && (anode.depth != bnode.depth) && dif>=1 && ((anode.depth-curnode.depth)==1))
					{
						int pair [] = {a,b};
						slots.put(slots.size(), pair);
					}
				}
			}
		}
		
		
		return slots;
	}




	public static void printSlots(HashMap<Integer, int[]> placestoallocatehp) {
		
		System.out.println("Slots to allocate honeypot: ");
		
		for(int[] pair: placestoallocatehp.values())
		{
			System.out.println("["+pair[0]+", "+pair[1]+"]");
		}
		
		
	}

}
