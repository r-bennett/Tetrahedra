import java.util.List;

public class Solver {
	private final static int attempts = 20000;
	private final static int enhancement_attempts = 4000;
	// private final static int[] primes = { 41  }; // Fudge to test a single cube
	//private final static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97}; 
	private final static int[] primes = {47, 67}; 
	
	public static void main(String[] args) {
		for(int k=0 ; k<primes.length ; ++k) {
			System.out.println("Working on size " + primes[k]);
			int best = 0;
			for(int i=0 ; i<attempts ; ++i) {
				Cube cube = new Cube(primes[k]);
				List<Point> points = Utils.generateRandomOrder(primes[k]);
				for(Point point : points) {
					if(cube.colourable(point)) {
						cube.colour(point);
					}
				}
				int preEnhancementSize = cube.numberColoured();
				long avg_time = 0;
				for(int j=0 ; j<enhancement_attempts ; ++j) {
					long startTime = System.currentTimeMillis();
					if(j%1 == 0) {
						System.out.println("enhancement " + (j+1) + ", attempt " + (i+1) + ", cube " + primes[k]);
					}
					if(cube.swapColouredPointDumbFast()) {
						long time = (System.currentTimeMillis() - startTime) / 1000;
						avg_time = (avg_time * j + time) / (j+1);
						System.out.println("Took " + time + "s. Projected time remaining: " + ((399-j)*avg_time)/60 + " mins");
						int added = cube.addMorePoints();
						if(added > 0) {
							System.out.println("added " + added);
						}
					} else {
						break;
					}
				}
				if (cube.numberColoured() > preEnhancementSize) {
					System.out.println("whoop, made it better by " + (cube.numberColoured() - preEnhancementSize));
				}
				if(cube.numberColoured() > best) {
					cube.filePrint();	
					best = cube.numberColoured();
				}
			}
		}
	}
}
