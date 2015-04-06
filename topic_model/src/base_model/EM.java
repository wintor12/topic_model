package base_model;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;



import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.special.Gamma;

import base_model.Tools.ArrayIndexComparator;

public class EM {

	public int num_topics; // topic numbers   K
	public int VAR_MAX_ITER;
	public double VAR_CONVERGED;
	public int EM_MAX_ITER;
	public double EM_CONVERGED;
	public double alpha;     //dirichlet prior of topic mixture 
	public double beta;      //dirichlet prior of word mixture, used for gibbs initialize suf statistic
	public String path = ""; //running path
	public String path_res = ""; //result path
	public Corpus corpus;

	public EM(String path, String path_res, int num_topics, Corpus corpus, double beta) {
		this.path = path;
		this.path_res = path_res;
		this.num_topics = num_topics; // topic numbers
		this.VAR_MAX_ITER = 20;
		this.VAR_CONVERGED = 1e-6;
		this.EM_MAX_ITER = 100;
		this.EM_CONVERGED = 1e-4;
		this.alpha = 0.1;
		this.corpus = corpus;
		this.beta = beta;
	}

	public EM(String path, String path_res, int num_topics, Corpus corpus, int vAR_MAX_ITER, double vAR_CONVERGED,
			int eM_MAX_ITER, double eM_CONVERGED, double alpha, double beta) {
		this.path_res = path_res;
		this.num_topics = num_topics;
		this.VAR_MAX_ITER = vAR_MAX_ITER;
		this.VAR_CONVERGED = vAR_CONVERGED;
		this.EM_MAX_ITER = eM_MAX_ITER;
		this.EM_CONVERGED = eM_CONVERGED;
		this.alpha = alpha;
		this.path = path;
		this.corpus = corpus;
		this.beta = beta;
	}
	
	public double doc_e_step(Document doc, Model model, Suffstats ss)
	{
		double likelihood = 0.0;
		likelihood = lda_inference(doc, model);
		
		// update sufficient statistics
		double gamma_sum = 0;
		for(int k = 0; k < model.num_topics; k++)
		{
			gamma_sum += doc.gamma[k];
			ss.alpha_suffstas += Gamma.digamma(doc.gamma[k]);
		}
		ss.alpha_suffstas -= model.num_topics * Gamma.digamma(gamma_sum);
		for (int n = 0; n < doc.length; n++)
	    {
	        for (int k = 0; k < model.num_topics; k++)
	        {
	            ss.class_word[k][doc.ids[n]] += doc.counts[n]*doc.phi[n][k];
	            ss.class_total[k] += doc.counts[n]*doc.phi[n][k];
	        }
	    }

	    ss.num_docs += 1;
		return likelihood;
	}
	
	public double lda_inference(Document doc, Model model)
	{		
	    double likelihood = 0, likelihood_old = 0;
	    double[] digamma_gam = new double[model.num_topics];
	    
	    // compute posterior dirichlet
	    
	    //initialize varitional parameters gamma and phi
	    for (int k = 0; k < model.num_topics; k++)
	    {
	        doc.gamma[k] = model.alpha + (doc.total/((double) model.num_topics));
	        //compute digamma gamma for later use
	        digamma_gam[k] = Gamma.digamma(doc.gamma[k]);
	        for (int n = 0; n < doc.length; n++)
	            doc.phi[n][k] = 1.0/model.num_topics;
	    }
	    
	    double converged = 1;
	    int var_iter = 0;
	    double[] oldphi = new double[model.num_topics];  //????
	    while (converged > VAR_CONVERGED && var_iter < VAR_MAX_ITER)
	    {
	    	var_iter++;
//	    	System.out.println("var_iter: " + var_iter);
	    	for(int n = 0; n < doc.length; n++)
	    	{
	    		double phisum = 0;
	    		for(int k = 0; k < model.num_topics; k++)
	    		{
	    			oldphi[k] = doc.phi[n][k];
	    			//phi = beta * exp(digamma(gamma)) -> log phi = log (beta) + digamma(gamma)
	    			doc.phi[n][k] = model.log_prob_w[k][doc.ids[n]] + digamma_gam[k];
	    			if (k > 0)
	                    phisum = Tools.log_sum(phisum, doc.phi[n][k]);
	                else
	                    phisum = doc.phi[n][k]; // note, phi is in log space
	    		}	    		
	    		for (int k = 0; k < model.num_topics; k++)
	            {
	    			//Normalize phi, exp(log phi - log phisum) = phi/phisum
	                doc.phi[n][k] = Math.exp(doc.phi[n][k] - phisum);
	                doc.gamma[k] += doc.counts[n]*(doc.phi[n][k] - oldphi[k]);
	                digamma_gam[k] = Gamma.digamma(doc.gamma[k]);
	            }

	    	}
	    	likelihood = compute_likelihood(doc, model);
//		    System.out.println("likelihood: " + likelihood);		    
		    converged = (likelihood_old - likelihood) / likelihood_old;
//		    System.out.println(converged);
	        likelihood_old = likelihood;
	    }
	    
	    return likelihood;
	}
	
