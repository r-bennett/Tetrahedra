import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

	public static List<Point> generateRandomOrder(int n) {
		List<Point> points = new ArrayList<Point>();
		for(int i=0 ; i<n ; ++i) {
			for(int j=0 ; j<n ; ++j) {
				for(int k=0 ; k<n ; ++k) {
					points.add(new Point(i,j,k));
				}
			}
		}
		Collections.shuffle(points);
		return points;
	}

}
