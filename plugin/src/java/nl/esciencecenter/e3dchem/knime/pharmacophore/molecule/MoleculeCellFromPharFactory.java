package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.Map;

import org.knime.chem.types.SdfCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.SingleCellFactory;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

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
		String sep = "\n";
		Pharmacophore phar = new Pharmacophore(pharBlock);
		String atomTpl = "%10.4f%10.4f%10.4f %-3s 0  0  0  0  0  0  0  0  0  0  0  0";
		String countTpl = "%3s  0  0  0  0  0  0  0  0  0999 V2000%s";
		buf.append(phar.getIdentifier()).append(sep);
		buf.append(" KNIME Pharmacophore 2 Molecule node").append(sep);
		buf.append(sep);
		buf.append(String.format(countTpl, phar.size(), sep));
		for (PharmacophorePoint p : phar.getPoints()) {
			buf.append(String.format(atomTpl, p.cx, p.cy, p.cz, elements.get(p.type))).append(sep);

		}
		buf.append("M  END").append(sep);
		buf.append("$$$$").append(sep);
		return buf.toString();
	}

}
