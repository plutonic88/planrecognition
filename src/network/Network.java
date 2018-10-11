package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Network {

	public static Random rand = new Random(100);



	public static int randInt(int min, int max) {

		//rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static void constructNetwork(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			if(i>=23)
			{
				v = randInt(11, 15);
			}

			if(i != 9)
			{
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			}
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1,2});

		net.get(1).addNeighbors(new int[] {3, 4, 5, 8});
		net.get(2).addNeighbors(new int[] {4, 5, 6, 10});

		net.get(3).addNeighbors(new int[] {7, 8, 13});
		net.get(4).addNeighbors(new int[] {8, 10, 14, 15});
		net.get(5).addNeighbors(new int[] {8, 10, 14, 15});
		net.get(6).addNeighbors(new int[] {10, 11, 16});

		net.get(7).addNeighbors(new int[] {12, 13, 18});
		net.get(8).addNeighbors(new int[] {13, 14, 19});
		//net.get(9).addNeighbors(new int[] {14, 15});
		net.get(10).addNeighbors(new int[] {14, 15, 16, 21});
		net.get(11).addNeighbors(new int[] {16, 17, 22});



		net.get(12).addNeighbors(new int[] {18});
		net.get(13).addNeighbors(new int[] {18, 19, 23});
		net.get(14).addNeighbors(new int[] {19, 20, 24});
		net.get(15).addNeighbors(new int[] {20, 21, 25});
		net.get(16).addNeighbors(new int[] {21, 22, 26});
		net.get(17).addNeighbors(new int[] {22});


		net.get(18).addNeighbors(new int[] {23});
		net.get(19).addNeighbors(new int[] {23, 24});
		net.get(20).addNeighbors(new int[] {24, 25});
		net.get(21).addNeighbors(new int[] {25, 26});
		net.get(22).addNeighbors(new int[] {26});



		/**
		 * create exploits
		 */
		for(int i=0; i<nexploits; i++)
		{
			int c = randInt(1,6);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			//System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */



		net.get(0).addExploits(new int[] {0, 1, 2, 3, 4, 5, 6, 7});

		net.get(1).addExploits(new int[] {0,1,3,4,6,2,5,7});
		net.get(2).addExploits(new int[] {0,1,3,4,6,2,5,7});

		net.get(3).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(4).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(5).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(6).addExploits(new int[] {0,3,4,6,1,2,5,7});

		net.get(7).addExploits(new int[] {0,1,2,3});
		net.get(8).addExploits(new int[] {0,1,2,3});
		//net.get(9).addExploits(new int[] {0, 1, 3});
		net.get(10).addExploits(new int[] {4,5,6,7});
		net.get(11).addExploits(new int[] {4,5,6,7});



		net.get(12).addExploits(new int[] {0,1,2,3});
		net.get(13).addExploits(new int[] {0,1,2,3});
		net.get(14).addExploits(new int[] {0,1,2,3,4,5,6,7});
		net.get(15).addExploits(new int[] {0,1,2,3,4,5,6,7});
		net.get(16).addExploits(new int[] {4,5,6,7});
		net.get(17).addExploits(new int[] {4,5,6,7});


		net.get(18).addExploits(new int[] {0,1});
		net.get(19).addExploits(new int[] {0,1,2,3});
		net.get(20).addExploits(new int[] {2,3,4,5});
		net.get(21).addExploits(new int[] {4,5,6,7});
		net.get(22).addExploits(new int[] {6,7});


		net.get(23).addExploits(new int[] {0,1,2,3});
		net.get(24).addExploits(new int[] {0,1,2,3});
		net.get(25).addExploits(new int[] {4,5,6,7});
		net.get(26).addExploits(new int[] {4,5,6,7});











	}
	
	
	public static void constructNetwork23(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			/*if(i>=23)
			{
				v = randInt(11, 15);
			}*/

			//if(i != 9)
			{
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			}
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1,2});

		net.get(1).addNeighbors(new int[] {3, 4, 5});
		net.get(2).addNeighbors(new int[] {4, 5, 6});

		net.get(3).addNeighbors(new int[] {7, 8});
		net.get(4).addNeighbors(new int[] {8, 9, 10});
		net.get(5).addNeighbors(new int[] {8, 9, 10});
		net.get(6).addNeighbors(new int[] {10, 11});

		net.get(7).addNeighbors(new int[] {12, 13});
		net.get(8).addNeighbors(new int[] {12, 13, 14});
		net.get(9).addNeighbors(new int[] {12, 13, 14, 15});
		net.get(10).addNeighbors(new int[] {13, 14, 15});
		net.get(11).addNeighbors(new int[] {14, 15});



		net.get(12).addNeighbors(new int[] {16});
		net.get(13).addNeighbors(new int[] {16, 17});
		net.get(14).addNeighbors(new int[] {16, 17, 18, 19});
		net.get(15).addNeighbors(new int[] {18, 19});
		
		
		net.get(16).addNeighbors(new int[] {20});
		net.get(17).addNeighbors(new int[] {20, 21});
		net.get(18).addNeighbors(new int[] {21, 22});
		net.get(19).addNeighbors(new int[] {22});
		
		
		/*net.get(20).addNeighbors(new int[] {24, 25});
		net.get(21).addNeighbors(new int[] {25, 26});
		net.get(22).addNeighbors(new int[] {26});*/



		/**
		 * create exploits
		 */
		for(int i=0; i<nexploits; i++)
		{
			int c = randInt(1,6);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			//System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */

		
		
		for(int i=0; i<nnodes; i++)
		{
			int exlt = randInt(1, exploits.size());
			
			
			ArrayList<Integer> explts = new ArrayList<Integer>();
			
			
			for(Integer eid: exploits.keySet())
			{
				explts.add(eid);
			}
			
			
			int extoadd[] = new int[exlt];
			for(int e=0; e<exlt; e++)
			{
				int pe = randInt(0, explts.size()-1);
				extoadd[e] = explts.get(pe);
				explts.remove(pe);
				
			}
			
			net.get(i).addExploits(extoadd);
			
		}


		/*net.get(0).addExploits(new int[] {0, 1, 2, 3, 4});

		net.get(1).addExploits(new int[] {0,1,2, 3,4});
		net.get(2).addExploits(new int[] {0,1,2,3,4});

		net.get(3).addExploits(new int[] {0,1});
		net.get(4).addExploits(new int[] {1,2});
		net.get(5).addExploits(new int[] {3,4});
		net.get(6).addExploits(new int[] {2,4});

		net.get(7).addExploits(new int[] {0,1});
		net.get(8).addExploits(new int[] {2,3});
		net.get(9).addExploits(new int[] {1,3});
		net.get(10).addExploits(new int[] {0,4});
		net.get(11).addExploits(new int[] {3,4});



		net.get(12).addExploits(new int[] {0,3});
		net.get(13).addExploits(new int[] {1,4});
		net.get(14).addExploits(new int[] {2,4});
		net.get(15).addExploits(new int[] {2,3});
		
		
		
		net.get(16).addExploits(new int[] {3,4});
		net.get(17).addExploits(new int[] {0,2});
		net.get(18).addExploits(new int[] {0,1});
		net.get(19).addExploits(new int[] {0,4});
		
		
		net.get(20).addExploits(new int[] {2,3});
		net.get(21).addExploits(new int[] {0,4});
		net.get(22).addExploits(new int[] {1,3});*/


		/*net.get(23).addExploits(new int[] {0,1,2,3});
		net.get(24).addExploits(new int[] {0,1,2,3});
		net.get(25).addExploits(new int[] {4,5,6,7});
		net.get(26).addExploits(new int[] {4,5,6,7});*/











	}
	
	
	public static void constructNetwork13(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		
		Network.rand = new Random(nnodes);
		
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			
			
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1,2});

		net.get(1).addNeighbors(new int[] {3, 4, 5});
		net.get(2).addNeighbors(new int[] {3, 4, 5});

		net.get(3).addNeighbors(new int[] {6,7});
		net.get(4).addNeighbors(new int[] {6, 7,8,9});
		net.get(5).addNeighbors(new int[] {8, 9});
		
		
		net.get(6).addNeighbors(new int[] {10, 11});
		net.get(7).addNeighbors(new int[] {11});
		net.get(8).addNeighbors(new int[] {11});
		net.get(9).addNeighbors(new int[] {11, 12});
		
		
		net.get(10).addNeighbors(new int[] {13,14});
		net.get(11).addNeighbors(new int[] {14});
		net.get(12).addNeighbors(new int[] {14, 15});
		
		
		
		
		
		
		/*
		net.get(13).addNeighbors(new int[] {16, 17});
		net.get(14).addNeighbors(new int[] {16, 17, 18, 19});
		net.get(15).addNeighbors(new int[] {18, 19});
		
		
		net.get(16).addNeighbors(new int[] {20});
		net.get(17).addNeighbors(new int[] {20, 21});
		net.get(18).addNeighbors(new int[] {21, 22});
		net.get(19).addNeighbors(new int[] {22});*/
		
		
		/*net.get(20).addNeighbors(new int[] {24, 25});
		net.get(21).addNeighbors(new int[] {25, 26});
		net.get(22).addNeighbors(new int[] {26});*/



		/**
		 * create exploits
		 */
		
		
		
		for(int i=0; i<nexploits; i++)
		{
			
			int c = randInt(1,10);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */

		
		
		for(int i=0; i<nnodes; i++)
		{
			int l = exploits.size()/2;
			int exlt = randInt(1, l);
			
			
			ArrayList<Integer> explts = new ArrayList<Integer>();
			
			
			for(Integer eid: exploits.keySet())
			{
				explts.add(eid);
			}
			
			
			int extoadd[] = new int[exlt];
			for(int e=0; e<exlt; e++)
			{
				
				int pe = randInt(0, explts.size()-1);
				extoadd[e] = explts.get(pe);
				explts.remove(pe);
				
			}
			
			net.get(i).addExploits(extoadd);
			
		}


		

	}
	
	
	
	public static void constructNetwork10(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			if(i>=10)
			{
				v = randInt(11, 15);
			}

			//if(i != 9)
			{
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			}
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1,2});

		net.get(1).addNeighbors(new int[] {3, 4, 5});
		net.get(2).addNeighbors(new int[] {4, 5, 6});

		net.get(3).addNeighbors(new int[] {7});
		net.get(4).addNeighbors(new int[] {7, 8, 9});
		net.get(5).addNeighbors(new int[] {7, 8, 9});
		net.get(6).addNeighbors(new int[] {9});

		net.get(7).addNeighbors(new int[] {10});
		net.get(8).addNeighbors(new int[] {10, 11});
		net.get(9).addNeighbors(new int[] {11});
		


		/**
		 * create exploits
		 */
		for(int i=0; i<nexploits; i++)
		{
			int c = randInt(1,4);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */



		net.get(0).addExploits(new int[] {0});

		net.get(1).addExploits(new int[] {1});
		net.get(2).addExploits(new int[] {0});

		net.get(3).addExploits(new int[] {2});
		net.get(4).addExploits(new int[] {3});
		net.get(5).addExploits(new int[] {1});
		net.get(6).addExploits(new int[] {1});

		net.get(7).addExploits(new int[] {1});
		net.get(8).addExploits(new int[] {3});
		net.get(9).addExploits(new int[] {2});
		
		
		net.get(10).addExploits(new int[] {2});
		net.get(11).addExploits(new int[] {3});


	}
	
	
	public static void constructNetwork10V2(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			if(i>=8)
			{
				v = randInt(11, 15);
			}

			//if(i != 9)
			{
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			}
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1,2});

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {3, 4});

		net.get(3).addNeighbors(new int[] {5,6,7});
		net.get(4).addNeighbors(new int[] {5,6,7});
		
		net.get(5).addNeighbors(new int[] {8});
		net.get(6).addNeighbors(new int[] {8, 9, 10});
		net.get(7).addNeighbors(new int[] {9, 10});
		
		

		/**
		 * create exploits
		 */
		for(int i=0; i<nexploits; i++)
		{
			int c = randInt(1,4);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */



		net.get(0).addExploits(new int[] {0});

		net.get(1).addExploits(new int[] {1});
		net.get(2).addExploits(new int[] {2});

		net.get(3).addExploits(new int[] {0});
		net.get(4).addExploits(new int[] {0});
		net.get(5).addExploits(new int[] {1});
		net.get(6).addExploits(new int[] {2});

		net.get(7).addExploits(new int[] {0});
		net.get(8).addExploits(new int[] {0});
		net.get(9).addExploits(new int[] {1});
		net.get(10).addExploits(new int[] {2});
		
		
		


	}
	
	
	
	
	public static void constructNetworkWithExploits(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<nnodes; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);

			if(i>=23)
			{
				v = randInt(11, 15);
			}

			if(i != 9)
			{
				Node n = new Node(i, v, c);
				net.put(n.id, n);
			}
		}


		/**
		 * create exploits
		 */
		for(int i=0; i<nexploits; i++)
		{
			int c = randInt(1,6);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			//System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */



		net.get(0).addExploits(new int[] {0, 1, 2, 3, 4, 5, 6, 7});

		net.get(1).addExploits(new int[] {0,1,3,4,6,2,5,7});
		net.get(2).addExploits(new int[] {0,1,3,4,6,2,5,7});

		net.get(3).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(4).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(5).addExploits(new int[] {0,3,4,6,1,2,5,7});
		net.get(6).addExploits(new int[] {0,3,4,6,1,2,5,7});

		net.get(7).addExploits(new int[] {0,1,2,3});
		net.get(8).addExploits(new int[] {0,1,2,3});
		//net.get(9).addExploits(new int[] {0, 1, 3});
		net.get(10).addExploits(new int[] {4,5,6,7});
		net.get(11).addExploits(new int[] {4,5,6,7});



		net.get(12).addExploits(new int[] {0,1,2,3});
		net.get(13).addExploits(new int[] {0,1,2,3});
		net.get(14).addExploits(new int[] {0,1,2,3,4,5,6,7});
		net.get(15).addExploits(new int[] {0,1,2,3,4,5,6,7});
		net.get(16).addExploits(new int[] {4,5,6,7});
		net.get(17).addExploits(new int[] {4,5,6,7});


		net.get(18).addExploits(new int[] {0,1});
		net.get(19).addExploits(new int[] {0,1,2,3});
		net.get(20).addExploits(new int[] {2,3,4,5});
		net.get(21).addExploits(new int[] {4,5,6,7});
		net.get(22).addExploits(new int[] {6,7});


		net.get(23).addExploits(new int[] {0,1,2,3});
		net.get(24).addExploits(new int[] {0,1,2,3});
		net.get(25).addExploits(new int[] {4,5,6,7});
		net.get(26).addExploits(new int[] {4,5,6,7});



		/**
		 * create neighbors
		 */

		net.get(0).addNeighborsWithExploits(new int[] {1,2}, net);

		net.get(1).addNeighborsWithExploits(new int[] {3, 4, 5, 8}, net);
		net.get(2).addNeighborsWithExploits(new int[] {4, 5, 6, 10}, net);

		net.get(3).addNeighborsWithExploits(new int[] {7, 8, 13}, net);
		net.get(4).addNeighborsWithExploits(new int[] {8, 10, 14, 15}, net);
		net.get(5).addNeighborsWithExploits(new int[] {8, 10, 14, 15}, net);
		net.get(6).addNeighborsWithExploits(new int[] {10, 11, 16}, net);

		net.get(7).addNeighborsWithExploits(new int[] {12, 13, 18}, net);
		net.get(8).addNeighborsWithExploits(new int[] {13, 14, 19}, net);
		//net.get(9).addNeighbors(new int[] {14, 15});
		net.get(10).addNeighborsWithExploits(new int[] {14, 15, 16, 21}, net);
		net.get(11).addNeighborsWithExploits(new int[] {16, 17, 22}, net);



		net.get(12).addNeighborsWithExploits(new int[] {18}, net);
		net.get(13).addNeighborsWithExploits(new int[] {18, 19, 23}, net);
		net.get(14).addNeighborsWithExploits(new int[] {19, 20, 24}, net);
		net.get(15).addNeighborsWithExploits(new int[] {20, 21, 25}, net);
		net.get(16).addNeighborsWithExploits(new int[] {21, 22, 26}, net);
		net.get(17).addNeighborsWithExploits(new int[] {22}, net);


		net.get(18).addNeighborsWithExploits(new int[] {23}, net);
		net.get(19).addNeighborsWithExploits(new int[] {23, 24}, net);
		net.get(20).addNeighborsWithExploits(new int[] {24, 25}, net);
		net.get(21).addNeighborsWithExploits(new int[] {25, 26}, net);
		net.get(22).addNeighborsWithExploits(new int[] {26}, net);



		











	}
	
	

	public static void constructHoneyPots(HashMap<Integer, Node> honeypots, HashMap<Integer, Exploits> exploits, 
			int nhoneypots, int nnodes, int hpv, int hpc, boolean sameval, boolean allexploit, HashMap<Integer,Node> net, boolean pickfromnet, int[] goals) {
		
		
		
		
		

			if(pickfromnet)
			{
				ArrayList<Node> left =new ArrayList<Node>();
				
				for(Node n: net.values())
				{
					boolean ing = false;
					
					for(int i=0; i<goals.length; i++)
					{
						if(n.id==goals[i])
						{
							ing = true;
							break;
						}
					}
					
					
					if(!ing) 
					{
						left.add(n);
					}
				}
				
				for(int i=0; i<(nhoneypots); i++)
				{
					
					int r = randInt(0, left.size()-1);
					
					Node pn = left.get(r);
					left.remove(r);
					
					Node n = new Node(i+nnodes, pn.value, pn.cost);
					n.ishoneypot = true;
					
					for(Integer e: pn.exploits.values())
					{
						n.exploits.put(e, e);
					}
					
					honeypots.put(n.id, n);
					
				}
				
				
			}
			else
			{
				for(int i=0; i<(nhoneypots); i++)
				{

					int v = randInt(5, 10);
					int c = randInt(1,4);

					if(sameval)
					{
						v = 7;
						c = 3;
					}

					Node n = new Node(i+nnodes, v, c);
					n.ishoneypot = true;

					if(allexploit)
					{
						n.addExploits(new int[] {0,1,2,3,4});
					}
					else
					{
						n.addExploits(new int[] {i});
					}

					honeypots.put(n.id, n);
				}

		}
		
		
		
	}
	
	
	public static void constructExploits(HashMap<Integer, Node> honeypots, HashMap<Integer, Exploits> exploits, 
			int nhoneypots, int nnodes, int hpv, int hpc, boolean sameval, boolean allexploit, HashMap<Integer,Node> net, boolean pickfromnet, int[] goals) {
		
		
		
		
		

			if(pickfromnet)
			{
				ArrayList<Node> left =new ArrayList<Node>();
				
				for(Node n: net.values())
				{
					boolean ing = false;
					
					for(int i=0; i<goals.length; i++)
					{
						if(n.id==goals[i])
						{
							ing = true;
							break;
						}
					}
					
					
					if(!ing) 
					{
						left.add(n);
					}
				}
				
				for(int i=0; i<(nhoneypots); i++)
				{
					
					int r = randInt(0, left.size()-1);
					
					Node pn = left.get(r);
					left.remove(r);
					
					Node n = new Node(i+nnodes, pn.value, pn.cost);
					n.ishoneypot = true;
					
					for(Integer e: pn.exploits.values())
					{
						n.exploits.put(e, e);
					}
					
					honeypots.put(n.id, n);
					
				}
				
				
			}
			else
			{
				for(int i=0; i<(nhoneypots); i++)
				{

					int v = randInt(5, 10);
					int c = randInt(1,4);

					if(sameval)
					{
						v = 7;
						c = 3;
					}

					Node n = new Node(i+nnodes, v, c);
					n.ishoneypot = true;

					if(allexploit)
					{
						n.addExploits(new int[] {0,1,2,3,4});
					}
					else
					{
						n.addExploits(new int[] {i});
					}

					honeypots.put(n.id, n);
				}

		}
		
		
		
	}

	

}
