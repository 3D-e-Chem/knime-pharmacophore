package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

public class CliqueAligner {
	private Pharmacophore probe;
	private Pharmacophore reference;
	private List<PointPair> clique;
	private SimpleMatrix matrix;
	private double rmsd;
	
	public CliqueAligner(Pharmacophore probe, Pharmacophore reference, List<PointPair> clique) throws NoOverlapFoundException {
		super();
		this.probe = probe;
		this.reference = reference;
		this.clique = clique;
		matrix = computeMatrix();
		rmsd = computeRMSD();
	}

	private SimpleMatrix filterRefPointsByClique() {
		int[] indexes = clique.stream().mapToInt(p -> p.first).toArray();
		return reference.getFilteredPoints(indexes);
	}

	private SimpleMatrix filterProbePointsByClique() {
		int[] indexes = clique.stream().mapToInt(p -> p.second).toArray();
		return probe.getFilteredPoints(indexes);
	}
	
	public static SimpleMatrix move(SimpleMatrix points, SimpleMatrix offset) {
		points.setColumn(0, 0, points.extractVector(false, 0).plus(offset.get(0, 0)).matrix_F64().getData());
		points.setColumn(1, 0, points.extractVector(false, 1).plus(offset.get(0, 1)).matrix_F64().getData());
		points.setColumn(2, 0, points.extractVector(false, 2).plus(offset.get(0, 2)).matrix_F64().getData());
		return points;
	}

	public static SimpleMatrix centroid(SimpleMatrix points) {
		DMatrixRMaj colSums = org.ejml.dense.row.CommonOps_DDRM.sumCols(points.matrix_F64(), null);
		return SimpleMatrix.wrap(colSums).divide(points.numRows());
	}
	
	private SimpleMatrix computeMatrix() throws NoOverlapFoundException {
		if (clique.isEmpty()) {
			throw new NoOverlapFoundException();
		}

		SimpleMatrix refCliquePoints = filterRefPointsByClique();
		SimpleMatrix probeCliquePoints = filterProbePointsByClique();

		// KABSCH algorithm to translate+rotate probe points on to ref points

		SimpleMatrix refCentroid = centroid(refCliquePoints);
		SimpleMatrix probeCentroid = centroid(probeCliquePoints);

		SimpleMatrix refCentered = move(refCliquePoints, refCentroid.negative());
		SimpleMatrix probeCentered = move(probeCliquePoints, probeCentroid.negative());

		SimpleMatrix cov = refCentered.transpose().mult(probeCentered);
		SimpleSVD<SimpleMatrix> svd = cov.svd();

		double d = svd.getV().mult(svd.getU().transpose()).determinant();
		SimpleMatrix U = svd.getU();
		if (d < 0) {
            // correct our rotation matrix to ensure a right-handed coordinate system
            U.setColumn(2, 0, U.extractVector(false, 2).scale(d).matrix_F64().getData());
		}

		SimpleMatrix R = U.mult(svd.getV().transpose());

		// 4x4 matrix
		SimpleMatrix matrix = SimpleMatrix.identity(4);
		matrix.setRow(0, 0, R.extractVector(true, 0).matrix_F64().getData());
		matrix.setRow(1, 0, R.extractVector(true, 1).matrix_F64().getData());
		matrix.setRow(2, 0, R.extractVector(true, 2).matrix_F64().getData());
		// the R is for rotating around origin, but probe and reference are
		// offsetted
		// so substract probe offset after rotation and add reference offset
		matrix.set(0, 3, refCentroid.get(0, 0) - (R.get(0, 0) * probeCentroid.get(0, 0))
				- (R.get(0, 1) * probeCentroid.get(0, 1)) - (R.get(0, 2) * probeCentroid.get(0, 2)));
		matrix.set(1, 3, refCentroid.get(0, 1) - (R.get(1, 0) * probeCentroid.get(0, 0))
				- (R.get(1, 1) * probeCentroid.get(0, 1)) - (R.get(1, 2) * probeCentroid.get(0, 2)));
		matrix.set(2, 3, refCentroid.get(0, 2) - (R.get(2, 0) * probeCentroid.get(0, 0))
				- (R.get(2, 1) * probeCentroid.get(0, 1)) - (R.get(2, 2) * probeCentroid.get(0, 2)));
		return matrix;
	}
	
	private SimpleMatrix alignPoints(SimpleMatrix unaligned) {
		SimpleMatrix aligned = new SimpleMatrix(unaligned.numRows(), 3);
		int nrRows = unaligned.numRows();
		for (int i = 0; i < nrRows; i++) {
			double cx = unaligned.get(i, 0);
			double cy = unaligned.get(i, 1);
			double cz = unaligned.get(i, 2);
			double cx2 = matrix.get(0, 0) * cx + matrix.get(0, 1) * cy + matrix.get(0, 2) * cz + matrix.get(0, 3);
			double cy2 = matrix.get(1, 0) * cx + matrix.get(1, 1) * cy + matrix.get(1, 2) * cz + matrix.get(1, 3);
			double cz2 = matrix.get(2, 0) * cx + matrix.get(2, 1) * cy + matrix.get(2, 2) * cz + matrix.get(2, 3);
			aligned.set(i, 0, cx2);
			aligned.set(i, 1, cy2);
			aligned.set(i, 2, cz2);
		}
		return aligned;
	}
	
	private double computeRMSD() {
		SimpleMatrix ref = filterRefPointsByClique();
		SimpleMatrix unaligned = filterProbePointsByClique();
		SimpleMatrix aligned = alignPoints(unaligned);
		double delta = 0;
		int nrRows = aligned.numRows();
		for (int i = 0; i < nrRows; i++) {
			delta += Math.pow(ref.get(i, 0) - aligned.get(i, 0), 2) + Math.pow(ref.get(i, 1) - aligned.get(i, 1), 2)
					+ Math.pow(ref.get(i, 2) - aligned.get(i, 2), 2);
		}
        return Math.sqrt((1.0 / nrRows) * delta);
	}
	
	public double[] getMatrixAsArray() {
		return matrix.matrix_F64().getData();
	}

	public SimpleMatrix getMatrix() {
		return matrix;
	}

	public double getRMSD() {
		return rmsd;
	}

	public Pharmacophore getAligned() {
		return probe.transform(matrix);
	}

    public int getCliqueSize() {
        return clique.size();
    }
}
