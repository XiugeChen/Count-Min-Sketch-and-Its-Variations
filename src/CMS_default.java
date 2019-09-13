// COMP90056 Assignment A 2019s2

// xiugec 961392
// Xiuge Chen
// xiugec@student.unimelb.edu.au
/*
 This is a starting point, and you are encouraged to make changes
*/

public class CMS_default implements CMS {
	private int width;
	private int depth;
	private Hash h[];
	private long c[][];

	CMS_default(int d, int w) {
		width = w;
		depth = d;
		h = new Hash[depth];
		c = new long[depth][width];
		
		for (int j =0; j<depth; ++j) {
			h[j] = new Hash();
		}
	}

	public void update(Object o, int freq) {
		for (int j = 0; j < depth; j++) {
			c[j][h[j].h_basic(o, width)] += freq;
		}
	}

	public long query(Object o) {
		long min = c[0][h[0].h_basic(o, width)];
		long current;
		
		for (int j=1; j < depth ; j++) {
			current = c[j][h[j].h_basic(o, width)];
			
			if(current < min) {
				min = current;
			}
		}
		return min;
	}
}
