package planrecognition;

import java.util.HashMap;

import agents.Attacker;
import network.Exploits;
import network.Network;
import network.Node;

public class PlanrecognitionExp {
	
	public static void doFixedPolicyWithDefenseExp1(boolean withdefense, int chosenattacker, int chosenpolicy, boolean minentropy, boolean maxoverlap, boolean expoverlap) throws Exception {




		
		/*boolean withdefense = true;
		
		int chosenattacker = 1;
		int chosenpolicy = 0;
		
		boolean minentropy = false;*/
		
		
		
		boolean mincommonoverlap = !(minentropy);
		
		/*if(minentropy)
		{
			mincommonoverlap = false;
		}
		else
		{
			mincommonoverlap = true;
		}*/
		
		
		boolean samevalhp = false;
		boolean allexphp = true; // if false, this can lead to blocking action, attacker might not have any policy...
		
		
		

		int[] goals = {7,8,9};
		int startnode = 0;
		int nnodes = 10;//27;
		int nhoneypots = 5;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		boolean pickfromnet = true;
		
		

		int nexploits = 6;

		

		boolean singlegoal = true;
		boolean singlepath = false;
		int startnodeid = 0;
		int npath = 1;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		//Network.constructNetwork(net, exploits, nnodes, nexploits);
		
		
		Network.constructNetwork10(net, exploits, nnodes, nexploits);

		
		/**
		 * construct honeypots from real node configurations
		 * Imagine that attacker is naive
		 */
		Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc, samevalhp, allexphp, net, pickfromnet, goals);

		System.out.println("Network construction... \ndone");
		
		
		System.out.println("Attacker construction... \ndone");

		PlanRecognition.printNetwork(net);
		System.out.println();
		
		System.out.println("*****************Honeypots******************");
		PlanRecognition.printNetwork(honeypots);


		if(singlegoal)
		{
			//PlanRecognition.constructAttackersSingleGoal(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
			
			PlanRecognition.constructAttackers(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();
		
		

		
		
		PlanRecognition.playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap);





	}
	
	
public static void doFixedExploitPolicyWithDefenseExp1(boolean withdefense, int chosenattacker, int chosenpolicy, boolean minentropy, boolean maxoverlap, boolean expoverlap) throws Exception {




		
		/*boolean withdefense = true;
		
		int chosenattacker = 1;
		int chosenpolicy = 0;
		
		boolean minentropy = false;*/
		
		
		
		boolean mincommonoverlap = !(minentropy);
		
		/*if(minentropy)
		{
			mincommonoverlap = false;
		}
		else
		{
			mincommonoverlap = true;
		}*/
		
		
		boolean samevalhp = false;
		boolean allexphp = true; // if false, this can lead to blocking action, attacker might not have any policy...
		
		
		

		int[] goals = {23, 24, 25, 26};
		int startnode = 0;
		int nnodes = 27;
		int nhoneypots = 8;
		int hpdeploylimit = 2;
		int hpv = 8;
		int hpc = 2;
		boolean pickfromnet = true;
		
		

		int nexploits = 8;

		

		boolean singlegoal = true;
		boolean singlepath = false;
		int startnodeid = 0;
		int npath = 1;


		HashMap<Integer, Node> net = new HashMap<Integer, Node>();
		HashMap<Integer, Exploits> exploits = new HashMap<Integer, Exploits>();
		HashMap<Integer, Attacker> attackers = new HashMap<Integer, Attacker>();


		HashMap<Integer, Node> honeypots = new HashMap<Integer, Node>();


		Network.constructNetworkWithExploits(net, exploits, nnodes, nexploits);

		
		/**
		 * construct honeypots from real node configurations
		 * Imagine that attacker is naive
		 */
		Network.constructHoneyPots(honeypots, exploits, nhoneypots, nnodes, hpv, hpc, samevalhp, allexphp, net, pickfromnet, goals);

		System.out.println("Network construction... \ndone");
		
		
		System.out.println("Attacker construction... \ndone");

		PlanRecognition.printNetworkWithExploits(net);
		System.out.println();
		
		System.out.println("*****************Honeypots******************");
		PlanRecognition.printNetworkWithExploits(honeypots);


		if(singlegoal)
		{
			PlanRecognition.constructAttackersWithExploitsSingleGoal(startnodeid ,attackers, net, exploits, singlepath, npath, chosenattacker, maxoverlap, expoverlap);
		}
		else
		{
			PlanRecognition.constructAttackersMultGoal(attackers, net, exploits, singlepath, npath);
		}




		

		//HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> policylib = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

		//constructPolicyLib();
		
		

		
		
		PlanRecognition.playGameWithNaiveDefense(chosenattacker, chosenpolicy, net, exploits, attackers, goals, 
				honeypots, hpdeploylimit, singlepath, npath, startnode, withdefense, minentropy, mincommonoverlap, maxoverlap, expoverlap);





	}

}
