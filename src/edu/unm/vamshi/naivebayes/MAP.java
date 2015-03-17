package edu.unm.vamshi.naivebayes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MAP {

	public static int countVocabularySize(String vocabularypath) throws IOException {
		int count = 0;
		BufferedReader br;

		br = new BufferedReader(new FileReader(vocabularypath));

		while ((br.readLine()) != null) {
			count++;
		}
		System.out.println("Vocabulary Count: " + count);
		br.close();
		return count;
	}

	public static HashMap<String, HashMap<String, String>> populateTrainDataMap(String traindatapath) throws IOException {
		HashMap<String, HashMap<String, String>> trainDataMap = new HashMap<String, HashMap<String, String>>();
		BufferedReader br;
		String sCurrentLine;
		br = new BufferedReader(new FileReader(traindatapath));
		
		while ((sCurrentLine = br.readLine()) != null) {
			String[] words = sCurrentLine.split("\\s+"); 
			addToMap(trainDataMap, words[0], words[1], words[2]);
		}

		br.close();
		return trainDataMap;
	}
	
	private static void addToMap(HashMap<String, HashMap<String, String>> trainDataMap, String docId, String wordId, String count) {
//		System.out.println("docId : " + docId + " wordId : " + wordId + " count : " + count);
		
		if (!trainDataMap.containsKey(docId)) {
			trainDataMap.put(docId, new HashMap<String, String>());
		}
		trainDataMap.get(docId).put(wordId, count);
	}

	public static HashMap<String, HashMap<String, Integer>> calculateCountOfXiInYk(HashMap<Integer, String> trainLabelsMap,
									    HashMap<String, HashMap<String, String>> trainDataMap) {
		HashMap<String, HashMap<String, Integer>> countOfXiInYkMap = new HashMap<String, HashMap<String, Integer>>();
		
		for (Map.Entry<Integer, String> trainlabelsEntry : trainLabelsMap.entrySet()) { // (docID, Yk)
			for (Map.Entry<String, HashMap<String, String>> trainDataEntry : trainDataMap.entrySet()) { // (docId, wordId, count)
				// wordId(xi), count, [docid], yk
				if(trainlabelsEntry.getKey().toString().equals(trainDataEntry.getKey())) { // docIds are same
					for (Map.Entry<String, String> wordIdCountEntry : trainDataEntry.getValue().entrySet()) {
						addToMap1(countOfXiInYkMap, wordIdCountEntry.getKey(), Integer.parseInt(wordIdCountEntry.getValue()), trainlabelsEntry.getValue()); // (map to populate, wordId, count, Yk)
					}
				}
			}
		}
		return countOfXiInYkMap;
	}
	
	private static void addToMap1(HashMap<String, HashMap<String, Integer>> countOfXiInYkMap, String wordId, Integer count, String Yk) {
//		System.out.println("wordId : " + wordId + " count : " + count + " Yk : " + Yk);
		
		if (!countOfXiInYkMap.containsKey(wordId)) {
			countOfXiInYkMap.put(wordId, new HashMap<String, Integer>());
		} else {
			if(countOfXiInYkMap.get(wordId).containsKey(Yk)) {
				countOfXiInYkMap.get(wordId).put(Yk, countOfXiInYkMap.get(wordId).get(Yk) + count);
			} else {
				countOfXiInYkMap.get(wordId).put(Yk, count);
			}
		}		
	}

	// entry 1: countOfXiInYk		- wordId, (Yk, count)   -- (Yk, count) -> entry3
	// entry 2: trainLabelsCountMap	- Yk, Count
	
	// MAP for P(X|Y)
	// P(Xi/Yk)=(count of Xi in Yk)+(alpha-1)(total words in Yk)+((alpha-1)*(length of vocab list)))
	public static HashMap<String, HashMap<String, Double>> calculatePXibyYk(HashMap<String, HashMap<String, Integer>> countOfXiInYk, HashMap<String, Integer> trainLabelsCountMap, double alpha, int vocabularyCount) {
		HashMap<String, HashMap<String, Double>> pXiYk_trainMap = new HashMap<String, HashMap<String, Double>>();
		
		double pXiYk;
		for(Map.Entry<String, HashMap<String, Integer>> entry1 : countOfXiInYk.entrySet()){
			for(Map.Entry<String, Integer> entry2 : trainLabelsCountMap.entrySet()){
				for(Map.Entry<String, Integer> entry3 : entry1.getValue().entrySet()){
					if(entry3.getKey().equals(entry2.getKey())){
						pXiYk = (entry3.getValue() + alpha - 1 )/(entry2.getValue() + ((alpha - 1) * vocabularyCount));
						addToMap2(pXiYk_trainMap, entry1.getKey(), entry3.getKey(), pXiYk);
//						System.out.println("wordId : " + entry1.getKey() + " Yk : " + entry3.getKey() + " pXiYk : " + pXiYk);
					}
				}
			}
		}
		
		return pXiYk_trainMap;
	}

	private static void addToMap2(HashMap<String, HashMap<String, Double>> pXiYk_trainMap, String wordId, String Yk, double pXiYk) {

		if (!pXiYk_trainMap.containsKey(wordId)) {
			pXiYk_trainMap.put(wordId, new HashMap<String, Double>());
		} else {
			pXiYk_trainMap.get(wordId).put(Yk, pXiYk);
		}		
	}
	
	// wordId, Yk, pXiYk -- pXiYk_trainMap
	// docId, wordId, count -- trainDataMap
	// Yk, pYk -- pYk_trainLabelsMle
	// docId    -- docIdset
	// Ynew=argmax[ log2(P(Yk))+(sum over i)(# of Xnewi)log2(P(Xi|Yk))]
	public static HashMap<String, String> calculatePYkbyDocId(HashMap<String, HashMap<String, Double>> pXiYk_trainMap, HashMap<String, HashMap<String, String>> trainDataMap, HashMap<String, Double> pYk_trainLabelsMle, HashSet<Integer> docIdset) {
		HashMap<String, String> pYkDocId_trainMap = new HashMap<String, String>();
		double Ynew;
		ArrayList<Double> Yall = new ArrayList<Double>();
		
		for(Integer docId : docIdset){
			for(Map.Entry<String, HashMap<String, String>> trainDataMapEntry1 : trainDataMap.entrySet()){
				if(trainDataMapEntry1.getKey().equals(docId)){ // comparing docId in (docId, wordId, count) with docId (set)
					for(Map.Entry<String, String> trainDataMapEntry2 : trainDataMapEntry1.getValue().entrySet()){
						for(Map.Entry<String, HashMap<String, Double>> pXiYk_trainMapEntry1 : pXiYk_trainMap.entrySet()){
							for(Map.Entry<String, Double> pXiYk_trainMapEntry2 : pXiYk_trainMapEntry1.getValue().entrySet()){
								if(pXiYk_trainMapEntry1.getKey().equals(trainDataMapEntry2.getKey())){
									for(Map.Entry<String, Double> pYk_trainLabelsMleEntry : pYk_trainLabelsMle.entrySet()){
										if(pYk_trainLabelsMleEntry.getKey().equals(pXiYk_trainMapEntry2.getKey())){
											Ynew = log(pYk_trainLabelsMleEntry.getValue()) + (trainDataMapEntry2.getValue()) *
										}
									}
								}
							}
						}						
					}
				}
			}
		}
		
		return pYkDocId_trainMap;
	}
}
