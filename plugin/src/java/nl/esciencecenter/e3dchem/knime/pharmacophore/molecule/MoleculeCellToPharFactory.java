package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.Map;

import org.knime.chem.types.SdfValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.SingleCellFactory;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;

public class MoleculeCellToPharFactory extends SingleCellFactory {
	private int colIndex;
	private Map<String, String> elements;

	public MoleculeCellToPharFactory(DataColumnSpec newColSpec, int colIndex, Map<String, String> elements) {
		super(newColSpec);
		this.colIndex = colIndex;
		this.elements = elements;
	}

	@Override
	public DataCell getCell(DataRow row) {
		String molBlock = ((SdfValue) row.getCell(colIndex)).getSdfValue();
		String pharBlock = mol2phar(molBlock);
		return new PharCell(pharBlock);
	}

	private String mol2phar(String molBlock) {
		StringBuilder buf = new StringBuilder(512);
		String sep = System.getProperty("line.separator");
		String pharTpl = "%s %.4f %.4f %.4f 1 0 0 0 0" + sep;

		String[] lines = molBlock.split("\\r?\\n");
		buf.append(lines[1]).append(sep);
		for (int i = 4; i < lines.length; i++) {
			String[] cols = lines[i].split("\\s+");
			String element = elements.get(cols[3]);
			buf.append(String.format(pharTpl, element, Float.parseFloat(cols[0]), Float.parseFloat(cols[1]),
					Float.parseFloat(cols[2])));
		}
		buf.append("$$$$").append(sep);
		return buf.toString();
	}

}
