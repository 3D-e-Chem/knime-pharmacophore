package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AlignFactoryTest {
	
	@Test
    public void testGetNrNodeViews() {
		AlignFactory factory = new AlignFactory();
		
		int nrviews = factory.getNrNodeViews();
		
		assertEquals(1, nrviews);
	}
}