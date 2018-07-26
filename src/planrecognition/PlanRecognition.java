package planrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;

public class PlanRecognition {

	public static void doFixedPolicyExp1() {





		int[] goals = {23, 24, 25, 26};

		int chosenattacker = 2;
		int chosenpolicy = 1;
		boolean singlegoal = true;
		boolean singlepath = true;
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
			PlanRecognition.constructAttackersSingleGoal(attackers, net, exploits, singlepath, npath);
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
	
	
	public static void doFixedPolicyWithDefenseExp1() {





		int[] goals = {23, 24, 25, 26};
		int nnodes = 27;
		int nhoneypots = 8;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		
		int nexploits = 8;

		int chosenattacker = 2;
		int chosenpolicy = 1;
		boolean singlegoal = true;
		boolean singlepath = true;
		int npath = 4;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();
		
		
		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		Network.constructNetwork(net, exploits, nnodes, nexploits);
		
		Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc);

		System.out.println("Network construction... \ndone");


		if(singlegoal)
		{
			PlanRecognition.constructAttackersSingleGoal(attackers, net, exploits, singlepath, npath);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}

		
		

		System.out.println("Attacker construction... \ndone");

		printNetwork(net);
		System.out.println();
		printAttackers(attackers);
		System.out.println("*****************Honeypots******************");
		printNetwork(honeypots);

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
		
		//constructPolicyLib();

		playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, honeypots, hpdeploylimit);





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
					Logger.logit(" attacker "+ i + " prior "+ priorsattackertype[i]+"\n");
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
	 */
	private static void playGameWithNaiveDefense(int chosenattacker, int chosenpolicy, HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits,
			HashMap<Integer, Attacker> attackers, int[] goals, HashMap<Integer,Node> honeypots, int hpdeploylimit) {


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
		ArrayList<Integer> currenthps = new ArrayList<Integer>();
		
		int round = 0;
		while(true)
		{
			Logger.logit("\n***************** round "+round+" **********************\n");
			System.out.println("\n********************round "+round+"*********************");
			
			
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
			double posteriorattackertype[] = new double[attackers.size()];
			double posteriorplang[][] = new double[attackers.size()][goals.length];
			if(oactions.size()==0)
			{
				System.out.println("No observed actions\nUpdating the posteriors...");
				posteriorattackertype = priorsattackertype;
				posteriorplang = priorforplang;
				for(int i=0; i<attackers.size(); i++)
				{
					System.out.println(" attacker "+ i + " prior "+ priorsattackertype[i]);
				}
				for(int i=0; i<attackers.size(); i++)
				{
					System.out.println(" attacker "+ i + " posterior "+ posteriorattackertype[i]);
					//priorsattackertype[i] = posteriorattackertype[i];

				}
				for(int a=0; a<attackers.size(); a++)
				{
					
					System.out.println("Attacker "+ a +": ");
					for(int i=0; i<priorforplang[a].length; i++)
					{
						System.out.print(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
					}
					System.out.println();

				}
				for(int a=0; a<attackers.size(); a++)
				{
					System.out.println("\nattacker "+ a);
					
					for(int g=0; g<goals.length; g++)
					{
						System.out.println(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
						
						//priorforplang[a][g] = posteriorplang[a][g];
					}
				}
				
			}
			else // there are observed actions
			{
				System.out.println("\nComputing Posterior probs for attacker types...\n");
				/**
				 * posterior
				 */
				posteriorattackertype = computePosteriorAttackerType(oactions, attackers, net, priorsattackertype);
				for(int i=0; i<attackers.size(); i++)
				{
					System.out.println(" attacker "+ i + " prior "+ priorsattackertype[i]);
				}
				for(int i=0; i<attackers.size(); i++)
				{
					System.out.println(" attacker "+ i + " posterior "+ posteriorattackertype[i]);
					priorsattackertype[i] = posteriorattackertype[i];

				}
				System.out.println("\nPriors for goals regarding plans...\n");

				for(int a=0; a<attackers.size(); a++)
				{
					
					System.out.println("Attacker "+ a +": ");
					for(int i=0; i<priorforplang[a].length; i++)
					{
						System.out.print(" goal "+ goals[i]+" prior: "+priorforplang[a][i]+"\n");
					}
					System.out.println();

				}
				
				System.out.println("computing posterior on attacker plan given the priors");
				posteriorplang = posteriorPlang(attackers, net, priorforplang, oactions, goals, priorsattackertype);
				writeBUpdatesForAttackerType(posteriorattackertype);
				writeBayesianUpdatesForPlan(posteriorplang);
				for(int a=0; a<attackers.size(); a++)
				{
					System.out.println("\nattacker "+ a);
					
					for(int g=0; g<goals.length; g++)
					{
						System.out.println(" goal "+ goals[g] + ", posterior: "+ posteriorplang[a][g]);
						
						priorforplang[a][g] = posteriorplang[a][g];
					}
				}
				
				
				
				/**
				 * Now defender makes a defensive move naively
				 * 1. See which slots are free
				 * 2. see which honeypots are free
				 * 3. compute posterior for every combination of hps and networks for each attacker
				 * 4. Then deploy the hp where the prob for type increases most
				 * 5. If the prob does not increase, see if the goal increases
				 */

				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/************************************************END****************************************************/
			
			
			
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
			
			
			

			/**
			 * if there are multiple goals, we need to check against all the possible goals
			 */
			if(chosenatt.goals.containsValue(observedaction))
			{
				break;
			}


		}

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

			System.out.print("exploits: "+"");
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

		attackers.put(0, a0);
		attackers.put(1, a1);
		attackers.put(2, a2);
		attackers.put(3, a3);
		/*attackers.put(3, a3);
		attackers.put(4, a4);
		attackers.put(5, a5);*/


		//return goals;


	}
	
	
	public static void constructAttackersSingleGoal(HashMap<Integer, Attacker> attackers, HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, boolean singlepath, int npath) {

		int id = 0;

		//int goals[] = {26,23,25,24};
		
		/*boolean singlepath = false;
		int npath = 3;*/

		Attacker a0  = new Attacker(id++);
		//a0.goals.put(0, 23);
		a0.goals.put(0, 26);
	//	a0.goals.put(1, 24);
		a0.addExploits(new int[] {0, 1});
		//a0.findFixedPolifyBFS(net, exploits, 23);
		//a0.findFixedPolifyBFS(net, exploits, 24);
		a0.findFixedPolifyMinCost(net, exploits, 26, singlepath, npath);
		a0.removeDuplicatePolicies();
		//a0.addPolicy(0, new int[] {0, 2, 5, 10, 16, 21, 26});
		//a0.addPolicy(1, new int[] {0, 1, 4, 15, 21, 26});
		//a0.addPolicy(1, new int[] {0, 2, 6, 10, 15, 20, 24});
		


		Attacker a1  = new Attacker(id++);
		a1.goals.put(0, 23);
		//a1.goals.put(1, 24);
		//a1.goals.put(1, 25);
		a1.addExploits(new int[] {2, 3});
		//a1.findFixedPolifyBFS(net, exploits, 23, singlepath, npath);
		a1.findFixedPolifyMinCost(net, exploits, 23, singlepath, npath);
		a1.removeDuplicatePolicies();
		/*
		 * a1.findFixedPolifyBFS(net, exploits, 24);
		a1.findFixedPolifyBFS(net, exploits, 25);*/
		//a1.addPolicy(0, new int[] {0, 1, 3, 8, 14, 19, 23});
		//a1.addPolicy(1, new int[] {0, 2, 5, 14, 20, 25});

		Attacker a2  = new Attacker(id++);
		//a2.goals.put(0, 23);
		a2.goals.put(0, 24);
		//a2.goals.put(2, 25);
		//a2.goals.put(3, 26);
		
		a2.addExploits(new int[] {4,5});
		//a2.findFixedPolifyBFS(net, exploits, 24, singlepath, npath);
		a2.findFixedPolifyMinCost(net, exploits, 24, singlepath, npath);
		a2.removeDuplicatePolicies();
		//a2.findFixedPolifyBFS(net, exploits, 25);
		//a2.findFixedPolifyBFS(net, exploits, 26);
		
		//a2.addPolicy(0, new int[] {0, 1, 3, 7, 13, 18, 23});
		//a2.addPolicy(0, new int[] {0, 2, 5, 14, 20, 24});
		//a2.addPolicy(2, new int[] {0, 2, 5, 10, 15, 20, 25});
		//a2.addPolicy(3, new int[] {0, 2, 6, 10, 16, 21, 26});


		Attacker a3  = new Attacker(id++);
		a3.goals.put(0, 25);
		//a3.goals.put(1, 24);
		a3.addExploits(new int[] {6, 7});
		//a3.findFixedPolifyBFS(net, exploits, 23);
		//a3.findFixedPolifyBFS(net, exploits, 24);
		//a3.findFixedPolifyBFS(net, exploits, 25, singlepath, npath);
		a3.findFixedPolifyMinCost(net, exploits, 25, singlepath, npath);
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

		attackers.put(0, a0);
		attackers.put(1, a1);
		attackers.put(2, a2);
		attackers.put(3, a3);
		/*
		attackers.put(4, a4);
		attackers.put(5, a5);*/


		//return goals;

	}

}
