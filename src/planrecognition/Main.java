/**
 * 
 */
package planrecognition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.function.Predicate;

/**
 * @author anjonsunny
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		
		//PlanRecognition.doFixedPolicyExp1();
		
		
		boolean withdefense = true;
		
		boolean minentropy = true;
		
		//boolean expoverlap = false;
		boolean maxoverlap = false;
		
		boolean expoverlap = !maxoverlap;
		
		
		int chosenattacker = 0;
		int chosenpolicy = 0;
		
		
		//ArrayList<int[]> comb = new ArrayList<int[]>();
		
		//permute(new int[]{0,1,2}, comb);
		
		//perm();
		
		
		
		
		
		//PlanrecognitionExp.doFixedPolicyWithDefenseExp1(withdefense, chosenattacker, chosenpolicy, minentropy, maxoverlap, expoverlap);
		
		PlanrecognitionExp.doFixedExploitPolicyWithDefenseExp1(withdefense, chosenattacker, chosenpolicy, minentropy, maxoverlap, expoverlap);
		

	}
	
	
	public static void perm() 
	{
        int[] chars = {0, 1, 2}; // max # index of paths
        
        int pathlimits [] = {2,2,2}; 
        
        ArrayList<int[]> perms = new ArrayList<int[]>();
        
        
        // looking for bba
        permute2(chars, 3, perms, pathlimits);
        
        printComb(perms);
    }
 
    static void permute2(int[] a, int k, ArrayList<int[]> perms, int[] pathlimits) 
    {
        int n = a.length;
        if (k < 1 || k > n)
            throw new IllegalArgumentException("Illegal number of positions.");
 
        int[] indexes = new int[n];
        int total = (int) Math.pow(n, k);
 
        while (total-- > 0) 
        {
        	int [] arr = new int[n - (n - k)];
        	
        	boolean ok = true;
        	
            for (int i = 0; i < n - (n - k); i++)
            {
               // System.out.print(a[indexes[i]]);
            	
            	
                arr[i] = a[indexes[i]];
                if(arr[i]>=pathlimits[i])
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
	
	
public static void permute(int[] arr, ArrayList<int[]> comb){
	    
		int pathlimits [] = {1,2,1};
		
	
		
		permuteHelper(arr, 0, comb);
	    
		printComb(comb);
		
		ArrayList<int[]> newcomb = refineComb(comb, pathlimits);
		
		System.out.println("after");
		
		printComb(newcomb);
		
	}

	private static void printComb(ArrayList<int[]> comb) {
		
		
		
		for(int[] x: comb)
		{
			for(int y: x)
			{
				System.out.print(y+" ");
			}
			System.out.println();
		}
		
	}

	
	
	
	
	private static ArrayList<int[]> refineComb(ArrayList<int[]> comb, int[] pathlimits) {
		
		ArrayList<int[]> combnew = new ArrayList<int[]>();
		
		
		for(int [] c: comb)
		{
			boolean ok = true;
			int j=0;
			for(int i: pathlimits)
			{
				
				if(c[j]>i)
				{
					ok = false;
					break;
				}
				j++;
					
			}
			if(ok)
			{
				combnew.add(c);
			}
		}
		
		return combnew;
		
		
		
	}

	private static void permuteHelper(int[] arr, int index, ArrayList<int[]> comb){
	    if(index >= arr.length - 1){ //If we are at the last element - nothing left to permute
	        //System.out.println(Arrays.toString(arr));
	        //Print the array
	    	int[] tmp = new int[arr.length];
	    	
	       // System.out.print("[");
	        for(int i = 0; i < arr.length - 1; i++)
	        {
	           // System.out.print(arr[i] + ", ");
	            tmp[i] = arr[i];
	        }
	        if(arr.length > 0) 
	        {
	           // System.out.print(arr[arr.length - 1]);
	            tmp[arr.length - 1] = arr[arr.length - 1];
	        }
	       // System.out.println("]");
	        
	        comb.add(tmp);
	        return;
	    }

	    for(int i = index; i < arr.length; i++){ //For each index in the sub array arr[index...end]

	        //Swap the elements at indices index and i
	        int t = arr[index];
	        arr[index] = arr[i];
	        arr[i] = t;

	        //Recurse on the sub array arr[index+1...end]
	        permuteHelper(arr, index+1, comb);

	        //Swap the elements back
	        t = arr[index];
	        arr[index] = arr[i];
	        arr[i] = t;
	    }
	}

}
