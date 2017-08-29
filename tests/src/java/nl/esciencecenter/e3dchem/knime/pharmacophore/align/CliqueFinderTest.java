package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class CliqueFinderTest {

	static Pharmacophore sampleProbe() {
		String sep = "\n";
		String probeStr = String.join(sep,
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
		Pharmacophore probe = new Pharmacophore(probeStr);
		return probe;
	}

	static Pharmacophore sampleReference() {
		String sep = "\n";
		String referenceStr = String.join(sep,
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
		Pharmacophore reference = new Pharmacophore(referenceStr);
		return reference;
	}
	@Test(expected=IllegalArgumentException.class)
	public void test_zeroBestCliqueCount_exception() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid");
		Pharmacophore reference = new Pharmacophore("someid");
		
		new CliqueFinder(probe, reference, 1.0, 3000, 0);
	}
	
	@Test(expected=NoOverlapFoundException.class)
	public void test_emptyProbeAndReference_exception() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid");
		Pharmacophore reference = new Pharmacophore("someid");
		
		new CliqueFinder(probe, reference, 1.0, 3000, 1);
	}

	@Test(expected=NoOverlapFoundException.class)
	public void test_singlepointProbeAndReference_exception() throws NoOverlapFoundException {
		PharmacophorePoint point = new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1);
		List<PharmacophorePoint> points = Arrays.asList(point);
		Pharmacophore probe = new Pharmacophore("someid", points);
		Pharmacophore reference = new Pharmacophore("someid", points);
		
		new CliqueFinder(probe, reference, 1.0, 3000, 1);
	}

	@Test
	public void test_sameDoublePoints_singleClique() throws NoOverlapFoundException {
		List<PharmacophorePoint> points = Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		);
		Pharmacophore probe = new Pharmacophore("someid", points);
		Pharmacophore reference = new Pharmacophore("someid", points);

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(0, 0), new PointPair(1, 1));
		assertEquals(expected, clique);
	}
	
	@Test
	public void test_sameTypeDoublePoints_singleClique() throws NoOverlapFoundException {
		List<PharmacophorePoint> points = Arrays.asList(
				new PharmacophorePoint("LIPO", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		);
		Pharmacophore probe = new Pharmacophore("someid", points);
		Pharmacophore reference = new Pharmacophore("someid", points);

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(1, 0));
		assertEquals(expected, clique);
	}
	
	@Test
	public void test_sameDoublePointsSwapped_singleClique() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1)
		));
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		));

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(1, 0), new PointPair(0, 1));
		assertEquals(expected, clique);
	}

	@Test
	public void test_sameTypeDoublePointsSwapped_singleClique() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", 7.5, -0.5, 0.0, 1)
		));
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("LIPO", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		));

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(1, 0));
		assertEquals(expected, clique);
	}

	@Test(expected=NoOverlapFoundException.class)
	public void test_sameDoublePointsAboveCutoff_exception() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 0.0, 0.0, 0.0, 1),
				new PharmacophorePoint("LIPO", 1.0, 0.0, 0.0, 1)
		));
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 0.0, 0.0, 0.0, 1),
				new PharmacophorePoint("LIPO", 5.0, 0.0, 0.0, 1)
		));

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		finder.getBestCliques();
	}
	
	@Test
	public void test_sameTriplePoints_singleClique() throws NoOverlapFoundException {
		List<PharmacophorePoint> points = Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1)
		);
		Pharmacophore probe = new Pharmacophore("someid", points);
		Pharmacophore reference = new Pharmacophore("someid", points);

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(0, 0), new PointPair(1, 1), new PointPair(2, 2));
		assertEquals(expected, clique);
	}
	
	@Test
	public void test_sameDoublePointsThirdDiff_singleClique() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1),
				new PharmacophorePoint("NEGC", -0.5, 3.5, 0.0, 1)
		));
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("POSC", -0.5, 3.5, 0.0, 1),
				new PharmacophorePoint("HDON", 7.5, -0.5, 0.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		));

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
		List<PointPair> clique = cliques.poll();
		List<PointPair> expected = Arrays.asList(new PointPair(1, 0), new PointPair(2, 1));
		assertEquals(expected, clique);
	}
	
	@Test(expected=NoOverlapFoundException.class)
	public void test_sameDoublePointsToFarAway_exception() throws NoOverlapFoundException {
		Pharmacophore probe = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, 5.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, -5.0, 1)
		));
		Pharmacophore reference = new Pharmacophore("someid", Arrays.asList(
				new PharmacophorePoint("HDON", 7.5, -0.5, -5.0, 1),
				new PharmacophorePoint("LIPO", -6.5, -0.5, 0.0, 1)
		));

		new CliqueFinder(probe, reference, 1.0, 3000, 1);
	}
	
	@Test
	public void test_reallive_best1() throws NoOverlapFoundException {
		Pharmacophore reference = sampleReference();
		Pharmacophore probe = sampleProbe();

		CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 1);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(1, cliques.size());
	}

	@Test
	public void test_reallive_best3() throws NoOverlapFoundException {
		Pharmacophore reference = sampleReference();
		Pharmacophore probe = sampleProbe();

        CliqueFinder finder = new CliqueFinder(probe, reference, 1.0, 3000, 3);
		Queue<List<PointPair>> cliques = finder.getBestCliques();
		
		assertEquals(3, cliques.size());
	}
}
