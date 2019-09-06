// COMP90056 Assignment A 2019s2

// if you edit this file, write your name/login here

public class CMS_conservative implements CMS {
	private int width;
	private int depth;
	private Hash h[];
	private int c[][];

	CMS_conservative(int d, int w) {
		width = w;
		depth = d;
		h = new Hash[depth];
		c = new int[depth][width];
		
		for (int j =0; j<depth; ++j) {
			h[j] = new Hash();
		}
	}

	public void update(Object o, int freq) {
		Integer f_e = query(o);
		
		for (int j = 0; j < depth; j++) {
			int col = h[j].h_basic(o, width);
			
			if (f_e + freq > c[j][col]) {
				c[j][col] = f_e + freq;
			}
		}
	}

	public int query(Object o) {
		int min = c[0][h[0].h_basic(o, width)];
		int current;
		
		for (int j=1; j < depth ; j++) {
			current = c[j][h[j].h_basic(o, width)];
			
			if(current < min) {
				min = current;
			}
		}
		return min;
	}
}
