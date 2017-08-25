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
	public void test_align_bestonly() throws NoOverlapFoundException {
		CliqueAligner alignment = Aligner.align(probe, reference, 1.0, 300);

		assertEquals(1.44028, alignment.getRMSD(), 0.0001);
	}

	@Test
	public void test_align_best3() throws NoOverlapFoundException {
		List<CliqueAligner> alignments = Aligner.align(probe, reference, 1.0, 300, 3);

		assertEquals(3, alignments.size());
		double[] rmsds = alignments.stream().map(CliqueAligner::getRMSD).mapToDouble(c -> c).toArray();
		double[] expected = new double[] { 0.60851, 1.41477, 1.44028 };
		assertArrayEquals(expected, rmsds, 0.0001);
	}
}
