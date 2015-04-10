# topic_model

This project implment LDA referencing LDA-C code wrote by David Blei


The main program is in process.Test. 
1 Set run_path, data_path, stopwords_path mannually.

2		Preprocess p = new Preprocess(run_path, data_path, stopwords_path);
		p.getWords();  
   	p.getWordsAndTrees();
		p.findEdges();
		p.getSentences();  // Extract sentences from data_path and output to data_sentences. Sentences are only used to train word2vec
		
3  run python code to remove all words not in Engilish dictionary

4  run python code to train data_sentences using word2vec, output sim_matrix folder

5  new corpus, then store similarity matrix into memory

6  Run EM_s
