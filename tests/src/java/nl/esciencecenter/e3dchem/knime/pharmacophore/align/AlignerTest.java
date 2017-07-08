package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class AlignerTest {

	@Test
	public void test() throws NoOverlapFoundException {
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
						"LIPO 6.1775 0.2833 16.1111 0 0 0 0 0", "LIPO -4.4496 -1.8999 26.3564 0 0 0 0 0", "$$$" });
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
						"LIPO 22.6927 21.2814 8.5115 0 0 0 0 0 0", "LIPO 20.6163 21.6970 6.7951 0 0 0 0 0 0", "$$$" });
		Aligner aligner = new Aligner(probe, reference, 1.0);
		aligner.transformation();

		SimpleMatrix expectedMatrix = new SimpleMatrix(
				new double[][] { { -0.470, 0.742, 0.478, 21.891 }, { -0.207, 0.434, -0.877, 16.653 },
						{ -0.858, -0.511, -0.050, -11.795 }, { 0.000, 0.000, 0.000, 1.000 } });
		SimpleMatrix matrix = new SimpleMatrix(4, 4, true, aligner.getMatrix());
		assertTrue(expectedMatrix.isIdentical(matrix, 0.001));

		double expectedRmsd = 8.0191315E-16;
		assertEquals(expectedRmsd, aligner.getRMSD(), 1E-18);

		String expectedAligned = String.join(sep,
				new String[] { "TODO store id", "HACC 35.2416 -1.9208 -12.6333 1 0 0 0 0",
						"HDON 36.1026 -1.0540 -15.2672 1 0 0 0 0", "HDON 26.2046 -3.0546 -14.0220 1 0 0 0 0",
						"AROM 40.9051 -2.5887 -12.0375 1 0 0 0 0", "HDON 34.9967 -3.7449 -18.3703 1 0 0 0 0",
						"NEGC 34.0493 -4.2675 -17.9353 1 0 0 0 0", "HDON 38.2088 -4.6742 -16.9226 1 0 0 0 0",
						"NEGC 39.0338 -3.8505 -16.8934 1 0 0 0 0", "NEGC 38.1937 -5.4662 -16.0667 1 0 0 0 0",
						"NEGC 36.5501 -3.9711 -17.2979 1 0 0 0 0", "LIPO 33.9152 -5.6305 -11.9242 1 0 0 0 0",
						"LIPO 32.5431 -4.0503 -14.2047 1 0 0 0 0", "LIPO 33.6428 -4.3062 -12.9341 1 0 0 0 0",
						"LIPO 34.0934 -5.3768 -10.2653 1 0 0 0 0", "LIPO 36.3069 -3.5776 -8.3478 1 0 0 0 0",
						"LIPO 41.1317 -6.6472 -14.0921 1 0 0 0 0", "LIPO 38.1483 -7.4127 -11.5701 1 0 0 0 0",
						"LIPO 38.2616 -6.3630 -14.2825 1 0 0 0 0", "LIPO 34.1376 -5.2011 -17.5440 1 0 0 0 0",
						"LIPO 34.4138 -5.4192 -15.0233 1 0 0 0 0", "LIPO 33.3619 -6.0681 -19.2481 1 0 0 0 0",
						"LIPO 24.3843 0.3894 -15.6301 1 0 0 0 0", "LIPO 32.1822 0.1274 -13.3046 1 0 0 0 0",
						"LIPO 33.4794 -1.8340 -12.8098 1 0 0 0 0", "LIPO 32.0394 -2.8790 -19.9108 1 0 0 0 0",
						"LIPO 38.3909 -8.8584 -9.5716 1 0 0 0 0", "HACC 25.3868 -0.2458 -13.5900 1 0 0 0 0",
						"HDON 24.2359 3.0038 -13.2495 1 0 0 0 0", "LIPO 27.0139 1.4461 -13.1659 1 0 0 0 0",
						"LIPO 28.2405 3.5172 -13.3154 1 0 0 0 0", "LIPO 30.1792 1.8258 -16.2141 1 0 0 0 0",
						"LIPO 27.5059 -3.7104 -14.7049 1 0 0 0 0", "LIPO 28.9221 -2.9614 -15.2358 1 0 0 0 0",
						"LIPO 25.7610 4.3784 -17.7539 1 0 0 0 0", "LIPO 29.2356 1.8076 -18.6820 1 0 0 0 0",
						"LIPO 26.9056 1.3720 -18.0521 1 0 0 0 0", "LIPO 35.1812 -6.3570 -8.3338 1 0 0 0 0", "$$$$",
						"" });

		assertEquals(expectedAligned, aligner.getAligned());
	}

	@Test
	public void test_move() {
		Aligner aligner = new Aligner("", "", 1.0);
		SimpleMatrix input = SimpleMatrix.identity(3);
		SimpleMatrix offset = new SimpleMatrix(new double[][] { { 1, 2, 3 } });
		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 2, 2, 3 }, { 1, 3, 3 }, { 1, 2, 4 } });

		SimpleMatrix output = aligner.move(input, offset);

		assertTrue(expected.isIdentical(output, 0.00001));
	}

	@Test
	public void test_centroid() {
		Aligner aligner = new Aligner("", "", 1.0);
		SimpleMatrix input = new SimpleMatrix(new double[][] { { 1, 2, 0 }, { 1, 3, 4 }, { 1, 4, -4 } });
		SimpleMatrix expected = new SimpleMatrix(new double[][] { { 1, 3, 0 } });

		SimpleMatrix output = aligner.getCentroid(input);

		assertTrue(expected.isIdentical(output, 0.00001));
	}

}
