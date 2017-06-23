package nl.esciencecenter.e3dchem.knime.pharmacophore.reader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReaderFactoryTest {
	
	@Test
    public void testGetNrNodeViews() {
		ReaderFactory factory = new ReaderFactory();
		
		int nrviews = factory.getNrNodeViews();
		
		assertEquals(0, nrviews);
	}
}