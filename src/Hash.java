// COMP90056 Assignment A 2019s2

// if you edit this file, write your name/login here

public class Hash
{
	private int p = 1073741789; //smaller than 2^30
	private int a,b;		// only use for hash tables < 24593 in size

	public Hash()
	{
		a=StdRandom.uniform(p-1)+1;
		b=StdRandom.uniform(p); // changed from p-1
		System.out.format("a %16d b %12d p %12d %n", a,b,p);
	}
	public int h2u(int x,int range)
	{
		long prod = (long)a*(long)x;
		prod += (long)b;
		long y = prod % (long) p;
		int r = (int) y % range;
		//System.out.format("x %12d y %12d r %12d %n", x,y,r);
		return r;
	}

	public int h_basic(Object key,int domain)
	{
		// domain should be something like 0x0fffffff
		int key_int = key.hashCode();
		return h2u(key_int, domain);
 	}
}
