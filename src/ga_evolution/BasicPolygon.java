package ga_evolution;

import java.util.ArrayList;
import java.util.Random;

import org.opencv.core.Point;
import org.opencv.core.Scalar;


public class BasicPolygon {
	final static int POLY_MIN_POINTS = 3;
	final static int POLY_MAX_POINTS = 5;
	final static int POLYGONS = 20;
	private static final int OFFSET = 10;
	Scalar color; // color attribute of polygon
	ArrayList<Point> points;

	static Random r = new Random();

	public BasicPolygon() {
		color = null;
		points = new ArrayList<Point>();
	}

	public BasicPolygon(Scalar color, ArrayList<Point> points) {
		this.color = color;
		this.points = points;

	}

	static ArrayList<BasicPolygon> copyList(ArrayList<BasicPolygon> original) {
		ArrayList<BasicPolygon> copy = new ArrayList<BasicPolygon>();
//		System.out.println("Inside copyList :\n "+original+"------"+copy);

		BasicPolygon originalPolygon;
		BasicPolygon copyPolygon;

		for (int i = 0; i < original.size(); i++) {
			originalPolygon = original.get(i);
			copyPolygon = new BasicPolygon();

			// copy color
			copyPolygon.color = originalPolygon.color.clone();
			if (copyPolygon.color == originalPolygon.color) {
				System.out.println("ERROR: Same Color! ");
				System.exit(-7);
			}

			// copy each point
			for (Point p : originalPolygon.points) {
				copyPolygon.points.add(p.clone());
			}

			// for debugging
			if (originalPolygon.points.size() != copyPolygon.points.size()) {
				System.out.println("Quiting!");
				System.exit(-6);
			} else {
				for (int j = 0; j < originalPolygon.points.size(); j++) {
					if (originalPolygon.points.get(j) == copyPolygon.points
							.get(j)) {
						System.out.println("ERROR!! SAME address!!");
						System.exit(6);
					}
				}

			}

			copy.add(copyPolygon);
//			System.out.println("Inside copyList :\n "+original+"------"+copy);

		}

		return copy;
	}

	/**
	 * !!!!!! Here the point generated CAN BE OUTSIDE THE IMAGE
	 * 
	 * @param width
	 * @param height
	 * @return a MatOfPoint object with single Point(x,y)
	 */
	public static Point generatePoint(int width, int height) {
		// generate a random point between -OFFSET and widhth/height + OFFSET

		// x = random.randrange(0 - OFFSET, width + OFFSET, 1)
		// y = random.randrange(0 - OFFSET, height + OFFSET, 1)

		int x = (r.nextInt(width + 2 * OFFSET)) - OFFSET;
		int y = (r.nextInt(height + 2 * OFFSET)) - OFFSET;
		System.out.println("Point generated :" + x + " " + y);
		return (new Point(x, y));
	}

	// Here the width=imgsize.width and height =imgsize.height
	public void mutate(int width, int height) {
		System.out.println("Mutating Polygon: with parameters " + width + " "
				+ height);

		if (r.nextDouble() <= 0.5) {

			// changing the color of the polygon
			int idx = r.nextInt(4);
			int value = r.nextInt(256);
			// System.out.println("value is " + value);

			// scheme is BGRA
			int blue = (int) this.color.val[0];
			int green = (int) this.color.val[1];
			int red = (int) this.color.val[2];
			int alpha = (int) this.color.val[3];
			System.out.println("Changing color id:" + idx);
			System.out.println("Old color:" + blue + " " + green + " " + red
					+ " " + alpha);
			System.out.println("Now , idx= " + idx + " is val= " + value);

			switch (idx) {
			case 0:
				blue = value;
				break;
			case 1:
				green = value;
				break;
			case 2:
				red = value;
				break;
			case 3:
				alpha = value;
				break;
			}
			// scheme is BGRA
			System.out.println("New color:" + blue + " " + green + " " + red
					+ " " + alpha);

			this.color = new Scalar(blue, green, red, alpha);

		} else {
			// changing a point at a random index in points array

			int random = r.nextInt(this.points.size());
			System.out.println("Changing a point: at index " + random);
			// System.out.println("random is" + random);
			this.points.set(random, generatePoint(width, height));
		}

	}

	public String displayColor() {
		double[] color = this.color.val;
		String retColor = ("( " + color[0] + "," + color[1] + "," + color[2]
				+ "," + color[3] + ")");
		return retColor;
	}
}
