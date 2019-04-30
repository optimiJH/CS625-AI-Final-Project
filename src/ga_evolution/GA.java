package ga_evolution;

import java.util.ArrayList;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class GA {
	private static final boolean DEBUG = true;
	Size imageSize;
	ArrayList<BasicPolygon> polygons;
	int generation = 0;
	static Random r = new Random();

	public GA(Size imageSize, ArrayList<BasicPolygon> polygons) {
		this.imageSize = imageSize;
		this.polygons = polygons;
	}

	public static MatOfPoint helperFillPoly(ArrayList<Point> list) {
		MatOfPoint matofpoint = new MatOfPoint();
		if (list.size() > 0) {
			matofpoint.fromList(list);
		} else {
			System.out
					.println(" The polygon list is zero!! Can't add anything");
			System.exit(4);
		}
		return matofpoint;
	}

	static double fitness(Mat child, Mat parent) {
		// throw new UnsupportedOperationException("Not supported yet."); //To
		// change body of generated methods, choose Tools | Templates.

		double fitness = 0.0;

		// Pixels from both the images
		double[] ParentPixel;
		double[] ChildPixel;

		// difference between the BRG values
		double d_r = 0, d_g = 0, d_b = 0;

		// System.out.println(child.size().height + " " + child.size().width +
		// " "
		// + parent.size().height + " " + parent.size().width);
		if (child.size().height == parent.size().height
				&& child.size().width == parent.size().width) {
			// System.out.println("child and Parent have equal sizes");
			for (int i = 0; i < child.size().height; i++) {
				for (int j = 0; j < child.size().width; j++) {
					ChildPixel = child.get(i, j);
					ParentPixel = parent.get(i, j);
					// System.out.println("ChildPixel length" +
					// ChildPixel.length);
					// System.out.println("Parent: " + ParentPixel[0] + " "
					// + ParentPixel[1] + " " + ParentPixel[2] + " ");//
					// +ParentPixel[3]+" ");
					// System.out.println("Child " + ChildPixel[0] + " "
					// + ChildPixel[1] + " " + ChildPixel[2] + " ");//
					// +ChildPixel[3]+" ");

					d_b = ParentPixel[0] - ChildPixel[0];
					d_r = ParentPixel[1] - ChildPixel[1];
					d_g = ParentPixel[2] - ChildPixel[2];
					// fitness += Math.sqrt(d_b * d_b + d_r * d_r + d_g * d_g);
					fitness += Math.sqrt(d_b * d_b + d_r * d_r + d_g * d_g);
				}
			}

			// System.out.println("Comparing done");

		} else {
			System.out.println("child and Parent have different sizes");

		}

		return fitness;
	}

	public GA mutate() {
		// System.out.println("Mutating Dna:");
		ArrayList<BasicPolygon> mutatedPolygons = BasicPolygon.copyList(this.polygons);


		// pick a polygon from the polygon list
		BasicPolygon randomPolygon = mutatedPolygons.get(r.nextInt(mutatedPolygons
				.size()));

		System.out.println("Changing polygon" + randomPolygon);

		// mutate that Polygon
		randomPolygon.mutate((int) imageSize.width, (int) imageSize.height);

		return (new GA(new Size(this.imageSize.height, this.imageSize.width),
				mutatedPolygons));
	}

	public Mat draw(int picNumber) {

		Size size = this.imageSize;

		Mat img = new Mat((int) size.width, (int) size.height, CvType.CV_8UC4,
				new Scalar(0, 0, 0, 255));

		Mat draw = new Mat((int) size.width, (int) size.height, CvType.CV_8UC4,
				new Scalar(0, 0, 0, 255));

		// blend all the polygons onto the image
		for (int i = 0; i < this.polygons.size(); i++) {
			draw = blender(draw, this.polygons.get(i));
			// Highgui.imwrite("./src/NewimagesBlender.png", draw);
		}

		draw.copyTo(img, draw);

		if (picNumber % 100 == 0) {
			System.out.println("At  image : ./"
					+ String.format("%03d", picNumber) + ".jpg");
		}
		if (picNumber % 1000 == 0) {

			Mat newimg = new Mat((int) size.width, (int) size.height,
					CvType.CV_8UC3, new Scalar(0, 0, 0));
			Imgproc.GaussianBlur(draw, newimg, new Size(5, 5), 50);

			System.out.println("Drawing a new image : ./"
					+ String.format("%03d", picNumber) + ".jpg");
			Imgcodecs.imwrite("./src/Images/" + String.format("%03d", picNumber)
					+ ".jpg", newimg);
		}
		return img;
	}

	/**
	 * Blends the given polygon onto the image using its alpha value
	 * 
	 * @param srcImg
	 * @param p
	 * @return refer to :
	 *         http://bistr-o-mathik.org/2012/06/13/simple-transparency
	 *         -in-opencv/
	 */
	public static Mat blender(Mat srcImg, BasicPolygon p) {
		// create a copy of the image
		Mat polyImg = Mat
				.zeros(srcImg.width(), srcImg.height(), CvType.CV_8UC4);
		srcImg.copyTo(polyImg);

		// Draw the polygon onto the image  //TODO
		Imgproc.fillConvexPoly(polyImg, GA.helperFillPoly(p.points), p.color);

		// calculating opacity - converting alpha value in the range 0-1
		double opacity = (p.color.val[3]) / 255;

		// copy the new image on the older one
		Core.addWeighted(polyImg, opacity, srcImg, 1 - opacity, 0, srcImg);

		Imgcodecs.imwrite("./src/blenderPolyDraw" + ".png", polyImg);

		return srcImg;
	}

	public static GA generateDna(Size imageSize) {
		// System.out.println("generating RANDOM DNA");
		ArrayList<BasicPolygon> polygons = new ArrayList<BasicPolygon>();

		for (int i = 0; i < BasicPolygon.POLYGONS; i++) {
			int random = r.nextInt(BasicPolygon.POLY_MAX_POINTS
					- BasicPolygon.POLY_MIN_POINTS + 1)
					+ BasicPolygon.POLY_MIN_POINTS;

			ArrayList<Point> points = new ArrayList<Point>();

			for (int j = 0; j < random; j++) {
				points.add(BasicPolygon.generatePoint((int) imageSize.width,
						(int) imageSize.height));
			}

			// All polygons white initially
			Scalar color = new Scalar(255, 255, 255, 255);
			polygons.add(new BasicPolygon(color, points));
			// System.out.println(polygons);

		}
		return new GA(imageSize, polygons);

	}

	public static void displayDna(GA dna) {

		if (DEBUG) {
			System.out.println("dna is " + dna);
			System.out.println("ImageSize is  ( " + dna.imageSize.height
					+ " , " + dna.imageSize.width + " )");
			System.out.println("polygonsList is " + dna.polygons);

			int polygonCounter = 0;
			for (BasicPolygon p : dna.polygons) {
				// displayColor
				System.out.println(polygonCounter + " : " + " color is "
						+ p.displayColor());

				// displayPoints
				int pointCounter = 0;
				for (Point q : p.points) {
					System.out.println(q);
					pointCounter++;
				}

				polygonCounter++;
			}

		}

	}
}
