package process;

import base_model.Corpus;
import base_model.EM;

public class Test {
	
	//The LDA output running folder. All folders and files created here during running topic model 
//	public static String folder_path = "C:\\Exp\\lda\\20news_test\\";  
	public static String folder_path = "/Users/tongwang/Desktop/exp/lda/20news_test/";  
	//The original data set folder
//	public static String data_path = "C:\\Exp\\lda\\20news_test\\data\\";
	public static String data_path = "/Users/tongwang/Desktop/exp/lda/20news_test/data/";
	//The original data set folder
	public static String stopwords_path = "/Users/tongwang/Desktop/exp/lda/20news_test/stopwords2.txt";

	public static void main(String[] args) {
		
//		Preprocess p = new Preprocess(folder_path, data_path, stopwords_path);
//		p.getWords();
//		p.getWordsAndTrees();
//		p.findEdges();
		
		Corpus corpus = new Corpus(folder_path, 2, 0.8);
		EM em = new EM(folder_path, 5, corpus);
		em.run_em();

	}

}
