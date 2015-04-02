package gtrf;
import base_model.RunEM;

public class RunEM_g extends RunEM{

	public RunEM_g(int num_topics, int vAR_MAX_ITER, double vAR_CONVERGED,
			int eM_MAX_ITER, double eM_CONVERGED, double alpha, String path) {
		super(num_topics, vAR_MAX_ITER, vAR_CONVERGED, eM_MAX_ITER, eM_CONVERGED,
				alpha, path);
	}

	public RunEM_g(String path, int num_topics) {
		super(path, num_topics);
	}
	
	

}
