// COMP90056 Assignment A 2019s2

// xiugec 961392
// Xiuge Chen
// xiugec@student.unimelb.edu.au

public class CMS_Morris implements CMS {
	int width;
	int depth;
	private Hash h[];
	private Morris c_morris[][];

	CMS_Morris(int d, int w) {
		width = w;
		depth = d;
		h = new Hash[depth];
		c_morris = new Morris[depth][width];
		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < width; j++) {
				c_morris[i][j] = new Morris();
			}
		}
		
		for (int j =0; j<depth; ++j) {
			h[j] = new Hash();
		}
	}

	public void update(Object o, int freq) {	
		if (freq > 0) {
			for (int j = 0; j < depth; j++) {
				c_morris[j][h[j].h_basic(o, width)].increment(freq);
			}
		}
	}

	public int query(Object o) {
		int min = c_morris[0][h[0].h_basic(o, width)].mycount();
		int current;
		
		for (int j=1; j < depth ; j++) {
			current = c_morris[j][h[j].h_basic(o, width)].mycount();
			
			if(current < min) {
				min = current;
			}
		}
		return min;
	}
}
