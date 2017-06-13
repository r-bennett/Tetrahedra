public class Plane {
	// plane is px + qy + rz + s = 0
	public int p,q,r,s;
	
	public Plane(Point n, Point a) { // n normal, a point on plane
		constructorHelp(n, a);
	}
	
	public Plane(Point a, Point b, Point c) {
		Point ab = Point.difference(a, b);
		Point ac = Point.difference(a, c);
		Point normal = Point.cross(ab, ac);
		constructorHelp(normal, a);
	}
	
	public Plane(Plane plane) {
		this.p = plane.p;
		this.q = plane.q;
		this.r = plane.r;
		this.s = plane.s;
	}
	
	private void constructorHelp(Point n, Point a) {
		p = n.x;
		q = n.y;
		r = n.z;
		s  = -1 * (p*a.x + q*a.y + r*a.z);
	}

	public boolean contains(Point point) {
		return (point.x * p) + (point.y * q) + (point.z * r) + s == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + p;
		result = prime * result + q;
		result = prime * result + r;
		result = prime * result + s;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// naive equals - planes could be equivalent with different scalar multiples, and not return as equal
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plane other = (Plane) obj;
		if (p != other.p)
			return false;
		if (q != other.q)
			return false;
		if (r != other.r)
			return false;
		if (s != other.s)
			return false;
		return true;
	}
	
	public void prettyPrint() {
		System.out.println(p + "x + " + q + "y + " + r + "z + " + s + " = 0");
	}

}
