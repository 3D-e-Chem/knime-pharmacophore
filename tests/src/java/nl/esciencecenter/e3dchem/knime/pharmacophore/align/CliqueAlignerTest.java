package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class CliqueAlignerTest {
	private List<PointPair> buildClique(int capacity) {
		List<PointPair> list = new ArrayList<>();
		for (int j = 0; j < capacity; j++) {
			list.add(new PointPair(j, j));
		}
		return list;
	}

	public void assertPharmacophoreEquals(Pharmacophore expected, Pharmacophore actual, double tol) {
		assertEquals("Identifier", expected.getIdentifier(), actual.getIdentifier());
		List<String> expectedTypes = expected.getPoints().stream().map(PharmacophorePoint::getType)
				.collect(Collectors.toList());
		List<String> actualTypes = actual.getPoints().stream().map(PharmacophorePoint::getType)
				.collect(Collectors.toList());
		assertEquals("Types", expectedTypes, actualTypes);
		SimpleMatrix expectedMat = expected.getPointsMatrix();
		SimpleMatrix actualMat = actual.getPointsMatrix();
		if (!expectedMat.isIdentical(actualMat, tol)) {
			assertEquals("Coordinates", expectedMat.toString(), actualMat.toString());
		}
	}

	@Test
	public void test_centroid() {
		SimpleMatrix source = new SimpleMatrix(new double[][] { { 1, 1, 1 }, { 3, 2, 1 }, { 1, 3, 2 }, { 1, 1, 3 } });

		SimpleMatrix actual = CliqueAligner.centroid(source);

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 1.500, 1.750, 1.750 } });
		assertTrue(expected.isIdentical(actual, 0.001));
	}

	@Test
	public void test_move() {
		SimpleMatrix input = SimpleMatrix.identity(3);
		SimpleMatrix offset = new SimpleMatrix(new double[][] { { 1, 2, 3 } });
		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 2, 2, 3 }, { 1, 3, 3 }, { 1, 2, 4 } });

		SimpleMatrix output = CliqueAligner.move(input, offset);

		assertTrue(expected.isIdentical(output, 0.00001));
	}

	@Test(expected=NoOverlapFoundException.class)
	public void test_emptyClique_exception() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid");
		Pharmacophore probe = new Pharmacophore("someid");
		List<PointPair> clique = buildClique(0);

		new CliqueAligner(probe, reference, clique);
	}
	
	@Test
	public void test_same() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.35, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.35, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(probe, aligned, 0.001);
		assertEquals(0.0, aligner.getRMSD(), 0.0001);
		SimpleMatrix expected = SimpleMatrix.identity(4);
		assertTrue("matrix", expected.isIdentical(aligner.getMatrix(), 0.001));
	}

	@Test
	public void test_getMatrixAsArray() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.35, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.35, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		double[] result = aligner.getMatrixAsArray();
		double[] expected = new double[] { 
				1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, 1.0
		};
		assertArrayEquals(result, expected, 0.001);
	}
	
	@Test
	public void test_translatedXY() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.35, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		// probe has translated by x+1, y-2
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 8.5, -2.5, 0.0, 1), new PharmacophorePoint("LIPO", -5.5, -2.5, 0.0, 1),
				new PharmacophorePoint("NEGC", 0.5, 1.5, 0.0, 1), new PharmacophorePoint("AROM", 0.5, -4.5, 0.0, 1),
				new PharmacophorePoint("HYBL", 1.0, -2.0, 1.35, 1),
				new PharmacophorePoint("HYBH", 1.0, -2.0, -1.35, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
		assertEquals(8.133220e-16, aligner.getRMSD(), 1e-21);
	}

	@Test
	public void test_rotatedXClockwise() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.05, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.05, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.05, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.05, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.15, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		// rotated along x axis 90 degrees clockwise
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, 0.05, 0.5, 1), new PharmacophorePoint("LIPO", -6.5, 0.05, 0.5, 1),
				new PharmacophorePoint("NEGC", -0.5, 0.05, -3.5, 1), new PharmacophorePoint("AROM", -0.5, 0.05, 2.5, 1),
				new PharmacophorePoint("HYBL", 0.0, 1.15, 0.0, 1), new PharmacophorePoint("HYBH", 0.0, -1.35, 0.0, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
		assertEquals(1.0275812e-15, aligner.getRMSD(), 1e-20);
	}

	@Test
	public void test_rotatedXAntiClockwise() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.05, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.05, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.05, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.05, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.15, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		// rotated along x axis 90 degrees anti clockwise
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("HDON", 7.5, -0.05, -0.5, 1),
						new PharmacophorePoint("LIPO", -6.5, -0.05, -0.5, 1),
						new PharmacophorePoint("NEGC", -0.5, -0.05, 3.5, 1),
						new PharmacophorePoint("AROM", -0.5, -0.05, -2.5, 1),
						new PharmacophorePoint("HYBL", 0.0, -1.15, 0.0, 1),
						new PharmacophorePoint("HYBH", 0.0, 1.35, 0.0, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
		assertEquals(1.02758e-15, aligner.getRMSD(), 1e-19);
	}

	@Test
	public void test_translatedXAndRotatedXCW() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.05, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.05, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.05, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.05, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.15, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));
		// translated by x+1 and rotated along x axis 90 degrees clockwise
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 8.5, 0.05, 0.5, 1), new PharmacophorePoint("LIPO", -5.5, 0.05, 0.5, 1),
				new PharmacophorePoint("NEGC", 0.5, 0.05, -3.5, 1), new PharmacophorePoint("AROM", 0.5, 0.05, 2.5, 1),
				new PharmacophorePoint("HYBL", 1.0, 1.15, 0.0, 1), new PharmacophorePoint("HYBH", 1.0, -1.35, 0.0, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
		assertEquals(8.133220e-16, aligner.getRMSD(), 1e-20);
	}

	@Test
	public void test_translatedXZAndRotatedXCW() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.05, 1), new PharmacophorePoint("LIPO", -6.5, -0.5, 0.05, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.05, 1), new PharmacophorePoint("AROM", -0.5, -2.5, 0.05, 1),
				new PharmacophorePoint("HYBL", 0.0, 0.0, 1.15, 1), new PharmacophorePoint("HYBH", 0.0, 0.0, -1.35, 1)));

		// translated by x+1, y+2,z-1 and rotated along y axis 90 degrees
		// clockwise
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("HDON", -1.05, -0.5, -6.5, 1),
						new PharmacophorePoint("LIPO", -1.05, -0.5, 7.5, 1),
						new PharmacophorePoint("NEGC", -1.05, 3.5, 1.5, 1),
						new PharmacophorePoint("AROM", -1.05, -2.5, 1.5, 1),
						new PharmacophorePoint("HYBL", -2.15, 0, 1, 1), new PharmacophorePoint("HYBH", 0.35, 0, 1, 1)));
		List<PointPair> clique = buildClique(6);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
		assertEquals(1.066394e-15, aligner.getRMSD(), 1e-20);
	}
	
	@Test
	public void test_probeBiggerThanRef() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1), new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
				new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1), new PharmacophorePoint("HDON", 15.5, -2.5, 1.0, 1)));
		List<PointPair> clique = buildClique(3);

		CliqueAligner aligner = new CliqueAligner(probe, reference, clique);

		SimpleMatrix actual = aligner.getMatrix();

		actual.setColumn(3, 0, 0, 0, 0);
		SimpleMatrix expected = SimpleMatrix.identity(4);
		assertTrue("No rotation and ignore translate", expected.isIdentical(actual, 0.001));
		assertEquals(0.0, aligner.getRMSD(), 0.0001);
	}
}
