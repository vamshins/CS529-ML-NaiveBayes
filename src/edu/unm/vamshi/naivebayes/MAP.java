package edu.unm.vamshi.naivebayes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
}
