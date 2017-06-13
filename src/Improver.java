
public class Improver {
	
	private static final String filename = "input.txt";
	private static final int enhancement_attempts = 1000;

	public static void main(String[] args) {
		Cube cube = new Cube(filename);
		int best = cube.numberColoured();
		System.out.println("Starting a cube size " + cube.getSize() + " with " + best + " coloured");
		int addMore = cube.addMorePoints();
		if(addMore > 0) {
			best = cube.numberColoured();
			cube.filePrint();
			System.out.println("added " + addMore);
		}
		
		for(int j=0 ; j<enhancement_attempts ; ++j) {
			System.out.println("enhancement " + (j+1));
			if(cube.swapColouredPointDumbFast()) {
				int added = cube.addMorePoints();
				if(added > 0) {
					System.out.println("added " + added);
					cube.filePrint();
				}
			} else {
				break;
			}
		}
	}

}
