package edu.unm.vamshi.naivebayes;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
		int testLabelsTotalCount = 7505;
		int vocabularyCount = 61188;
		double beta;
		if(args.length == 1){
			beta = Double.parseDouble(args[0]);
		}
		else{
			System.out.println("Beta value is not provided. Taking default value i.e., 1.0/vocabularyCount");
			beta = 1.0 / vocabularyCount; // 0.0000163430738054
		}
		System.out.println("    -> beta value : " + beta);
		
		try {
			System.out.println("train.label count : " + trainLabelsTotalCount);
			System.out.println("test.label count  :" + testLabelsTotalCount);
			System.out.println("vocabularyCount count : " + vocabularyCount);
			
			// Get the train.label contents in the matrix [DocId, Yk]
			System.out.println("Getting the train.label contents in the matrix [DocId, Yk]...");
			HashMap<Integer, String> trainLabelsMap; // (docID, Yk) // train.label
			trainLabelsMap = MLE.getDocumentLabels((new File("data/train.label")).getAbsolutePath());
			System.out.println("    -> completed!");
			
			// Get the test.label contents in the matrix [DocId, Yk]
			System.out.println("Getting the test.label contents in the matrix [DocId, Yk]...");
			HashMap<Integer, String> testLabelsMap; // (docID, Yk) // test.label
			testLabelsMap = MLE.getDocumentLabels((new File("data/test.label")).getAbsolutePath());
			System.out.println("    -> completed!");
			
			// Calculate Yk counts into the matrix [Yk, Count]...
			System.out.println("Calculating Yk counts...");
			HashMap<String, Integer> trainLabelsCount; // (Yk, Count) 
			trainLabelsCount = MLE.countTrainLabels(trainLabelsMap);
			System.out.println("    -> completed!");

			double pYk;

			HashMap<String, Double> pYk_trainLabelsMle = new HashMap<String, Double>(); // (Yk, pYk)
			
			// Calculate Priors using P(Yk) = (# of docs labeled Yk)/(total # of docs)
			System.out.println("Calculating Priors (pYk)...");
			for (Map.Entry<String, Integer> entry : trainLabelsCount.entrySet()) {
//				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				pYk = ((double) entry.getValue()) / trainLabelsTotalCount;
				pYk_trainLabelsMle.put(entry.getKey(), pYk);
			}
			System.out.println("    -> Calculation completed!");
			
//			System.out.println("pYk_trainLabelsMle : " + pYk_trainLabelsMle);

			System.out.println("Loading train.data file into the application in the matrix (docId, wordId, count)...");						
			HashMap<String, HashMap<String, String>> trainDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
			trainDataMap = MAP.populateTrainDataMap((new File("data/train.data")).getAbsolutePath());
			System.out.println("    -> Loaded train.data file!");
			
			System.out.println("Loading test.data file into the application in the matrix (docId, wordId, count)...");
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
			
			System.out.println("\nAccuracy of classification of test labels: " + ((double)correctlyClassifiedLabels/7505)*100 + " %");
			
			System.out.println("\nPrinting Confusion Matrix : \n");
			for (int j = 0; j < 20; j++) {
				for (int k = 0; k < 20; k++) {
					System.out.print(confusionMatrix[j][k] + "\t");
				}
				System.out.println();
			}
			
			System.out.println("Loading vocabulary.txt into the application...");
			HashMap<Integer, String> vocabularyMap = new HashMap<Integer, String>();
			vocabularyMap = MAP.loadVocabulary((new File("data/vocabulary.txt")).getAbsolutePath());			
			System.out.println("    -> completed");
			
			double pxiyk = 0;
			double argmaxPxiyk = 0;
			HashMap <String, Double> row;
			HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
			System.out.println();
			System.out.println("Printing top 100 words with highest measure...");
			// Printing top 100 words with highest measure
			// wordId, Yk, pXiYk
			for (int p = 1; p <= 61188; p++) {
				row = pXiYk_trainMap.get(String.valueOf(p));
				for (int q = 1; q <= 20; q++) {
					pxiyk = row.get(String.valueOf(q));
					if(q==1){
						argmaxPxiyk=pxiyk;
					}
					if (argmaxPxiyk < pxiyk) {
						argmaxPxiyk = pxiyk;
					}
				}
				hm.put(p, argmaxPxiyk);
			}
//			System.out.println(hm);
			
			// sort the map by values in descending order.
			Map<Integer, Double> sortedMap = sortByComparator(hm);
			
			// printing the top 100 words in the map.
			int count=100;
			for (Entry<Integer, Double> sortedMapEntry : sortedMap.entrySet()) {
				if(count==0)
					break;
				System.out.print(vocabularyMap.get(sortedMapEntry.getKey()) + " ");
				count--;
			}
			
			System.out.println("\n\nExecution completed! Exiting the application.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static Map<Integer, Double> sortByComparator(Map<Integer, Double> unsortMap) {
		 
		// Convert Map to List
		List<Map.Entry<Integer, Double>> list = 
			new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1,
                                           Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
 
}
