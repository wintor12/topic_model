package base_model;

public class RunEM {

	int num_topics; // topic numbers
	int VAR_MAX_ITER;
	double VAR_CONVERGED;
	int EM_MAX_ITER;
	double EM_CONVERGED;
	double alpha;
	String path = "";
	Corpus corpus;

	public RunEM(String path, int num_topics, Corpus corpus) {
		this.path = path;
		this.num_topics = num_topics; // topic numbers
		this.VAR_MAX_ITER = 20;
		this.VAR_CONVERGED = 1e-6;
		this.EM_MAX_ITER = 100;
		this.EM_CONVERGED = 1e-4;
		this.alpha = 0.1;
	}

	public RunEM(int num_topics, int vAR_MAX_ITER, double vAR_CONVERGED,
			int eM_MAX_ITER, double eM_CONVERGED, double alpha, String path) {
		this.num_topics = num_topics;
		this.VAR_MAX_ITER = vAR_MAX_ITER;
		this.VAR_CONVERGED = vAR_CONVERGED;
		this.EM_MAX_ITER = eM_MAX_ITER;
		this.EM_CONVERGED = eM_CONVERGED;
		this.alpha = alpha;
		this.path = path;
	}
	
	

}
