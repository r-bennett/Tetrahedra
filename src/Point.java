import java.io.FileWriter;

public class Point {
	public int x, y, z;
	
	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public static Point cross(Point a, Point b) {
		return new Point(a.y*b.z - a.z*b.y,
						 a.z*b.x - a.x*b.z,
						 a.x*b.y - b.x*a.y);		
	}
	
	// Gives vector from a to b
	public static Point difference(Point a, Point b) {
		return new Point(b.x - a.x, b.y - a.y, b.z - a.z);
	} 
	
	public void prettyPrint() {
		System.out.println("x = " + x + ", y = " + y + ", z = " + z);
	}
	
	public void prettyPrintForFile() {
		System.out.print("(" + x + ", " + y + ", " + z + ")");
	}

	public void prettyPrintToFile(FileWriter fw) {
		try {
			fw.write("(" + x + ", " + y + ", " + z + ")");
		} catch(Exception e) {
			//lolzor
		}
	}
}
