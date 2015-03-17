package edu.unm.vamshi.naivebayes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MLE {
	
	public static HashSet<Integer> getDocIDs(String trainlabelpath) throws IOException {
		BufferedReader br = null;
		int docID = 0;
		HashSet<Integer> docIdset = new HashSet<Integer>();

		br = new BufferedReader(new FileReader(trainlabelpath));

		while ((br.readLine()) != null) {
			docID++;
			
			docIdset.add(docID);

		}

		br.close();
		return docIdset;
	}

	public static HashMap<Integer, String> getDocumentLabels(String trainLabelFile) throws IOException {
		BufferedReader br = null;
		String Yk;
		int docID = 0;
		HashMap<Integer, String> trainLabelsMap = new HashMap<Integer, String>();

		br = new BufferedReader(new FileReader(trainLabelFile));

		while ((Yk = br.readLine()) != null) {
			docID++;
			System.out.println(docID + " - " + Yk);

			trainLabelsMap.put(docID, Yk);

		}

		br.close();
		return trainLabelsMap;
	}

	public static HashMap<String, Integer> countTrainLabelsMap(HashMap<Integer, String> trainLabelsMap) {
		HashMap<String, ArrayList<String>> trainLabelsListMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, Integer> trainLabelsCountMap = new HashMap<String, Integer>();
		for (Map.Entry<Integer, String> entry : trainLabelsMap.entrySet()) {
			addToMap(trainLabelsListMap, entry.getKey().toString(), entry.getValue());			
		}
		System.out.println("------------");
		for (Map.Entry<String, ArrayList<String>> entry : trainLabelsListMap.entrySet()) {
			trainLabelsCountMap.put(entry.getKey(), entry.getValue().size());
		}
		return trainLabelsCountMap;
	}

	public static void addToMap(HashMap<String, ArrayList<String>> trainLabelsListMap, String key, String value) {
		System.out.println("Key : " + key + " Value : " + value);
		if (!trainLabelsListMap.containsKey(value)) {
			trainLabelsListMap.put(value, new ArrayList<String>());
		}
		trainLabelsListMap.get(value).add(key);
	}
}
