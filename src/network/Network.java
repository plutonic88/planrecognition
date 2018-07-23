package network;

import java.util.HashMap;
import java.util.Random;

public class Network {

	public static Random rand = new Random(100);



	public static int randInt(int min, int max) {

		//rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static void constructNetwork(HashMap<Integer,Node> net, HashMap<Integer,Exploits> exploits)
	{
		/**
		 * create 27 nodes
		 */

		for(int i=0; i<27; i++)
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
				net.put(i, n);
			}
		}





		/**
		 * create neighbors
		 */

		net.get(0).addNeighbors(new int[] {1, 2});

		net.get(1).addNeighbors(new int[] {3, 4});
		net.get(2).addNeighbors(new int[] {5, 6});

		net.get(3).addNeighbors(new int[] {7, 8});
		net.get(4).addNeighbors(new int[] {8, 15});
		net.get(5).addNeighbors(new int[] {10, 14});
		net.get(6).addNeighbors(new int[] {10, 11});

		net.get(7).addNeighbors(new int[] {12, 13});
		net.get(8).addNeighbors(new int[] {13, 14});
		//net.get(9).addNeighbors(new int[] {14, 15});
		net.get(10).addNeighbors(new int[] {15, 16});
		net.get(11).addNeighbors(new int[] {16, 17});



		net.get(12).addNeighbors(new int[] {18});
		net.get(13).addNeighbors(new int[] {18, 19});
		net.get(14).addNeighbors(new int[] {19, 20});
		net.get(15).addNeighbors(new int[] {20, 21});
		net.get(16).addNeighbors(new int[] {21, 22});
		net.get(17).addNeighbors(new int[] {22});


		net.get(18).addNeighbors(new int[] {23});
		net.get(19).addNeighbors(new int[] {23, 24});
		net.get(20).addNeighbors(new int[] {24, 25});
		net.get(21).addNeighbors(new int[] {25, 26});
		net.get(22).addNeighbors(new int[] {26});



		/**
		 * create exploits
		 */
		for(int i=0; i<8; i++)
		{
			int c = randInt(1,6);
			Exploits e = new Exploits(i, c);
			exploits.put(i, e);
			//System.out.println("Exploit id "+ e.id +", c: "+e.cost);

		}

		/*
		 * Assign exploits to nodes
		 */



		net.get(0).addExploits(new int[] {0, 1, 2, 3, 4});

		net.get(1).addExploits(new int[] {1,2,5,7});
		net.get(2).addExploits(new int[] {0,3,4,6});

		net.get(3).addExploits(new int[] {3,7});
		net.get(4).addExploits(new int[] {1,2,5,7});
		net.get(5).addExploits(new int[] {0,3,4,7});
		net.get(6).addExploits(new int[] {0,4,6});

		net.get(7).addExploits(new int[] {3});
		net.get(8).addExploits(new int[] {2,5,7});
		//net.get(9).addExploits(new int[] {0, 1, 3});
		net.get(10).addExploits(new int[] {0,4,7});
		net.get(11).addExploits(new int[] {0,6});



		net.get(12).addExploits(new int[] {3});
		net.get(13).addExploits(new int[] {2});
		net.get(14).addExploits(new int[] {3,4,5,7});
		net.get(15).addExploits(new int[] {0, 1,4,7});
		net.get(16).addExploits(new int[] {0,6});
		net.get(17).addExploits(new int[] {0});


		net.get(18).addExploits(new int[] {3});
		net.get(19).addExploits(new int[] {2,3,5});
		net.get(20).addExploits(new int[] {4,7});
		net.get(21).addExploits(new int[] {1,6});
		net.get(22).addExploits(new int[] {0});


		net.get(23).addExploits(new int[] {2,3,5,7});
		net.get(24).addExploits(new int[] {3,4,5,6});
		net.get(25).addExploits(new int[] {1,2,6,7});
		net.get(26).addExploits(new int[] {0,1,2,4});











	}

}
