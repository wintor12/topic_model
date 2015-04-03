package base_model;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public class Vocabulary {
	public Map<Integer, String> idToWord = null;
	public Map<String, Integer> wordToId = null;
	public Map<String, Integer> wordCount = null;
	
	
	public Vocabulary()
	{
		idToWord = new TreeMap<Integer, String>();
		wordToId = new TreeMap<String, Integer>();
		wordCount = new TreeMap<String, Integer>();
		
	}
	
	public int size()
	{
		return wordCount.size();
	}
	
	/**
	 * This method return the vocabulary of the whole corpus
	 * @param path         the bag of words document path, in data_words folder 
	 * @param min_count    remove any word which counts less than min_count
	 */
	public void getVocabulary(String path, int min_count)
	{
		List<File> dir = process.Preprocess.listDir(new File(path, "data_words").getAbsolutePath());
		
		//Calculate words counts
    	for(File d : dir)
    	{
    		String text = "";
    		try {
    			text = FileUtils.readFileToString(d);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		String[] words = text.split(" ");
    		for(String word: words)
    		{
    			if(wordCount.containsKey(word))
    			{
    				wordCount.put(word, wordCount.get(word) + 1);
    			}
    			else
    			{
    				wordCount.put(word, 1);	
    			}
    		}
    	}
    	    	
    	//Remove word counts pair which counts less than min_count, then create id word map
    	//Need to create a copy of map, otherwise concurrent exception    	    	
    	Map<String, Integer> temp_wordCount = new TreeMap<String, Integer>(wordCount);
    	int id = 0;
    	for (Map.Entry<String, Integer> entry : temp_wordCount.entrySet())
		{
    		int count = entry.getValue();
    		String word = entry.getKey();
    		if(count < min_count)
    		{
    			wordCount.remove(word);    			
    		}
    		else
    		{
    			idToWord.put(id, word);
    			wordToId.put(word, id);
    			id++;
    		}
		}
    	printToFile(new File(path, "idAndWord").getAbsolutePath());
	}

	
	public void printToFile(String filepath) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, String> entry : idToWord.entrySet())
		{
			int id = entry.getKey();
			String word = entry.getValue();
			sb.append(id + ":" + word);
			sb.append(System.getProperty("line.separator"));
		}
		try {
			FileUtils.writeStringToFile(new File(filepath), sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		sb = new StringBuilder();
		for (Map.Entry<String, Integer> entry : wordCount.entrySet())
		{
			int count = entry.getValue();
			String word = entry.getKey();
			sb.append(word + ":" + count);
			sb.append(System.getProperty("line.separator"));
		}
		try {
			FileUtils.writeStringToFile(new File(filepath + "2"), sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

