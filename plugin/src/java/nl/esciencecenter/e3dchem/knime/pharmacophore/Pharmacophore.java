package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class Pharmacophore {
	private String identifier;
	private List<PharmacophorePoint> points = new ArrayList<>();

	/**
	 * Construct a pharmacophore based on a block of a phar formatted string
	 * 
	 * @param pharBlock
	 */
	public Pharmacophore(String pharBlock) {
		for (String line : pharBlock.split("\\r?\\n")) {
			if (line.startsWith("$$$$")) {
				break;
			} else if (line.startsWith("#")) {
				// Skip comments
			} else {
				String[] cols = line.split("\\s+");
				if (cols.length < 9) {
					identifier = cols[0];
				} else {
					points.add(new PharmacophorePoint(cols));
				}
			}
		}
	}

	public Pharmacophore(String identifier, List<PharmacophorePoint> points) {
		this.identifier = identifier;
		this.points = points;
	}

	/**
	 * 
	 * @return Coordinates of points as a 3xN matrix
	 */
	public SimpleMatrix getPointsMatrix() {
		SimpleMatrix matrix = new SimpleMatrix(points.size(), 3);
		int i = 0;
		for (PharmacophorePoint point : points) {
			matrix.setRow(i++, 0, point.cx, point.cy, point.cz);
		}
		return matrix;
	}

	/**
	 * 
	 * @return euclidean distance matrix between the points. Matrix size is NxN, where N is the number of points
	 */
	public SimpleMatrix getDistancesBetweenPoints() {
		int nrPoints = points.size();
		SimpleMatrix distances = new SimpleMatrix(nrPoints, nrPoints);
		for (int i = 0; i < nrPoints; i++) {
			for (int j = 0; j < nrPoints; j++) {
				if (i < j) {
					double dist = points.get(i).distance(points.get(j));
					distances.set(i, j, dist);
					distances.set(j, i, dist);
				}
			}
		}
		return distances;
	}

	/**
	 * Returns pharmacophore as phar formatted block
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder(512);
		String sep = "\n";
		buf.append(identifier).append(sep);
		for (PharmacophorePoint point : points) {
			buf.append(point).append(sep);
		}
		buf.append("$$$$").append(sep);
		return buf.toString();
	}

	/**
	 * 
	 * @return Number of points
	 */
	public int size() {
		return points.size();
	}

	/**
	 * @param index
	 * @return point on index
	 */
	public PharmacophorePoint get(int index) {
		return points.get(index);
	}

	/**
	 * @param indexes
	 * @return Coordinates of points which are in indexes 
	 */
	public SimpleMatrix getFilteredPoints(int[] indexes) {
		SimpleMatrix matrix = new SimpleMatrix(indexes.length, 3);
		for (int i = 0; i < indexes.length; i++) {
			PharmacophorePoint point = points.get(indexes[i]);
			matrix.setRow(i, 0, point.cx, point.cy, point.cz);
		}
		return matrix;
	}

	/**
	 * 
	 * @param matrix 4x4 transformation matrix
	 * @return new Pharmacophore which has all it's points transformed by the matrix
	 */
	public Pharmacophore transform(SimpleMatrix matrix) {
		List<PharmacophorePoint> points2 = new ArrayList<>();
		for (PharmacophorePoint point : points) {
			points2.add(point.transform(matrix));
		}
		return new Pharmacophore(identifier, points2);
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<PharmacophorePoint> getPoints() {
		return points;
	}

	/**
	 * Reads a phar formatted stream and outputs a list of pharmacophores
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static List<Pharmacophore> fromStream(InputStream input) throws IOException {
		String sep = "\n";
		List<Pharmacophore> out = new ArrayList<>();
		String bsep = "$$$$";
		String pharBlock = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = in.readLine()) != null) {
			pharBlock += line + sep;
			if (line.startsWith(bsep)) {
				out.add(new Pharmacophore(pharBlock));
				pharBlock = "";
			}
		}
		return out;
	}
}
