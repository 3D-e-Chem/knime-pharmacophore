package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PharMoleculeConfigTest {

	@Test
	public void test_getPhar2elementMap_defaults() {
		PharMoleculeConfig config = new PharMoleculeConfig();

		Map<String, String> actual = config.getPhar2elementMap();

		Map<String, String> expected = new HashMap<>();
		expected.put("AROM", "S");
		expected.put("HYBH", "As");
		expected.put("HYBL", "Cl");
		expected.put("NEGC", "O");
		expected.put("HACC", "I");
		expected.put("HDON", "Y");
		expected.put("POSC", "N");
		expected.put("EXCL", "C");
		expected.put("LIPO", "Ra");

		assertEquals(expected, actual);
	}

}
