package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class PharmacophoreTest {
	private double precision = 0.001;

	@Test
	public void testPharmacophoreStringListOfPharmacophorePoint() {
		String id = "someid";
		List<PharmacophorePoint> points = Arrays.asList(new PharmacophorePoint("LIPO", 12.3971, 28.8415, 21.9387, 0));

		Pharmacophore actual = new Pharmacophore(id, points);

		assertEquals(id, actual.getIdentifier());
		assertEquals(points, actual.getPoints());
	}

	private Pharmacophore aPharmacophore() {
		Pharmacophore p = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 1, 1, 1, 0), new PharmacophorePoint("LIPO", 3, 2, 1, 0),
						new PharmacophorePoint("LIPO", 1, 3, 2, 0), new PharmacophorePoint("LIPO", 1, 1, 3, 0)));
		return p;
	}

	@Test
	public void testGetPointsMatrix() {
		Pharmacophore p = aPharmacophore();

		SimpleMatrix actual = p.getPointsMatrix();

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 1, 1, 1 }, { 3, 2, 1 }, { 1, 3, 2 }, { 1, 1, 3 } });
		assertTrue(expected.isIdentical(actual, precision));
	}

	@Test
	public void testGetDistancesBetweenPoints() {
		Pharmacophore p = aPharmacophore();

		SimpleMatrix actual = p.getDistancesBetweenPoints();

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 0.000, 2.010, 2.010, 1.585 },
				{ 2.010, 0.000, 0.000, 0.425 }, { 2.010, 0.000, 0.000, 0.425 }, { 1.585, 0.425, 0.425, 0.000 } });
		assertTrue(expected.isIdentical(actual, precision));
	}

	@Test
	public void testSize() {
		Pharmacophore p = aPharmacophore();

		assertEquals(4, p.size());
	}

	@Test
	public void testGet() {
		Pharmacophore p = aPharmacophore();

		PharmacophorePoint actual = p.get(0);

		PharmacophorePoint expected = new PharmacophorePoint("LIPO", 1, 1, 1, 0);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetFilteredPoints() {
		Pharmacophore p = aPharmacophore();

		SimpleMatrix actual = p.getFilteredPoints(new int[] { 1, 2 });

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 3, 2, 1 }, { 1, 3, 2 } });
		assertTrue(expected.isIdentical(actual, precision));
	}

	@Test
	public void testGetCentroid() {
		Pharmacophore p = aPharmacophore();

		SimpleMatrix actual = p.getCentroid();

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 1.500, 1.750, 1.750 } });
		assertTrue(expected.isIdentical(actual, precision));
	}

	@Test
	public void testTransform_identity_sameasin() {
		SimpleMatrix matrix = SimpleMatrix.identity(4);
		Pharmacophore p = aPharmacophore();

		Pharmacophore actual = p.transform(matrix);
		assertEquals(p.toString(), actual.toString());
	}

}