	public double compute_likelihood(Document doc, Model model)
	{
		double likelihood = 0, gamma_sum = 0, digamma_sum = 0;
	    double[] digamma_gam = new double[model.num_topics];
	    for(int k = 0; k < model.num_topics; k++)
	    {
	    	digamma_gam[k] = Gamma.digamma(doc.gamma[k]);
	    	gamma_sum += doc.gamma[k];
	    }
	    digamma_sum = Gamma.digamma(gamma_sum);
	    likelihood = Gamma.logGamma(model.alpha * model.num_topics) 
	    		- model.num_topics * Gamma.logGamma(model.alpha) 
	    		- Gamma.logGamma(gamma_sum);
	    for(int k = 0; k < model.num_topics; k++)
	    {
	    	likelihood += (model.alpha - 1) * (digamma_gam[k] - digamma_sum) 
	    			+ Gamma.logGamma(doc.gamma[k]) 
	    			- (doc.gamma[k] - 1) * (digamma_gam[k] - digamma_sum);
	    	for(int n = 0; n < doc.length; n++)
		    {
		    	if(doc.phi[n][k] > 0)
		    	{
		    		likelihood += doc.counts[n] * (doc.phi[n][k] * 
		    				((digamma_gam[k] - digamma_sum) - 
		    				Math.log(doc.phi[n][k]) +
		    				model.log_prob_w[k][doc.ids[n]]));
		    	}
		    }
	    }	    
	    
	    return likelihood;
	}
	
