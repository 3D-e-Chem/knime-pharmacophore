package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import static org.junit.Assert.assertEquals;

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
		Map<String, String> elements = new PharMoleculeConfig().getPhar2elementMap();
		DataColumnSpec spec = new DataColumnSpecCreator("Molecule as pharmacophore", PharCell.TYPE).createSpec();
		MoleculeCellFromPharFactory fact = new MoleculeCellFromPharFactory(spec, 0, elements);

		String result = fact.phar2mol(pharBlock);

		String expected = String.join(sep, new String[] { "id1", "KNIME Pharmacophore 2 Molecule node", "",
				"  0  0  0  0  0  0  0  0  0  0 V2000", "M END", "$$$$", "" });
		assertEquals(expected, result);
	}

	@Test
	public void phar2mol_singpointwithnormal_oneatom() {
		String sep = System.getProperty("line.separator");
		String pharBlock = String.join(sep,
				new String[] { "id1", "HDON 4.007 23.939 25.299 1 1 3.6554 24.6168 24.6533", "$$$$" });
		Map<String, String> elements = new PharMoleculeConfig().getPhar2elementMap();
		DataColumnSpec spec = new DataColumnSpecCreator("Molecule as pharmacophore", PharCell.TYPE).createSpec();
		MoleculeCellFromPharFactory fact = new MoleculeCellFromPharFactory(spec, 0, elements);

		String result = fact.phar2mol(pharBlock);

		String expected = String.join(sep,
				new String[] { "id1", "KNIME Pharmacophore 2 Molecule node", "", "  1  0  0  0  0  0  0  0  0  0 V2000",
						"    4.0070   23.9390   25.2990   Y 0  0  0  0  0  0  0  0  0  0  0  0", "M END", "$$$$", "" });
		assertEquals(expected, result);
	}

}
