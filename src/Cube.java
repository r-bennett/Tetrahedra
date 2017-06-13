import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cube { 
	private int n;

	private static final String outputFileName = "results.txt";
	private static final String newLine = "\r\n"; // TODO: to display output file correctly set this to "\n" for Unix, "\r\n" for Windows

	// All the coloured points
	private List<Point> colouredPoints;

	// All the planes which are currently occupied
	private List<Plane> planes;

	public Cube(int n) {
		this.n = n;
		colouredPoints = new ArrayList<Point>();
		planes = new ArrayList<Plane>();
	}

	public Cube(String filename) {
		colouredPoints = new ArrayList<Point>();
		planes = new ArrayList<Plane>();
		
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
						
			String line = br.readLine();
			while(line != null) {
				if(!line.isEmpty() && Character.isDigit(line.charAt(0))) {
					String[] words = line.split(" ");	
					int size = Integer.parseInt(words[0]);	
					this.n = size;		
					line = br.readLine();
					// line now holds the coordinate points
					String[] numbers = line.split("[^\\d]");
					int count = 0;
					int[] coords = new int[3];
					for(int i=0 ; i<numbers.length ; ++i) {
						if(numbers[i].isEmpty()) {
							continue;
						}
						coords[count % 3] = Integer.parseInt(numbers[i]);
						count++;
						if(count % 3 == 0) {
							this.colour(new Point(coords[0], coords[1], coords[2]));
						}
					}
					break;
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// lol
		}
	}
	
	public int getSize() {
		return n;
	}
	
	public boolean colourable(Point point) {
		for(Plane plane : planes) {
			if(plane.contains(point)) {
				return false;
			}
		}
		return true;
	}

	public void colour(Point point) {
		// Add all the new planes to the list
		for(int i=0 ; i<colouredPoints.size()-1 ; ++i) {
			for(int j=i+1 ; j<colouredPoints.size() ; ++j) {
				planes.add(new Plane(colouredPoints.get(i),
						colouredPoints.get(j),
						point)
						);
			}
		}
		colouredPoints.add(point);
	}

	public void uncolour(Point point) {
		// removes a point and all its planes from the colouring.
		// This is dependent on the current state of the cube being valid
		colouredPoints.remove(point);
		List<Plane> removals = new ArrayList<Plane>();
		for(Plane plane : planes) {
			if (plane.contains(point)) {
				removals.add(plane);
			}
		}
		planes.removeAll(removals);
	}

	public int numberColoured() {
		return colouredPoints.size();
	}

	public void prettyPrint() {
		if(colouredPoints.isEmpty()) {
			return;
		}
		colouredPoints.get(0).prettyPrintForFile();
		for(int i=1 ; i<colouredPoints.size() ; ++i) {
			System.out.print(", ");
			colouredPoints.get(i).prettyPrintForFile();
		}
		System.out.println();
	}

	public void filePrint() {
		if(colouredPoints.isEmpty()) {
			return;
		}
		try {
			FileWriter fw = new FileWriter(outputFileName, true);
			try {
				fw.write(newLine + newLine);
				fw.write("------------------------------------------------" + newLine);
				fw.write(n + " cube, " + numberColoured() + " points:" + newLine);
				colouredPoints.get(0).prettyPrintToFile(fw);
				for(int i=1 ; i<colouredPoints.size() ; ++i) {
					fw.write(", ");
					colouredPoints.get(i).prettyPrintToFile(fw);
				}
				fw.write(newLine);
			} finally {
				fw.close();
			}
		} catch (IOException e) {
			// lol
		}
	}

	public Plane getSinglePlane(Point point) {
		// returns null if point not on a single plane, otherwise returns the plane it is on
		Plane res = null;
		for(Plane plane : planes) {
			if(plane.contains(point)) {
				if(res != null) {
					return null;
				}
				res = new Plane(plane);
			}
		}
		return res;
	}


	public List<Point> getNonColouredPoints() {
		List<Point> nonColouredPoints = new ArrayList<Point>();
		for(int i=0 ; i<n ; ++i) {
			for(int j=0 ; j<n ; ++j) {
				for(int k=0 ; k<n ; ++k) {
					nonColouredPoints.add(new Point(i, j, k));
				}
			}
		}
		nonColouredPoints.removeAll(colouredPoints);
		return nonColouredPoints;
	}

	public class PointAndPlane {
		public Point point;
		public Plane plane;
		public PointAndPlane(Point point, Plane plane) {
			this.point = new Point(point);
			this.plane = new Plane(plane);
		}
		public PointAndPlane(PointAndPlane pap) {
			this.point = new Point(pap.point);
			this.plane = new Plane(pap.plane);
		}
	}

	public List<PointAndPlane> getAllNonColouredPointsOnSinglePlanes() {
		// We should only include each plane once - subsequent points on the same plane will be ignored
		List<PointAndPlane> pointsAndPlanes = new ArrayList<PointAndPlane>();
		List<Point> nonColouredPoints = getNonColouredPoints();
		// Shuffle so we hit a random point first
		Collections.shuffle(nonColouredPoints);
		for(Point point : nonColouredPoints) {
			Plane singlePlane = getSinglePlane(point);
			if(singlePlane != null) {
				// Check if we already have that plane with another point:
				boolean alreadyAdded = false;
				for(PointAndPlane pap : pointsAndPlanes) {
					if(pap.plane.equals(singlePlane)) {
						alreadyAdded = true;
						break;
					}
				}
				if(!alreadyAdded) {
					pointsAndPlanes.add(new PointAndPlane(point, singlePlane));
				}
			}
		}
		return pointsAndPlanes;
	}
	
	public PointAndPlane getRandomNonColouredPointOnSinglePlane() {
		List<Point> nonColouredPoints = getNonColouredPoints();
		// Shuffle so we hit a random point first
		Collections.shuffle(nonColouredPoints);
		for(Point point : nonColouredPoints) {
			Plane singlePlane = getSinglePlane(point);
			if(singlePlane != null) {
				return new PointAndPlane(point, singlePlane);
			}
		}
		return null;
	}

	public boolean swapColouredPoint() {
		// This method tries to uncolour 1  point, and recolour a different point.
		// The choice of points is designed to try to free up as many other points for possible colouring
		List<PointAndPlane> additionCandidates = getAllNonColouredPointsOnSinglePlanes();
		System.out.println("candidates: " + additionCandidates.size());
		int best = 0;
		Point toRemove = null;
		Point toAdd = null;
		for(Point colouredPoint : colouredPoints) {
			List<PointAndPlane> additions = new ArrayList<PointAndPlane>();
			for(PointAndPlane candidate : additionCandidates) {
				if(candidate.plane.contains(colouredPoint)) {
					additions.add(new PointAndPlane(candidate));
				}
			}
			if(additions.size() > best) {
				// we have a new candidate - save the points
				toRemove = new Point(colouredPoint);
				// pick a random one out of the additions to actually add
				toAdd = new Point(additions.get(0).point);
			}
		}
		if(best > 0) {
			// we found a candidate, so swap.
			uncolour(toRemove);
			colour(toAdd);
			return true;
		}
		return false;
	}

	public boolean swapColouredPointDumb() {
		// This method tries to uncolour 1  point, and recolour a different point.
		// The points for swapping are picked at random from the available options
		List<PointAndPlane> additionCandidates = getAllNonColouredPointsOnSinglePlanes();
		if(additionCandidates.isEmpty()) {
			return false;
		}
		int randomIndex = (int) (Math.random() * additionCandidates.size());
		PointAndPlane randomAdditionChoice = additionCandidates.get(randomIndex);
		
		// Find the coloured points on that plane
		List<Point> colouredPointsOnPlane = new ArrayList<Point>();
		for(Point colouredPoint : colouredPoints) {
			if(randomAdditionChoice.plane.contains(colouredPoint)) {
				colouredPointsOnPlane.add(new Point(colouredPoint));
			}
		}
		// If we didn't find three coloured points, something went wrong.
		assert colouredPointsOnPlane.size() == 3;
		randomIndex = (int) (Math.random() * 3);
		uncolour(colouredPointsOnPlane.get(randomIndex));
		colour(randomAdditionChoice.point);
		return true;
	}
	
	public boolean swapColouredPointDumbFast() {
		PointAndPlane addition = getRandomNonColouredPointOnSinglePlane();
		if(addition == null) {
			return false;
		}
		List<Point> colouredCopy = new ArrayList<Point>(colouredPoints);
		// Shuffle so we don't always get the same of the 3 points
		Collections.shuffle(colouredCopy);
		for(Point colouredPoint : colouredPoints) {
			if(addition.plane.contains(colouredPoint)) {
				// make the swap
				uncolour(colouredPoint);
				colour(addition.point);
				return true;
			}
		}
		assert false; // should never hit this return
		return false; // keeps the compiler happy
	}

	public int addMorePoints() {
		// looks through all the non-coloured points, and sees if any could be added in.
		// Adds them  and returns the numbers added.
		int count = 0;
		List<Point> nonColouredPoints = getNonColouredPoints();
		for(Point point : nonColouredPoints) {
			if(colourable(point)) {
				colour(point);
				++count;
			}
		}
		return count;
	}
}