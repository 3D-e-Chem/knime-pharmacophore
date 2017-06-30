package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.knime.chem.types.SdfCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.SingleCellFactory;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

public class MoleculeCellFromPharFactory extends SingleCellFactory {

	private int pharColIndex;
	private Map<String, String> elements;

	public MoleculeCellFromPharFactory(DataColumnSpec newColSpec, int pharColIndex, Map<String, String> elements) {
		super(newColSpec);
		this.pharColIndex = pharColIndex;
		this.elements = elements;
	}

	@Override
	public DataCell getCell(DataRow row) {
		String pharBlock = ((PharValue) row.getCell(pharColIndex)).getStringValue();
		String molBlock = phar2mol(pharBlock);
		DataCell resultCell = SdfCellFactory.create(molBlock);
		return resultCell;
	}

	public String phar2mol(String pharBlock) {
		StringBuilder buf = new StringBuilder(512);
		String sep = System.getProperty("line.separator");
		List<String> atoms = new ArrayList<>();
		String atomTpl = "%10.4f%10.4f%10.4f %3s 0  0  0  0  0  0  0  0  0  0  0  0%s";
		String countTpl = "%3s  0  0  0  0  0  0  0  0  0 V2000%s";
		for (String line : pharBlock.split("\\r?\\n")) {
			if (line.startsWith("$$$$")) {
				break;
			} else if (line.startsWith("#")) {
				// Skip comments
			} else {
				String[] cols = line.split("\\s+");
				if (cols.length < 9) {
					// pharmacophore name
					buf.append(line).append(sep);
					buf.append("KNIME Pharmacophore 2 Molecule node").append(sep);
					buf.append(sep);
				} else {
					// point -> atom
					atoms.add(String.format(atomTpl, Float.parseFloat(cols[1]), Float.parseFloat(cols[2]),
							Float.parseFloat(cols[3]), elements.get(cols[0]), sep));
				}
			}
		}
		buf.append(String.format(countTpl, atoms.size(), sep));
		for (String atom : atoms) {
			buf.append(atom);
		}
		buf.append("M END").append(sep);
		buf.append("$$$$").append(sep);
		return buf.toString();
	}

}
