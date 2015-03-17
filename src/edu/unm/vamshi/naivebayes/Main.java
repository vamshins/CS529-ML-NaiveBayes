package edu.unm.vamshi.naivebayes;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.unm.vamshi.naivebayes.constants.Paths;

public class Main {

	public static void main(String[] args) {

		Paths paths = Paths.getInstance();
		HashSet<Integer> docIdset = new HashSet<Integer>();
		HashMap<Integer, String> trainLabelsMap; // (docID, Yk)
		HashMap<String, Integer> trainLabelsCountMap; // (Yk, Count)
		HashMap<String, Double> pYk_trainLabelsMle = new HashMap<String, Double>(); // (Yk, pYk)
		HashMap<String, HashMap<String, String>> trainDataMap = new HashMap<String, HashMap<String,String>>(); // (docId, wordId, count) 
		int trainLabelsTotalCount = 0;
		int vocabularyCount;
		double beta;
		double alpha;

		try {
			docIdset = MLE.getDocIDs(paths.getTrainlabelpath());
			trainLabelsMap = MLE.getDocumentLabels(paths.getTrainlabelpath());
			trainLabelsCountMap = MLE.countTrainLabelsMap(trainLabelsMap);

			// # of docs labeled Yk
			// total # of docs
			for (Map.Entry<String, Integer> entry : trainLabelsCountMap.entrySet()) {
				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				trainLabelsTotalCount += entry.getValue();
			}

			System.out.println("Train Label Count: " + trainLabelsTotalCount);

			double pYk;

			// P(Yk) = (# of docs labeled Yk)/(total # of docs)
			for (Map.Entry<String, Integer> entry : trainLabelsCountMap.entrySet()) {
//				 System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				pYk = ((double) entry.getValue()) / trainLabelsTotalCount;
				// System.out.println("pYk : " + pYk);
				pYk_trainLabelsMle.put(entry.getKey(), pYk);
			}

			vocabularyCount = MAP.countVocabularySize(paths.getVocabularypath());

			beta = 1 / vocabularyCount;

			alpha = beta + 1;

			trainDataMap = MAP.populateTrainDataMap(paths.getTraindatapath());
			
			HashMap<String, HashMap<String, Integer>> countOfXiInYk = MAP.calculateCountOfXiInYk(trainLabelsMap, trainDataMap); // (wordId, Yk, count)
			
			System.out.println("countOfXiInYk size : " + countOfXiInYk.size());
			
			/*for (Map.Entry<String, HashMap<String, Integer>> entry1 : countOfXiInYk.entrySet()) {
				for (Map.Entry<String, Integer> entry2 : entry1.getValue().entrySet()) {
					System.out.println("wordId : " + entry1.getKey() + " Yk : " + entry2.getKey() + " Count : " + entry2.getValue());
				}
			}*/
			
			// wordId, Yk, pXiYk
			HashMap<String, HashMap<String, Double>> pXiYk_trainMap = new HashMap<String, HashMap<String, Double>>();
			
			pXiYk_trainMap = MAP.calculatePXibyYk(countOfXiInYk, trainLabelsCountMap, alpha, vocabularyCount);

			/*for (Map.Entry<String, HashMap<String, Double>> entry1 : pXiYk_trainMap.entrySet()) {
				for (Map.Entry<String, Double> entry2 : entry1.getValue().entrySet()) {
					System.out.println("wordId : " + entry1.getKey() + " Yk : " + entry2.getKey() + " pXiYk : " + entry2.getValue());
				}
			}*/
			
			// docId, Yk
			HashMap<String, String> pYkDocId_trainMap = new HashMap<String, String>();
			
			pYkDocId_trainMap = MAP.calculatePYkbyDocId(pXiYk_trainMap, trainDataMap, pYk_trainLabelsMle, docIdset);
			
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
