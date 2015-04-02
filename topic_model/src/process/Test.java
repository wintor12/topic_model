package process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Test {
	
	//The LDA output running folder. All folders and files created here during running topic model 
	public static String folder_path = "C:\\Exp\\lda\\20news_test\\";  
	//The original data set folder
	public static String data_path = "C:\\Exp\\lda\\20news_test\\data\\";
	//The original data set folder
	public static String stopwords_path = "C:\\Exp\\lda\\stopwords2.txt";

	public static void main(String[] args) {
		
		Preprocess p = new Preprocess(folder_path, data_path, stopwords_path);
		p.getWordsAndTrees();

	}

}
