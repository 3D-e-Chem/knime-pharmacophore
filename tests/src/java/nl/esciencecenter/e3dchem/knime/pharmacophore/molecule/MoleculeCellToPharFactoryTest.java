package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;

public class MoleculeCellToPharFactoryTest {
	@Test
	public void test_mol2phar_noatoms_nopoints() {
		String sep = "\n";
		String sdfBlock = String.join(sep, new String[] { "someid", "progname", "somecomment",
				"  0  0  0  0  0  0  0  0  0  0999 V2000", "M  END", "$$$$" });
		Map<String, String> elements = new PharMoleculeConfig().getElement2PharMap();
		DataColumnSpec spec = new DataColumnSpecCreator("Pharmacophore", PharCell.TYPE).createSpec();
		MoleculeCellToPharFactory fact = new MoleculeCellToPharFactory(spec, 0, elements);

		String pharBlock = fact.mol2phar(sdfBlock);

		String expected = String.join(sep, new String[] { "someid", "$$$$", "" });
		assertEquals(expected, pharBlock);
	}

	@Test
	public void test_mol2phar_1atom_1point() {
		String sep = "\n";
		String sdfBlock = String.join(sep,
				new String[] { "someid", "progname", "somecomment", "  1  0  0  0  0  0  0  0  0  0999 V2000",
						"    6.6920   24.4910   24.9910 As  0  0  0  0  0  0  0  0  0  0  0  0", "M  END", "$$$$" });
		Map<String, String> elements = new PharMoleculeConfig().getElement2PharMap();
		DataColumnSpec spec = new DataColumnSpecCreator("Pharmacophore", PharCell.TYPE).createSpec();
		MoleculeCellToPharFactory fact = new MoleculeCellToPharFactory(spec, 0, elements);

		String pharBlock = fact.mol2phar(sdfBlock);

		String expected = String.join(sep, new String[] { "someid", "HYBH 6.692 24.491 24.991 1 0 0 0 0", "$$$$", "" });
		assertEquals(expected, pharBlock);
	}
}
