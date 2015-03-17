package edu.unm.vamshi.naivebayes.constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Paths {
	private String vocabularypath;
	private String newsgrouplabelspath;
	private String trainlabelpath;
	private String traindatapath;
	private String testlabelpath;
	private String testdatapath;
	

	
	Properties prop = new Properties();
	InputStream input = null;

	public String getVocabularypath() {
		return vocabularypath;
	}

	public void setVocabularypath(String vocabularypath) {
		this.vocabularypath = vocabularypath;
	}

	public String getNewsgrouplabelspath() {
		return newsgrouplabelspath;
	}

	public void setNewsgrouplabelspath(String newsgrouplabelspath) {
		this.newsgrouplabelspath = newsgrouplabelspath;
	}

	public String getTrainlabelpath() {
		return trainlabelpath;
	}

	public void setTrainlabelpath(String trainlabelpath) {
		this.trainlabelpath = trainlabelpath;
	}

	public String getTraindatapath() {
		return traindatapath;
	}

	public void setTraindatapath(String traindatapath) {
		this.traindatapath = traindatapath;
	}

	public String getTestlabelpath() {
		return testlabelpath;
	}

	public void setTestlabelpath(String testlabelpath) {
		this.testlabelpath = testlabelpath;
	}

	public String getTestdatapath() {
		return testdatapath;
	}

	public void setTestdatapath(String testdatapath) {
		this.testdatapath = testdatapath;
	}

	private static Paths singletonPaths = new Paths();

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	private Paths() {
		try {
//			input = new FileInputStream("C:/Users/Vamshi/Documents/cs529_ml/NaiveBayes/src/resources/project.properties");
			input = new FileInputStream("C:/Users/Vamshi/Documents/cs529_ml/NaiveBayes/src/resources/project.properties");
			// load a properties file
			prop.load(input);
			
			vocabularypath = prop.getProperty("vocabularypath");
			newsgrouplabelspath = prop.getProperty("newsgrouplabelspath");
			trainlabelpath = prop.getProperty("trainlabelpath");
			traindatapath = prop.getProperty("traindatapath");
			testlabelpath = prop.getProperty("testlabelpath");
			testdatapath = prop.getProperty("testdatapath");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Static 'instance' method */
	public static Paths getInstance() {
		return singletonPaths;
	}
}
