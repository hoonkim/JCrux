package edu.hyu.cs.types;


public class Pair2<A , B >{

	public A first;
	public B second;
	
	protected Pair2(){
		
	}

	protected Pair2(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public static <A, B> Pair2<A, B> of(
			A first, B second) {
		return new Pair2<A, B>(first, second);
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		if (this == obj)
			return true;
		return equal(first, ((Pair<?, ?>) obj).first)
				&& equal(second, ((Pair<?, ?>) obj).second);
	}

	// TODO : move this to a helper class.
	private boolean equal(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ')';
	}
	

}