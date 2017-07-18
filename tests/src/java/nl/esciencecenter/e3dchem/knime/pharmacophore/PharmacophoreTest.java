package nl.esciencecenter.e3dchem.knime.pharmacophore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class PharmacophoreTest {
	private double precision = 0.001;

	@Test
	public void testPharmacophoreString() {
		String sep = "\n";
		String pharBlock = String.join(sep, new String[] { "someid", "HACC -1.7076 2.2682 22.7126 0 0 0 0 0", "$$$$" });

		Pharmacophore actual = new Pharmacophore(pharBlock);

		assertEquals("someid", actual.getIdentifier());
		List<PharmacophorePoint> expected = Arrays.asList(new PharmacophorePoint("HACC", -1.7076, 2.2682, 22.7126, 0));
		assertEquals(expected, actual.getPoints());
	}

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

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 0.000, 2.236, 2.236, 2.000 },
				{ 2.236, 0.000, 2.449, 3.000 }, { 2.236, 2.449, 0.000, 2.236 }, { 2.000, 3.000, 2.236, 0.000 } });
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
	public void testTransform_identity_sameasin() {
		SimpleMatrix matrix = SimpleMatrix.identity(4);
		Pharmacophore p = aPharmacophore();

		Pharmacophore actual = p.transform(matrix);
		assertEquals(p.toString(), actual.toString());
	}

	@Test
	public void test_fromStream() throws IOException {
		String sep = "\n";
		String pharBlocks = String.join(sep,
				new String[] { "someid1", "HACC -1.7076 2.2682 22.7126 0 0 0 0 0", "$$$$", "someid2",
						"HACC -1.7076 2.2682 22.7126 0 0 0 0 0", "$$$$", "someid3",
						"HACC -1.7076 2.2682 22.7126 0 0 0 0 0", "$$$$" });
		InputStream input = new ByteArrayInputStream(pharBlocks.getBytes(Charset.defaultCharset()));

		List<Pharmacophore> actual = Pharmacophore.fromStream(input);

		List<Pharmacophore> expected = Arrays.asList(
				new Pharmacophore("someid1",
						Arrays.asList(new PharmacophorePoint("HACC", -1.7076, 2.2682, 22.7126, 0))),
				new Pharmacophore("someid2",
						Arrays.asList(new PharmacophorePoint("HACC", -1.7076, 2.2682, 22.7126, 0))),
				new Pharmacophore("someid3",
						Arrays.asList(new PharmacophorePoint("HACC", -1.7076, 2.2682, 22.7126, 0))));
		assertEquals(expected.toString(), actual.toString());
	}

}
