package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.util.Objects;

import org.ejml.simple.SimpleMatrix;

public class PharmacophorePoint {
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
		this.type = cols[0];
		this.cx = Double.parseDouble(cols[1]);
		this.cy = Double.parseDouble(cols[2]);
		this.cz = Double.parseDouble(cols[3]);
		this.alpha = Double.parseDouble(cols[4]);
		this.norm = cols[5];
		this.nx = Double.parseDouble(cols[6]);
		this.ny = Double.parseDouble(cols[7]);
		this.nz = Double.parseDouble(cols[8]);
	}

	public PharmacophorePoint(String type, double cx, double cy, double cz, double alpha, String norm, double nx,
			double ny, double nz) {
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
		this.type = type;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.alpha = alpha;
		this.norm = "0";
		this.nx = 0;
		this.ny = 0;
		this.nz = 0;
	}

	public String toString() {
		return String.join(" ", toArray());
	}

	private String[] toArray() {
		return new String[] { type, String.format("%.4f", cx), String.format("%.4f", cy), String.format("%.4f", cz),
				String.format("%.4f", alpha), norm, String.format("%.4f", nx), String.format("%.4f", ny),
				String.format("%.4f", nz) };
	}

	public SimpleMatrix getPointAsMatrix() {
		return new SimpleMatrix(new double[][] { { cx, cy, cz } });
	}

	public double distance(PharmacophorePoint other) {
		double a = Math.sqrt(this.getPointAsMatrix().elementPower(2).elementSum());
		double b = Math.sqrt(other.getPointAsMatrix().elementPower(2).elementSum());
		double dist = Math.abs(a - b);
		return dist;
	}

	public boolean hasNormal() {
		return norm.equals("1");
	}

	public PharmacophorePoint transform(SimpleMatrix matrix) {
		double cx2 = matrix.get(0, 0) * cx + matrix.get(0, 1) * cy + matrix.get(0, 2) * cz + matrix.get(0, 3);
		;
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
}
