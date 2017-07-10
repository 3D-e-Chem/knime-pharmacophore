package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class AlignerTest {

	@Test
	public void test_reallive() throws NoOverlapFoundException {
		String sep = System.getProperty("line.separator");
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
		Aligner aligner = new Aligner(probe, reference);

		SimpleMatrix expectedMatrix = new SimpleMatrix(new double[][] { { 0.525, 0.213, -0.824, 21.891 },
				{ -0.655, 0.720, -0.231, 16.653 }, { 0.544, 0.661, 0.517, -11.795 }, { 0.000, 0.000, 0.000, 1.000 } });
		SimpleMatrix matrix = aligner.getMatrix();
		assertTrue(expectedMatrix.isIdentical(matrix, 0.001));

		double expectedRmsd = 4.4364E-16;
		assertEquals(expectedRmsd, aligner.getRMSD(), 1E-18);

		String expectedAligned = String.join(sep,
				new String[] { "probid", "HACC 2.7626 14.15 0.5234 0 0 0 0 0", "HDON 4.3225 14.8022 2.8838 0 0 0 0 0",
						"HDON 7.047 6.5217 -2.3635 0 0 0 0 0", "AROM -0.7535 17.9771 2.9455 0 0 0 0 0",
						"HDON 4.5639 11.605 5.6788 0 0 0 0 0", "NEGC 4.4274 10.6618 5.0068 0 0 0 0 0",
						"HDON 1.6063 13.6453 6.2847 0 0 0 0 0", "NEGC 1.7743 14.7904 6.4282 0 0 0 0 0",
						"NEGC 0.6113 13.2946 5.7878 0 0 0 0 0", "NEGC 3.0984 12.8135 5.6004 0 0 0 0 0",
						"LIPO 0.417 10.9119 0.3309 0 0 0 0 0", "LIPO 3.3764 10.4689 1.122 0 0 0 0 0",
						"LIPO 2.0066 11.3672 0.6685 0 0 0 0 0", "LIPO -0.349 11.5481 -1.0316 0 0 0 0 0",
						"LIPO -1.1411 14.731 -2.0592 0 0 0 0 0", "LIPO -2.6644 15.0792 5.8941 0 0 0 0 0",
						"LIPO -3.0695 12.9187 2.5753 0 0 0 0 0", "LIPO -0.9786 13.1273 4.5893 0 0 0 0 0",
						"LIPO 3.5229 10.1991 4.9852 0 0 0 0 0", "LIPO 1.9302 10.7769 3.0859 0 0 0 0 0",
						"LIPO 4.168 8.7177 6.2684 0 0 0 0 0", "LIPO 11.1906 7.1022 -2.8673 0 0 0 0 0",
						"LIPO 6.0342 13.109 -0.9659 0 0 0 0 0", "LIPO 3.7666 12.8823 -0.2038 0 0 0 0 0",
						"LIPO 7.4016 9.6905 5.2906 0 0 0 0 0", "LIPO -5.2424 12.5657 1.4363 0 0 0 0 0",
						"HACC 9.2013 7.8408 -3.9007 0 0 0 0 0", "HDON 11.8753 9.1845 -5.6457 0 0 0 0 0",
						"LIPO 9.3901 10.2183 -3.9409 0 0 0 0 0", "LIPO 10.3373 12.432 -3.8037 0 0 0 0 0",
						"LIPO 9.7076 12.1525 0.0103 0 0 0 0 0", "LIPO 6.3089 6.9057 -0.9859 0 0 0 0 0",
						"LIPO 6.4284 8.3191 -0.0715 0 0 0 0 0", "LIPO 14.4415 10.2682 -1.5537 0 0 0 0 0",
						"LIPO 11.4286 10.9439 1.61 0 0 0 0 0", "LIPO 11.9205 9.0868 0.0852 0 0 0 0 0",
						"LIPO -2.5668 12.1021 -1.8374 0 0 0 0 0", "$$$$", "" });
		assertEquals(expectedAligned, aligner.getAligned().toString());
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
	public void test_probeSameAsRef() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		Aligner aligner = new Aligner(probe, reference);

		SimpleMatrix actual = aligner.getMatrix();

		SimpleMatrix expected = SimpleMatrix.identity(4);
		assertTrue(expected.isIdentical(actual, 0.00001));
	}

	@Test
	public void test_probeTranslated() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		// probe has translated by x+1, y+0, z-2
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 1.5, 0.5, -1.5, 1),
						new PharmacophorePoint("AROM", 1.5, 0.0, -1.5, 1),
						new PharmacophorePoint("HDON", 1.5, 0.5, -2.0, 1)));
		Aligner aligner = new Aligner(probe, reference);

		SimpleMatrix actual = aligner.getMatrix();

		SimpleMatrix expected = SimpleMatrix.identity(4);
		expected.set(0, 3, -1);
		expected.set(2, 3, 2);
		assertTrue(actual.toString(), expected.isIdentical(actual, 0.00001));
	}

	@Test
	public void test_probeRotatedRef() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.0, 0.5, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", 0.5, 0.5, 0.0, 1)));
		// HDON is rotated along y axis 90 degrees
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("HDON", 0.0, 0.5, 0.5, 1),
						new PharmacophorePoint("LIPO", 0.5, 0.0, 0.5, 1),
						new PharmacophorePoint("AROM", 0.5, 0.5, 0.0, 1)));
		Aligner aligner = new Aligner(probe, reference);

		// System.out.println(reference.getPointsMatrix());
		// System.out.println(aligner.getAligned().getPointsMatrix());

		SimpleMatrix actual = aligner.getMatrix();

		SimpleMatrix expected = new SimpleMatrix(
				new double[][] { { 0, 0, 1, 0 }, { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 1 }, });
		// System.out.println(actual);
		assertTrue(expected.isIdentical(actual, 0.00001));
	}

	@Test
	public void test_scaled_identity() throws NoOverlapFoundException {
		Pharmacophore reference = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.5, 0.0, 0.0, 1),
						new PharmacophorePoint("AROM", 0.0, 0.0, 0.5, 1),
						new PharmacophorePoint("HDON", -0.5, 0.0, -0.5, 1)));
		Pharmacophore probe = new Pharmacophore("someid",
				Arrays.asList(new PharmacophorePoint("LIPO", 0.25, 0.0, 0.0, 1),
						new PharmacophorePoint("AROM", 0.0, 0.0, 0.25, 1),
						new PharmacophorePoint("HDON", -0.25, 0.0, -0.25, 1)));
		Aligner aligner = new Aligner(probe, reference);

		SimpleMatrix actual = aligner.getMatrix();

		SimpleMatrix expected = SimpleMatrix.identity(4);
		assertTrue(expected.isIdentical(actual, 0.001));
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
}
