package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

public class Pharmacophore {
	private String identifier;
	private List<PharmacophorePoint> points = new ArrayList<>();

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

	public SimpleMatrix getPointsMatrix() {
		SimpleMatrix matrix = new SimpleMatrix(points.size(), 3);
		int i = 0;
		for (PharmacophorePoint point : points) {
			matrix.setRow(i++, 0, point.cx, point.cy, point.cz);
		}
		return matrix;
	}

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

	public String toString() {
		StringBuilder buf = new StringBuilder(512);
		String sep = System.getProperty("line.separator");
		buf.append(identifier).append(sep);
		for (PharmacophorePoint point : points) {
			buf.append(point).append(sep);
		}
		buf.append("$$$$").append(sep);
		return buf.toString();
	}

	public int size() {
		return points.size();
	}

	public PharmacophorePoint get(int index) {
		return points.get(index);
	}

	public SimpleMatrix getFilteredPoints(int[] indexes) {
		SimpleMatrix matrix = new SimpleMatrix(indexes.length, 3);
		for (int i = 0; i < indexes.length; i++) {
			PharmacophorePoint point = points.get(indexes[i]);
			matrix.setRow(i, 0, point.cx, point.cy, point.cz);
		}
		return matrix;
	}

	public SimpleMatrix getCentroid() {
		DMatrixRMaj colSums = org.ejml.dense.row.CommonOps_DDRM.sumCols(getPointsMatrix().matrix_F64(), null);
		return SimpleMatrix.wrap(colSums).divide(points.size());
	}

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

	public static List<Pharmacophore> fromStream(InputStream input) throws IOException {
		String sep = System.getProperty("line.separator");
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
