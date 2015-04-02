package process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class Preprocess {
	
	public String path_documents; //The original data set folder
	public String path;  //The original data set folder
	public String path_words;
	public String path_trees;
	public List<String> stopwords;
	
	public Preprocess(String run_path, String data_path, String stopwords_path)
	{
		this.path_documents = data_path;
		this.path = run_path;
		this.path_words = new File(run_path, "data_words").getAbsolutePath();
		this.path_trees = new File(run_path, "data_trees").getAbsolutePath();
		String text = "";
		try {
			text = FileUtils.readFileToString(new File(stopwords_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] words = text.split(" ");
		stopwords = Arrays.asList(words);
	}
	
	// Generate bag of words documents
	public void getWords() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		for (File file : listDir(path_documents)) {
			String name = file.getName();
			System.out.println(name);
			StringBuilder sb = new StringBuilder(); // bag of words
			String text = "";
			try {
				text = FileUtils.readFileToString(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Annotation document = new Annotation(text);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for (CoreMap s : sentences) {
				for (CoreLabel token : s.get(TokensAnnotation.class)) {
					String word = token.toString().toLowerCase();
					if (word.matches("[a-z]+")) {
						if (!stopwords.contains(word))
							sb.append(word + " ");
					}
				}
			}
			try {
				FileUtils.writeStringToFile(new File(path_words, name),
						sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Generate bag of words documents and dependency trees documents from
	// "path_documents"
	// Output to path_words and path_trees
	public void getWordsAndTrees() {
		LexicalizedParser lp = LexicalizedParser.loadModel(
				"edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		for (File file : listDir(path_documents)) {
			String name = file.getName();
			System.out.println(name);
			StringBuilder sb = new StringBuilder(); // bag of words
			StringBuilder sb2 = new StringBuilder(); // trees
			String text = "";
			try {
				text = FileUtils.readFileToString(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Annotation document = new Annotation(text);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for (CoreMap s : sentences) {
				ArrayList<String> sent = new ArrayList<String>();
				for (CoreLabel token : s.get(TokensAnnotation.class)) {
					String word = token.toString().toLowerCase();
					if (word.matches("[a-z]+")) {
						if (!stopwords.contains(word))
							sb.append(word + " ");
						sent.add(token.get(TextAnnotation.class));
					}
				}
				try {
					String[] sentence = sent.toArray(new String[sent.size()]);
					Tree parse = lp.apply(Sentence.toWordList(sentence));
					GrammaticalStructure gs = gsf
							.newGrammaticalStructure(parse);
					Collection<TypedDependency> tdl = gs
							.typedDependenciesCCprocessed();
					// System.out.println(tdl);
					Iterator it = tdl.iterator();
					while (it.hasNext()) {
						String wordnode = it.next().toString();
						sb2.append(wordnode.substring(wordnode.indexOf('('),
								wordnode.indexOf(')') + 1) + "\t");
					}
					sb2.append(System.getProperty("line.separator"));
				} catch (Exception e) {
					System.out.println("error sentence:" + s.toString());
				}
			}
			try {
				FileUtils.writeStringToFile(new File(path_words, name),
						sb.toString());
				FileUtils.writeStringToFile(new File(path_trees, name),
						sb2.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * list files in a folder
	 */	
	public static List<File> listDir(String folder)
	{
		File f = new File(folder);
		File fe[] = f.listFiles();
		if(fe == null)
		{
			System.out.println("No such directory!!");
			return null;
		}
		List<File> files = new LinkedList<File>(Arrays.asList(fe));
		for(int i = 0; i < files.size(); i++)
		{
			File file = files.get(i);
			if (!file.isFile() || file.isHidden() || file.getName().startsWith(".")) 
			{
				files.remove(i);
			}
		}
		return files;
	}

}
