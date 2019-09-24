package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.ArrayList;
import java.util.Map;

import org.knime.chem.types.SdfValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.SingleCellFactory;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

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

	public String mol2phar(String molBlock) {
		String[] lines = molBlock.split("\\r?\\n");
		ArrayList<PharmacophorePoint> points = new ArrayList<>();
		Pharmacophore phar = new Pharmacophore(lines[0], points);
		for (int i = 4; i < lines.length - 2; i++) {
			String[] cols = lines[i].trim().split("\\s+");
			if (cols.length != 6 && cols.length != 10  && cols.length != 16 ) {
				// Skip all non atom sections of molblock
				continue;
			}
			String phartype = elements.get(cols[3]);
			points.add(new PharmacophorePoint(phartype, Float.parseFloat(cols[0]), Float.parseFloat(cols[1]),
					Float.parseFloat(cols[2]), 1.0));
		}
		return phar.toString();
	}
}
