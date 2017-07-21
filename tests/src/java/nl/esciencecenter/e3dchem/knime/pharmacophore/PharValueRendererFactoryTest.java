package nl.esciencecenter.e3dchem.knime.pharmacophore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValueRenderer.Factory;

public class PharValueRendererFactoryTest {

	@Test
	public void test_description() {
		Factory fact = new PharValueRenderer.Factory();
		assertEquals("Phar string", fact.getDescription());
	}
}
