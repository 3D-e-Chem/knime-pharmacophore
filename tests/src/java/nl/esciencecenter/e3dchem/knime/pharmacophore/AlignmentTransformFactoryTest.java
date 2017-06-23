package nl.esciencecenter.e3dchem.knime.pharmacophore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.esciencecenter.e3dchem.knime.pharmacophore.AlignmentTransformFactory;

public class AlignmentTransformFactoryTest {
	
	@Test
    public void testGetNrNodeViews() {
		AlignmentTransformFactory factory = new AlignmentTransformFactory();
		
		int nrviews = factory.getNrNodeViews();
		
		assertEquals(1, nrviews);
	}
}