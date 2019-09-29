package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agents.Attacker;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import planrecognition.PlanrecognitionExp;

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



	public static ArrayList<ArrayList<double[]>> solveHPDeploymentMultAttacker(int[][][] w, int start, ArrayList<Integer> goals, int e,
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





			IloNumVar b[][] = new IloNumVar[totalconf][goals.size()];

			for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{
					b[c][a] = cplex.numVar(0, Double.MAX_VALUE);
				}

				//conf[c] = cplex.numVar(0, 1);
			}



			IloNumVar f[] = new IloNumVar[goals.size()];


			for(int a=0; a<nplayer; a++)
			{
				f[a] = cplex.numVar(0, Double.MAX_VALUE);
			}

			//conf[c] = cplex.numVar(0, 1);



			//IloNumVar v[] = cplex.numVar(0.0, totalconf-1);


			IloNumVar[] conf = new IloNumVar[totalconf];
			for(int c=0; c<totalconf; c++)
			{

				conf[c] = cplex.boolVar();

				//conf[c] = cplex.numVar(0, 1);
			}


			//IloNumVar conf = cplex.numVar(0, 1);







			IloLinearNumExpr obj = cplex.linearNumExpr();

			//for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{
					obj.addTerm(priors[a], f[a]);
				}


			}
			cplex.addMinimize(obj);






			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					expr1.addTerm(1, f[a]);
					expr1.addTerm(-1, b[c][a]);

				}

				expr1.addTerm(M, conf[c]);
				cplex.addLe(expr1, M);

			}


			/*for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					//expr1.addTerm(1, f[a]);
					expr1.addTerm(1, b[c][a]);

				}

				//expr1.addTerm(M, conf[c]);
				cplex.addGe(expr1, 0);

			}
			 */


			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					expr1.addTerm(1, f[a]);
					expr1.addTerm(-1, b[c][a]);

				}

				//expr1.addTerm(M, conf[c]);
				cplex.addGe(expr1, 0);

			}






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

					expr1.addTerm(-1, b[c][a]);

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

					expr1.addTerm(-1, b[c][a]);

					//expr1.addTerm(M, conf[c]);

					cplex.addGe(expr1, 0);
				}

			}




			for(int c1=0; c1<totalconf; c1++)
			{
				for(int c2=0; c2<totalconf; c2++)
				{

					if(c1!=c2)
					{

						IloLinearNumExpr expr1 = cplex.linearNumExpr();


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(priors[a], b[c1][a]);

						}


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(-priors[a], b[c2][a]);

						}


						expr1.addTerm(M, conf[c1]);

						expr1.addTerm(M, conf[c2]);


						cplex.addLe(expr1, 2*M);


					}
				}

			}


			for(int c1=0; c1<totalconf; c1++)
			{
				for(int c2=0; c2<totalconf; c2++)
				{

					if(c1!=c2)
					{

						IloLinearNumExpr expr1 = cplex.linearNumExpr();


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(priors[a], b[c1][a]);

						}


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(-priors[a], b[c2][a]);

						}


						//expr1.addTerm(M, conf[c1]);

						//expr1.addTerm(M, conf[c2]);


						cplex.addGe(expr1, 0);


					}
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

								cplex.addGe(expr1, 1.0);
							}
							else if(goals.get(a) == i)
							{

								cplex.addLe(expr1, -1.0);
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

			cplex.addGe(expr, 2.0);




			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());

			for(int c=0; c<totalconf; c++)
			{

				//if(cplex.getValue(conf[c])>0)
				{

					for(int a=0; a<nplayer; a++)
					{
						if(cplex.getValue(b[c][a])>0)
						{
							System.out.println("c "+c+", attacker "+a+": "+ cplex.getValue(b[c][a]));
						}
					}
				}
			}

			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{
					res = new ArrayList<double[]>();

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

										double[] ar = {i,j, k, c, ob};
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



	public static ArrayList<ArrayList<double[]>> solveHPDeploymentMultAttackerCommPath(int[][][] w, int start, ArrayList<Integer> goals, int e,
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





			IloNumVar b[][] = new IloNumVar[totalconf][goals.size()];

			for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{
					b[c][a] = cplex.numVar(0, Double.MAX_VALUE);
				}

				//conf[c] = cplex.numVar(0, 1);
			}



			IloNumVar f[] = new IloNumVar[goals.size()];


			for(int a=0; a<nplayer; a++)
			{
				f[a] = cplex.numVar(0, Double.MAX_VALUE);
			}

			//conf[c] = cplex.numVar(0, 1);



			//IloNumVar v[] = cplex.numVar(0.0, totalconf-1);


			IloNumVar[] conf = new IloNumVar[totalconf];
			for(int c=0; c<totalconf; c++)
			{

				conf[c] = cplex.boolVar();

				//conf[c] = cplex.numVar(0, 1);
			}


			//IloNumVar conf = cplex.numVar(0, 1);







			IloLinearNumExpr obj = cplex.linearNumExpr();

			//for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{
					obj.addTerm(priors[a], f[a]);
				}


			}
			cplex.addMinimize(obj);






			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					expr1.addTerm(1, f[a]);
					expr1.addTerm(-1, b[c][a]);

				}

				expr1.addTerm(M, conf[c]);
				cplex.addLe(expr1, M);

			}


			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					//expr1.addTerm(1, f[a]);
					expr1.addTerm(1, b[c][a]);

				}

				//expr1.addTerm(M, conf[c]);
				cplex.addGe(expr1, 0);

			}



			for(int c=0; c<totalconf; c++)
			{
				IloLinearNumExpr expr1 = cplex.linearNumExpr();

				for(int a=0; a<nplayer; a++)
				{
					expr1.addTerm(1, f[a]);
					expr1.addTerm(-1, b[c][a]);

				}

				//expr1.addTerm(M, conf[c]);
				cplex.addGe(expr1, 0);

			}






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

					expr1.addTerm(-1, b[c][a]);

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

					expr1.addTerm(-1, b[c][a]);

					//expr1.addTerm(M, conf[c]);

					cplex.addGe(expr1, 0);
				}

			}




			for(int c1=0; c1<totalconf; c1++)
			{
				for(int c2=0; c2<totalconf; c2++)
				{

					if(c1!=c2)
					{

						IloLinearNumExpr expr1 = cplex.linearNumExpr();


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(priors[a], b[c1][a]);

						}


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(-priors[a], b[c2][a]);

						}


						expr1.addTerm(M, conf[c1]);

						expr1.addTerm(M, conf[c2]);


						cplex.addLe(expr1, 2*M);


					}
				}

			}


			for(int c1=0; c1<totalconf; c1++)
			{
				for(int c2=0; c2<totalconf; c2++)
				{

					if(c1!=c2)
					{

						IloLinearNumExpr expr1 = cplex.linearNumExpr();


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(priors[a], b[c1][a]);

						}


						for(int a=0; a<nplayer; a++)
						{

							expr1.addTerm(-priors[a], b[c2][a]);

						}


						//expr1.addTerm(M, conf[c1]);

						//expr1.addTerm(M, conf[c2]);


						cplex.addGe(expr1, 0);


					}
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

								cplex.addGe(expr1, 1.0);
							}
							else if(goals.get(a) == i)
							{

								cplex.addLe(expr1, -1.0);
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

			cplex.addGe(expr, 1.0);




			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());

			for(int c=0; c<totalconf; c++)
			{

				//if(cplex.getValue(conf[c])>0)
				{

					for(int a=0; a<nplayer; a++)
					{
						if(cplex.getValue(b[c][a])>0)
						{
							System.out.println("c "+c+", attacker "+a+": "+ cplex.getValue(b[c][a]));
						}
					}
				}
			}

			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{
					res = new ArrayList<double[]>();

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

										double[] ar = {i,j, k, c, ob};
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



	public static ArrayList<ArrayList<double[]>> solveDummy(int[][][] w, int start, ArrayList<Integer> goals, int e,
			int nattakers, int[][][][] hpdeploymentcosts, int totalconf, double[] priors) {



		int n= 5;

		int p[][] = new int[n][n];


		goals.clear();


		goals.add(4);
		goals.add(3);




		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				p[i][j] = 100;
			}
		}




		p[0][1] = 1;
		p[1][3] = 1;
		p[3][4] = 1;


		p[0][2] = 1;
		p[2][3] = 1;
		p[3][4] = 1;



		p[1][2] = 1;
		p[2][3] = 1;

		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				String key = i+","+j;

				edgeids.put(key, count);
				edgecost.put(count++, p[i][j]);
			}
		}



		double M = 1000.0;



		try
		{




			IloCplex cplex = new IloCplex();
			IloNumVar[][][] x = new IloNumVar[2][n][n];


			for(int a=0; a<2; a++)
			{

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{

						x[a][i][j] = cplex.boolVar();



					}
				}
			}


			IloNumVar[] y = new IloNumVar[2];


			for(int a=0; a<2; a++)
			{

				y[a] = cplex.boolVar();


			}






			IloNumVar k[] = new IloNumVar[2];

			for(int a=0; a<2; a++)
			{
				//for(int i=0; i<n; i++)
				{
					k[a] = 	cplex.numVar(0, Double.POSITIVE_INFINITY);
				}
			}


			IloNumVar b[] = new IloNumVar[2];

			for(int a=0; a<2; a++)
			{
				b[a] = 	cplex.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}




			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int a=0; a<2; a++)
			{
				obj.addTerm(1, k[a]);

			}

			cplex.addMaximize(obj);





			for(int a=0; a<2; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						expr.addTerm(p[i][j], x[a][i][j]);

					}
				}

				expr.addTerm(-1, b[a]);

				cplex.addGe(expr, 0);

			}

			for(int a=0; a<2; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						expr.addTerm(p[i][j], x[a][i][j]);

					}
				}

				expr.addTerm(-1, b[a]);

				cplex.addLe(expr, 0);

			}




			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{

						IloLinearNumExpr expr1 = cplex.linearNumExpr();

						for(int i=0; i<n; i++)
						{
							for(int j=0; j<n; j++)
							{
								expr1.addTerm(1, x[a1][i][j]);
								expr1.addTerm(1, x[a2][i][j]);

							}
						}

						expr1.addTerm(M, y[a1]);
						expr1.addTerm(-1, k[a1]);
						cplex.addLe(expr1, M);



					}
				}
			}

			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					//if(a1 != a2)
					{
						for(int len = 0; len<n; len++)
						{
							IloLinearNumExpr expr1 = cplex.linearNumExpr();

							for(int i=0; i<n; i++)
							{
								for(int j=0; j<n; j++)
								{
									expr1.addTerm(1, x[a1][i][j]);
									expr1.addTerm(1, x[a2][i][j]);

								}
							}

							expr1.addTerm(M, y[a1]);
							expr1.addTerm(-1, k[a1]);
							cplex.addGe(expr1, (2*(n*n-1))-len+M);


						}
					}
				}
			}


			for(int a=0; a<2; a++)
			{


				IloLinearNumExpr expr1 = cplex.linearNumExpr();
				//for(int i=0; i<n; i++)
				{
					expr1.addTerm(1, y[a]);

				}
				cplex.addEq(expr1, 1);
			}








			for(int a=0; a<2; a++)
			{

				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();


					for(int j=0; j<n; j++)
					{
						if(i!=j)
						{

							expr1.addTerm(1.0, x[a][i][j]);
							expr1.addTerm(-1.0, x[a][j][i]);

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




			for(int a=0; a<2; a++)
			{

				for(int i=0; i<n; i++)
				{

					for(int j=0; j<n; j++)
					{
						if(i!=j)
						{
							IloLinearNumExpr expr2 = cplex.linearNumExpr();


							expr2.addTerm(1.0, x[a][i][j]);


							cplex.addGe(expr2, 0.0);
						}

					}

				}
			}







			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			for(int a=0; a<2; a++)
			{


				if( cplex.getValue(k[a])>0)
				{
					System.out.println("player "+ a + " "+ cplex.getValue(b[a]));
				}

			}


			for(int a=0; a<2; a++)
			{
				res = new ArrayList<double[]>();


				for(int i=0; i<n; i++)
				{


					for(int j=0; j<n; j++)
					{

						if( i!=j && cplex.getValue(x[a][i][j])>0)
						{

							System.out.print("x["+i+"]["+j+"]="+cplex.getValue(x[a][i][j])+ " ");
							//cplex.getValue(x[i]);

							double[] ar = {i,j, ob};
							res.add(ar);


						}



					}
				}
				System.out.println();
			}
			if(res.size()>0)
			{
				results.add(res);
			}




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}


	public static ArrayList<ArrayList<double[]>> solveDummy2(int[][][][] w, int start, ArrayList<Integer> goals, int nexploits,
			int nattackers, int[][][][][] hpdeploymentcosts, int totalconf, double[] priors) {

		int[] mincost = {5, 5, 5};

		int n= 12;
		
		 nattackers = 3;

		//int L = 3;

		int p[][] = new int[n][n];

		int M = 5;


		goals.clear();


		goals.add(9);
		goals.add(10);
		goals.add(11);
		



		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				p[i][j] = 100;
			}
		}



		p[0][2] = 1;
		p[2][4] = 1;
		p[4][6] = 1;
		p[6][8] = 1;
		
		p[8][9] = 1;
		p[9][9] = 0;

		p[0][2] = 1;
		p[2][3] = 1;
		p[3][5] = 1;
		p[5][7] = 1;
		p[7][10] = 1;
		p[10][10] = 0;
		
		
		p[0][1] = 1;
		p[1][5] = 1;
		p[5][11] = 1;
		
		p[11][11] = 1;
		
		





		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		//HashMap<Integer, String> edgebackids = new HashMap<Integer, String>();
		//HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				//if(i != j)
				{

					String key = i+","+j;

					edgeids.put(key, count);
					//edgebackids.put(count, key);
					count++;
					//edgecost.put(count++, p[i][j]);
				}
			}
		}
		
		
		
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				//if(i != j)
				{

					String key = i+","+j;

					System.out.println("key "+ key+ ": "+ edgeids.get(key));
				}
			}
		}



		//double M = 1000.0;



		try
		{




			IloCplex cplex = new IloCplex();




			/**
			 * objective: max d 

			d: length of maximum overlapping path between any pair of attacker types
			d_ij: length of overlap between i and j 

			constraint: d >= d_ij  for all ij

			d_ijm: binary variable representing whether the paths of i and j overlap up to and including move m (note that this is on vector for each pair) 
			e_tm: binary variable representing whether or not type t takes this edge on turn m (one for each type, assuming we have a bound on # of moves) 

			constraint: d_ijm < e_tm for all m, ij (i.e., this can only be set to 1 if both types i and j make this move) 
			constraint: e_tm <= e_t(m-1) for all t,m  (i.e., once this is set to zero it must always be 0 going forward)  





			 */

			int Z = 10000;


			IloNumVar d = cplex.numVar(0, Double.MAX_VALUE);


			IloNumVar[][] d_ij = new IloNumVar[nattackers][nattackers];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					d_ij[a1][a2] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}


			

			IloNumVar[][][][] d_ijme = new IloNumVar[nattackers][nattackers][M][count];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					for(int i=0; i<M; i++)
					{
						for(int j=0; j<count; j++)
						{
							d_ijme[a1][a2][i][j] = cplex.boolVar();
						}
					}
				}
			}




			/*
			 * e_tm
			 */

			IloNumVar[][][] e_tem = new IloNumVar[nattackers][count][M];

			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<count; i++)
				{
					for(int j=0; j<M; j++)
					{
						e_tem[a][i][j] = cplex.boolVar();
					}
				}
			}





			/**
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * 
			 */


			/**
			 * obj max d
			 */


			IloLinearNumExpr obj = cplex.linearNumExpr();


			for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=a1+1; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						obj.addTerm(1, d_ij[a1][a2]);


					}
				}
			}


			cplex.addMaximize(obj);






			/**
			 * constraint 3
			 */


			IloLinearNumExpr ex = cplex.linearNumExpr();


			ex.addTerm(1, d);

			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						ex.addTerm(-1, d_ij[a1][a2]);
					}
				}
			}

			cplex.addEq(ex, 0);






			/**
			 * 
			 * constraint  4 dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij = sum_m  dijme 
			 *  
			 *  
			 */




			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						expr.addTerm(1, d_ij[a1][a2]);
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<count; i++)
							{
								expr.addTerm(-1, d_ijme[a1][a2][k][i]);
							}
						}


						cplex.addEq(expr, 0);


					}

				}
			}






			/**
			 * d_ijme <= e_tem
			 */

			for(int k=0; k<M; k++)
			{

				for(int a1=0; a1<nattackers; a1++)
				{
					for(int a2=0; a2<nattackers; a2++)
					{
						if(a1 != a2)
						{
							for(int i=0; i<count; i++)
							{

								IloLinearNumExpr expr2 = cplex.linearNumExpr();
								expr2.addTerm(1, d_ijme[a1][a2][k][i]);

								expr2.addTerm(-1, e_tem[a1][i][k]);

								cplex.addLe(expr2, 0);



								IloLinearNumExpr expr3 = cplex.linearNumExpr();
								expr3.addTerm(1, d_ijme[a1][a2][k][i]);

								expr3.addTerm(-1, e_tem[a2][i][k]);

								cplex.addLe(expr3, 0);

							}
						}
					}
				}
			}


			/**
			 * constraint: d_ijme <= d_ij(m-1)e for all t,m  (i.e., once this is set to zero it must always be 0 going forward)
			 */
			for(int a1=0; a1<nattackers; a1++)
			{

				for(int a2=0; a2<nattackers; a2++)
				{

					if(a1 != a2)
					{

						for(int m=1; m<M; m++)
						{

							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int i=0; i<count; i++)
							{
								expr.addTerm(1, d_ijme[a1][a2][m][i]);
								expr.addTerm(-1, d_ijme[a1][a2][m-1][i]);

							}
							cplex.addLe(expr, 0);



						}

					}

				}

			}





			
			/**
			 * for all type i
			 * 	  sum_em e_iem*w_iem <= mincost
			 *        
			 */



			for(int a=0; a<nattackers; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							String key = i+","+j;
							int id1 = edgeids.get(key);
							expr.addTerm(p[i][j], e_tem[a][id1][k]);
						}

					}

				}
				cplex.addLe(expr, mincost[a]);

			}
			
			
			
			
			
			

			for(int a=0; a<nattackers; a++)
			{


				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							String key = i+","+j;
							int id1 = edgeids.get(key);
							expr.addTerm(p[i][j], e_tem[a][id1][k]);
						}

					}
				}
				cplex.addGe(expr, 0);

			}



			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int k=0; k<(M); k++)
					{
						for(int j=0; j<n; j++)
						{
							String k1 = i+","+j;
							int id1 = edgeids.get(k1);
							expr1.addTerm(1.0, e_tem[a][id1][k]);
							
							
							String k2 = j+","+i;
							int id2 = edgeids.get(k2);
							expr1.addTerm(-1.0, e_tem[a][id2][k]);
							
						}

					}

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
			
			
			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{

					if(i != start && (i != goals.get(a) ))
					{
						for(int k=1; k<(M); k++)
						{
							IloLinearNumExpr expr1 = cplex.linearNumExpr();
							for(int j=0; j<n; j++)
							{
								String k1 = j+","+i;
								int id1 = edgeids.get(k1);
								expr1.addTerm(1.0, e_tem[a][id1][k-1]);
							}


							for(int j=0; j<n; j++)
							{
								String k2 = i+","+j;
								int id2 = edgeids.get(k2);
								expr1.addTerm(-1.0, e_tem[a][id2][k]);
							}
							
							cplex.addEq(expr1, 0);

						}

					}
					

				}

			}

			
			for(int a1=0; a1<nattackers; a1++)
			{
				for(int k=0; k<M; k++)
				{

					IloLinearNumExpr expr2 = cplex.linearNumExpr();

					for(int i=0; i<count; i++)
					{
						expr2.addTerm(1, e_tem[a1][i][k]);
					}
					cplex.addEq(expr2, 1);
				}

			}
			
			
			for(int a=0; a<nattackers; a++)
			{


				int k = 0;
				IloLinearNumExpr expr2 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{

						if(i==start)
						{
							String k1 = i+","+j;
							int id1 = edgeids.get(k1);
							expr2.addTerm(1.0, e_tem[a][id1][k]);
						}
					}
				}

				cplex.addEq(expr2, 1);

			}


			for(int a=0; a<nattackers; a++)
			{
				int k = M-1;

				
				IloLinearNumExpr expr2 = cplex.linearNumExpr();


				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{

						if(j==goals.get(a))
						{
							String k1 = i+","+j;
							int id1 = edgeids.get(k1);
							expr2.addTerm(1.0, e_tem[a][id1][k]);
						}
					}
				}

				cplex.addEq(expr2, 1);

			}





			
			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ ob+"\n");



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=a1+1; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						if(cplex.getValue(d_ij[a1][a2])>=0)
						{
							System.out.println("d_ij["+a1+"]["+a2+"] = "+cplex.getValue(d_ij[a1][a2]));
						}

					}
				}
			}

			System.out.println();


			

			System.out.println();


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						for(int m=0; m<M; m++)
						{
							for(int i=0; i<count; i++)
							{

								if(cplex.getValue(d_ijme[a1][a2][m][i])>0)
								{
									System.out.println("d_ijme["+a1+"]["+a2+"]["+m+"]["+i+"] = "+cplex.getValue(d_ijme[a1][a2][m][i]));
								}
							}
						}
						System.out.println();

					}
				}
			}



			System.out.println();




			for(int a=0; a<nattackers; a++)
			{

				for(int m=0; m<M; m++)
				{

					res = new ArrayList<double[]>();

					for(int i=0; i<count; i++)
					{

						if(cplex.getValue(e_tem[a][i][m])>0)
						{

							System.out.print("e_tem["+a+"]["+i+"]["+m+"]="+cplex.getValue(e_tem[a][i][m])+ " ");
							//cplex.getValue(x[i]);

							double[] ar = {i, ob};
							res.add(ar);


						}




					}
					System.out.println();
				}
				System.out.println();
			}
			
			System.out.println();
			
			for(int a=0; a<nattackers; a++)
			{

				System.out.println("a"+a+": ");
				for(int m=0; m<M; m++)
				{

					res = new ArrayList<double[]>();

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{

							String key = i + ","+ j;
							int id = edgeids.get(key);

							if(cplex.getValue(e_tem[a][id][m])>0)
							{



								System.out.print(i+"->"+j+"->");
								//cplex.getValue(x[i]);

								double[] ar = {i, ob};
								res.add(ar);

							}
						}

					}
					System.out.println();
					
				}
				System.out.println();
			}

			
			

			/*for(int a=0; a<2; a++)
			{

				for(int m=0; m<M; m++)
				{
					for(int i=0; i<n; i++)
					{

						res = new ArrayList<double[]>();

						for(int j=0; j<n; j++)
						{



							if(cplex.getValue(x_kijm[a][i][j][m])>0)
							{

								String k1 = i +","+j;
								int id1 =  edgeids.get(k1);
								//System.out.println("key: "+ k1);
								//System.out.println("["+ id1 + "]["+ m + "] = 1");


								System.out.print("x_kij["+a+"]["+i+"]["+j+"]["+m+"]="+cplex.getValue(x_kijm[a][i][j][m])+ " ");
								//cplex.getValue(x[i]);



							}
						}

					}
					System.out.println();
				}
				System.out.println();
			}
*/
			
			
			if(res.size()>0)
			{
				results.add(res);
			}




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	public static ArrayList<ArrayList<double[]>> attackerPolicyInItMILP(int[][][][] p, int start, ArrayList<Integer> goals, int nexploits,
			int nattackers, double[] priors, int[] mincost, int n, int chosenattacker, HashMap<Integer,Integer> atmap, HashMap<Integer,Integer> atmapback) {

		
		int M = PlanrecognitionExp.NSTEP;
		/**
		 * for every goal add self loop
		 */
		
		for(int a=0; a<atmap.size(); a++)
		{

			for(int g: goals)
			{
				p[a][g][g][0] = 0; 

			}
		}
		
		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		//HashMap<Integer, String> edgebackids = new HashMap<Integer, String>();
		//HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				for(int e=0; e<nexploits; e++)
				{

					String key = i+","+j+","+e;
					
					

					edgeids.put(key, count);
					
					//System.out.println("key "+ key+ ": "+ edgeids.get(key));
					//edgebackids.put(count, key);
					count++;
					//edgecost.put(count++, p[i][j]);
				}
			}
		}
		
		
		
		//double M = 1000.0;



		try
		{

			IloCplex cplex = new IloCplex();




			/**
			 * objective: max d 

			d: length of maximum overlapping path between any pair of attacker types
			d_ij: length of overlap between i and j 

			constraint: d >= d_ij  for all ij

			d_ijm: binary variable representing whether the paths of i and j overlap up to and including move m (note that this is on vector for each pair) 
			e_tm: binary variable representing whether or not type t takes this edge on turn m (one for each type, assuming we have a bound on # of moves) 

			constraint: d_ijm < e_tm for all m, ij (i.e., this can only be set to 1 if both types i and j make this move) 
			constraint: e_tm <= e_t(m-1) for all t,m  (i.e., once this is set to zero it must always be 0 going forward)  





			 */

			int Z = 10000;


			IloNumVar d = cplex.numVar(0, Double.MAX_VALUE);


			IloNumVar[][] d_ij = new IloNumVar[nattackers][nattackers];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					//if(a2 != chosenattacker)
					{
					
						d_ij[a1][a2] = cplex.numVar(0, Double.MAX_VALUE);
					}
				}
			}


			

			IloNumVar[][][][] d_ijme = new IloNumVar[nattackers][nattackers][M][count];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					for(int i=0; i<M; i++)
					{
						for(int j=0; j<count; j++)
						{
							d_ijme[a1][a2][i][j] = cplex.boolVar();
						}
					}
				}
			}




			/*
			 * e_tm
			 */

			IloNumVar[][][] e_tem = new IloNumVar[nattackers][count][M];

			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<count; i++)
				{
					for(int j=0; j<M; j++)
					{
						e_tem[a][i][j] = cplex.boolVar();
					}
				}
			}





			/**
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * 
			 */


			/**
			 * obj max d
			 */


			IloLinearNumExpr obj = cplex.linearNumExpr();


			//for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(chosenattacker != a2)
					{

						obj.addTerm(1, d_ij[chosenattacker][a2]);


					}
				}
			}


			cplex.addMaximize(obj);






			/**
			 * constraint 3
			 */


			IloLinearNumExpr ex = cplex.linearNumExpr();


			ex.addTerm(1, d);

			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						ex.addTerm(-1, d_ij[a1][a2]);
					}
				}
			}

			cplex.addEq(ex, 0);






			/**
			 * 
			 * constraint  4 dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij = sum_m  dijme 
			 *  
			 *  
			 */




			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						expr.addTerm(1, d_ij[a1][a2]);
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<count; i++)
							{
								expr.addTerm(-1, d_ijme[a1][a2][k][i]);
							}
						}


						cplex.addEq(expr, 0);


					}

				}
			}






			/**
			 * d_ijme <= e_tem
			 */

			for(int k=0; k<M; k++)
			{

				for(int a1=0; a1<nattackers; a1++)
				{
					for(int a2=0; a2<nattackers; a2++)
					{
						if(a1 != a2)
						{
							for(int i=0; i<count; i++)
							{

								IloLinearNumExpr expr2 = cplex.linearNumExpr();
								expr2.addTerm(1, d_ijme[a1][a2][k][i]);

								expr2.addTerm(-1, e_tem[a1][i][k]);

								cplex.addLe(expr2, 0);



								IloLinearNumExpr expr3 = cplex.linearNumExpr();
								expr3.addTerm(1, d_ijme[a1][a2][k][i]);

								expr3.addTerm(-1, e_tem[a2][i][k]);

								cplex.addLe(expr3, 0);

							}
						}
					}
				}
			}


			/**
			 * constraint: d_ijme <= d_ij(m-1)e for all t,m  (i.e., once this is set to zero it must always be 0 going forward)
			 */
			for(int a1=0; a1<nattackers; a1++)
			{

				for(int a2=0; a2<nattackers; a2++)
				{

					if(a1 != a2)
					{

						for(int m=1; m<M; m++)
						{

							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int i=0; i<count; i++)
							{
								expr.addTerm(1, d_ijme[a1][a2][m][i]);
								expr.addTerm(-1, d_ijme[a1][a2][m-1][i]);

							}
							cplex.addLe(expr, 0);



						}

					}

				}

			}





			
			/**
			 * for all type i
			 * 	  sum_em e_iem*w_iem <= mincost
			 *        
			 */



			for(int a=0; a<nattackers; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}

				}
				cplex.addLe(expr, mincost[a]);

			}
			
			
			
			
			
			

			for(int a=0; a<nattackers; a++)
			{


				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
							
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}
				}
				cplex.addGe(expr, 0);

			}



			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int k=0; k<(M); k++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr1.addTerm(1.0, e_tem[a][id1][k]);


								String k2 = j+","+i+","+e;
								int id2 = edgeids.get(k2);
								expr1.addTerm(-1.0, e_tem[a][id2][k]);
							}

						}

					}

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
			
			
			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{

					if(i != start && (i != goals.get(a) ))
					{
						for(int k=1; k<(M); k++)
						{
							IloLinearNumExpr expr1 = cplex.linearNumExpr();
							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k1 = j+","+i+","+e;
									int id1 = edgeids.get(k1);
									expr1.addTerm(1.0, e_tem[a][id1][k-1]);
								}
							}


							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k2 = i+","+j+","+e;
									int id2 = edgeids.get(k2);
									expr1.addTerm(-1.0, e_tem[a][id2][k]);
								}
							}
							
							cplex.addEq(expr1, 0);

						}

					}
					

				}

			}

			
			for(int a1=0; a1<nattackers; a1++)
			{
				for(int k=0; k<M; k++)
				{

					IloLinearNumExpr expr2 = cplex.linearNumExpr();

					for(int i=0; i<count; i++)
					{
						expr2.addTerm(1, e_tem[a1][i][k]);
					}
					cplex.addEq(expr2, 1);
				}

			}
			
			
			for(int a=0; a<nattackers; a++)
			{


				int k = 0;
				IloLinearNumExpr expr2 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{
							if(i==start)
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}


			for(int a=0; a<nattackers; a++)
			{
				int k = M-1;


				IloLinearNumExpr expr2 = cplex.linearNumExpr();


				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{

							if(j==goals.get(a))
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}





			
			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ ob+"\n");



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			//for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(chosenattacker != a2)
					{
						if(cplex.getValue(d_ij[chosenattacker][a2])>=0)
						{
							System.out.println("d_ij["+chosenattacker+"]["+a2+"] = "+cplex.getValue(d_ij[chosenattacker][a2]));
						}

					}
				}
			}

			System.out.println();


			

			System.out.println();


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						for(int m=0; m<M; m++)
						{
							for(int i=0; i<count; i++)
							{

								if(cplex.getValue(d_ijme[a1][a2][m][i])>0)
								{
									System.out.println("d_ijme["+a1+"]["+a2+"]["+m+"]["+i+"] = "+cplex.getValue(d_ijme[a1][a2][m][i]));
								}
							}
						}
						System.out.println();

					}
				}
			}



			System.out.println();




			for(int a=0; a<nattackers; a++)
			{

				for(int m=0; m<M; m++)
				{

					//res = new ArrayList<double[]>();

					for(int i=0; i<count; i++)
					{

						if(cplex.getValue(e_tem[a][i][m])>0)
						{

							System.out.print("e_tem["+a+"]["+i+"]["+m+"]="+cplex.getValue(e_tem[a][i][m])+ " ");
							//cplex.getValue(x[i]);

							//double[] ar = {i, ob};
							//res.add(ar);


						}




					}
					System.out.println();
				}
				System.out.println();
			}
			
			System.out.println();
			
			for(int a=0; a<nattackers; a++)
			{

				res = new ArrayList<double[]>();
				System.out.println("a"+a+": ");
				for(int m=0; m<M; m++)
				{

					//

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

							String key = i + ","+ j+","+e;
							int id = edgeids.get(key);

							if(cplex.getValue(e_tem[a][id][m])>0)
							{



								System.out.print(i+"->"+j+"("+e+")");
								//cplex.getValue(x[i]);

								double[] ar = {i, j, e};
								res.add(ar);

							}
							}
						}

					}
					System.out.println();
					
				}
				if(res.size()>0)
				{
					results.add(res);
				}
				System.out.println();
			}

			
			
			cplex.clearModel();
			cplex.end();
			
			
			
			




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	public static ArrayList<ArrayList<double[]>> solveMaxOvelapMILPV2(int[][][][] p, int start, ArrayList<Integer> goals, int nexploits,
			int nattackers, HashMap<Integer,Double> priorsattackertype, 
			HashMap<Integer,Integer> mincost, int n, HashMap<Integer,Attacker> attackers, HashMap<Integer,Integer> attackermap, HashMap<Integer,Integer> attackermapback) {

		
		
		int[] minc = new int[nattackers];
		
		
		int at = 0;
		
		for(Integer c: mincost.values())
		{
			minc[at++] = c;
		}
		
		
		int M = PlanrecognitionExp.NSTEP;
		/**
		 * for every goal add self loop
		 */
		
		for(int a: attackers.keySet())
		{

			for(int g: goals)
			{
				p[attackermap.get(a)][g][g][0] = 0; 

			}
		}
		
		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		//HashMap<Integer, String> edgebackids = new HashMap<Integer, String>();
		//HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				for(int e=0; e<nexploits; e++)
				{

					String key = i+","+j+","+e;
					
					

					edgeids.put(key, count);
					
					//System.out.println("key "+ key+ ": "+ edgeids.get(key));
					//edgebackids.put(count, key);
					count++;
					//edgecost.put(count++, p[i][j]);
				}
			}
		}
		
		
		
		//double M = 1000.0;



		try
		{

			IloCplex cplex = new IloCplex();




			/**
			 * objective: max d 

			d: length of maximum overlapping path between any pair of attacker types
			d_ij: length of overlap between i and j 

			constraint: d >= d_ij  for all ij

			d_ijm: binary variable representing whether the paths of i and j overlap up to and including move m (note that this is on vector for each pair) 
			e_tm: binary variable representing whether or not type t takes this edge on turn m (one for each type, assuming we have a bound on # of moves) 

			constraint: d_ijm < e_tm for all m, ij (i.e., this can only be set to 1 if both types i and j make this move) 
			constraint: e_tm <= e_t(m-1) for all t,m  (i.e., once this is set to zero it must always be 0 going forward)  





			 */

			int Z = 10000;


			IloNumVar d = cplex.numVar(0, Double.MAX_VALUE);


			IloNumVar[][] d_ij = new IloNumVar[nattackers][nattackers];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					d_ij[a1][a2] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}


			

			IloNumVar[][][][] d_ijme = new IloNumVar[nattackers][nattackers][M][count];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					for(int i=0; i<M; i++)
					{
						for(int j=0; j<count; j++)
						{
							d_ijme[a1][a2][i][j] = cplex.boolVar();
						}
					}
				}
			}




			/*
			 * e_tm
			 */

			IloNumVar[][][] e_tem = new IloNumVar[nattackers][count][M];

			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<count; i++)
				{
					for(int j=0; j<M; j++)
					{
						e_tem[a][i][j] = cplex.boolVar();
					}
				}
			}





			/**
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * 
			 */


			/**
			 * obj max d
			 */


			IloLinearNumExpr obj = cplex.linearNumExpr();


			for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=a1+1; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						obj.addTerm(1, d_ij[a1][a2]);


					}
				}
			}


			cplex.addMaximize(obj);






			/**
			 * constraint 3
			 */


			IloLinearNumExpr ex = cplex.linearNumExpr();


			ex.addTerm(1, d);

			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						ex.addTerm(-1, d_ij[a1][a2]);
					}
				}
			}

			cplex.addEq(ex, 0);






			/**
			 * 
			 * constraint  4 dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij = sum_m  dijme 
			 *  
			 *  
			 */




			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						expr.addTerm(1, d_ij[a1][a2]);
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<count; i++)
							{
								expr.addTerm(-1, d_ijme[a1][a2][k][i]);
							}
						}


						cplex.addEq(expr, 0);


					}

				}
			}






			/**
			 * d_ijme <= e_tem
			 */

			for(int k=0; k<M; k++)
			{

				for(int a1=0; a1<nattackers; a1++)
				{
					for(int a2=0; a2<nattackers; a2++)
					{
						if(a1 != a2)
						{
							for(int i=0; i<count; i++)
							{

								IloLinearNumExpr expr2 = cplex.linearNumExpr();
								expr2.addTerm(1, d_ijme[a1][a2][k][i]);

								expr2.addTerm(-1, e_tem[a1][i][k]);

								cplex.addLe(expr2, 0);



								IloLinearNumExpr expr3 = cplex.linearNumExpr();
								expr3.addTerm(1, d_ijme[a1][a2][k][i]);

								expr3.addTerm(-1, e_tem[a2][i][k]);

								cplex.addLe(expr3, 0);

							}
						}
					}
				}
			}


			/**
			 * constraint: d_ijme <= d_ij(m-1)e for all t,m  (i.e., once this is set to zero it must always be 0 going forward)
			 */
			for(int a1=0; a1<nattackers; a1++)
			{

				for(int a2=0; a2<nattackers; a2++)
				{

					if(a1 != a2)
					{

						for(int m=1; m<M; m++)
						{

							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int i=0; i<count; i++)
							{
								expr.addTerm(1, d_ijme[a1][a2][m][i]);
								expr.addTerm(-1, d_ijme[a1][a2][m-1][i]);

							}
							cplex.addLe(expr, 0);



						}

					}

				}

			}





			
			/**
			 * for all type i
			 * 	  sum_em e_iem*w_iem <= mincost
			 *        
			 */



			for(int a=0; a<nattackers; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}

				}
				cplex.addLe(expr, minc[a]);

			}
			
			
			
			
			
			

			for(int a=0; a<nattackers; a++)
			{


				IloLinearNumExpr expr = cplex.linearNumExpr();

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
							
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}
				}
				cplex.addGe(expr, 0);

			}



			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int k=0; k<(M); k++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr1.addTerm(1.0, e_tem[a][id1][k]);


								String k2 = j+","+i+","+e;
								int id2 = edgeids.get(k2);
								expr1.addTerm(-1.0, e_tem[a][id2][k]);
							}

						}

					}

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
			
			
			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{

					if(i != start && (i != goals.get(a) ))
					{
						for(int k=1; k<(M); k++)
						{
							IloLinearNumExpr expr1 = cplex.linearNumExpr();
							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k1 = j+","+i+","+e;
									int id1 = edgeids.get(k1);
									expr1.addTerm(1.0, e_tem[a][id1][k-1]);
								}
							}


							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k2 = i+","+j+","+e;
									int id2 = edgeids.get(k2);
									expr1.addTerm(-1.0, e_tem[a][id2][k]);
								}
							}
							
							cplex.addEq(expr1, 0);

						}

					}
					

				}

			}

			
			for(int a1=0; a1<nattackers; a1++)
			{
				for(int k=0; k<M; k++)
				{

					IloLinearNumExpr expr2 = cplex.linearNumExpr();

					for(int i=0; i<count; i++)
					{
						expr2.addTerm(1, e_tem[a1][i][k]);
					}
					cplex.addEq(expr2, 1);
				}

			}
			
			
			for(int a=0; a<nattackers; a++)
			{


				int k = 0;
				IloLinearNumExpr expr2 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{
							if(i==start)
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}


			for(int a=0; a<nattackers; a++)
			{
				int k = M-1;


				IloLinearNumExpr expr2 = cplex.linearNumExpr();


				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{

							if(j==goals.get(a))
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}





			
			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ ob+"\n");



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			/*for(int a1=0; a1<nattackers-1; a1++)
			{
				for(int a2=a1+1; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						if(cplex.getValue(d_ij[a1][a2])>=0)
						{
							System.out.println("d_ij["+a1+"]["+a2+"] = "+cplex.getValue(d_ij[a1][a2]));
						}

					}
				}
			}

			System.out.println();


			

			System.out.println();
*/

			/*for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						for(int m=0; m<M; m++)
						{
							for(int i=0; i<count; i++)
							{

								if(cplex.getValue(d_ijme[a1][a2][m][i])>0)
								{
									System.out.println("d_ijme["+a1+"]["+a2+"]["+m+"]["+i+"] = "+cplex.getValue(d_ijme[a1][a2][m][i]));
								}
							}
						}
						System.out.println();

					}
				}
			}



			System.out.println();
*/



			/*for(int a=0; a<nattackers; a++)
			{

				for(int m=0; m<M; m++)
				{

					//res = new ArrayList<double[]>();

					for(int i=0; i<count; i++)
					{

						if(cplex.getValue(e_tem[a][i][m])>0)
						{

							System.out.print("e_tem["+a+"]["+i+"]["+m+"]="+cplex.getValue(e_tem[a][i][m])+ " ");
							//cplex.getValue(x[i]);

							//double[] ar = {i, ob};
							//res.add(ar);


						}




					}
					System.out.println();
				}
				System.out.println();
			}
			
			System.out.println();*/
			
			for(int a=0; a<nattackers; a++)
			{

				res = new ArrayList<double[]>();
				//System.out.println("a"+a+": ");
				for(int m=0; m<M; m++)
				{

					//

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

							String key = i + ","+ j+","+e;
							int id = edgeids.get(key);

							if(cplex.getValue(e_tem[a][id][m])>0)
							{



								//System.out.print(i+"->"+j+"("+e+")");
								//cplex.getValue(x[i]);

								double[] ar = {i, j, e, ob};
								res.add(ar);

							}
							}
						}

					}
					//System.out.println();
					
				}
				if(res.size()>0)
				{
					results.add(res);
				}
				//System.out.println();
			}

			
			

			
			
			
			cplex.clearModel();
			cplex.end();




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	public static ArrayList<ArrayList<double[]>> solveMaxExpOvelapMILPV2(int[][][][] p, int start, ArrayList<Integer> goals, int nexploits,
			int nattackers, HashMap<Integer,Double> priorsattackertype, HashMap<Integer,Integer> mincost, int n, HashMap<Integer,Integer> atmap,
			HashMap<Integer,Integer> atmapback, double[][] overlap, HashMap<Integer,Integer> attackermap, HashMap<Integer,Integer> attackermapback, HashMap<Integer,Attacker> attackers) {

		
		
		int[] minc = new int[nattackers];
		
		
		int at = 0;
		
		for(Integer a: mincost.keySet())
		{
			
			minc[atmap.get(a)] = mincost.get(a);
		}
		
		
		int M = 6;
		/**
		 * for every goal add self loop
		 */
		
		for(int a=0; a<nattackers; a++)
		{
			for(int g: goals)
			{
				p[a][g][g][0] = 0; 

			}
		}
		
		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		//HashMap<Integer, String> edgebackids = new HashMap<Integer, String>();
		//HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				for(int e=0; e<nexploits; e++)
				{

					String key = i+","+j+","+e;
					
					

					edgeids.put(key, count);
					
					//System.out.println("key "+ key+ ": "+ edgeids.get(key));
					//edgebackids.put(count, key);
					count++;
					//edgecost.put(count++, p[i][j]);
				}
			}
		}
		
		
		
		//double M = 1000.0;



		try
		{

			IloCplex cplex = new IloCplex();




			/**
			 * objective: max d 

			d: length of maximum overlapping path between any pair of attacker types
			d_ij: length of overlap between i and j 

			constraint: d >= d_ij  for all ij

			d_ijm: binary variable representing whether the paths of i and j overlap up to and including move m (note that this is on vector for each pair) 
			e_tm: binary variable representing whether or not type t takes this edge on turn m (one for each type, assuming we have a bound on # of moves) 

			constraint: d_ijm < e_tm for all m, ij (i.e., this can only be set to 1 if both types i and j make this move) 
			constraint: e_tm <= e_t(m-1) for all t,m  (i.e., once this is set to zero it must always be 0 going forward)  





			 */

			int Z = 10000;


			IloNumVar d = cplex.numVar(0, Double.MAX_VALUE);


			IloNumVar[][] d_ij = new IloNumVar[nattackers][nattackers];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					d_ij[a1][a2] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}


			

			IloNumVar[][][][] d_ijme = new IloNumVar[nattackers][nattackers][M][count];


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					for(int i=0; i<M; i++)
					{
						for(int j=0; j<count; j++)
						{
							d_ijme[a1][a2][i][j] = cplex.boolVar();
						}
					}
				}
			}




			/*
			 * e_tm
			 */

			IloNumVar[][][] e_tem = new IloNumVar[nattackers][count][M];

			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<count; i++)
				{
					for(int j=0; j<M; j++)
					{
						e_tem[a][i][j] = cplex.boolVar();
					}
				}
			}





			/**
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * 
			 */


			/**
			 * obj max d
			 */


			IloLinearNumExpr obj = cplex.linearNumExpr();


			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						obj.addTerm(1, d_ij[a1][a2]);
					}
				}
			}


			cplex.addMaximize(obj);






			/**
			 * constraint 3
			 */


			IloLinearNumExpr ex = cplex.linearNumExpr();


			ex.addTerm(1, d);

			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						ex.addTerm(-1, d_ij[a1][a2]);
					}
				}
			}

			cplex.addEq(ex, 0);






			/**
			 * 
			 * constraint  4 dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij = sum_m  dijme 
			 *  
			 *  
			 */




			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						expr.addTerm(1, d_ij[a1][a2]);
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<count; i++)
							{
								expr.addTerm(-1, d_ijme[a1][a2][k][i]);
							}
						}


						cplex.addEq(expr, 0);


					}

				}
			}






			/**
			 * d_ijme <= e_tem
			 */

			for(int k=0; k<M; k++)
			{

				for(int a1=0; a1<nattackers; a1++)
				{
					for(int a2=0; a2<nattackers; a2++)
					{
						if(a1 != a2)
						{
							for(int i=0; i<count; i++)
							{

								IloLinearNumExpr expr2 = cplex.linearNumExpr();
								expr2.addTerm(1, d_ijme[a1][a2][k][i]);

								expr2.addTerm(-1, e_tem[a1][i][k]);

								cplex.addLe(expr2, 0);



								IloLinearNumExpr expr3 = cplex.linearNumExpr();
								expr3.addTerm(1, d_ijme[a1][a2][k][i]);

								expr3.addTerm(-1, e_tem[a2][i][k]);

								cplex.addLe(expr3, 0);

							}
						}
					}
				}
			}


			/**
			 * constraint: d_ijme <= d_ij(m-1)e for all t,m  (i.e., once this is set to zero it must always be 0 going forward)
			 */
			for(int a1=0; a1<nattackers; a1++)
			{

				for(int a2=0; a2<nattackers; a2++)
				{

					if(a1 != a2)
					{

						for(int m=1; m<M; m++)
						{

							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int i=0; i<count; i++)
							{
								expr.addTerm(1, d_ijme[a1][a2][m][i]);
								expr.addTerm(-1, d_ijme[a1][a2][m-1][i]);

							}
							cplex.addLe(expr, 0);



						}

					}

				}

			}





			
			/**
			 * for all type i
			 * 	  sum_em e_iem*w_iem <= mincost
			 *        
			 */



			for(int a=0; a<nattackers; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();
				
				//int atindex = attackermap.get(a);

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}

				}
				cplex.addLe(expr, minc[a]);

			}
			
			
			
			
			
			

			for(int a=0; a<nattackers; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();
				
				//int atindex = attackermap.get(a);

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							for(int e=0; e<nexploits; e++)
							{
							
								String key = i+","+j+","+e;
								int id1 = edgeids.get(key);
								expr.addTerm(p[a][i][j][e], e_tem[a][id1][k]);
							}
						}

					}
				}
				cplex.addGe(expr, 0);

			}



			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					for(int k=0; k<(M); k++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr1.addTerm(1.0, e_tem[a][id1][k]);


								String k2 = j+","+i+","+e;
								int id2 = edgeids.get(k2);
								expr1.addTerm(-1.0, e_tem[a][id2][k]);
							}

						}

					}

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
			
			
			for(int a=0; a<nattackers; a++)
			{
				for(int i=0; i<n; i++)
				{

					if(i != start && (i != goals.get(a) ))
					{
						for(int k=1; k<(M); k++)
						{
							IloLinearNumExpr expr1 = cplex.linearNumExpr();
							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k1 = j+","+i+","+e;
									int id1 = edgeids.get(k1);
									expr1.addTerm(1.0, e_tem[a][id1][k-1]);
								}
							}


							for(int j=0; j<n; j++)
							{
								for(int e=0; e<nexploits; e++)
								{
									String k2 = i+","+j+","+e;
									int id2 = edgeids.get(k2);
									expr1.addTerm(-1.0, e_tem[a][id2][k]);
								}
							}
							
							cplex.addEq(expr1, 0);

						}

					}
					

				}

			}

			
			for(int a1=0; a1<nattackers; a1++)
			{
				for(int k=0; k<M; k++)
				{

					IloLinearNumExpr expr2 = cplex.linearNumExpr();

					for(int i=0; i<count; i++)
					{
						expr2.addTerm(1, e_tem[a1][i][k]);
					}
					cplex.addEq(expr2, 1);
				}

			}
			
			
			for(int a=0; a<nattackers; a++)
			{


				int k = 0;
				IloLinearNumExpr expr2 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{
							if(i==start)
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}


			for(int a=0; a<nattackers; a++)
			{
				int k = M-1;


				IloLinearNumExpr expr2 = cplex.linearNumExpr();


				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int e=0; e<nexploits; e++)
						{

							if(j==goals.get(a))
							{
								String k1 = i+","+j+","+e;
								int id1 = edgeids.get(k1);
								expr2.addTerm(1.0, e_tem[a][id1][k]);
							}
						}
					}
				}

				cplex.addEq(expr2, 1);

			}





			
			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ ob+"\n");



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{
						if(cplex.getValue(d_ij[a1][a2])>=0)
						{
							//System.out.println("d_ij["+a1+"]["+a2+"] = "+cplex.getValue(d_ij[a1][a2]));
							
							overlap[a1][a2] = cplex.getValue(d_ij[a1][a2]);
							
						}

					}
				}
			}

		//	System.out.println();


			

			//System.out.println();


			/*for(int a1=0; a1<nattackers; a1++)
			{
				for(int a2=0; a2<nattackers; a2++)
				{
					if(a1 != a2)
					{

						for(int m=0; m<M; m++)
						{
							for(int i=0; i<count; i++)
							{

								if(cplex.getValue(d_ijme[a1][a2][m][i])>0)
								{
									System.out.println("d_ijme["+a1+"]["+a2+"]["+m+"]["+i+"] = "+cplex.getValue(d_ijme[a1][a2][m][i]));
								}
							}
						}
						System.out.println();

					}
				}
			}



			System.out.println();
*/



			/*for(int a=0; a<nattackers; a++)
			{

				for(int m=0; m<M; m++)
				{

					//res = new ArrayList<double[]>();

					for(int i=0; i<count; i++)
					{

						if(cplex.getValue(e_tem[a][i][m])>0)
						{

							System.out.print("e_tem["+a+"]["+i+"]["+m+"]="+cplex.getValue(e_tem[a][i][m])+ " ");
							//cplex.getValue(x[i]);

							//double[] ar = {i, ob};
							//res.add(ar);


						}




					}
					System.out.println();
				}
				System.out.println();
			}
			
			System.out.println();*/
			
			for(int a=0; a<nattackers; a++)
			{

				res = new ArrayList<double[]>();
				//System.out.println("a"+a+": ");
				for(int m=0; m<M; m++)
				{

					//

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							
							for(int e=0; e<nexploits; e++)
							{

							String key = i + ","+ j+","+e;
							int id = edgeids.get(key);

							if(cplex.getValue(e_tem[a][id][m])>0)
							{



								//System.out.print(i+"->"+j+"("+e+")");
								//cplex.getValue(x[i]);

								double[] ar = {i, j, e, ob};
								res.add(ar);

							}
							}
						}

					}
					//System.out.println();
					
				}
				if(res.size()>0)
				{
					results.add(res);
				}
				//System.out.println();
			}

			
			

			
			cplex.clearModel();
			cplex.end();

			
			




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}
	
	
	
	
	public static ArrayList<ArrayList<double[]>> solveDummy3(int[][][] w, int start, ArrayList<Integer> goals, int e,
			int nattakers, int[][][][] hpdeploymentcosts, int totalconf, double[] priors) {

		int[] mincost = {5, 5};

		int n= 11;

		//int L = 3;

		int p[][] = new int[n][n];

		int M = 5;


		goals.clear();


		goals.add(10);
		goals.add(9);




		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				p[i][j] = 100;
			}
		}




		p[0][2] = 1;
		p[2][4] = 1;
		p[4][6] = 1;
		p[6][8] = 1;
		p[8][10] = 1;
		p[10][10] = 0;


		/*p[0][1] = 1;
		p[1][3] = 1;
		p[3][4] = 1;*/


		p[0][1] = 1;
		p[1][3] = 1;
		p[3][5] = 1;
		p[5][7] = 1;
		p[7][9] = 1;
		p[9][9] = 0;





		HashMap<String, Integer> edgeids = new HashMap<String, Integer>();
		//HashMap<Integer, String> edgebackids = new HashMap<Integer, String>();
		//HashMap<Integer, Integer> edgecost = new HashMap<Integer, Integer>();

		int count = 0;

		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				//if(i != j)
				{

					String key = i+","+j;

					edgeids.put(key, count);
					//edgebackids.put(count, key);
					count++;
					//edgecost.put(count++, p[i][j]);
				}
			}
		}
		
		
		
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				//if(i != j)
				{

					String key = i+","+j;

					System.out.println("key "+ key+ ": "+ edgeids.get(key));
				}
			}
		}



		//double M = 1000.0;



		try
		{




			IloCplex cplex = new IloCplex();




			/**
			 * objective: max d 

			d: length of maximum overlapping path between any pair of attacker types
			d_ij: length of overlap between i and j 

			constraint: d >= d_ij  for all ij

			d_ijm: binary variable representing whether the paths of i and j overlap up to and including move m (note that this is on vector for each pair) 
			e_tm: binary variable representing whether or not type t takes this edge on turn m (one for each type, assuming we have a bound on # of moves) 

			constraint: d_ijm < e_tm for all m, ij (i.e., this can only be set to 1 if both types i and j make this move) 
			constraint: e_tm <= e_t(m-1) for all t,m  (i.e., once this is set to zero it must always be 0 going forward)  





			 */

			int Z = 10000;


			IloNumVar d = cplex.numVar(0, Double.MAX_VALUE);


			IloNumVar[][] d_ij = new IloNumVar[2][2];


			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					d_ij[a1][a2] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}


			IloNumVar[][][][] x_kijm = new IloNumVar[2][n][n][M];


			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<n; a2++)
				{
					for(int a3=0; a3<n; a3++)
					{
						for(int a4=0; a4<M; a4++)
						{
							x_kijm[a1][a2][a3][a4] = cplex.boolVar();
						}
					}
				}
			}



			
			IloNumVar[][][][][] d_ijmuv = new IloNumVar[2][2][M][n][n];


			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					for(int m=0; m<M; m++)
					{

						for(int i=0; i<n; i++)
						{

							for(int j=0; j<n; j++)
							{
								d_ijmuv[a1][a2][m][i][j] = cplex.boolVar();
							}
						}
					}
				}
			}










			/*


			IloNumVar b[] = new IloNumVar[2];

			for(int a=0; a<2; a++)
			{
				b[a] = 	cplex.numVar(-1000, 1000);
			}
			 */



			/**
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * 
			 */


			/**
			 * obj max d
			 */


			IloLinearNumExpr obj = cplex.linearNumExpr();


			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{

						obj.addTerm(1, d_ij[a1][a2]);


					}
				}
			}


			cplex.addMaximize(obj);






			/**
			 * constraint 3
			 */


			IloLinearNumExpr ex = cplex.linearNumExpr();


			ex.addTerm(1, d);

			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{

						ex.addTerm(-1, d_ij[a1][a2]);



					}
				}
			}

			cplex.addEq(ex, 0);






			/**
			 * 
			 * constraint  4 dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij = sum_m  dijme 
			 *  
			 *  
			 */




			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						expr.addTerm(1, d_ij[a1][a2]);
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<n; i++)
							{
								for(int j=0; j<n; j++)
								{
									expr.addTerm(-1, d_ijmuv[a1][a2][k][i][j]);
								}
							}
						}


						cplex.addEq(expr, 0);


					}

				}
			}





			/**
			 * 
			 * 
			 * d: length of maximum overlapping path between any pair of attacker types
			 * d_ij: length of overlap between i and j 
			 * d_ijm: binary variable representing whether the paths of i and j overlap up to and 
			 * including move m (note that this is on vector for each pair) 
			 * e_tem: binary variable representing whether or not type t takes edge e on 
			 * turn m (one for each type, assuming we have a bound on # of moves) 
			 * 
			 * 
			 * obj max d
			 * 
			 * 
			 * constraint: d >= d_ij  for all ij
			 * 
			 * 
			 * for all i
			 * 	for all j 
			 * 		d >= d_ij	
			 * 
			 * 
			 * 
			 * constraint: dij >= sum_m  dijm for all ij 
			 * 
			 * The idea is that the vector d_ijm is 1 only for the initial edges (moves) that 
			 * are the same for types i and j. So the length of the overlap is the the sum of this vector. 
			 * 
			 *  
			 *  for all i
			 *  	for all j
			 *  		dij >= sum_m  dijm 
			 * 
			 * 
			 * 
			 * 
			 * constraint: d_ijm < e_tm for all m, ij
			 * (i.e., this can only be set to 1 if both types i and j make this move)
			 * 
			 * 
			 * y = x1 AND x2 can be written as the following constraints
			 * 
			 * 
			 * y >= x1 + x2 -1 
			 * y <= x1
			 * y <= x2
			 * 0 <= y <= 1
			 * 
			 * 
			 * So wrote the constraints as 
			 * 
			 * constraint d_ijm >= e_tem + e_tem - 1
			 * 
			 * 
			 * e is the edge identifier
			 * 
			 * for all m
			 *   for all i
			 *      for all j
			 *         for all e
			 *             d_ijm >= e_tem + e_t'em - 1
			 * 
			 * 
			 * 
			 * constraint d_ijm <= e_tem
			 * 
			 * for all m
			 *   for all i
			 *      for all j
			 *         for all e
			 *             d_ijm <= e_tem
			 * 
			 * 
			 * constraint 0 <= d_ijm <= 1
			 * 
			 * 
			 * d_ijm <= 1
			 * 
			 * for all i
			 * 	 for all j
			 *      for all m
			 *           d_ijm <= 1
			 *           
			 *           
			 *for all i
			 * 	 for all j
			 *      for all m
			 *           d_ijm >= 0     
			 *           
			 *                 
			 *     
			 *                 
			 *constraint: d_ijm <= d_ij(m-1) for all t,e,m  (i.e., once this is set to zero it must always be 0 going forward)                 
			 *                 
			 * for all i
			 * 	 for all e
			 *      for all m   
			 *          e_iem <= e_ie(m-1)    
			 *          
			 *                   
			 *constraint 
			 *
			 *for all type i
			 *  sum_em e_iem*w_iem <= mincost  
			 *  
			 *                   
			 * and other path flow constraints                  
			 *           
			 */






			//constraint 5

			/*	for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{
						for(int k=0; k<M; k++)
						{
							for(int i=0; i<count; i++)
							{

								IloLinearNumExpr expr = cplex.linearNumExpr();


								expr.addTerm(1, d_ijme[a1][a2][k][i]);

								expr.addTerm(-1, e_tem[a1][i][k]);

								expr.addTerm(-1, e_tem[a2][i][k]);

								cplex.addGe(expr, -1);

							}

						}
					}
				}
			}

			 */




			/**
			 * d_ijme <= e_tem
			 */

			for(int k=0; k<M; k++)
			{

				for(int a1=0; a1<2; a1++)
				{
					for(int a2=0; a2<2; a2++)
					{
						if(a1 != a2)
						{
							for(int i=0; i<n; i++)
							{

								for(int j=0; j<n; j++)
								{



									IloLinearNumExpr expr2 = cplex.linearNumExpr();
									expr2.addTerm(1, d_ijmuv[a1][a2][k][i][j]);

									expr2.addTerm(-1, x_kijm[a1][i][j][k]);

									//cplex.addLe(expr2, 0);



									IloLinearNumExpr expr3 = cplex.linearNumExpr();
									expr3.addTerm(1, d_ijmuv[a1][a2][k][i][j]);

									expr3.addTerm(-1, x_kijm[a2][i][j][k]);

									//cplex.addLe(expr3, 0);


								}





							}



						}
					}
				}
			}











			/**
			 * d_ijm >= 0
			 * 
			 * for all i
			 * 	 for all j
			 *      for all m
			 *           d_ijm >= 0
			 * 
			 */

			



			/**
			 * constraint: d_ijme <= d_ij(m-1)e for all t,m  (i.e., once this is set to zero it must always be 0 going forward)
			 */
			for(int a1=0; a1<2; a1++)
			{

				for(int a2=0; a2<2; a2++)
				{

					if(a1 != a2)
					{

						for(int m=1; m<M; m++)
						{

							IloLinearNumExpr expr = cplex.linearNumExpr();
							for(int i=0; i<n; i++)
							{

								for(int j=0; j<n; j++)
								{


									expr.addTerm(1, d_ijmuv[a1][a2][m][i][j]);
									expr.addTerm(-1, d_ijmuv[a1][a2][m-1][i][j]);

								}

							}
							cplex.addLe(expr, 0);



						}

					}

				}

			}





			



			/**
			 * manually set up the e_tem variable
			 */


			/*for(int a=0; a<2; a++)
			{

				for(int k=0; k<M; k++)
				{

					for(int i=0; i<count; i++)
					{
						IloLinearNumExpr expr = cplex.linearNumExpr();

						if(a==0 && k==0 && i==1)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);

						}
						else if(a==0 && k==1 && i==8)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);
						}
						else if(a==0 && k==2 && i==16)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);
						}
						else if(a==1 && k==0 && i==1)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);

						}
						else if(a==1 && k==1 && i==8)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);
						}
						else if(a==1 && k==2 && i==17)
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 1);
						}
						else
						{
							expr.addTerm(1, e_tem[a][i][k]);
							cplex.addEq(expr, 0);
						}






					}
				}
			}
			 */


			/**
			 * for all type i
			 * 	  sum_em e_iem*w_iem <= mincost
			 *        
			 */



			
			
			for(int a=0; a<2; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						
						for(int m=0; m<M; m++)
						{
						
							expr.addTerm(p[i][j], x_kijm[a][i][j][m]);
							
						}
					}

				}


				cplex.addLe(expr, mincost[a]);

			}
			
			
			for(int a=0; a<2; a++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{
						for(int m=0; m<M; m++)
						{
						
						expr.addTerm(p[i][j], x_kijm[a][i][j][m]);
						}
					}

				}


				cplex.addGe(expr,0);

			}

			
			
			

			
			
			
			
			
			
			
			

			
			
			
			
			
			
			// player a
			for(int a=0; a<2; a++)
			{
				// node i
				for(int i=0; i<n; i++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();
					// step m1
					for(int m1=0; m1<M; m1++)
					{
						// ndoe j
						for(int j=0; j<n; j++)
						{
							expr1.addTerm(1.0, x_kijm[a][i][j][m1]);

						}
					}

					for(int m2=0; m2<M; m2++)
					{
						// ndoe j
						for(int j=0; j<n; j++)
						{
							expr1.addTerm(-1.0, x_kijm[a][j][i][m2]);

						}

					}


					if(start == i )
					{
						cplex.addEq(expr1, 1.0);
					}
					else if(goals.get(a) == i )
					{
						cplex.addEq(expr1, -1.0);
					}
					else 
					{
						cplex.addEq(expr1, 0.0);
					}



				}
			}
			
			
			for(int a=0; a<2; a++)
			{
				for(int m=0; m<M; m++)
				{
					IloLinearNumExpr expr1 = cplex.linearNumExpr();

					for(int i=0; i<n; i++)
					{
						for(int j=0; j<n; j++)
						{
							expr1.addTerm(1.0, x_kijm[a][i][j][m]);
						}
					}
					cplex.addEq(expr1, 1);
				}
			}
			
			
			
			for(int a=0; a<2; a++)
			{


				int k = 0;
				IloLinearNumExpr expr2 = cplex.linearNumExpr();

				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{

						if(i==start)
						{
							
							expr2.addTerm(1.0, x_kijm[a][i][j][k]);
						}
					}
				}

				cplex.addEq(expr2, 1);

			}


			for(int a=0; a<2; a++)
			{
				int k = M-1;
				
				IloLinearNumExpr expr2 = cplex.linearNumExpr();
				for(int i=0; i<n; i++)
				{
					for(int j=0; j<n; j++)
					{

						if(j==goals.get(a))
						{
							
							expr2.addTerm(1.0, x_kijm[a][i][j][k]);
						}
					}
				}

				cplex.addEq(expr2, 1);

			}
			
			
			

			







			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ ob+"\n");



			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();



			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{
						if(cplex.getValue(d_ij[a1][a2])>=0)
						{
							System.out.println("d_ij["+a1+"]["+a2+"] = "+cplex.getValue(d_ij[a1][a2]));
						}

					}
				}
			}

			System.out.println();


			

			System.out.println();


			for(int a1=0; a1<2; a1++)
			{
				for(int a2=0; a2<2; a2++)
				{
					if(a1 != a2)
					{

						for(int m=0; m<M; m++)
						{
							for(int i=0; i<n; i++)
							{

								for(int j=0; j<n; j++)
								{

									if(cplex.getValue(d_ijmuv[a1][a2][m][i][j])>0)
									{
										System.out.println("d_ijmuv["+a1+"]["+a2+"]["+m+"]["+i+"]["+j+"] = "+cplex.getValue(d_ijmuv[a1][a2][m][i][j]));
									}
								}
							}
							System.out.println();

						}
					}
				}
			}



			System.out.println();




			
			

			for(int a=0; a<2; a++)
			{

				for(int m=0; m<M; m++)
				{
					for(int i=0; i<n; i++)
					{

						res = new ArrayList<double[]>();

						for(int j=0; j<n; j++)
						{



							if(cplex.getValue(x_kijm[a][i][j][m])>0)
							{

								String k1 = i +","+j;
								int id1 =  edgeids.get(k1);
								//System.out.println("key: "+ k1);
								//System.out.println("["+ id1 + "]["+ m + "] = 1");


								System.out.print("x_kij["+a+"]["+i+"]["+j+"]["+m+"]="+cplex.getValue(x_kijm[a][i][j][m])+ " ");
								//cplex.getValue(x[i]);



							}
						}

					}
					System.out.println();
				}
				System.out.println();
			}

			
			
			if(res.size()>0)
			{
				results.add(res);
			}




			return results;








		}
		catch(Exception ex)
		{

		}



		return null;


	}





	public static ArrayList<ArrayList<double[]>> solveHPDeploymentMultAttackerWorstCase(int[][][] w, int start, ArrayList<Integer> goals, int e,
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


			IloNumVar[][][][][] y = new IloNumVar[totalconf][nplayer][n][n][e];

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
								y[c][a][i][j][k] = cplex.boolVar();
							}


						}
					}
				}
			}





			IloNumVar b[][] = new IloNumVar[totalconf][goals.size()];

			for(int c=0; c<totalconf; c++)
			{
				for(int a=0; a<nplayer; a++)
				{
					b[c][a] = cplex.numVar(Double.MIN_VALUE, Double.MAX_VALUE);
				}

				//conf[c] = cplex.numVar(0, 1);
			}


			IloNumVar z[] = new IloNumVar[goals.size()];


			for(int a=0; a<nplayer; a++)
			{
				z[a] = cplex.numVar(Double.MIN_VALUE, Double.MAX_VALUE);
			}

			//conf[c] = cplex.numVar(0, 1);



			double epsln = 0.0;


			//IloNumVar v[] = cplex.numVar(0.0, totalconf-1);


			IloNumVar[] conf = new IloNumVar[totalconf];
			for(int c=0; c<totalconf; c++)
			{
				conf[c] = cplex.boolVar();

				//conf[c] = cplex.numVar(0, 1);
			}


			//IloNumVar conf = cplex.numVar(0, 1);







			IloLinearNumExpr obj = cplex.linearNumExpr();



			for(int a=0; a<nplayer; a++)
			{
				obj.addTerm(priors[a], z[a]);
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

					expr1.addTerm(-1, b[c][a]);

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

					expr1.addTerm(-1, b[c][a]);

					cplex.addGe(expr1, 0);
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
								expr1.addTerm(hpdeploymentcosts[c][i][j][k], y[c][a][i][j][k]);
								//expr1.addTerm(1, conf[c], x[c][i][j][k]);
							}


						}
					}

					expr1.addTerm(-1, b[c][a]);


					expr1.addTerm(M, conf[c]);

					cplex.addLe(expr1, M+epsln);
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
								expr1.addTerm(hpdeploymentcosts[c][i][j][k], y[c][a][i][j][k]);
								//expr1.addTerm(1, conf[c], x[c][i][j][k]);
							}


						}
					}

					expr1.addTerm(-1, b[c][a]);

					expr1.addTerm(epsln, conf[c]);



					cplex.addGe(expr1, epsln);
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
									expr1.addTerm(1.0, y[c][a][i][j][k]);
									expr1.addTerm(-1.0, y[c][a][j][i][k]);
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

									expr.addTerm(1.0, y[c][a][i][j][k]);

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







			for(int c=0; c<totalconf; c++)
			{


				for(int a=0; a<nplayer; a++)
				{

					IloLinearNumExpr expr1 = cplex.linearNumExpr();


					expr1.addTerm(1, b[c][a]); 
					expr1.addTerm(-1, z[a]);
					expr1.addTerm(M, conf[c]);
					cplex.addLe(expr1, M);


				}
			}


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
									expr1.addTerm(-1.0, y[c][a][j][i][k]);
								}
							}

						}

						cplex.addLe(expr1, 0);


					}
				}
			}





			cplex.solve();

			double ob = cplex.getObjValue();

			System.out.println(" obj: "+ cplex.getObjValue());

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{
					if(cplex.getValue(b[c][a])>0)
					{
						System.out.println(a+": "+ cplex.getValue(b[c][a]));
					}
				}
			}

			ArrayList<double[]> res = new ArrayList<double[]>();

			ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();

			for(int c=0; c<totalconf; c++)
			{

				for(int a=0; a<nplayer; a++)
				{
					res = new ArrayList<double[]>();

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

										double[] ar = {i,j, k, c, ob};
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
