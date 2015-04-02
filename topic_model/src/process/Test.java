package process;

public class Test {
	
	//The LDA output running folder. All folders and files created here during running topic model 
	public static String folder_path = "C:\\Exp\\lda\\20news_test\\";  
	//The original data set folder
	public static String data_path = "C:\\Exp\\lda\\20news_test\\data\\";
	//The original data set folder
	public static String stopwords_path = "C:\\Exp\\lda\\stopwords2.txt";

	public static void main(String[] args) {
		
		Preprocess p = new Preprocess(folder_path, data_path, stopwords_path);
//		p.getWords();
//		p.getWordsAndTrees();
//		p.findEdges();
		System.out.println("Hello World");

	}

}
