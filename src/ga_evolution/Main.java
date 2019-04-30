package ga_evolution;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;



public class Main {

	static int MAX_ITER = 1000000;

	public static void main(String[] args) {
//		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

		 System.load("C:\\Users\\jackw\\Libraries\\opencv\\build\\java\\x64\\opencv_java400.dll");

		// if (args.length < 2) {
		// System.out.println("here ");
		// System.exit(1);
		// }
		// String path = args[1];
		// String path =
		// "/Users/girigb/MyComputer/workspace/eclipsejava/EvoLisa/src/Mondrian.jpg";
		// String path = "./src/MondarinBig.jpg";
//		String path = "./src/New.jpg";
//		String path = "./src/lukisan-mondrian.jpg";
//		String path = "./src/mondrian200.jpg";
		String path = "./src/complicated_mondrian.jpg";
//		String path = "./src/small_mondrian.jpg";
//		String path = "./src/complicated_mondrian.jpg";
		// if (args.length == 3) {
		// MAX_ITER = Integer.parseInt(args[2]);
		// }

		
		System.out.println("In the begining");
		Mat image = Imgcodecs.imread(path);
		if (image.empty()) {
			System.out.println("Couldn't load file !");
			System.exit(2);
		}

		int generation = 0, picNumber = 0;
		Size imageSize = image.size();

		GA dna = GA.generateDna(imageSize);
		GA.displayDna(dna);
		GA mutatedDna;

		Mat parent = dna.draw(picNumber);

		double parentFitness = GA.fitness(parent, image);
		System.out.println("The initial fitness is " + parentFitness);

		while (true) {
			mutatedDna = dna.mutate();
			
			System.out.println("display mutated dna "+mutatedDna);
			GA.displayDna(mutatedDna);

			Mat child = mutatedDna.draw(picNumber);

			double childFitness = GA.fitness(child, image);

			System.out.println("The mutated fitness is " + childFitness);

			if (childFitness < parentFitness) {
				dna = mutatedDna;
				parentFitness = childFitness;
				System.out.println("**************picking child with fitness "
						+ childFitness);
			} else {
				System.out.println("keeping parent with fitness "
						+ parentFitness);
			}

			generation += 1;
			picNumber += 1;

			System.out.println("Generation " + generation);

		}

	}

}
