package process;

import java.io.File;

import mgtrf.EM_m;
import gtrf.EM_g;
import base_model.Corpus;
import base_model.EM;
import base_model.GibbsSampling;

public class Test {
	
	//The LDA output running folder. All folders and files created here during running topic model 
	public static String run_path = "C:\\Exp\\lda\\20news_test6\\";  
	//The original data set folder
	public static String data_path = "C:\\Exp\\lda\\20news_test6\\data\\";
    //The stop words folder
	public static String stopwords_path = "C:\\Exp\\lda\\stopwords2.txt";

//	public static String run_path = "/Users/tongwang/Desktop/exp/lda/20news_test/";  
//	public static String data_path = "/Users/tongwang/Desktop/exp/lda/20news_test/data/";
//	public static String run_path = "/Users/tongwang/Desktop/exp/lda/20news/";  
//	public static String data_path = "/Users/tongwang/Desktop/exp/lda/20news/data/";
//	public static String stopwords_path = "/Users/tongwang/Desktop/exp/lda/20news_test/stopwords2.txt";
	
	public static void main(String[] args) {
		
//		Preprocess p = new Preprocess(run_path, data_path, stopwords_path);
//		p.getWords();
//		p.getWordsAndTrees();
//		p.findEdges();
		
		double alpha = 0.1;
		double beta = 0.1;
		int min_count = 2;
		double train_percentage = 0.8;
		double lambda2 = 0.2;
		double lambda4 = 0.8;
		int K = 2;
		int[] ks = {50, 60, 80, 100, 200};
		//folder path is the running path, path_res is the output result path
//		for(int k = 2; k < 20; k++){
//		Corpus corpus = new Corpus(run_path, min_count, train_percentage, "LDA");
//		String path_res = new File(run_path, "res_" + k).getAbsolutePath();	
//		EM em = new EM(run_path, path_res, k, corpus, beta);
//		em.run_em("LDA");
//		}
//		for(int k = 20; k <= 40; k+=5){
//			Corpus corpus = new Corpus(run_path, min_count, train_percentage, "LDA");
//			String path_res = new File(run_path, "res_" + k).getAbsolutePath();	
//			EM em = new EM(run_path, path_res, k, corpus, beta);
//			em.run_em("LDA");
//			}
//		for(int i = 0; i < ks.length; i++){
//		Corpus corpus = new Corpus(run_path, min_count, train_percentage, "LDA");
//		String path_res = new File(run_path, "res_" + ks[i]).getAbsolutePath();	
//		EM em = new EM(run_path, path_res, ks[i], corpus, beta);
//		em.run_em("LDA");
//		}
		
//		for(int i = 2; i < 21; i++){
//		Corpus corpus = new Corpus(run_path, min_count, train_percentage, "LDA");
//		String path_res = new File(run_path, "res_" + i).getAbsolutePath();
//		GibbsSampling g = new GibbsSampling(run_path, path_res, i, corpus, 0, alpha, beta, 10, 500, 1000);
//		g.run_gibbs();
//		}
		
		
		for(int k = 2; k < 20; k++){
			Corpus corpus2 = new Corpus(run_path, min_count, train_percentage, "GTRF");
			String path_res2 = new File(run_path, "res_" + k + "_" + lambda2).getAbsolutePath();
			EM_g em2 = new EM_g(run_path, path_res2, k, corpus2, beta, lambda2);
			em2.run_em("GTRF");	
			
			Corpus corpus3 = new Corpus(run_path, min_count, train_percentage, "MGTRF");
			String path_res3 = new File(run_path, "res_" + k + "_" + lambda2 + "_" + lambda4).getAbsolutePath();
			EM_m em3 = new EM_m(run_path, path_res3, k, corpus3, beta, lambda2, lambda4);
			em3.run_em("MGTRF");
		}
		
		for(int k = 20; k <= 40; k+=5){
			Corpus corpus2 = new Corpus(run_path, min_count, train_percentage, "GTRF");
			String path_res2 = new File(run_path, "res_" + k + "_" + lambda2).getAbsolutePath();
			EM_g em2 = new EM_g(run_path, path_res2, k, corpus2, beta, lambda2);
			em2.run_em("GTRF");	
			
			Corpus corpus3 = new Corpus(run_path, min_count, train_percentage, "MGTRF");
			String path_res3 = new File(run_path, "res_" + k + "_" + lambda2 + "_" + lambda4).getAbsolutePath();
			EM_m em3 = new EM_m(run_path, path_res3, k, corpus3, beta, lambda2, lambda4);
			em3.run_em("MGTRF");
		}
		
		for(int i = 0; i < ks.length; i++){
			Corpus corpus2 = new Corpus(run_path, min_count, train_percentage, "GTRF");
			String path_res2 = new File(run_path, "res_" + ks[i] + "_" + lambda2).getAbsolutePath();
			EM_g em2 = new EM_g(run_path, path_res2, ks[i], corpus2, beta, lambda2);
			em2.run_em("GTRF");	
			
			Corpus corpus3 = new Corpus(run_path, min_count, train_percentage, "MGTRF");
			String path_res3 = new File(run_path, "res_" + ks[i] + "_" + lambda2 + "_" + lambda4).getAbsolutePath();
			EM_m em3 = new EM_m(run_path, path_res3, ks[i], corpus3, beta, lambda2, lambda4);
			em3.run_em("MGTRF");
		}

	}

}
