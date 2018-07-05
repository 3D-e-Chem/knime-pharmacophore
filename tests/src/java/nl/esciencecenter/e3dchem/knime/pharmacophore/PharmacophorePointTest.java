package nl.esciencecenter.e3dchem.knime.pharmacophore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class PharmacophorePointTest {
	private PharmacophorePoint aPoint() {
		return new PharmacophorePoint("LIPO", 12.3971, 28.8415, 21.9387, 1.0);
	}

	private PharmacophorePoint bPoint() {
		return new PharmacophorePoint("LIPO", 12.3971, 28.8415, 21.9387, 0, 1.234, 2.456, 3.789);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPharmaCophore_badtype() {
		new PharmacophorePoint("FOOBAR", 12.3971, 28.8415, 21.9387, 1.0);
	}

	@Test
	public void testPharmacophorePointStringArray() {
		String[] cols = new String[] { "LIPO", "12.3971", "28.8415", "21.9387", "1", "0", "0", "0", "0" };
		PharmacophorePoint actual = new PharmacophorePoint(cols);
		PharmacophorePoint expected = aPoint();
		assertEquals(expected, actual);
	}

	@Test
	public void testToString() {
		PharmacophorePoint p = aPoint();
		String actual = p.toString();
		String expected = "LIPO 12.3971 28.8415 21.9387 1 0 0 0 0";
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

	@Test
	public void testTransformWithNormal_identity_sameasin() {
		SimpleMatrix matrix = SimpleMatrix.identity(4);

		PharmacophorePoint point = bPoint();

		PharmacophorePoint actual = point.transform(matrix);

		PharmacophorePoint expected = bPoint();
		assertEquals(expected, actual);
	}

	@Test
	public void test_getDefaultAlpha_AROM() {
		assertEquals(0.7, PharmacophorePoint.getDefaultAlpha("AROM"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_HDON() {
		assertEquals(1.0, PharmacophorePoint.getDefaultAlpha("HDON"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_HACC() {
		assertEquals(1.0, PharmacophorePoint.getDefaultAlpha("HACC"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_LIPO() {
		assertEquals(0.7, PharmacophorePoint.getDefaultAlpha("LIPO"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_POSC() {
		assertEquals(1.0, PharmacophorePoint.getDefaultAlpha("POSC"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_NEGC() {
		assertEquals(1.0, PharmacophorePoint.getDefaultAlpha("NEGC"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_HYBH() {
		assertEquals(1.0, PharmacophorePoint.getDefaultAlpha("HYBH"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_HYBL() {
		assertEquals(0.7, PharmacophorePoint.getDefaultAlpha("HYBL"), 0.0001);
	}

	@Test
	public void test_getDefaultAlpha_EXCL() {
		assertEquals(1.7, PharmacophorePoint.getDefaultAlpha("EXCL"), 0.0001);
	}

}
