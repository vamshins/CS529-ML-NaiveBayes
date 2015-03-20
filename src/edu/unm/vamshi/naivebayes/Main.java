package edu.unm.vamshi.naivebayes;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.unm.vamshi.naivebayes.constants.Paths;

public class Main {

	public static void main(String[] args) {

		Paths paths = Paths.getInstance();
		
		int trainLabelsTotalCount = 11269;
		int vocabularyCount = 61188;
		double bseta;
		double beta;

		try {
			HashSet<Integer> docIdset = new HashSet<Integer>();
			docIdset = MLE.getDocIDs(paths.getTrainlabelpath());
			System.out.println("docIdset : " + docIdset.size());
			
			HashMap<Integer, String> trainLabelsMap; // (docID, Yk) // train.label
			trainLabelsMap = MLE.getDocumentLabels(paths.getTrainlabelpath());
			
			HashMap<Integer, String> testLabelsMap; // (docID, Yk) // test.label
			testLabelsMap = MLE.getDocumentLabels(paths.getTestlabelpath());

			HashMap<String, Integer> trainLabelsCount; // (Yk, Count) 
			trainLabelsCount = MLE.countTrainLabels(trainLabelsMap);

			System.out.println("Train Label Count: " + trainLabelsTotalCount);

			double pYk;

			HashMap<String, Double> pYk_trainLabelsMle = new HashMap<String, Double>(); // (Yk, pYk)
			
			// P(Yk) = (# of docs labeled Yk)/(total # of docs)
			for (Map.Entry<String, Integer> entry : trainLabelsCount.entrySet()) {
//				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				pYk = ((double) entry.getValue()) / trainLabelsTotalCount;
				pYk_trainLabelsMle.put(entry.getKey(), pYk);
			}
			
			System.out.println("pYk_trainLabelsMle : " + pYk_trainLabelsMle);
			beta = 1.0 / vocabularyCount; // 1.0000163430738054

			
			HashMap<String, HashMap<String, String>> trainDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
			
			trainDataMap = MAP.populateTrainDataMap(paths.getTraindatapath());
			System.out.println("trainDataMap size : " + trainDataMap.size());
			
			HashMap<String, HashMap<String, String>> testDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
			
			testDataMap = MAP.populateTrainDataMap(paths.getTestdatapath());
			System.out.println("testDataMap size : " + testDataMap.size());
			
			HashMap<String, HashMap<String, Integer>> countOfXiInYk = MAP.calculateCountOfXiInYk(trainLabelsMap, trainDataMap); // (wordId, Yk, count)
			
			System.out.println("countOfXiInYk size : " + countOfXiInYk.size());
			
			/*for (Map.Entry<String, HashMap<String, Integer>> entry1 : countOfXiInYk.entrySet()) {
				for (Map.Entry<String, Integer> entry2 : entry1.getValue().entrySet()) {
					System.out.println("wordId : " + entry1.getKey() + " Yk : " + entry2.getKey() + " Count : " + entry2.getValue());
				}
			}*/
			
			HashMap<String, HashMap<String, Double>> pXiYk_trainMap = new HashMap<String, HashMap<String, Double>>(); // wordId, count, pXiYk
			
			pXiYk_trainMap = MAP.calculatePXibyYk(countOfXiInYk, trainLabelsCount, beta, vocabularyCount);
			
			System.out.println("pXiYk_trainMap size : " + pXiYk_trainMap.size());
			
			/*for (Map.Entry<String, HashMap<String, Double>> entry1 : pXiYk_trainMap.entrySet()) {
				for (Map.Entry<String, Double> entry2 : entry1.getValue().entrySet()) {
					System.out.println("wordId : " + entry1.getKey() + " Yk : " + entry2.getKey() + " pXiYk : " + entry2.getValue());
				}
			}*/
//			System.exit(1);
			// docId, YkNew
			/*HashMap<String, String> pYkDocId_trainMap = new HashMap<String, String>();
			
			pYkDocId_trainMap = MAP.calculateTrainPYkbyDocId(pXiYk_trainMap, trainDataMap, pYk_trainLabelsMle, docIdset);
			*/
			
			HashMap<Integer, Integer> pYkDocId_testMap = new HashMap<Integer, Integer>();
			
			pYkDocId_testMap = MAP.calculateTestPYkbyDocId(pXiYk_trainMap, testDataMap, pYk_trainLabelsMle);
			
			System.out.println(testLabelsMap);
			System.out.println(pYkDocId_testMap);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
