package network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

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


	public static void constructNetwork3(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
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

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {5, 6});

		net.get(3).addNeighbors(new int[] {7, 8});
		net.get(4).addNeighbors(new int[] {8, 9});
		net.get(5).addNeighbors(new int[] {8, 9, 10});


		net.get(6).addNeighbors(new int[] {10});
		net.get(7).addNeighbors(new int[] {11});
		net.get(8).addNeighbors(new int[] {11, 12});
		net.get(9).addNeighbors(new int[] {12, 13});


		net.get(10).addNeighbors(new int[] {13});








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

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */


		net.get(0).addExploits(new int[] {0, 1});

		net.get(1).addExploits(new int[] {2});
		net.get(2).addExploits(new int[] {3});

		net.get(3).addExploits(new int[] {4});
		net.get(4).addExploits(new int[] {2});
		net.get(5).addExploits(new int[] {3});
		net.get(6).addExploits(new int[] {4});

		net.get(7).addExploits(new int[] {2});
		net.get(8).addExploits(new int[] {3});
		net.get(9).addExploits(new int[] {0});
		net.get(10).addExploits(new int[] {1});
		net.get(11).addExploits(new int[] {3});



		net.get(12).addExploits(new int[] {4});
		net.get(13).addExploits(new int[] {2});




		/*for(int i=0; i<nnodes; i++)
		{
			int l = 1;//exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
			}


			int extoadd[] = new int[exlt];
			for(int e=0; e<exlt; e++)
			{

				int pe = randInt(0, explts.size()-1);
				extoadd[e] = explts.get(pe);
				explts.remove(pe);

			}

			net.get(i).addExploits(extoadd);

		}*/




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

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {5, 6});

		net.get(3).addNeighbors(new int[] {7, 8});
		net.get(4).addNeighbors(new int[] {8, 9});
		net.get(5).addNeighbors(new int[] {8, 9, 10});


		net.get(6).addNeighbors(new int[] {10});
		net.get(7).addNeighbors(new int[] {11});
		net.get(8).addNeighbors(new int[] {11, 12});
		net.get(9).addNeighbors(new int[] {12, 13});
		net.get(10).addNeighbors(new int[] {13});








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

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */

		//		
		//		net.get(0).addExploits(new int[] {0, 1});
		//
		//		net.get(1).addExploits(new int[] {2});
		//		net.get(2).addExploits(new int[] {3});
		//
		//		net.get(3).addExploits(new int[] {4});
		//		net.get(4).addExploits(new int[] {2});
		//		net.get(5).addExploits(new int[] {3});
		//		net.get(6).addExploits(new int[] {4});
		//
		//		net.get(7).addExploits(new int[] {2});
		//		net.get(8).addExploits(new int[] {3});
		//		net.get(9).addExploits(new int[] {0});
		//		net.get(10).addExploits(new int[] {1});
		//		net.get(11).addExploits(new int[] {3});
		//
		//
		//
		//		net.get(12).addExploits(new int[] {4});
		//		net.get(13).addExploits(new int[] {2});




		for(int i=0; i<nnodes; i++)
		{
			int l = exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
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


	public static void constructNetwork30(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
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

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {5, 6});

		net.get(3).addNeighbors(new int[] {7});
		net.get(4).addNeighbors(new int[] {7, 8});
		net.get(5).addNeighbors(new int[] {8, 9});
		net.get(6).addNeighbors(new int[] {9, 10});

		net.get(7).addNeighbors(new int[] {11, 12});
		net.get(8).addNeighbors(new int[] {11, 13});
		net.get(9).addNeighbors(new int[] {12, 14});
		net.get(10).addNeighbors(new int[] {13, 14});



		net.get(11).addNeighbors(new int[] {15, 16});
		net.get(12).addNeighbors(new int[] {16, 17});
		net.get(13).addNeighbors(new int[] {17, 18});
		net.get(14).addNeighbors(new int[] {18});


		net.get(15).addNeighbors(new int[] {19, 20});
		net.get(16).addNeighbors(new int[] {19, 21});
		net.get(17).addNeighbors(new int[] {20, 22});
		net.get(18).addNeighbors(new int[] {21, 22});


		net.get(19).addNeighbors(new int[] {23});
		net.get(20).addNeighbors(new int[] {23, 24});
		net.get(21).addNeighbors(new int[] {24, 25});
		net.get(22).addNeighbors(new int[] {25, 26});


		net.get(23).addNeighbors(new int[] {27});
		net.get(24).addNeighbors(new int[] {27, 28});
		net.get(25).addNeighbors(new int[] {28, 29});
		net.get(26).addNeighbors(new int[] {29});














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

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */


		/*net.get(0).addExploits(new int[] {0, 1});

		net.get(1).addExploits(new int[] {2, 3});
		net.get(2).addExploits(new int[] {2, 3});

		net.get(3).addExploits(new int[] {1, 4});
		net.get(4).addExploits(new int[] {2, 3});
		net.get(5).addExploits(new int[] {1, 3});
		net.get(6).addExploits(new int[] {3, 4});

		net.get(7).addExploits(new int[] {2, 4});
		net.get(8).addExploits(new int[] {2, 3});
		net.get(9).addExploits(new int[] {0, 2});
		net.get(10).addExploits(new int[] {3, 4});
		net.get(11).addExploits(new int[] {2, 3});



		net.get(12).addExploits(new int[] {0, 4});
		net.get(13).addExploits(new int[] {2, 4});



		net.get(14).addExploits(new int[] {1, 2});
		net.get(15).addExploits(new int[] {1, 3});
		net.get(16).addExploits(new int[] {1, 4});
		net.get(17).addExploits(new int[] {0, 2});
		net.get(18).addExploits(new int[] {1, 3});
		net.get(19).addExploits(new int[] {0, 4});
		net.get(20).addExploits(new int[] {2, 4});


		net.get(21).addExploits(new int[] {2, 3});
		net.get(22).addExploits(new int[] {2, 3});
		net.get(23).addExploits(new int[] {2, 4});
		net.get(24).addExploits(new int[] {1, 3});
		net.get(25).addExploits(new int[] {3, 4});
		net.get(26).addExploits(new int[] {1, 4});
		net.get(27).addExploits(new int[] {2});

		net.get(28).addExploits(new int[] {3});
		net.get(29).addExploits(new int[] {4});


		 */


		for(int i=0; i<nnodes; i++)
		{
			int l = 3;//exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
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


	public static void constructNetwork223(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
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

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {5, 6});

		net.get(3).addNeighbors(new int[] {7});
		net.get(4).addNeighbors(new int[] {7, 8});
		net.get(5).addNeighbors(new int[] {8, 9});
		net.get(6).addNeighbors(new int[] {9, 10});

		net.get(7).addNeighbors(new int[] {11, 12});
		net.get(8).addNeighbors(new int[] {11, 13});
		net.get(9).addNeighbors(new int[] {12, 14});
		net.get(10).addNeighbors(new int[] {13, 14});



		net.get(11).addNeighbors(new int[] {15, 16});
		net.get(12).addNeighbors(new int[] {16, 17});
		net.get(13).addNeighbors(new int[] {17, 18});
		net.get(14).addNeighbors(new int[] {18, 19});


		net.get(15).addNeighbors(new int[] {19, 20});
		net.get(16).addNeighbors(new int[] {19, 21});
		net.get(17).addNeighbors(new int[] {20, 22});
		net.get(18).addNeighbors(new int[] {21, 22});
		net.get(19).addNeighbors(new int[] {22});


		/*net.get(19).addNeighbors(new int[] {23});
		net.get(20).addNeighbors(new int[] {23, 24});
		net.get(21).addNeighbors(new int[] {24, 25});
		net.get(22).addNeighbors(new int[] {25, 26});*/

		/*
		net.get(23).addNeighbors(new int[] {27});
		net.get(24).addNeighbors(new int[] {27, 28});
		net.get(25).addNeighbors(new int[] {28, 29});
		net.get(26).addNeighbors(new int[] {29});
		 */













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

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */


		/*net.get(0).addExploits(new int[] {0, 1});

		net.get(1).addExploits(new int[] {2, 3});
		net.get(2).addExploits(new int[] {2, 3});

		net.get(3).addExploits(new int[] {1, 4});
		net.get(4).addExploits(new int[] {2, 3});
		net.get(5).addExploits(new int[] {1, 3});
		net.get(6).addExploits(new int[] {3, 4});

		net.get(7).addExploits(new int[] {2, 4});
		net.get(8).addExploits(new int[] {2, 3});
		net.get(9).addExploits(new int[] {0, 2});
		net.get(10).addExploits(new int[] {3, 4});
		net.get(11).addExploits(new int[] {2, 3});



		net.get(12).addExploits(new int[] {0, 4});
		net.get(13).addExploits(new int[] {2, 4});



		net.get(14).addExploits(new int[] {1, 2});
		net.get(15).addExploits(new int[] {1, 3});
		net.get(16).addExploits(new int[] {1, 4});
		net.get(17).addExploits(new int[] {0, 2});
		net.get(18).addExploits(new int[] {1, 3});
		net.get(19).addExploits(new int[] {0, 4});
		net.get(20).addExploits(new int[] {2, 4});


		net.get(21).addExploits(new int[] {2, 3});
		net.get(22).addExploits(new int[] {2, 3});
		net.get(23).addExploits(new int[] {2, 4});
		net.get(24).addExploits(new int[] {1, 3});
		net.get(25).addExploits(new int[] {3, 4});
		net.get(26).addExploits(new int[] {1, 4});
		net.get(27).addExploits(new int[] {2});

		net.get(28).addExploits(new int[] {3});
		net.get(29).addExploits(new int[] {4});


		 */


		for(int i=0; i<nnodes; i++)
		{
			int l = 2;//exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
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

	public static void constructNetwork18(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits, int nnodes, int nexploits)
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

		net.get(0).addNeighbors(new int[] {1});

		net.get(1).addNeighbors(new int[] {2, 3});
		net.get(2).addNeighbors(new int[] {4, 5});

		net.get(3).addNeighbors(new int[] {5, 6});
		net.get(4).addNeighbors(new int[] {7, 8});
		net.get(5).addNeighbors(new int[] {8, 9});
		net.get(6).addNeighbors(new int[] {9, 10});

		net.get(7).addNeighbors(new int[] {11});
		net.get(8).addNeighbors(new int[] {11, 12});
		net.get(9).addNeighbors(new int[] {12, 13});
		net.get(10).addNeighbors(new int[] {13, 14});



		net.get(11).addNeighbors(new int[] {15});
		net.get(12).addNeighbors(new int[] {15, 16});
		net.get(13).addNeighbors(new int[] {16, 17});
		net.get(14).addNeighbors(new int[] {17});


		/*net.get(15).addNeighbors(new int[] {19, 20});
		net.get(16).addNeighbors(new int[] {19, 21});
		net.get(17).addNeighbors(new int[] {20, 22});
		net.get(18).addNeighbors(new int[] {21, 22});
		net.get(19).addNeighbors(new int[] {22});*/


		/*net.get(19).addNeighbors(new int[] {23});
		net.get(20).addNeighbors(new int[] {23, 24});
		net.get(21).addNeighbors(new int[] {24, 25});
		net.get(22).addNeighbors(new int[] {25, 26});*/

		/*
		net.get(23).addNeighbors(new int[] {27});
		net.get(24).addNeighbors(new int[] {27, 28});
		net.get(25).addNeighbors(new int[] {28, 29});
		net.get(26).addNeighbors(new int[] {29});
		 */













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

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */


		/*net.get(0).addExploits(new int[] {0, 1});

		net.get(1).addExploits(new int[] {2, 3});
		net.get(2).addExploits(new int[] {2, 3});

		net.get(3).addExploits(new int[] {1, 4});
		net.get(4).addExploits(new int[] {2, 3});
		net.get(5).addExploits(new int[] {1, 3});
		net.get(6).addExploits(new int[] {3, 4});

		net.get(7).addExploits(new int[] {2, 4});
		net.get(8).addExploits(new int[] {2, 3});
		net.get(9).addExploits(new int[] {0, 2});
		net.get(10).addExploits(new int[] {3, 4});
		net.get(11).addExploits(new int[] {2, 3});



		net.get(12).addExploits(new int[] {0, 4});
		net.get(13).addExploits(new int[] {2, 4});



		net.get(14).addExploits(new int[] {1, 2});
		net.get(15).addExploits(new int[] {1, 3});
		net.get(16).addExploits(new int[] {1, 4});
		net.get(17).addExploits(new int[] {0, 2});
		net.get(18).addExploits(new int[] {1, 3});
		net.get(19).addExploits(new int[] {0, 4});
		net.get(20).addExploits(new int[] {2, 4});


		net.get(21).addExploits(new int[] {2, 3});
		net.get(22).addExploits(new int[] {2, 3});
		net.get(23).addExploits(new int[] {2, 4});
		net.get(24).addExploits(new int[] {1, 3});
		net.get(25).addExploits(new int[] {3, 4});
		net.get(26).addExploits(new int[] {1, 4});
		net.get(27).addExploits(new int[] {2});

		net.get(28).addExploits(new int[] {3});
		net.get(29).addExploits(new int[] {4});


		 */


		for(int i=0; i<nnodes; i++)
		{
			int l = 2;//exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
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
			int nhoneypots, int nnodes, int hpv, int hpc, boolean sameval, boolean allexploit, HashMap<Integer,Node> net, boolean pickfromnet, int[] goals, boolean randhp, boolean uniqueexploithp) {




		
		if(uniqueexploithp)
		{
			
			int count = 0;
			for(int i=0; i<(exploits.size()); i++)
			{

				
				
				//Node pn = left.get(i);

				/**
				 * check if there exists same conf honeypot
				 */
				
				int v = randInt(5, 10);
				int c = randInt(1,4);
				
				if(honeypots.size()==0)
				{
					
					
					Node n = new Node(count+nnodes, v, c);
					n.ishoneypot = true;

					
						n.exploits.put(exploits.get(i).id, exploits.get(i).id);
					//}

					honeypots.put(n.id, n);
					count++;
				}
				else
				{

					//System.out.println(x);
					
//					boolean existssame = existsHoney(pn, honeypots);
//					
//					if(pn.exploits.size()==exploits.size())
//					{
//						continue;
//					}
//					else
//					{
//						existssame = false;
//					}
//					
					

					
						//left.remove(r);

						Node n = new Node(count+nnodes, v, c);
						n.ishoneypot = true;

						

						
						n.exploits.put(exploits.get(i).id, exploits.get(i).id);
						

						count++;
						honeypots.put(n.id, n);
					
				}
				if(honeypots.size()==nhoneypots)
				{
					break;
				}


			}

			
		}
		else if(pickfromnet)
		{
			ArrayList<Node> left =new ArrayList<Node>();


			/**
			 * sort and pick
			 */

			if(!randhp)
			{

				int[][] srted = new int[net.size()-goals.length][2];

				for(int i=0; i<srted.length; i++)
				{

					Node n = net.get(i);
					int minexpltval = minExploit(n, exploits);
					srted[n.id][0] = n.id;
					srted[n.id][1] = minexpltval;
				}


				int[] swap = {0,0};

				for (int k = 0; k < srted.length; k++) 
				{
					for (int d = 1; d < srted.length-k; d++) 
					{
						if (srted[d-1][1] > srted[d][1])    // ascending order
						{
							swap = srted[d];
							srted[d]  = srted[d-1];
							srted[d-1] = swap;
						}
					}
				}

				for (int k = 0; k < srted.length; k++) 
				{
					left.add(net.get(srted[k][0]));
				}
			}
			else
			{
				//ArrayList<Node> left =new ArrayList<Node>();

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
			}
			
			
			




			int count = 0;
			for(int i=0; /*i<(left.size())*/; i++)
			{

				//int r = left.get(i);

				
				System.out.println(i);
				
				if(i==21)
				{
					System.out.println(i);
				}
				
				Node pn = left.get(i);

				/**
				 * check if there exists same conf honeypot
				 */
				if(honeypots.size()==0)
				{
					Node n = new Node(count+nnodes, pn.value, pn.cost);
					n.ishoneypot = true;

					for(Integer e: pn.exploits.values())
					{
						n.exploits.put(e, e);
					}

					honeypots.put(n.id, n);
					count++;
				}
				else
				{

					//System.out.println(x);
					
					boolean existssame = existsHoney(pn, honeypots);
					
					if(pn.exploits.size()==exploits.size())
					{
						continue;
					}
					else
					{
						existssame = false;
					}
					
					

					if(!existssame)
					{

						//left.remove(r);

						Node n = new Node(count+nnodes, pn.value, pn.cost);
						n.ishoneypot = true;

						if(n.id==26)
						{
							int v=1;
						}

						for(Integer e: pn.exploits.values())
						{
							n.exploits.put(e, e);
						}

						count++;
						honeypots.put(n.id, n);
					}
				}
				if(honeypots.size()==nhoneypots)
				{
					break;
				}


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


	private static boolean existsHoney(Node pn, HashMap<Integer, Node> honeypots) {

		boolean f = true;
		for(Node h: honeypots.values())
		{
			f = true;
			if(pn.exploits.size() == h.exploits.size())
			{
				for(Integer e: pn.exploits.values())
				{
					if(!h.exploits.containsValue(e))
					{
						f = false;
						break;
					}
				}
			}
			else
			{
				f = false;
			}

			if(f)
				return true;

		}


		return f;
	}

	private static int minExploit(Node n, HashMap<Integer,Exploits> exploits) {


		int minval = 1000;


		for(int e: n.exploits.values())
		{
			if(exploits.get(e).cost<minval)
			{
				minval = exploits.get(e).cost;
			}
		}


		return minval;
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

	public static void generateNetwork(HashMap<Integer, Node> net, int nstep, int nodeamin, int nodeamax, int ngoal, int minedge, double density, int nexploits, HashMap<Integer,Exploits> exploits) 
	{

		int nnode = 0;
		int[] nodeperstage = new int[nstep];
		nodeperstage[0] = 1;
		nodeperstage[nstep-1] = ngoal;
		nnode = ngoal + 1;

		ArrayList<ArrayList<Integer>> nodeassignment = new ArrayList<ArrayList<Integer>>();
		for(int i=1; i<nstep-1; i++)
		{
			nodeperstage[i] = nodeperstage[i-1] + randInt(nodeamin, nodeamax);
			nnode += nodeperstage[i];

		}

		for(int i=0; i<nstep; i++)
		{
			System.out.println("Step "+i + ", #nodes "+ nodeperstage[i]);
		}
		System.out.println("total #nodes "+ nnode);

		for(int i=0; i<nnode; i++)
		{
			int v = randInt(5, 10);
			int c = randInt(1,4);
			Node n = new Node(i, v, c);
			net.put(n.id, n);

		}

		int nodeindex = 0;

		for(int i=0; i<nstep; i++)
		{
			int n = nodeperstage[i];
			ArrayList<Integer> nodes = new ArrayList<Integer>();
			for(int j=0; j<n; j++)
			{
				nodes.add(nodeindex++);
			}
			nodeassignment.add(nodes);

		}

		for(int i=0; i<nstep; i++)
		{
			System.out.print("Step "+i+": ");
			ArrayList<Integer> nodes = nodeassignment.get(i);
			for(int j: nodes)
			{

				System.out.print(" "+j);
			}
			System.out.println();

		}

		for(int i=0; i<nstep; i++)
		{
			System.out.print("\n\n Step "+i+": \n");


			if(i<(nstep-1))
			{
				ArrayList<Integer> nodes = nodeassignment.get(i);
				ArrayList<Integer> nextnodes = nodeassignment.get(i+1);

				for(int j: nodes)
				{
					// for each node choose neighbors
					Node n = net.get(j);
					n.step=i;
					// choose number of neighbors
					int nnei = (int)Math.ceil(nextnodes.size()*density);
					if(nnei==0)
					{
						nnei=2;
					}
					System.out.println("node "+ n.id + ", #nei: "+ nnei);
					System.out.print("neighbors: ");

					int neibors [] = new int[nnei];
					for(int k=0; k<nnei; k++)
					{
						int neiindex = randInt(k, nextnodes.size()-1);
						int neiid = nextnodes.get(neiindex);
						System.out.print(neiid+" ");
						neibors[k] = neiid;
						Collections.swap(nextnodes, k, neiindex);

					}
					System.out.println();
					n.addNeighbors(neibors);
				}
			}
			else
			{
				ArrayList<Integer> nodes = nodeassignment.get(i);
				for(int j: nodes)
				{
					// for each node choose neighbors
					Node n = net.get(j);
					n.step=i;
				}
			}




		}

		System.out.print("");
		for(int i=0; i<nexploits; i++)
		{

			int c = i+1;//randInt(1,i);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}



	}

	public static void addExploits(HashMap<Integer, Node> net, HashMap<Integer, Exploits> exploits, int nnodes, int stepsperstage, int nstep, 
			int nattacker, double overlappingexploitspercentage, ArrayList<ArrayList<Integer>> exploitsector) {
		// TODO Auto-generated method stub



		int exploitpersector = exploits.size()/nattacker;

		// = new ArrayList<>();



		int ecount=0;
		int sectorcount=0;
		for(int i=0; i<exploits.size(); i++)
		{

			if(exploitsector.size()<=sectorcount)
			{
				exploitsector.add(new ArrayList<>());
			}
			exploitsector.get(sectorcount).add(i);

			ecount++;

			if(ecount==exploitpersector)
			{
				sectorcount++;
				ecount=0;
			}


		}






		/**
		 * 1. # of stage is same as the number of steps
		 * 2. In each stage divide the nodes into parts equally distributed to the number of goals/ # of attackers.
		 * 3. For each stage choose different density of overlapping vulnerabilities for each of the vulnerabilities
		 */


		ArrayList<TreeMap<Integer, Node>> stagenodes = new ArrayList<>();
		int nstage = nstep;

		/**
		 * divide the nodes into stage
		 */

		for(Node node: net.values())
		{
			if(stagenodes.size()<=node.step)
			{
				stagenodes.add(new TreeMap<Integer, Node>());
			}
			stagenodes.get(node.step).put(node.id, node);
		}

		printStages(stagenodes);


		/**
		 * for each stage divide the nodes into equal numbers divided by the number of goals or attackers. 
		 */

		int stage = 0;

		for(TreeMap<Integer, Node> step: stagenodes)
		{
			double sectorincrement = -1;
			
			if(stage<=3)
			{
				sectorincrement = Math.floor((1.0*step.size())/(1.0 *nattacker));
			}
			else
			{
				sectorincrement = Math.ceil((1.0*step.size())/(1.0 *nattacker));
			}
			
			

			if(stage==3)
			{
				//System.out.println(stage);
			}

			if(sectorincrement<1)
			{
				sectorincrement = 1;
			}

			/**
			 * 1. divide the nodes into the attackers starting in ascending order
			 * 2. If there are less then 
			 * 
			 */

			ArrayList<ArrayList<Integer>> sectornodes = new ArrayList<>();
			int sector = 0;
			int count = 0;
			for(Entry<Integer, Node> entry: step.entrySet())
			{

				// Place every nodes into its own sector


				if(sectornodes.size()<=sector)
				{
					sectornodes.add(new ArrayList<>());
				}
				sectornodes.get(sector).add(entry.getKey());


				count++;
				if(count==sectorincrement)
				{
					count=0;
					sector++;
					sector= sector%nattacker;
				}

			}

			System .out.println("Stage "+ stage + " secotr nodes:");

			int scount=0;
			for(ArrayList<Integer> sc: sectornodes)
			{

				System.out.println("sector "+ scount);
				for(Integer nd: sc)
				{
					net.get(nd).sector= scount;
					System.out.print(nd+",");
				}
				System.out.println();

				scount++;
			}





			/**
			 * for each sector first assign the regular exploits
			 * then depending on the overlapping parameter assign randomly exploits from other sector
			 */

			/**
			 * how many exploits to pick?
			 */

			double exploitstoadd = Math.ceil((exploits.size()/nattacker)*(overlappingexploitspercentage/100.0));





			for(ArrayList<Integer> sc: sectornodes)
			{
				for(Integer nd: sc)
				{
					Node node = net.get(nd);

					if(node.id <=2)
					{
						addAllAxploits(node, exploits, 0, exploits.size());
					}
					else
					{

						int sectr = node.sector;



						// get the exploits from other sector

						for(int s=0; s<exploitsector.size(); s++)
						{
							if(s != sectr)
							{
								ArrayList<Integer> explts = exploitsector.get(s);
								// now randomly pick exploits

								int tmpcount = (int)exploitstoadd;

								//Random rn = new Random();

								for(int i=0; i<exploitstoadd; i++)
								{
									int index = randInt(i, explts.size()-1);

									int ex = exploits.get(explts.get(index)).id;

									node.exploits.put(ex, ex);

									// swap

									int tmp = explts.get(i);
									explts.set(i, explts.get(index));
									explts.set(index, tmp);
								}
							}
							else if(s== sectr)
							{
								// add the regular exploits 
								ArrayList<Integer> explts = exploitsector.get(s);
								node.addExploits(explts);

							}
						}

					}


				}



			}







			stage++;
		}














		double[] overlapsforsteps = new double[nstep];




		/*for(int i=0; i<nnodes; i++)
		{
			int l = exploits.size()/2;
			int exlt = randInt(1, l);


			ArrayList<Integer> explts = new ArrayList<Integer>();


			for(Integer eid: exploits.keySet())
			{
				//if(eid != 3)
				{
					explts.add(eid);
				}
			}


			int extoadd[] = new int[exlt];
			for(int e=0; e<exlt; e++)
			{

				int pe = randInt(0, explts.size()-1);
				extoadd[e] = explts.get(pe);
				explts.remove(pe);

			}

			net.get(i).addExploits(extoadd);

		}*/


	}

	private static void addAllAxploits(Node node, HashMap<Integer, Exploits> exploits,  int start, int until) {
		// TODO Auto-generated method stub

		for(int e=start; e<until; e++)
		{
			node.exploits.put(exploits.get(e).id, exploits.get(e).id);
		}

	}

	private static void printStages(ArrayList<TreeMap<Integer, Node>> stagenodes) {


		int s=0;

		for(TreeMap<Integer, Node> stage: stagenodes)
		{
			System.out.println("Stage "+ s++);

			for(Entry<Integer, Node> entry: stage.entrySet())
			{
				System.out.print(entry.getKey()+", ");
			}
			System.out.println();

		}



	}



}
