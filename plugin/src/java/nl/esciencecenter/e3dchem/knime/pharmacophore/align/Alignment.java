package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

public class Alignment {
	private String pharmacophore;
	private double[] transform;

	public Alignment(String pharmacophore, double[] transform) {
		super();
		this.pharmacophore = pharmacophore;
		this.transform = transform;
	}

	public String getPharmacophore() {
		return pharmacophore;
	}

	public double[] getTransform() {
		return transform;
	}

}
