package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;

public class PharmacophorePoint {
	public static final Set<String> VALID_TYPES = new HashSet<>(
			Arrays.asList("AROM", "HDON", "HACC", "LIPO", "POSC", "NEGC", "HYBH", "HYBL", "EXCL"));
	public String type;
	public double cx;
	public double cy;
	public double cz;
	public double alpha;
	public String norm;
	public double nx;
	public double ny;
	public double nz;

	public PharmacophorePoint(String[] cols) {
		this(cols[0], Double.parseDouble(cols[1]), Double.parseDouble(cols[2]), Double.parseDouble(cols[3]),
				Double.parseDouble(cols[4]), cols[5], Double.parseDouble(cols[6]), Double.parseDouble(cols[7]),
				Double.parseDouble(cols[8]));
	}

	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha, String norm, double nx,
			double ny, double nz) {
		if (!VALID_TYPES.contains(type)) {
			throw new IllegalArgumentException("Unknown type, should be one of " + String.join(",", VALID_TYPES));
		}
		this.type = type;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.alpha = alpha;
		this.norm = norm;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}

	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha) {
		this(type, cx, cy, cz, alpha, "0", 0, 0, 0);
	}

	public String toString() {
		return String.join(" ", toArray());
	}

	private String[] toArray() {
		DecimalFormat df = new DecimalFormat("0.####");
		return new String[] { type, df.format(cx), df.format(cy), df.format(cz), df.format(alpha), norm, df.format(nx),
				df.format(ny), df.format(nz) };
	}

	public SimpleMatrix getPointAsMatrix() {
		return new SimpleMatrix(new double[][] { { cx, cy, cz } });
	}

	public double distance(PharmacophorePoint other) {
		return Math.sqrt(
				Math.pow(this.cx - other.cx, 2) + Math.pow(this.cy - other.cy, 2) + Math.pow(this.cz - other.cz, 2));
	}

	public boolean hasNormal() {
		return norm.equals("1");
	}

	public PharmacophorePoint transform(SimpleMatrix matrix) {
		double cx2 = matrix.get(0, 0) * cx + matrix.get(0, 1) * cy + matrix.get(0, 2) * cz + matrix.get(0, 3);
		double cy2 = matrix.get(1, 0) * cx + matrix.get(1, 1) * cy + matrix.get(1, 2) * cz + matrix.get(1, 3);
		double cz2 = matrix.get(2, 0) * cx + matrix.get(2, 1) * cy + matrix.get(2, 2) * cz + matrix.get(2, 3);
		double nx2 = nx;
		double ny2 = ny;
		double nz2 = nz;
		if (hasNormal()) {
			nx2 = matrix.get(0, 0) * nx + matrix.get(0, 1) * ny + matrix.get(0, 2) * nz + matrix.get(0, 3);
			ny2 = matrix.get(1, 0) * nx + matrix.get(1, 1) * ny + matrix.get(1, 2) * nz + matrix.get(1, 3);
			nz2 = matrix.get(2, 0) * nx + matrix.get(2, 1) * ny + matrix.get(2, 2) * nz + matrix.get(2, 3);
		}
		return new PharmacophorePoint(type, cx2, cy2, cz2, alpha, norm, nx2, ny2, nz2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, cx, cy, cz, alpha, norm, nx, ny, nz);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PharmacophorePoint that = (PharmacophorePoint) o;
		return Objects.equals(type, that.type) && Objects.equals(cx, that.cx) && Objects.equals(cy, that.cy)
				&& Objects.equals(cz, that.cz) && Objects.equals(alpha, that.alpha) && Objects.equals(norm, that.norm)
				&& Objects.equals(nx, that.nx) && Objects.equals(ny, that.ny) && Objects.equals(nz, that.nz);
	}

	public String getType() {
		return type;
	}
}
