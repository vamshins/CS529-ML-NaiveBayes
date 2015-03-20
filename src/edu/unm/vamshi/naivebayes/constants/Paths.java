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

	public String getNewsgrouplabelspath() {
		return newsgrouplabelspath;
	}

	public String getTrainlabelpath() {
		return trainlabelpath;
	}

	public String getTraindatapath() {
		return traindatapath;
	}

	public String getTestlabelpath() {
		return testlabelpath;
	}

	public String getTestdatapath() {
		return testdatapath;
	}

	public Paths(String propertiesFilePath) {
		try {
			System.out.println("Properties File: " + propertiesFilePath);
			input = new FileInputStream(propertiesFilePath);
			// load properties file
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
}
