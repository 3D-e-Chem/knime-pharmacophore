package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class PharmacophorePointTest {

	private PharmacophorePoint aPoint() {
		return new PharmacophorePoint("LIPO", 12.3971, 28.8415, 21.9387, 0, "0", 0, 0, 0);
	}

	@Test
	public void testPharmacophorePointStringArray() {
		String[] cols = new String[] { "LIPO", "12.3971", "28.8415", "21.9387", "0", "0", "0", "0", "0" };
		PharmacophorePoint actual = new PharmacophorePoint(cols);
		PharmacophorePoint expected = aPoint();
		assertEquals(expected, actual);
	}

	@Test
	public void testToString() {
		PharmacophorePoint p = aPoint();
		String actual = p.toString();
		String expected = "LIPO 12.3971 28.8415 21.9387 0.0000 0 0.0000 0.0000 0.0000";
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPointAsMatrix() {
		SimpleMatrix actual = aPoint().getPointAsMatrix();
		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 12.3971, 28.8415, 21.9387 } });
		assertTrue(expected.isIdentical(actual, 0.0001));
	}

	@Test
	public void testDistance_samepoint_distance0() {
		double actual = aPoint().distance(aPoint());
		assertEquals(0.0, actual, 0.0001);
	}

	@Test
	public void testHasNormal() {
		assertFalse(aPoint().hasNormal());
	}

	@Test
	public void testTransform_identity_sameasin() {
		SimpleMatrix matrix = SimpleMatrix.identity(4);

		PharmacophorePoint actual = aPoint().transform(matrix);

		PharmacophorePoint expected = aPoint();
		assertEquals(expected, actual);
	}

}
