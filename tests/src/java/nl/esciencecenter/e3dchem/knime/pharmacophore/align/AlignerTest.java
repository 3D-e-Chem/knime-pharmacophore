package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class AlignerTest {

	@Test
	public void test_reallive() throws NoOverlapFoundException {
		String sep = System.getProperty("line.separator");
		String reference = String.join(sep,
				new String[] { "refid", "LIPO 26.0369 11.9800 4.3352 0 0 0 0 0 0",
						"LIPO 25.4947 12.4949 2.8223 0 0 0 0 0 0", "HDON 19.5809 17.5262 6.2020 0 0 0 0 0 0",
						"NEGC 20.3908 17.2075 6.9782 0 0 0 0 0 0", "NEGC 19.9137 17.6862 5.0959 0 0 0 0 0 0",
						"NEGC 18.4865 18.9091 6.7297 0 0 0 0 0 0", "NEGC 19.0379 25.9383 15.2001 0 0 0 0 0 0",
						"HDON 23.7426 26.2927 14.6188 0 0 0 0 0 0", "LIPO 16.9648 16.8184 12.5531 0 0 0 0 0 0",
						"LIPO 16.1188 17.5788 13.7996 0 0 0 0 0 0", "HACC 19.2926 17.1766 15.7563 0 0 0 0 0 0",
						"HDON 21.8512 17.5877 17.7648 0 0 0 0 0 0", "LIPO 21.6134 15.9862 15.1898 0 0 0 0 0 0",
						"LIPO 26.7691 19.8439 16.4741 0 0 0 0 0 0", "LIPO 29.1815 19.1760 16.1647 0 0 0 0 0 0",
						"LIPO 25.7991 21.2142 14.4885 0 0 0 0 0 0", "LIPO 27.2749 20.4918 14.1040 0 0 0 0 0 0",
						"LIPO 24.5309 21.1357 13.3778 0 0 0 0 0 0", "LIPO 23.9291 23.3071 12.7534 0 0 0 0 0 0",
						"LIPO 28.3741 16.1960 19.2624 0 0 0 0 0 0", "LIPO 25.9780 15.8915 15.4798 0 0 0 0 0 0",
						"LIPO 24.0567 16.5469 12.4554 0 0 0 0 0 0", "LIPO 24.9876 13.9507 11.4379 0 0 0 0 0 0",
						"LIPO 23.5648 15.1316 15.5744 0 0 0 0 0 0", "AROM 22.3755 20.4466 6.0574 0 0 0 0 0 0",
						"LIPO 23.8808 20.8064 10.9438 0 0 0 0 0 0", "LIPO 26.5379 14.6626 7.0921 0 0 0 0 0 0",
						"LIPO 25.3444 15.7184 4.3437 0 0 0 0 0 0", "HDON 23.2381 11.3095 3.8981 0 0 0 0 0 0",
						"HDON 23.5066 12.4505 3.4648 0 0 0 0 0 0", "LIPO 15.6875 21.3788 11.7234 0 0 0 0 0 0",
						"AROM 24.7701 15.9888 2.9569 0 0 0 0 0 0", "LIPO 24.4291 19.9667 5.6702 0 0 0 0 0 0",
						"LIPO 21.8130 19.6184 3.9196 0 0 0 0 0 0", "LIPO 21.1677 14.4833 8.2061 0 0 0 0 0 0",
						"LIPO 22.5933 12.8494 6.5634 0 0 0 0 0 0", "LIPO 19.3598 20.0103 15.4982 0 0 0 0 0 0",
						"LIPO 29.4735 17.1238 15.8539 0 0 0 0 0 0", "LIPO 28.4358 15.3412 17.9075 0 0 0 0 0 0",
						"LIPO 22.6927 21.2814 8.5115 0 0 0 0 0 0", "LIPO 20.6163 21.6970 6.7951 0 0 0 0 0 0", "$$$$" });
		String probe = String.join(sep,
				new String[] { "probid", "HACC -1.7076 2.2682 22.7126 0 0 0 0 0",
						"HDON -0.0317 4.6294 22.4973 0 0 0 0 0", "HDON 3.9657 -4.2182 19.4535 0 0 0 0 0",
						"AROM -4.7420 5.8751 25.9774 0 0 0 0 0", "HDON 3.7079 4.2267 24.4837 0 0 0 0 0",
						"NEGC 3.8882 3.0747 24.4667 0 0 0 0 0", "HDON 1.1487 5.4662 26.7621 0 0 0 0 0",
						"NEGC 0.5654 6.4209 26.4331 0 0 0 0 0", "NEGC 0.5855 4.6737 27.4061 0 0 0 0 0",
						"NEGC 2.1046 4.7328 25.3711 0 0 0 0 0", "LIPO -0.9244 -0.6887 25.2947 0 0 0 0 0",
						"LIPO 1.3499 0.1450 23.3679 0 0 0 0 0", "LIPO -0.2041 0.2003 24.0542 0 0 0 0 0",
						"LIPO -2.4841 -1.2942 25.0740 0 0 0 0 0", "LIPO -5.5423 0.1490 24.4589 0 0 0 0 0",
						"LIPO -2.2450 5.3313 29.7474 0 0 0 0 0", "LIPO -2.8484 1.4969 28.8643 0 0 0 0 0",
						"LIPO -0.7917 3.4229 28.1349 0 0 0 0 0", "LIPO 3.7043 2.5349 25.3078 0 0 0 0 0",
						"LIPO 1.4568 1.3568 25.5041 0 0 0 0 0", "LIPO 5.7106 2.4540 25.7827 0 0 0 0 0",
						"LIPO 5.4877 -3.2515 15.6444 0 0 0 0 0", "LIPO -0.1181 1.2310 19.4872 0 0 0 0 0",
						"LIPO -0.7460 1.0889 21.8024 0 0 0 0 0", "LIPO 6.2402 3.1961 22.3875 0 0 0 0 0",
						"LIPO -4.3778 0.0278 30.1472 0 0 0 0 0", "HACC 3.3976 -3.8262 16.5781 0 0 0 0 0",
						"HDON 2.9732 -3.4432 13.1614 0 0 0 0 0", "LIPO 1.9186 -2.1014 15.8518 0 0 0 0 0",
						"LIPO 1.0415 -0.2159 14.6303 0 0 0 0 0", "LIPO 2.9680 1.9694 17.1866 0 0 0 0 0",
						"LIPO 4.0759 -3.1885 20.6854 0 0 0 0 0", "LIPO 3.7108 -1.5415 20.7330 0 0 0 0 0",
						"LIPO 5.8368 0.5870 12.9128 0 0 0 0 0", "LIPO 5.5328 2.5229 16.8755 0 0 0 0 0",
						"LIPO 6.1775 0.2833 16.1111 0 0 0 0 0", "LIPO -4.4496 -1.8999 26.3564 0 0 0 0 0", "$$$$" });
		Aligner aligner = new Aligner(probe, reference);

		double expectedRmsd = 8.11258E-16;
		assertEquals(expectedRmsd, aligner.getRMSD(), 1E-18);

		String expectedAligned = String.join(sep, new String[] { "probid", "HACC 22.8831 33.4981 6.067 0 0 0 0 0",
				"HDON 19.9951 33.2857 5.8561 0 0 0 0 0", "HDON 25.1749 35.0962 -2.7122 0 0 0 0 0",
				"AROM 21.5033 33.9001 11.6178 0 0 0 0 0", "HDON 18.1871 36.672 4.0238 0 0 0 0 0",
				"NEGC 19.0417 37.0892 3.3489 0 0 0 0 0", "HDON 18.53 37.1627 7.618 0 0 0 0 0",
				"NEGC 18.0732 36.3424 8.3096 0 0 0 0 0", "NEGC 19.4848 37.7219 7.9865 0 0 0 0 0",
				"NEGC 18.6403 36.602 5.8688 0 0 0 0 0", "LIPO 24.8287 36.9451 5.4702 0 0 0 0 0",
				"LIPO 22.9173 35.9942 3.2291 0 0 0 0 0", "LIPO 23.7218 35.9125 4.7241 0 0 0 0 0",
				"LIPO 26.2079 36.291 6.1898 0 0 0 0 0", "LIPO 26.7367 34.0391 8.7321 0 0 0 0 0",
				"LIPO 20.4599 38.3327 11.5364 0 0 0 0 0", "LIPO 23.9986 38.5118 9.7219 0 0 0 0 0",
				"LIPO 21.272 38.1544 8.7682 0 0 0 0 0", "LIPO 19.5699 37.8973 3.6661 0 0 0 0 0",
				"LIPO 21.7971 37.4883 4.8279 0 0 0 0 0", "LIPO 18.5034 39.1678 2.439 0 0 0 0 0",
				"LIPO 23.623 32.1819 -5.3312 0 0 0 0 0", "LIPO 22.9392 31.7302 2.769 0 0 0 0 0",
				"LIPO 23.3471 33.4881 4.3558 0 0 0 0 0", "LIPO 17.6813 36.2609 0.6409 0 0 0 0 0",
				"LIPO 26.0373 39.4212 10.7988 0 0 0 0 0", "HACC 25.2428 32.2819 -3.6174 0 0 0 0 0",
				"HDON 25.2521 29.0683 -4.9111 0 0 0 0 0", "LIPO 24.6586 30.5091 -2.1322 0 0 0 0 0",
				"LIPO 23.6179 28.5159 -1.2603 0 0 0 0 0", "LIPO 20.663 30.8294 -0.2919 0 0 0 0 0",
				"LIPO 24.2275 35.8758 -1.6707 0 0 0 0 0", "LIPO 23.0651 35.2543 -0.6169 0 0 0 0 0",
				"LIPO 20.3178 28.8086 -5.2117 0 0 0 0 0", "LIPO 18.7792 31.4664 -2.0317 0 0 0 0 0",
				"LIPO 20.2953 31.7761 -3.9346 0 0 0 0 0", "LIPO 27.7747 36.7504 7.9811 0 0 0 0 0", "$$$$", "" });
		assertEquals(expectedAligned, aligner.getAligned().toString());

		SimpleMatrix expectedMatrix = new SimpleMatrix(new double[][] { { -0.559, -0.829, -0.026, 24.404, },
				{ 0.419, -0.309, 0.854, 15.521, }, { -0.716, 0.466, 0.520, -8.019, }, { 0.000, 0.000, 0.000, 1.000 } });
		SimpleMatrix matrix = aligner.getMatrix();
		System.out.println(matrix);
		assertTrue(expectedMatrix.isIdentical(matrix, 0.001));
	}

	@Test
	public void test_move() {
		SimpleMatrix input = SimpleMatrix.identity(3);
		SimpleMatrix offset = new SimpleMatrix(new double[][] { { 1, 2, 3 } });
		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 2, 2, 3 }, { 1, 3, 3 }, { 1, 2, 4 } });

		SimpleMatrix output = Aligner.move(input, offset);

		assertTrue(expected.isIdentical(output, 0.00001));
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

		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(probe, aligned, 0.001);
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

		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
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
		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
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
		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
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
		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
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
		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();
		assertPharmacophoreEquals(reference, aligned, 0.001);
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
	public void test_scaled_nochange() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.0, 0.0, 1),
						new PharmacophorePoint("AROM", 0.0, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", -0.5, 0.0, -0.5, 1)));
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.25, 0.0, 0.0, 1),
						new PharmacophorePoint("AROM", 0.0, 0.0, 0.25, 1),
						new PharmacophorePoint("HDON", -0.25, 0.0, -0.25, 1)));
		Aligner aligner = new Aligner(probe, reference);

		Pharmacophore aligned = aligner.getAligned();

		assertPharmacophoreEquals(probe, aligned, 0.001);
	}

	@Test(expected = NoOverlapFoundException.class)
	public void test_nooverlapetype() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("HACC", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("POSC", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("NEGC", 0.5, 0.5, 0.0, 1)));
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		new Aligner(probe, reference);
	}

	@Test(expected = NoOverlapFoundException.class)
	public void test_toofewpoints() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1)));
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1)));
		new Aligner(probe, reference);
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
		Aligner aligner = new Aligner(probe, reference);

		SimpleMatrix actual = aligner.getMatrix();

		actual.setColumn(3, 0, 0, 0, 0);
		SimpleMatrix expected = SimpleMatrix.identity(4);
		assertTrue("No rotation and ignore translate", expected.isIdentical(actual, 0.001));
	}

	@Test
	public void testGetCentroid() {
		SimpleMatrix source = new SimpleMatrix(new double[][] { { 1, 1, 1 }, { 3, 2, 1 }, { 1, 3, 2 }, { 1, 1, 3 } });

		SimpleMatrix actual = Aligner.centroid(source);

		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 1.500, 1.750, 1.750 } });
		assertTrue(expected.isIdentical(actual, 0.001));
	}

}
