package edu.unm.vamshi.naivebayes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

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
	
	// trainLabelsMap - (docID, Yk) // train.label
	// trainDataMap   - (docId, wordid, count)
	// returns wordId, Yk, count
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
			if(countOfXiInYkMap.get(wordId).containsKey(Yk)) {
				countOfXiInYkMap.get(wordId).put(Yk, countOfXiInYkMap.get(wordId).get(Yk) + count);
			} else {
				countOfXiInYkMap.get(wordId).put(Yk, count);
			}
		} else {
			if(countOfXiInYkMap.get(wordId).containsKey(Yk)) {
				countOfXiInYkMap.get(wordId).put(Yk, countOfXiInYkMap.get(wordId).get(Yk) + count);
			} else {
				countOfXiInYkMap.get(wordId).put(Yk, count);
			}
		}		
	}

	// countOfXiInYk		- wordId, (Yk, count)
	// trainLabelsCount	- Yk, Count
	
	// MAP for P(X|Y)
	// P(Xi/Yk)=(count of Xi in Yk)+(alpha-1)(total words in Yk)+((alpha-1)*(length of vocab list)))
	
	// return (wordId, Yk, pXiYk)
	public static HashMap<String, HashMap<String, Double>> calculatePXibyYk(HashMap<String, HashMap<String, Integer>> countOfXiInYk, HashMap<String, Integer> trainLabelsCount, double beta, int vocabularyCount) {
		HashMap<String, HashMap<String, Double>> pXiYk_trainMap = new HashMap<String, HashMap<String, Double>>();
		
		double pXiYk;
		int count;
		
		for (int i = 1; i <= 61188; i++) { // total vocabulary
			for (int j = 1; j <= 20; j++) { // total categories
				try {
					count = countOfXiInYk.get(String.valueOf(i)).get(String.valueOf(j));
				} catch (Exception e) {
					count = 0;
				}
				
				/*System.out.println("i : " + i + "\n" +
									"j : " + j + "\n" +
									trainLabelsCount.get(String.valueOf(j)));*/
				pXiYk = ((double)(count + beta) )/(trainLabelsCount.get(String.valueOf(j)) + ((beta) * vocabularyCount));
				addToMap2(pXiYk_trainMap, String.valueOf(i), String.valueOf(j), pXiYk);
			}
		}
		/*
		for(Map.Entry<String, HashMap<String, Integer>> countOfXiInYkEntry1 : countOfXiInYk.entrySet()){
			for(Map.Entry<String, Integer> trainLabelsCountMapEntry1 : trainLabelsCount.entrySet()){
				for(Map.Entry<String, Integer> countOfXiInYkEntry2 : countOfXiInYkEntry1.getValue().entrySet()){
					if(countOfXiInYkEntry2.getKey().equals(trainLabelsCountMapEntry1.getKey())){
						pXiYk = (countOfXiInYkEntry2.getValue() + alpha - 1 )/(trainLabelsCountMapEntry1.getValue() + ((alpha - 1) * vocabularyCount));
						addToMap2(pXiYk_trainMap, countOfXiInYkEntry1.getKey(), countOfXiInYkEntry2.getKey().toString(), pXiYk);
//						System.out.println("wordId : " + countOfXiInYkEntry1.getKey() + " count : " + countOfXiInYkEntry2.getKey() + " pXiYk : " + pXiYk);
					}
				}
			}
		}*/
		
		return pXiYk_trainMap;
	}

	private static void addToMap2(HashMap<String, HashMap<String, Double>> pXiYk_trainMap, String wordId, String Yk, double pXiYk) {

		if (!pXiYk_trainMap.containsKey(wordId)) {
			pXiYk_trainMap.put(wordId, new HashMap<String, Double>());
			pXiYk_trainMap.get(wordId).put(Yk, pXiYk);
		} else {
			pXiYk_trainMap.get(wordId).put(Yk, pXiYk);
		}		
	}
	
	// wordId, Yk, pXiYk -- pXiYk_trainMap -- 61188
	// docId, wordId, count -- trainDataMap -- 11269
	// Yk, pYk -- pYk_trainLabelsMle -- 20
	// docId    -- docIdset
	// Ynew=argmax[ log2(P(Yk))+(sum over i)(# of Xnewi)log2(P(Xi|Yk))]
	public static HashMap<String, String> calculateTrainPYkbyDocId(HashMap<String, HashMap<String, Double>> pXiYk_trainMap, HashMap<String, HashMap<String, String>> trainDataMap, HashMap<String, Double> pYk_trainLabelsMle, HashSet<Integer> docIdset) {
		HashMap<String, String> pYkDocId_trainMap = new HashMap<String, String>();
		/*Likelihoods llh = new Likelihoods();
		ArrayList<Likelihoods> arrllh = new ArrayList<Likelihoods>();*/
		int docCount=docIdset.size();
		double Ynew;
		HashMap<String, String> wordIdCount_in_docId;
		
		HashMap<Integer, Integer> docIdYkResults = new HashMap<Integer, Integer>();
		double prior;
		double argmax = 0;
		int max;
		
		double pXiYk;
		
		for (Integer docId : docIdset) { // 11269 iterations
//		for (int docId = 1; docId <= 1; docId++) {
			argmax = 0;
			max = 0;
			wordIdCount_in_docId = trainDataMap.get(String.valueOf(docId));
//			System.out.println(wordIdCount_in_docId);
			for (int yk = 1; yk <= 20; yk++) { // iterate over Categories (Yk) 20 iterations
				Ynew = 0;
				prior = log2(pYk_trainLabelsMle.get(String.valueOf(yk))); // pYk
				
//				prior = pYk_trainLabelsMle.get(String.valueOf(yk)); // pYk
				
//				System.out.println("pYk : " + prior);
//				System.out.println("docId : " + docId + " Yk : " + i + " pYk : " + prior);
				/*for (Entry<String, String> wordIdCount_in_docId_entry : wordIdCount_in_docId.entrySet()) {
					try {
						docId_Word_Yk_pXiYk = pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()).get(String.valueOf(yk));
					} catch (Exception e) {
						docId_Word_Yk_pXiYk = 1;
					}
					
					System.out.println("docId : " + docId + 
					"\n\t pYk : " + prior +
					"\n\t Word : " + wordIdCount_in_docId_entry.getKey() +
					"\n\t Count : " + wordIdCount_in_docId_entry.getValue() + 
					"\n\t Yk-pXiYk : " + pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()) +
					"\n\t docId-Word-Yk-pXiYk : " + log2(docId_Word_Yk_pXiYk));
		
//					docId_Word_Yk_pXiYk = pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()).get(String.valueOf(i));
					Ynew += Integer.parseInt(wordIdCount_in_docId_entry.getValue()) * log2(docId_Word_Yk_pXiYk);
					break;
				}*/
				
				for (Entry<String, String> wordIdCount_in_docId_entry : wordIdCount_in_docId.entrySet()) {
					pXiYk = pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()).get(String.valueOf(yk));
					/*System.out.println("docId : " + docId + 
					"\n\t pYk : " + prior +
					"\n\t Word : " + wordIdCount_in_docId_entry.getKey() +
					"\n\t Count : " + wordIdCount_in_docId_entry.getValue() + 
					"\n\t Yk-pXiYk : " + pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()) +
					"\n\t docId-Word-Yk-pXiYk : " + log2(docId_Word_Yk_pXiYk));*/
		
					Ynew += Double.parseDouble(wordIdCount_in_docId_entry.getValue()) * log2(pXiYk);
//					Ynew += Math.pow(pXiYk, Double.parseDouble(wordIdCount_in_docId_entry.getValue()));
				}
				Ynew = prior + Ynew;
				/*
				System.out.println("Before\nYnew : " + Ynew + 
						" argmax : " +argmax +
						" max : " + max);*/

				if (argmax < Ynew) {
					argmax = Ynew;
					max = yk;
					/*System.out.println("After\nYnew : " + Ynew +
							" argmax : " +argmax +
							" max : " + max);*/
				}

				
			}
			
			docIdYkResults.put(docId, max);
			
			docCount--;
			System.out.println("Iterations left : " + docCount);
		}
		
		System.out.println(docIdYkResults);
		
		/*for(Map.Entry<String, HashMap<String, String>> trainDataMapEntry1: trainDataMap.entrySet()){
//			System.out.println("Current DocId : " + trainDataMapEntry1.getKey());
			for(Map.Entry<String, String> trainDataMapEntry2 : trainDataMapEntry1.getValue().entrySet()){
				for(Entry<String, HashMap<String, Double>> pXiYk_trainMapEntry1 : pXiYk_trainMap.entrySet()){
					if(trainDataMapEntry2.getKey().equals(pXiYk_trainMapEntry1.getKey())){
						for(Map.Entry<String, Double> pXiYk_trainMapEntry2 : pXiYk_trainMapEntry1.getValue().entrySet()){
//							addToMap3(pXiYk_trainMap, countOfXiInYkEntry1.getKey(), countOfXiInYkEntry2.getKey().toString(), pXiYk);
							System.out.println("docId : " + trainDataMapEntry1.getKey() +
									" WordIdPxy : " + pXiYk_trainMapEntry1.getKey() +
//									" WordIdt : " + trainDataMapEntry2.getKey() +
									" count : " + trainDataMapEntry2.getValue() +
									" Yk : " + pXiYk_trainMapEntry2.getKey() +
									" pXiYk : " + pXiYk_trainMapEntry2.getValue());
							llh.str[0] = trainDataMapEntry1.getKey(); // docId
							llh.str[1] = pXiYk_trainMapEntry1.getKey(); // WordIdPxy or WordIdt
							llh.str[2] = trainDataMapEntry2.getValue(); // count
							llh.str[3] = pXiYk_trainMapEntry2.getKey(); // Yk
							llh.str[4] = pXiYk_trainMapEntry2.getValue().toString(); // pXiYk
							arrllh.add(llh);
							
							if(count % 5000 == 0 )
								System.out.println("Processed : " + count);
						}
					}
				}
			}
			count--;
			System.out.println("Iterations left : " + count);
		}
		
		System.out.println("llh : " + arrllh.size());
		
		double Ynew = 0;
//		HashMap<Integer, Double> YnewList = new HashMap<Integer, Double>();
		String[][] YnewArr = new String[1][];
		int docCount = docIdset.size();
		for (Integer docId : docIdset) {
			Ynew = 0;
			for (Entry<String, Double> pYk_trainLabelsMleEntry1 : pYk_trainLabelsMle.entrySet()) {
				Ynew = log(pYk_trainLabelsMleEntry1.getValue()); // adding Yk
				for (Likelihoods likelihoods : arrllh) {
					if (pYk_trainLabelsMleEntry1.getKey().equals(likelihoods.str[3]) && docId.equals(likelihoods.str[0])) {
						Ynew = Ynew + Integer.parseInt(likelihoods.str[2]) * log(Double.parseDouble(likelihoods.str[4]));
					}
				}
				YnewList.put(docId, Ynew);
			}
			docCount--;
			System.out.println("Ynew Iterations left : " + docCount);
		}
		int i = 0;
		for (Integer docId : docIdset) {
			Ynew = 0;
			for (Likelihoods likelihoods : arrllh) {
				if(docId.equals(likelihoods.str[0]) && Ynew == 0 && pYk_trainLabelsMle.containsKey(likelihoods.str[3])){
					Ynew = pYk_trainLabelsMle.get(likelihoods.str[3]);
				}
				if (docId.equals(likelihoods.str[0]) && Ynew != 0) {
					Ynew = Ynew + Integer.parseInt(likelihoods.str[2]) * log(Double.parseDouble(likelihoods.str[4]));
				}
			}
//			YnewList.put(docId, Ynew);
			YnewArr[i][0] = docId.toString();
			YnewArr[i][1] = Double.toString(Ynew);
			docCount--;
			System.out.println("Ynew Iterations left : " + docCount);
			i++;
		}
		
		double Ynew;
		ArrayList<Double> Yall = new ArrayList<Double>();
		
		for(Integer docId : docIdset){
			Ynew = 0;
			for(Map.Entry<String, HashMap<String, String>> trainDataMapEntry1 : trainDataMap.entrySet()){
				if(trainDataMapEntry1.getKey().equals(docId)){ // comparing docId in (docId, wordId, count) with docId (set)
					for(Map.Entry<String, String> trainDataMapEntry2 : trainDataMapEntry1.getValue().entrySet()){
						for(Map.Entry<String, HashMap<String, Double>> pXiYk_trainMapEntry1 : pXiYk_trainMap.entrySet()){
							for(Map.Entry<String, Double> pXiYk_trainMapEntry2 : pXiYk_trainMapEntry1.getValue().entrySet()){
								if(pXiYk_trainMapEntry1.getKey().equals(trainDataMapEntry2.getKey())){
									for(Map.Entry<String, Double> pYk_trainLabelsMleEntry : pYk_trainLabelsMle.entrySet()){
										if(pYk_trainLabelsMleEntry.getKey().equals(pXiYk_trainMapEntry2.getKey())){
											Ynew = Ynew + log(pYk_trainLabelsMleEntry.getValue()) + (Integer.parseInt(trainDataMapEntry2.getValue())) * log(pXiYk_trainMapEntry2.getValue());
										}
									}
								}
							}
						}
					}
				}
				Yall.add(Ynew);
			}
			pYkDocId_trainMap.put(docId.toString(), Collections.max(Yall).toString());
		}*/
		
		return pYkDocId_trainMap;
	}
	
	public static HashMap<Integer, Integer> calculateTestPYkbyDocId(HashMap<String, HashMap<String, Double>> pXiYk_trainMap, HashMap<String, HashMap<String, String>> testDataMap, HashMap<String, Double> pYk_trainLabelsMle) {
		
		int docCount=7505;
		double Ynew;
		HashMap<String, String> wordIdCount_in_docId;
		
		HashMap<Integer, Integer> docIdYkResults = new HashMap<Integer, Integer>();
		double prior;
		double argmax = 0;
		int max;
		
		double pXiYk;
		
		for (int docId = 1; docId <= 7505; docId++) {
			max = 0;
			wordIdCount_in_docId = testDataMap.get(String.valueOf(docId));
			
			for (int yk = 1; yk <= 20; yk++) { // iterate over Categories (Yk) 20 iterations
				Ynew = 0;
				prior = log2(pYk_trainLabelsMle.get(String.valueOf(yk))); // pYk
				
				for (Entry<String, String> wordIdCount_in_docId_entry : wordIdCount_in_docId.entrySet()) {
					pXiYk = pXiYk_trainMap.get(wordIdCount_in_docId_entry.getKey()).get(String.valueOf(yk));
		
					Ynew += Double.parseDouble(wordIdCount_in_docId_entry.getValue()) * log2(pXiYk);
				}
				Ynew = prior + Ynew;				
				if(yk==1){
					argmax=Ynew;
					max=yk;
				}
				if (argmax < Ynew) {
					argmax = Ynew;
					max = yk;
				}
				
				/*System.out.println("Ynew : " + Ynew + 
						" argmax : " +argmax +
						" max : " + max);*/
				
			}
			
			docIdYkResults.put(docId, max);			
			docCount--;
			if(docCount % 500 == 0)
				System.out.println("        -> Iterations left : " + docCount);
		}
		
		return docIdYkResults;
	}

	private static double log2(Double value) {
		return Math.log(value)/Math.log(2);
	}

	public static HashMap<Integer, String> loadVocabulary(String vocabularyTxtPath) throws IOException {
		BufferedReader br = null;
		int docID = 0;
		HashMap<Integer, String> vocabularyMap = new HashMap<Integer, String>();
		String str;
		br = new BufferedReader(new FileReader(vocabularyTxtPath));

		while ((str = br.readLine()) != null) {
			docID++;
			
			vocabularyMap.put(docID, str);

		}

		br.close();
		return vocabularyMap;
	}
}
