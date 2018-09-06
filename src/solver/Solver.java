package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class Solver {

	public static ArrayList<int[]> solve(int[][] w, ArrayList<Integer> starts, ArrayList<Integer> goals)
	{




		int n= w.length;



		try
		{


			//IloNumVar[] x = new IloNumVar[nJointSchedule];//cplex.numVarArray(nPath, 0, 1);

			IloCplex cplex = new IloCplex();

			//variables
			IloNumVar[][] x = new IloNumVar[n][n];
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					x[i][j] = cplex.numVar(0,1);
				}
			}


			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						obj.addTerm(w[i][j], x[i][j]);
					}
				}
			}
			cplex.addMinimize(obj);





			/**
			 * constraint 2
			 */

			int f = 1;
			int f2 = 1;
			int s = -1;
			int g = -1;

			for(int i=0; i<n; i++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();


				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						expr1.addTerm(1.0, x[i][j]);
						expr1.addTerm(-1.0, x[j][i]);
					}

				}

				//if(i!=j)
				{

					if(starts.contains(i) && f==1)
					{
						f=0;
						s= i;
						cplex.addEq(expr1, 1.0);
					}
					else if(goals.contains(i) && f2==1)
					{
						f2=0;
						g = i;
						cplex.addEq(expr1, -1.0);
					}
					else
					{
						cplex.addEq(expr1, 0.0);
					}
				}
			}


			/**
			 * constraint 3
			 */

			for(int i=0; i<n; i++)
			{

				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();
						expr.addTerm(1.0, x[i][j]);
						cplex.addGe(expr, 0.0);
					}

				}

			}


			cplex.solve();

			System.out.println(" obj: "+ cplex.getObjValue());

			ArrayList<int[]> res = new ArrayList<int[]>();

			for(int i=0; i<n; i++)
			{


				for(int j=0; j<n; j++)
				{

					if( i!=j && cplex.getValue(x[i][j])>0)
					{

						System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[i][j])+ " ");
						//cplex.getValue(x[i]);

						int[] ar = {i,j};
						res.add(ar);


					}


				}
			}

			System.out.println();


			return res;








		}
		catch(Exception ex)
		{

		}



		return null;

	}

	public static ArrayList<int[]> solve3DCost(int[][][] w, int start, int goal, int e) {

		int n= w.length;
		//int e = expl



		try
		{


			//IloNumVar[] x = new IloNumVar[nJointSchedule];//cplex.numVarArray(nPath, 0, 1);

			IloCplex cplex = new IloCplex();

			//variables
			IloNumVar[][][] x = new IloNumVar[n][n][e];
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					for(int k=0; k<e; k++)
					{
						x[i][j][k] = cplex.numVar(0,1);
					}


				}
			}


			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						for(int k=0; k<e; k++)
						{
							obj.addTerm(w[i][j][k], x[i][j][k]);
						}
					}
				}
			}
			cplex.addMinimize(obj);




			/**
			 * constraint 2
			 */



			for(int i=0; i<n; i++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();


				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						for(int k=0; k<e; k++)
						{
							expr1.addTerm(1.0, x[i][j][k]);
							expr1.addTerm(-1.0, x[j][i][k]);
						}
					}

				}

				//if(i!=j)
				{

					if(start == i)
					{

						cplex.addEq(expr1, 1.0);
					}
					else if(goal == i)
					{

						cplex.addEq(expr1, -1.0);
					}
					else
					{
						cplex.addEq(expr1, 0.0);
					}
				}
			}


			/**
			 * constraint 3
			 */

			for(int i=0; i<n; i++)
			{

				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();
						for(int k=0; k<e; k++)
						{

							expr.addTerm(1.0, x[i][j][k]);

						}
						cplex.addGe(expr, 0.0);
					}

				}

			}


			cplex.solve();

			System.out.println(" obj: "+ cplex.getObjValue());

			ArrayList<int[]> res = new ArrayList<int[]>();

			for(int i=0; i<n; i++)
			{


				for(int j=0; j<n; j++)
				{

					for(int k=0; k<e; k++)
					{
						if( i!=j && cplex.getValue(x[i][j][k])>0)
						{

							System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[i][j][k])+ " ");
							//cplex.getValue(x[i]);

							int[] ar = {i,j, k};
							res.add(ar);


						}
					}


				}
			}

			System.out.println();


			return res;








		}
		catch(Exception ex)
		{

		}



		return null;


	}


	public static ArrayList<int[]> solve3DCostWithHP(int[][][] w, int start, int goal, int e, int nattakers, int[][][][] addedcost) {

		int n= w.length;
		//int e = expl



		try
		{


			//IloNumVar[] x = new IloNumVar[nJointSchedule];//cplex.numVarArray(nPath, 0, 1);

			IloCplex cplex = new IloCplex();

			//variables
			IloNumVar[][][] x = new IloNumVar[n][n][e];
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					for(int k=0; k<e; k++)
					{
						x[i][j][k] = cplex.numVar(0,1);
					}


				}
			}


			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						for(int k=0; k<e; k++)
						{
							obj.addTerm(w[i][j][k], x[i][j][k]);
						}
					}
				}
			}
			cplex.addMinimize(obj);




			/**
			 * constraint 2
			 */



			for(int i=0; i<n; i++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();


				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						for(int k=0; k<e; k++)
						{
							expr1.addTerm(1.0, x[i][j][k]);
							expr1.addTerm(-1.0, x[j][i][k]);
						}
					}

				}

				//if(i!=j)
				{

					if(start == i)
					{

						cplex.addEq(expr1, 1.0);
					}
					else if(goal == i)
					{

						cplex.addEq(expr1, -1.0);
					}
					else
					{
						cplex.addEq(expr1, 0.0);
					}
				}
			}


			/**
			 * constraint 3
			 */

			for(int i=0; i<n; i++)
			{

				for(int j=0; j<n; j++)
				{
					if(i!=j)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();
						for(int k=0; k<e; k++)
						{

							expr.addTerm(1.0, x[i][j][k]);

						}
						cplex.addGe(expr, 0.0);
					}

				}

			}


			cplex.solve();

			System.out.println(" obj: "+ cplex.getObjValue());

			ArrayList<int[]> res = new ArrayList<int[]>();

			for(int i=0; i<n; i++)
			{


				for(int j=0; j<n; j++)
				{

					for(int k=0; k<e; k++)
					{
						if( i!=j && cplex.getValue(x[i][j][k])>0)
						{

							System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[i][j][k])+ " ");
							//cplex.getValue(x[i]);

							int[] ar = {i,j, k};
							res.add(ar);


						}
					}


				}
			}

			System.out.println();


			return res;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	
	public static ArrayList<ArrayList<int[]>> solveHPDeploymentMultAttacker(int[][][] w, int start, ArrayList<Integer> goals, int e,
			int nattakers, int[][][][] hpdeploymentcosts, int totalconf, double[] priors) {

		int n= hpdeploymentcosts[0].length;
		int nplayer = goals.size();
		
		double M = 1000.0;
		//int e = expl

		/*totalconf = 1;
		
		hpdeploymentcosts[3][1][13][0] = 1;
		hpdeploymentcosts[3][13][10][0] = 1;*/
		
		
		//double[] priors = {0.33, 0.33, 0.33};
		


		try
		{


			//IloNumVar[] x = new IloNumVar[nJointSchedule];//cplex.numVarArray(nPath, 0, 1);

			IloCplex cplex = new IloCplex();

			//variables
			IloNumVar[][][][][] x = new IloNumVar[totalconf][nplayer][n][n][e];

			for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int k=0; k<e; k++)
						{
							x[c][a][i][j][k] = cplex.boolVar();
						}


					}
				}
				}
			}


			
			
			
			IloNumVar b[] = new IloNumVar[goals.size()];
			
			for(int c=0; c<nplayer; c++)
			{
				b[c] = cplex.numVar(0, Double.MAX_VALUE);
				
				//conf[c] = cplex.numVar(0, 1);
			}
			
			
			//IloNumVar v[] = cplex.numVar(0.0, totalconf-1);
			

			IloNumVar[] conf = new IloNumVar[totalconf];
			for(int c=0; c<totalconf; c++)
			{
				conf[c] = cplex.boolVar();
				
				//conf[c] = cplex.numVar(0, 1);
			}
			
			
			//IloNumVar conf = cplex.numVar(0, 1);
			






			IloLinearNumExpr obj = cplex.linearNumExpr();

			for(int c=0; c<nplayer; c++)
			{
				obj.addTerm(priors[c], b[c]);
				

			}
			cplex.addMinimize(obj);






			/**
			 * constraint 1
			 */
			
			
			
			

			for(int c=0; c<totalconf; c++)
			{
				

				for(int a=0; a<nplayer; a++)
				{
					
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int k=0; k<e; k++)
							{
								expr1.addTerm(hpdeploymentcosts[c][i][j][k], x[c][a][i][j][k]);
								//expr1.addTerm(1, conf[c], x[c][i][j][k]);
							}


						}
					}

					expr1.addTerm(-1, b[a]);

					expr1.addTerm(M, conf[c]);

					cplex.addLe(expr1, M);
				}

			}
			
			
			for(int c=0; c<totalconf; c++)
			{
				

				for(int a=0; a<nplayer; a++)
				{
					
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int k=0; k<e; k++)
							{
								expr1.addTerm(hpdeploymentcosts[c][i][j][k], x[c][a][i][j][k]);
								//expr1.addTerm(1, conf[c], x[c][i][j][k]);
							}


						}
					}

					expr1.addTerm(-1, b[a]);

					//expr1.addTerm(M, conf[c]);

					cplex.addGe(expr1, 0);
				}

			}
			
			
			
			
			
			




			/**
			 * constraint 2
			 */

			for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{

					for(int i=0; i<n; i++)
					{
						IloLinearNumExpr expr1 = cplex.linearNumExpr();


						for(int j=0; j<n; j++)
						{
							if(i!=j)
							{
								for(int k=0; k<e; k++)
								{
									expr1.addTerm(1.0, x[c][a][i][j][k]);
									expr1.addTerm(-1.0, x[c][a][j][i][k]);
								}
							}

						}

						//if(i!=j)
						{

							if(start == i)
							{

								cplex.addEq(expr1, 1.0);
							}
							else if(goals.get(a) == i)
							{

								cplex.addEq(expr1, -1.0);
							}
							else
							{
								cplex.addEq(expr1, 0.0);
							}
						}
					}
				}
			}


			/**
			 * constraint 3
			 */

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{

					for(int i=0; i<n; i++)
					{

						for(int j=0; j<n; j++)
						{
							if(i!=j)
							{
								IloLinearNumExpr expr = cplex.linearNumExpr();
								for(int k=0; k<e; k++)
								{

									expr.addTerm(1.0, x[c][a][i][j][k]);

								}
								cplex.addGe(expr, 0.0);
							}

						}

					}
				}
			}
			
			
			/**
			 * constraint 4
			 */
			
			IloLinearNumExpr expr = cplex.linearNumExpr();
			
			for(int c=0; c<totalconf; c++)
			{

				expr.addTerm(1.0, conf[c]);
			}
			
			cplex.addEq(expr, 1.0);
			
			


			cplex.solve();
			
			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());
			
			
			for(int a=0; a<nplayer; a++)
			{
				if(cplex.getValue(b[a])>0)
				{
					System.out.println(a+": "+ cplex.getValue(b[a]));
				}
			}

			ArrayList<int[]> res = new ArrayList<int[]>();
			
			ArrayList<ArrayList<int[]>> results = new ArrayList<ArrayList<int[]>>();

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{
					res = new ArrayList<int[]>();

					if(cplex.getValue(conf[c])>0)
					{


						for(int i=0; i<n; i++)
						{


							for(int j=0; j<n; j++)
							{

								for(int k=0; k<e; k++)
								{
									if( i!=j && cplex.getValue(x[c][a][i][j][k])>0)
									{

										System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[c][a][i][j][k])+ " ");
										//cplex.getValue(x[i]);

										int[] ar = {i,j, k, c};
										res.add(ar);


									}
								}


							}
						}
					}
					if(res.size()>0)
					{
						results.add(res);
					}
				}
			}

			System.out.println();


			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}




	public static ArrayList<int[]> solveHPDeploymentSingleAttacker(int[][][] w, int start, int goal, int e, int nattakers, int[][][][] hpdeploymentcosts, int totalconf) {

		int n= hpdeploymentcosts[0].length;
		
		double M = 1000.0;
		//int e = expl

		/*totalconf = 1;
		
		hpdeploymentcosts[3][1][13][0] = 1;
		hpdeploymentcosts[3][13][10][0] = 1;*/
		
		
		double[] priors = {0.33, 0.33, 0.33};
		


		try
		{


			//IloNumVar[] x = new IloNumVar[nJointSchedule];//cplex.numVarArray(nPath, 0, 1);

			IloCplex cplex = new IloCplex();

			//variables
			IloNumVar[][][][] x = new IloNumVar[totalconf][n][n][e];

			for(int c=0; c<totalconf; c++)
			{

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int k=0; k<e; k++)
						{
							x[c][i][j][k] = cplex.boolVar();
						}


					}
				}
			}


			
			
			
			IloNumVar b =  cplex.numVar(0, Double.MAX_VALUE);
			
			/*for(int c=0; c<goals.size(); c++)
			{
				b[c] = cplex.numVar(0, Double.MAX_VALUE);
				
				//conf[c] = cplex.numVar(0, 1);
			}
			*/
			
			//IloNumVar v[] = cplex.numVar(0.0, totalconf-1);
			

			IloNumVar[] conf = new IloNumVar[totalconf];
			for(int c=0; c<totalconf; c++)
			{
				conf[c] = cplex.boolVar();
				
				//conf[c] = cplex.numVar(0, 1);
			}
			
			
			//IloNumVar conf = cplex.numVar(0, 1);
			






			IloLinearNumExpr obj = cplex.linearNumExpr();

			//for(int c=0; c<goals.size(); c++)
			{
				obj.addTerm(1, b);
				

			}
			cplex.addMinimize(obj);






			/**
			 * constraint 1
			 */

			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int k=0; k<e; k++)
						{
							expr1.addTerm(hpdeploymentcosts[c][i][j][k], x[c][i][j][k]);
							//expr1.addTerm(1, conf[c], x[c][i][j][k]);
						}


					}
				}
				
				expr1.addTerm(-1, b);
				
				expr1.addTerm(M, conf[c]);

				cplex.addLe(expr1, M);

			}
			
			
			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				
				
				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int k=0; k<e; k++)
						{
							expr1.addTerm(hpdeploymentcosts[c][i][j][k], x[c][i][j][k]);
							//expr1.addTerm(1, conf[c], x[c][i][j][k]);
						}


					}
				}
				
				
				expr1.addTerm(-1, b);
				
				
				//expr1.addTerm(M, conf[c]);

				cplex.addGe(expr1, 0);

			}
			
			
			
			
			




			/**
			 * constraint 2
			 */

			for(int c=0; c<totalconf; c++)
			{

				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();


					for(int j=0; j<n; j++)
					{
						if(i!=j)
						{
							for(int k=0; k<e; k++)
							{
								expr1.addTerm(1.0, x[c][i][j][k]);
								expr1.addTerm(-1.0, x[c][j][i][k]);
							}
						}

					}

					//if(i!=j)
					{

						if(start == i)
						{

							cplex.addEq(expr1, 1.0);
						}
						else if(goal == i)
						{

							cplex.addEq(expr1, -1.0);
						}
						else
						{
							cplex.addEq(expr1, 0.0);
						}
					}
				}
			}


			/**
			 * constraint 3
			 */

			for(int c=0; c<totalconf; c++)
			{

				for(int i=0; i<n; i++)
				{

					for(int j=0; j<n; j++)
					{
						if(i!=j)
						{
							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int k=0; k<e; k++)
							{

								expr.addTerm(1.0, x[c][i][j][k]);

							}
							cplex.addGe(expr, 0.0);
						}

					}

				}
			}
			
			
			/**
			 * constraint 4
			 */
			
			IloLinearNumExpr expr = cplex.linearNumExpr();
			
			for(int c=0; c<totalconf; c++)
			{

				expr.addTerm(1.0, conf[c]);
			}
			
			cplex.addEq(expr, 1.0);
			
			


			cplex.solve();
			
			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());

			ArrayList<int[]> res = new ArrayList<int[]>();

			for(int c=0; c<totalconf; c++)
			{


				if(cplex.getValue(conf[c])>0)
				{


					for(int i=0; i<n; i++)
					{


						for(int j=0; j<n; j++)
						{

							for(int k=0; k<e; k++)
							{
								if( i!=j && cplex.getValue(x[c][i][j][k])>0)
								{

									System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[c][i][j][k])+ " ");
									//cplex.getValue(x[i]);

									int[] ar = {i,j, k, c};
									res.add(ar);


								}
							}


						}
					}
				}
			}

			System.out.println();


			return res;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	





}
