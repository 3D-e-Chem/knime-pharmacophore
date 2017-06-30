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
		expected.put("HYBH", "CL");
		expected.put("HYBL", "BR");
		expected.put("NEGC", "F");
		expected.put("HACC", "O");
		expected.put("HDON", "N");
		expected.put("POSC", "P");
		expected.put("EXCL", "B");
		expected.put("LIPO", "C");

		assertEquals(expected, actual);
	}

}
