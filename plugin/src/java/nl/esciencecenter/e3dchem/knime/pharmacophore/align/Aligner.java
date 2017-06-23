package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

public class Aligner {
	public Aligner(String reference) {
	}

	public Alignment align(String current) {
		double[] transform = new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
		return new Alignment(current, transform);
	}

}
