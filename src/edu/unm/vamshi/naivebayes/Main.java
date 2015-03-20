package edu.unm.vamshi.naivebayes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	// params
	// args[0] - Beta value
	// args[1] - Full path of Properties file which stores the 
	public static void main(String[] args) {
		System.out.println("java -jar NaiveBayes.jar [beta_value]");
		System.out.println("Options - \n\t beta_value (optional) - value varies from 0.00001 to 1");
		System.out.println("Starting the application...");
		System.out.println("-----------------------------------------------------------------------------------------------");
		int trainLabelsTotalCount = 11269;
		int vocabularyCount = 61188;
		double beta;
		if(args.length == 1){
			beta = Double.parseDouble(args[0]);
		}
		else{
			System.out.println("Beta value is not provided. Taking default value i.e., 1.0/vocabularyCount");
			beta = 1.0 / vocabularyCount; // 1.0000163430738054
		}
		System.out.println("    -> beta value : " + beta);
		
		try {
			
			HashMap<Integer, String> trainLabelsMap; // (docID, Yk) // train.label
			trainLabelsMap = MLE.getDocumentLabels((new File("data/train.label")).getAbsolutePath());
			
			HashMap<Integer, String> testLabelsMap; // (docID, Yk) // test.label
			testLabelsMap = MLE.getDocumentLabels((new File("data/test.label")).getAbsolutePath());

			HashMap<String, Integer> trainLabelsCount; // (Yk, Count) 
			trainLabelsCount = MLE.countTrainLabels(trainLabelsMap);

			System.out.println("Train Label Count: " + trainLabelsTotalCount);

			double pYk;

			HashMap<String, Double> pYk_trainLabelsMle = new HashMap<String, Double>(); // (Yk, pYk)
			
			// P(Yk) = (# of docs labeled Yk)/(total # of docs)
			System.out.println("Calculating Priors (pYk)...");
			for (Map.Entry<String, Integer> entry : trainLabelsCount.entrySet()) {
//				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				pYk = ((double) entry.getValue()) / trainLabelsTotalCount;
				pYk_trainLabelsMle.put(entry.getKey(), pYk);
			}
			System.out.println("    -> Calculation completed!");
			
//			System.out.println("pYk_trainLabelsMle : " + pYk_trainLabelsMle);

			System.out.println("Loading train.data file into the application...");						
			HashMap<String, HashMap<String, String>> trainDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
			trainDataMap = MAP.populateTrainDataMap((new File("data/train.data")).getAbsolutePath());
			System.out.println("    -> Loaded train.data file!");
			
			System.out.println("Loading test.data file into the application...");
			HashMap<String, HashMap<String, String>> testDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
			testDataMap = MAP.populateTrainDataMap((new File("data/test.data")).getAbsolutePath());
			System.out.println("    -> Loaded test.data file!");
			
			// (count of Xi in Yk)
			System.out.println("Calculating and loading Count of all the WordIds in different Categories... (count of Xi in Yk)");
			System.out.println("    -> Xi - WordId" + "\n"+
								"    -> Yk - Category/Class");
			HashMap<String, HashMap<String, Integer>> countOfXiInYk = new HashMap<String, HashMap<String,Integer>>(); 
			countOfXiInYk = MAP.calculateCountOfXiInYk(trainLabelsMap, trainDataMap); // (wordId, Yk, count)
			System.out.println("    -> Calculation and loading completed!");
			
			// MAP for P(X|Y)
			// P(Xi|Yk)=(count of Xi in Yk)+(beta)(total words inYk)+((beta)*(length of vocab list)))
			System.out.println("Calculating MAPs... (MAP for P(X|Y))");
			System.out.println("    -> using P(Xi|Yk)=(count of Xi in Yk)+(beta)(total words in Yk)+((beta)*(length of vocab list)))");
			HashMap<String, HashMap<String, Double>> pXiYk_trainMap = new HashMap<String, HashMap<String, Double>>(); // wordId, count, pXiYk
			pXiYk_trainMap = MAP.calculatePXibyYk(countOfXiInYk, trainLabelsCount, beta, vocabularyCount);
			System.out.println("    -> Calculation completed");
			
			//Ynew=argmax[ log2(P(Yk))+(sum over i)(# of Xnewi)log2(P(Xi|Yk))]
			System.out.println("Calculating the classifications of all the documents...");
			System.out.println("    -> using Ynew=argmax[ log2(P(Yk))+(sum over i)(# of Xnewi)log2(P(Xi|Yk))]");
			HashMap<Integer, Integer> pYkDocId_testMap = new HashMap<Integer, Integer>();
			pYkDocId_testMap = MAP.calculateTestPYkbyDocId(pXiYk_trainMap, testDataMap, pYk_trainLabelsMle);
			System.out.println("    -> Calculation completed");
			
//			System.out.println(testLabelsMap);
//			System.out.println(pYkDocId_testMap);
			
			Integer[] testLabelArray = new Integer[7505];
			Integer[] classifiedLabelArray = new Integer[7505];
			int i = 0;
			
			// Loading the testLabelsMap (test.label) to array testLabelArray[]
			for (Entry<Integer, String> x : testLabelsMap.entrySet()) {
				testLabelArray[i] = Integer.parseInt(x.getValue());				
				i++;
			}
			i = 0;
			
			// Loading the pYkDocId_testMap (obtained from the NB calculations above) to array classifiedLabelArray[]
			for (Entry<Integer, Integer> x : pYkDocId_testMap.entrySet()) {
				classifiedLabelArray[i] = x.getValue();
				i++;
			}
			
			// Declaring Confusion Matrix
			System.out.println("Creating empty Confusion Matrix...");
			Integer confusionMatrix[][] = new Integer[20][20];
			
			// Initialize Confusion Matrix to 0 (zero)
			for (int j = 0; j < 20; j++) {
				for (int k = 0; k < 20; k++) {
					confusionMatrix[j][k] = 0;
				}
			}
			System.out.println("    -> created");
			
			// Counter to count correctly classified labels
			int correctlyClassifiedLabels = 0;
			
			// Calculate correctly classified labels and populating Confusion Matrix
			System.out.println("Calculating correctly classified labels and populating Confusion Matrix");
			for (int j = 0; j < 7505; j++) {
				if (classifiedLabelArray[j] == testLabelArray[j]) {
					correctlyClassifiedLabels++;
				}
				confusionMatrix[classifiedLabelArray[j] - 1][testLabelArray[j] - 1] += 1;
			}
			System.out.println("    -> completed");
			
			System.out.println("\nAccuracy of classification of test labels: " + (double)correctlyClassifiedLabels/7505);
			
			System.out.println("\nPrinting Confusion Matrix : \n");
			for (int j = 0; j < 20; j++) {
				for (int k = 0; k < 20; k++) {
					System.out.print(confusionMatrix[j][k] + "\t");
				}
				System.out.println();
			}
			
			System.out.println("\n\nExecution completed! Exiting the application.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