	public void run_em(String type)
	{ 
		String path_model = new File(path_res, "model").getAbsolutePath();
		
		Model model = new Model(num_topics, corpus.num_terms, alpha);
		Suffstats ss = new Suffstats(model, corpus, beta);
		//Random initialize joint probability of p(w, k), and compute p(k) by sum over p(w, k)
//		ss.random_initialize_ss();
		ss.gibbs_initialize_ss();
		model.mle(ss, true); //get initial beta
		
		model.save_lda_model(new File(path_model, "init").getAbsolutePath());		
		
		//run EM 		
		double likelihood, likelihood_old = 0, converged = 1;
		int i = 0;
		StringBuilder sb = new StringBuilder(); //output likelihood and converged
		while(((converged < 0) || (converged > EM_CONVERGED) || (i <= 2)) && (i <= EM_MAX_ITER))
		{
			i++;
			System.out.println("**** em iteration " + i + "****");
			likelihood = 0;
			ss.beta_initialize_ss();
//			ss.zero_initialize_ss();
			//E step
			for(int d = 0; d < corpus.num_docs; d++)
			{
				if(d%100 == 0)
					System.out.println("document " + d);
				
				//Initialize gamma and phi to zero for each document
				corpus.docs[d].gamma = new double[model.num_topics];
				corpus.docs[d].phi = new double[corpus.maxLength()][num_topics];
				
				//Compute gamma, phi of each document, and update ss
				//Sum up likelihood of each document
				likelihood += doc_e_step(corpus.docs[d], model, ss); 
			}

			// M step
			//Update Model.beta and Model.alpha using ss
			model.mle(ss, false);
			
			// check for convergence
	        converged = (likelihood_old - likelihood) / likelihood_old;
	        if (converged < 0) 
	        	VAR_MAX_ITER = VAR_MAX_ITER * 2;
	        likelihood_old = likelihood;
	        	        
	        
	        // output model, likelihood and gamma
	        sb.append(likelihood +"\t" + converged + "\n");
	        model.save_lda_model(new File(path_model, i + "").getAbsolutePath());
	        save_gamma(model, new File(path_model, i + "_gamma"));
	        if(type.equals("GTRF"))
	        	save_doc_para(new File(path_model, i + "_doc"));
	        if(type.equals("MGTRF"))
				save_doc_para2(new File(path_model, i + "_doc"));
		}		
		File likelihood_file = new File(path_res, "likelihood");
		try {
			FileUtils.writeStringToFile(likelihood_file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//output the final model
		model.save_lda_model(new File(path_res, "final").getAbsolutePath());
		save_gamma(model, new File(path_res, "final_gamma"));
		if(type.equals("GTRF"))
			save_doc_para(new File(path_res, "final_doc"));
		if(type.equals("MGTRF"))
			save_doc_para2(new File(path_res, "final_doc"));
		
		// output the word assignments (for visualization) and top words for each document
		
//		String path_post = new File(path_res, "word_topic_post").getAbsolutePath();
//		String path_topwords = new File(path_res, "top_words").getAbsolutePath();
		for(int d = 0; d < corpus.num_docs; d++)
		{
			if(d%100 == 0)
				System.out.println("final e step document " + d);
			lda_inference(corpus.docs[d], model);
//			save_word_assignment(corpus.docs[d], new File(path_post, corpus.docs[d].doc_name));
//			save_top_words(10, corpus.docs[d], new File(path_topwords, corpus.docs[d].doc_name));
		}
		
		//Save top words of each topic among corpus
		save_top_words_corpus(20, model, new File(path_res, "top_words_corpus"));
		//Evaluation
		computePerplexity(model);
	}
	

	public void save_gamma(Model model, File file)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder sb_gamma = new StringBuilder();  //Save gamma for each EM iteration
        for(int d = 0; d < corpus.num_docs; d++)
        {
        	sb_gamma.append(corpus.docs[d].doc_name);
        	for(int k = 0; k < model.num_topics; k++)
        	{       		
        		sb_gamma.append("\t" + df.format(corpus.docs[d].gamma[k]));
        	}
        	sb_gamma.append(System.getProperty("line.separator"));
        }
        try {
			FileUtils.writeStringToFile(file, sb_gamma.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save_word_assignment(Document doc, File file)
	{
		StringBuilder sb = new StringBuilder();
		int K = doc.gamma.length;  //topics
		int N = doc.length;  
		for(int n = 0; n < N; n++)
		{
			String word = corpus.voc.idToWord.get(doc.ids[n]);
			sb.append(word);
			for(int k = 0; k < K; k++)
			{
				sb.append("\t" + doc.phi[n][k]);
			}
			sb.append("\n");
		}
		try {
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * return top M words of a topic in one document from phi
	 * @param M      top M words
	 * @param doc
	 * @param file   output file
	 */
	public void save_top_words(int M, Document doc, File file)
	{				
		int K = doc.gamma.length;  //topics
		int N = doc.length; 
		String[][] res = new String[M][K];
		for(int k = 0; k < K; k++)
		{
			double[] temp = new double[N];
			for(int n = 0; n < N; n++)
				temp[n] = doc.phi[n][k];
			Tools tools = new Tools();
			ArrayIndexComparator comparator = tools.new ArrayIndexComparator(temp);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			for(int i = 0; i < M; i++)
			{
				if(i == doc.ids.length)
					break;
				res[i][k] = corpus.voc.idToWord.get(doc.ids[indexes[i]]);
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < M; i++)
		{
			for(int k = 0; k < K; k++)
			{
				sb.append(String.format("%-15s" , res[i][k]));
			}
			sb.append(System.getProperty("line.separator"));
			
		}
		try {
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save top words of each topic from whole corpus using beta
	 * @param M     top M words
	 * @param file  output file
	 */
	public void save_top_words_corpus(int M, Model model, File file)
	{
		int K = num_topics;
		int V = corpus.num_terms;
		String[][] res = new String[M][K];
		for(int k = 0; k < K; k++)
		{			
			double[] temp = new double[V];
			for(int v = 0; v < V; v++)
				temp[v] = model.log_prob_w[k][v];
			Tools tools = new Tools();
			ArrayIndexComparator comparator = tools.new ArrayIndexComparator(temp);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			for(int i = 0; i < M; i++)
			{
				res[i][k] = corpus.voc.idToWord.get(indexes[i]);
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < M; i++)
		{
			for(int k = 0; k < K; k++)
			{
				sb.append(String.format("%-15s" , res[i][k]));
			}
			sb.append(System.getProperty("line.separator"));
			
		}
		try {
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method save parameters in GTRF model.
	 * @param corpus
	 * @param filename
	 */
	public void save_doc_para(File file)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder sb = new StringBuilder();  //Save parameters in each document for each EM iteration
		sb.append("doc_name");    		
		sb.append("\t" + "zeta1");
		sb.append("\t" + "zeta2");
		sb.append("\t" + "num_e");
		sb.append("\t" + "exp_ec");
		sb.append("\t" + "exp_theta_square");
		sb.append("\n");
        for(int d = 0; d < corpus.num_docs; d++)
        {
        	sb.append(corpus.docs[d].doc_name);    		
    		sb.append("\t" + df.format(corpus.docs[d].zeta1));
    		sb.append("\t" + df.format(corpus.docs[d].zeta2));
    		sb.append("\t" + df.format(corpus.docs[d].num_e));
    		sb.append("\t" + df.format(corpus.docs[d].exp_ec));
    		sb.append("\t" + df.format(corpus.docs[d].exp_theta_square));
        	sb.append("\n");
        }
        try {
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save_doc_para2(File file)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder sb = new StringBuilder();  //Save parameters in each document for each EM iteration
		sb.append("doc_name");    		
		sb.append("\t" + "zeta1");
		sb.append("\t" + "zeta2");
		sb.append("\t" + "num_e");
		sb.append("\t" + "exp_ec");
		sb.append("\t" + "num_e2");
		sb.append("\t" + "exp_ec2");
		sb.append("\t" + "exp_theta_square");
		sb.append("\n");
        for(int d = 0; d < corpus.num_docs; d++)
        {
        	sb.append(corpus.docs[d].doc_name);    		
    		sb.append("\t" + df.format(corpus.docs[d].zeta1));
    		sb.append("\t" + df.format(corpus.docs[d].zeta2));
    		sb.append("\t" + df.format(corpus.docs[d].num_e));
    		sb.append("\t" + df.format(corpus.docs[d].exp_ec));
    		sb.append("\t" + df.format(corpus.docs[d].num_e2));
    		sb.append("\t" + df.format(corpus.docs[d].exp_ec2));
    		sb.append("\t" + df.format(corpus.docs[d].exp_theta_square));
        	sb.append("\n");
        }
        try {
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void computePerplexity(Model model)
	{
		System.out.println("========evaluate========");
		double perplex = 0;
		int N = 0;
		StringBuilder sb = new StringBuilder();
		GibbsSampling gibbs = new GibbsSampling(path, path_res, num_topics, corpus, 1, alpha, beta);
		gibbs.run_gibbs();
		double[][] theta = gibbs.theta;
		for(int m = 0; m < corpus.docs_test.length; m++)
		{
			Document doc = corpus.docs_test[m];
			double log_p_w = 0;
			for(int n = 0; n < doc.length; n++)
			{
				double betaTtheta = 0;
				for(int k = 0; k < num_topics; k++)
				{
					betaTtheta += Math.exp(model.log_prob_w[k][doc.ids[n]])*theta[m][k];
				}
				log_p_w += doc.counts[n]*Math.log(betaTtheta);
				
			}
			N += doc.total;
			perplex += log_p_w;
		}
		perplex = Math.exp(-(perplex/N));
		perplex = Math.floor(perplex);
		System.out.println(perplex);
		System.out.println(theta[0][0]);
		sb.append("Perplexity: " + perplex);
		try {
			File eval = new File(path_res, "eval"); 
			FileUtils.writeStringToFile(eval, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void computePerplexity_old(Model model)
	{
		System.out.println("========evaluate========");
		double perplex = 0;
		int N = 0;
		StringBuilder sb = new StringBuilder();
		for(Document doc: corpus.docs_test)
		{
//			sb.append(doc.doc_name);
			//assign topic to each word in test set
			//Compute theta based on the formula of Gibbs sampling
			doc.assign_topic_to_word(model);
			
//			DecimalFormat df = new DecimalFormat("#.##");
//			for(int k = 0; k < model.num_topics; k++)
//			{
//				sb.append("\t" + df.format(doc.theta[k]));
//			}
//			sb.append("\n");
						
			double log_p_w = 0;
			for(int n = 0; n < doc.length; n++)
			{
				double betaTtheta = 0;
				for(int k = 0; k < num_topics; k++)
				{
					betaTtheta += Math.exp(model.log_prob_w[k][doc.ids[n]])*doc.theta[k];
				}
				log_p_w += doc.counts[n]*Math.log(betaTtheta);
				
			}
			N += doc.total;
			perplex += log_p_w;
		}
		perplex = Math.exp(-(perplex/N));
		perplex = Math.floor(perplex);
		System.out.println(perplex);
		sb.append("Perplexity: " + perplex);
		try {
			File eval = new File(path_res, "eval"); 
			System.out.println(eval.getAbsolutePath());
			FileUtils.writeStringToFile(eval, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
