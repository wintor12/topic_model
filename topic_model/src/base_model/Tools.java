package base_model;

import java.util.Comparator;


public class Tools {
	
	/*
	 * given log(a) and log(b), return log(a + b)
	 *
	 */
	static double log_sum(double log_a, double log_b) {
		double v;

		if (log_a < log_b) {
			v = log_b + Math.log(1 + Math.exp(log_a - log_b));
		} else {
			v = log_a + Math.log(1 + Math.exp(log_b - log_a));
		}
		return (v);
	}
	
	class ArrayIndexComparator implements Comparator<Integer>
	{
		double[] array;
		public ArrayIndexComparator(double[] array)
	    {
	        this.array = array;
	    }
		
		public Integer[] createIndexArray() 
		{
			Integer[] indexes = new Integer[array.length];
			for (int i = 0; i < array.length; i++) {
				indexes[i] = i; 
			}
			return indexes;
		}


		@Override
		public int compare(Integer index1, Integer index2) {
			if(array[index1] < array[index2])
				return 1;
			else if(array[index1] > array[index2])
				return -1;
			else
				return 0;
		}


	}
}

