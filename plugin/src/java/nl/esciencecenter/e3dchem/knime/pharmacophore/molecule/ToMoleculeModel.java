package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.Map;

import org.knime.chem.types.SdfCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

public class ToMoleculeModel extends PharMoleculeModel {

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		DataTableSpec inSpec = inData[0].getDataTableSpec();
		ColumnRearranger rearranger = createColumnRearranger(inSpec);
		BufferedDataTable outTable = exec.createColumnRearrangeTable(inData[0], rearranger, exec);
		return new BufferedDataTable[] { outTable };
	}

	private ColumnRearranger createColumnRearranger(DataTableSpec spec) {
		ColumnRearranger result = new ColumnRearranger(spec);
		// the following code appends a single column
		DataColumnSpecCreator appendSpecCreator = new DataColumnSpecCreator("Pharmacophore as molecule", SdfCell.TYPE);
		DataColumnSpec appendSpec = appendSpecCreator.createSpec();
		String colName = config.getColumn().getStringValue();
		int colIndex = spec.findColumnIndex(colName);
		Map<String, String> phar2element = config.getPhar2elementMap();
		result.append(new MoleculeCellFromPharFactory(appendSpec, colIndex, phar2element));
		return result;
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec spec = inSpecs[0];
		DataColumnSpec columnSpec = spec.getColumnSpec(config.getColumn().getStringValue());
		if (columnSpec == null || !columnSpec.getType().isCompatible(PharValue.class)) {
			for (DataColumnSpec col : spec) {
				if (col.getType().isCompatible(PharValue.class)) {
					setWarningMessage("Column '" + spec.getName() + "' automatically chosen as phar column");
					config.getColumn().setStringValue(col.getName());
					break;
				}
			}
			if (config.getColumn().getStringValue() == "") {
				throw new InvalidSettingsException("Table contains no phar column");
			}
		}

		ColumnRearranger c = createColumnRearranger(spec);
		return new DataTableSpec[] { c.createSpec() };
	}
}
