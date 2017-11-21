package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

public class AlignerTest {
	private Pharmacophore reference;
	private Pharmacophore probe;

	@Before
	public void setUp() {
		reference = CliqueFinderTest.sampleReference();
		probe = CliqueFinderTest.sampleProbe();
	}

	@Test
	public void test_align_best3() throws NoOverlapFoundException {
		List<CliqueAligner> alignments = Aligner.align(probe, reference, 1.0, 300, 3);

		assertEquals(3, alignments.size());
		double[] rmsds = alignments.stream().map(CliqueAligner::getRMSD).mapToDouble(c -> c).toArray();
        double[] expectedRmsds = new double[] { 1.44028, 1.41477, 1.7240467 };
        assertArrayEquals(expectedRmsds, rmsds, 0.0001);
        int[] cliqueSizes = alignments.stream().map(CliqueAligner::getCliqueSize).mapToInt(c -> c).toArray();
        int[] expectedCliqueSizes = new int[] { 6, 5, 4 };
        assertArrayEquals(expectedCliqueSizes, cliqueSizes);
	}
}
