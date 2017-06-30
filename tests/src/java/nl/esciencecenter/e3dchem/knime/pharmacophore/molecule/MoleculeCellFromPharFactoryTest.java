package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;

public class MoleculeCellFromPharFactoryTest {

	@Test
	public void phar2mol_nopoints_noatoms() {
		String sep = System.getProperty("line.separator");
		String pharBlock = String.join(sep, new String[] { "id1", "$$$$" });
		Map<String, String> elements = new HashMap<>();
		DataColumnSpec spec = new DataColumnSpecCreator("Molecule as pharmacophore", PharCell.TYPE).createSpec();
		MoleculeCellFromPharFactory fact = new MoleculeCellFromPharFactory(spec, 0, elements);

		String result = fact.phar2mol(pharBlock);

		String expected = String.join(sep, new String[] { "id1", "KNIME Pharmacophore 2 Molecule node", "",
				"  0  0  0  0  0  0  0  0  0  0 V2000", "M END", "$$$$", "" });
		assertEquals(result, expected);
	}

}
