package process;

import mgtrf.EM_m;
import gtrf.EM_g;
import base_model.Corpus;
import base_model.EM;

public class Test {
	
	//The LDA output running folder. All folders and files created here during running topic model 
	public static String folder_path = "C:\\Exp\\lda\\20news_test\\";  
	//The original data set folder
	public static String data_path = "C:\\Exp\\lda\\20news_test\\data\\";
    //The stop words folder
	public static String stopwords_path = "C:\\Exp\\ldastopwords2.txt";

//	public static String folder_path = "/Users/tongwang/Desktop/exp/lda/20news_test/";  
//	public static String data_path = "/Users/tongwang/Desktop/exp/lda/20news_test/data/";
//	public static String stopwords_path = "/Users/tongwang/Desktop/exp/lda/20news_test/stopwords2.txt";
	
	public static void main(String[] args) {
		
//		Preprocess p = new Preprocess(folder_path, data_path, stopwords_path);
//		p.getWords();
//		p.getWordsAndTrees();
//		p.findEdges();
		
		
		int min_count = 2;
		double train_percentage = 0.8;
		double lambda2 = 0.2;
		double lambda4 = 0.6;
		int topic = 5;
//		String type = "LDA";
//		String type = "GTRF";
		String type = "MGTRF";
		
//		Corpus corpus = new Corpus(folder_path, min_count, train_percentage, "LDA");
//		EM em = new EM(folder_path, topic, corpus);
//		String res_path = "res_" + topic;
//		em.run_em(res_path, "LDA");
		
//		Corpus corpus2 = new Corpus(folder_path, min_count, train_percentage, type);
//		EM_g em2 = new EM_g(folder_path, topic, corpus2, lambda2);
//		String res_path = "res_" + topic + "_" + lambda2;
//		em2.run_em(res_path, type);
		
		Corpus corpus3 = new Corpus(folder_path, min_count, train_percentage, type);
		EM_m em3 = new EM_m(folder_path, topic, corpus3, lambda2, lambda4);
		String res_path = "res_" + topic + "_" + lambda2 + "_" + lambda4;
		em3.run_em(res_path, type);

	}

}
