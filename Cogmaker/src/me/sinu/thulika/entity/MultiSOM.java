/*
 * Developer : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.entity;

import org.encog.ml.data.MLData;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.som.SOM;
import org.encog.util.EngineArray;

public class MultiSOM extends SOM{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5704311758898534270L;
	
	public MultiSOM(int inputNeuron, int outputNeuron) {
		 super(inputNeuron,outputNeuron);
	}

	public int[] matches(final MLData input, final int count)  {
		int[] retArray = new int[count]; 
		double[] res = new double[count];
		if (input.size() > getInputCount()) {
			throw new NeuralNetworkError(
					"Can't classify SOM with input size of " + getInputCount()
							+ " with input data of count " + input.size());
		}

		double[][] m = getWeights().getData();
		double[] inputData = input.getData();
		//double minDist = Double.POSITIVE_INFINITY;
		
		for(int i=0; i<count; i++) {
			res[i] = Double.POSITIVE_INFINITY;
			retArray[i] = -1;
		}
		
		//int result = -1;

		for (int i = 0; i < getOutputCount(); i++) {
			double dist = EngineArray.euclideanDistance(inputData, m[i]);
			/*if (dist < minDist) {
				minDist = dist;
				result = i;
			}*/
			for(int k=0; k<count; k++) {
				if(dist < res[k]) {
					for(int j=count-1; j>k; j--) {
						res[j] = res[j-1];
						retArray[j] = retArray[j-1];
					}
					
					res[k] = dist;
					retArray[k] = i;
					break;
				}
			}
			
		}

		//return result;
		return retArray;
	}
}
