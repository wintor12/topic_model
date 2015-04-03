package gtrf;
import base_model.Corpus;
import base_model.EM;

public class RunEM_g extends EM{

	public RunEM_g(String path, int num_topics, Corpus corpus,
			int vAR_MAX_ITER, double vAR_CONVERGED, int eM_MAX_ITER,
			double eM_CONVERGED, double alpha) {
		super(path, num_topics, corpus, vAR_MAX_ITER, vAR_CONVERGED, eM_MAX_ITER,
				eM_CONVERGED, alpha);
		// TODO Auto-generated constructor stub
	}

	public RunEM_g(String path, int num_topics, Corpus corpus) {
		super(path, num_topics, corpus);
		// TODO Auto-generated constructor stub
	}
	
	
	

}
