// COMP90056 Assignment A 2019s2

// xiugec 961392
// Xiuge Chen
// xiugec@student.unimelb.edu.au

public class Morris {
	/*
		You may need to make changes to the primitive data types
		to get the benefit of a Morris Counter
	*/
	private byte counter;

	public void increment() {
		double r = StdRandom.uniform();
		double p = Math.pow(2, counter);
		// double p = power2;
		if(r < 1/p) {
			counter++;
		}
	}

	public void increment(int f) {
		if (f < 0) {
			System.err.println("Morris counter cannot decrement");
			System.exit(1);
		}
		for (int i=0; i<f; ++i) {
			increment();
		}
	}

	public Morris() {
		counter = 0;
	}

	public int mycount() {
		return (int) (Math.pow(2, counter) - 1);
	}
}
