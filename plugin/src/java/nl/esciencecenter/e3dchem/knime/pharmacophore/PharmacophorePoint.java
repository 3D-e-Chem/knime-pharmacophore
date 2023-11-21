package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;

public class PharmacophorePoint {
	/**
	 * Set of valid pharmacophore types
	 */
	public static final Set<String> VALID_TYPES = new HashSet<>(
			Arrays.asList("AROM", "HDON", "HACC", "LIPO", "POSC", "NEGC", "HYBH", "HYBL", "EXCL"));
	/**
	 * The default alpha for each type
	 * 
	 * @param type
	 * @return alpha value
	 */
	public static double getDefaultAlpha(String type) {
		switch (type) {
		case "AROM":
			return 0.7;
		case "HDON":
			return 1.0;
		case "HACC":
			return 1.0;
		case "LIPO":
			return 0.7;
		case "POSC":
			return 1.0;
		case "NEGC":
			return 1.0;
		case "HYBH":
			return 1.0;
		case "HYBL":
			return 0.7;
		case "EXCL":
			return 1.7;
		default:
			throw new IllegalArgumentException("Unknown type, should be one of " + String.join(",", VALID_TYPES));
		}
	}
	public final String type;
	public final double cx;
	public final double cy;
	public final double cz;
	public final double alpha;
	public final String norm;
	public final double nx;
	public final double ny;
	public final double nz;

	/**
	 * Construct point based on split on whitespace of a line in a phar formatted file.
	 * @param cols
	 */
	public PharmacophorePoint(String[] cols) {
		this(cols[0], Double.parseDouble(cols[1]), Double.parseDouble(cols[2]), Double.parseDouble(cols[3]),
				Double.parseDouble(cols[4]), cols[5], Double.parseDouble(cols[6]), Double.parseDouble(cols[7]),
				Double.parseDouble(cols[8]));
	}

	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha, String norm, double nx,
			double ny, double nz) {
		if (!VALID_TYPES.contains(type)) {
			throw new IllegalArgumentException("Unknown \"" + type + "\" type, should be one of " + String.join(",", VALID_TYPES));
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

	/**
	 * Create point without direction
	 * 
	 * @param type
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param alpha
	 */
	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha) {
		this(type, cx, cy, cz, alpha, "0", 0, 0, 0);
	}

	/**
	 * Create point with direction
	 * 
	 * @param type
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param alpha
	 * @param nx Direction x
	 * @param ny Direction y
	 * @param nz Direction z
	 */
	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha, double nx,
			double ny, double nz) {
		this(type, cx, cy, cz, alpha, "1", nx, ny, nz);
	}

	public String toString() {
		return String.join(" ", toArray());
	}

	/**
	 * @return Point as string according to Phar file format
	 */
	private String[] toArray() {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(4);
		return new String[] {
				type,
				nf.format(cx),		
				nf.format(cy),		
				nf.format(cz),		
				nf.format(alpha),
				norm,
				nf.format(nx),		
				nf.format(ny),		
				nf.format(nz),		
		};
	}

	/**
	 * @return Point coordinate as a 3x1 matrix
	 */
	public SimpleMatrix getPointAsMatrix() {
		return new SimpleMatrix(new double[][] { { cx, cy, cz } });
	}

	/**
	 * Euclidean distance between this point and a other point
	 * 
	 * @param other
	 * @return distance
	 */
	public double distance(PharmacophorePoint other) {
		return Math.sqrt(
				Math.pow(this.cx - other.cx, 2) + Math.pow(this.cy - other.cy, 2) + Math.pow(this.cz - other.cz, 2));
	}

	public boolean hasNormal() {
		return norm.equals("1");
	}

	/**
	 * Transform by a transformation matrix
	 * 
	 * @param matrix 4x4 transformation matrix
	 * @return new point with transformed coordinates for center and normal
	 */
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
