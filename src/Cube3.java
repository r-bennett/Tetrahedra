import java.util.Vector;

public class Cube3 {
	private int n;  // cube size
	private boolean[][][] filled;
	private boolean[][][] unavailable;
	
	public Cube3(int n) {
		this.n = n;
		filled = new boolean[n][n][n];
		unavailable = new boolean[n][n][n];
	}
	
	public Cube3(Cube3 c) {
		this.n = c.n;
		filled = new boolean[n][n][n];
		unavailable = new boolean[n][n][n];
		for(int i=0 ; i<n ; ++i) {
			for(int j=0 ; j<n ; ++j) {
				for(int k=0 ; k<n ; ++k) {
					filled[i][j][k] = c.filled[i][j][k];
					unavailable[i][j][k] = c.unavailable[i][j][k];
				}
			}
		}
	}
	
	public Vector<Point> pointsOnPlane(Point a, Point b, Point c) {
		Vector<Point> points = new Vector<>();
		Plane plane = new Plane(a, b, c);
		for (int i=0 ; i<n ; ++i) {
			// fix x=i, then try and solve 2-var diophantine eqn left
			// for each solution found in range, add Point to return vector
			// Equation is y = (ez + f) / g at this point
			int f = -1 * (plane.s + i * plane.p);
			int e = -1 * plane.r;
			int g = plane.q;

			// special case that breaks the % arithmetic
			// eqn becomes ez + f = 0, y can be anything
			if(g==0) {
				// secondary special case that breaks the special case
				if(e==0) {
					// now equation is f=0, y and z can be anything
					if(f==0) {
						for(int y=0 ; y<n ; ++y) {
							for(int z=0 ; z<n ; ++z) {
								points.add(new Point(i,y,z));
							}
						}
					}
				} else {
					if(f%e == 0) {
						// its an int
						int z = f / e;
						if(z >= 0 && z < n) {
							// and it's in range
							// y can be anything
							for(int y=0 ; y<n ; ++y) {
								points.add(new Point(i,y,z));
							}
						}
					}
				}
			} else {

				int first = -1; // store the first z value that gives an int
				int second = -1; // and the next one
				for (int j=0 ; j<n ; ++j) {
					// fix z=j and see if we get an int
					int numerator = e*j + f;
					if(numerator % g == 0) {
						// it's an int
						int y = numerator / g;
						if(y >= 0 && y < n) {
							// and it's in range
							points.add(new Point(i,y,j));
						}
						// even if it's not in range, store it up
						if(first == -1) {
							first = j;
						} else {
							second = j;
							break;
						}
					}
				}
				if(second != -1) {
					// we still need to iterate through more z values, but now we can move in 
					// steps of second - first instead of 1
					int gap = second - first;
					for(int j=second+gap ; j<n ; j+=gap) {
						int numerator = e*j + f;
						if(numerator % g == 0) {
							// it's an int
							int y = numerator / g;
							if(y >= 0 && y < n) {
								// and it's in range
								points.add(new Point(i,y,j));
							}
						}
					}
				}
			}
		}
		return points;
	}
	
	public void randomPopulate() {
		for(int z=0 ; z<n ; ++z) {
			// populate layer z with 3 random points
			int count = 0;
			while(count < 3) {
				int x = (int)(Math.random() * n);
				int y = (int)(Math.random() * n);
				if(!filled[x][y][z]) {
					filled[x][y][z] = true;
					++count;
				}
			}
		}
	}
	
	// returns true if point was successfully filled, and updates filled and available arrays
	// otherwise returns false and does nothing
	public boolean fill(Point p) {
		if(unavailable[p.x][p.y][p.z]) {
			return false;
		}
		
		// generate all pairs of filled points so far, to find corresponding planes
		for(int i=0 ; i<n ; ++i) {
			for(int j=0 ; j<n ; ++j) {
				for(int k=0 ; k<n ; ++k) {
					if(filled[i][j][k]) {
						Point a = new Point(i,j,k);
						
						// there must be a better way to cycle through remaining points?
						for(int u=i ; u<n ; ++u) {
							for(int v=0 ; v<n ; ++v) {
								if(u==i && v==0) {
									v=j;
								}
								for(int w=0 ; w<n ; ++w) {
									if(u==i && v==j && w==0) {
										w=k+1;
										//so we don't read an out of bounds index from filled
										if(w>=n) {
											continue;
										}
									}
									if(filled[u][v][w]) {
										Point b = new Point(u,v,w);
										Vector<Point> problemPoints = pointsOnPlane(a,b,p);
										for(Point point : problemPoints) {
											if(point.equals(a) || point.equals(b) || point.equals(p)) {
												continue;
											}
											if(filled[point.x][point.y][point.z]) {
												return false;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		filled[p.x][p.y][p.z] = true;
		unavailable[p.x][p.y][p.z] = true;
		return true;
	}
	
	public Vector<Point> solve(Vector<Point> pointsFilled) {
		// super-naive implementation
		Vector<Point> best = pointsFilled;
		for (int i=0 ; i<n ; ++i) {
			for (int j=0 ; j<n ; ++j) {
				for (int k=0 ; k<n ; ++k) {
					Cube3 c = new Cube3(this);
					Point currentPoint = new Point(i,j,k);
					if(c.fill(currentPoint)) {
						@SuppressWarnings("unchecked")
						Vector<Point> newPointsFilled = (Vector<Point>) pointsFilled.clone();
						newPointsFilled.add(currentPoint);
						Vector<Point> res = c.solve(newPointsFilled);
						if(res.size() > best.size()) {
							best = res;
						}
					}
				}
			}
		}
		return best;
	}
	
	public static void main(String[] args) {
		Cube3 c = new Cube3(3);
		Vector<Point> best = c.solve(new Vector<>());
		for(Point p : best) {
			p.prettyPrint();
		}
	}
}
