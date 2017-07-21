package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.vector.doublevector.DoubleVectorCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class ToPointsModel extends NodeModel {

	public static final String CFGKEY_PHAR = "phar";

	private final SettingsModelString pharColumn = new SettingsModelString(ToPointsModel.CFGKEY_PHAR, "");
	
	protected ToPointsModel() {
		super(1, 1);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Auto-generated method stub
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Auto-generated method stub
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		pharColumn.saveSettingsTo(settings);
		
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		pharColumn.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		pharColumn.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {
		// Auto-generated method stub
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		BufferedDataContainer container = exec.createDataContainer(getOutputSpec());

		BufferedDataTable phars = inData[0];
		int pharIndex = phars.getSpec().findColumnIndex(pharColumn.getStringValue());
		
		long rowIndex = 0L;
		for (DataRow pharRow : phars) {
			PharCell cell = (PharCell) pharRow.getCell(pharIndex);
			if (cell.isMissing()) {
				setWarningMessage("Some pharmacophore where skipped due to missing value");
				continue;
			}
			Pharmacophore phar = cell.getPharmacophoreValue();
			StringCell pharId = new StringCell(phar.getIdentifier());
			for (PharmacophorePoint p: phar.getPoints()) {
				List<DataCell> cols = new ArrayList<>(5);
				cols.add(pharId);
				cols.add(new StringCell(p.type));
				cols.add(DoubleVectorCellFactory.createCell(new double[] {
						p.cx,
						p.cy,
						p.cz
				}));
				cols.add(new DoubleCell(p.alpha));
				if (p.hasNormal()) {
					cols.add(DoubleVectorCellFactory.createCell(new double[] {
							p.nx,
							p.ny,
							p.nz
					}));
				} else {
					cols.add(new MissingCell("Pharmacophore point without direction"));
				}
				DataRow row = new DefaultRow(
						RowKey.createRowKey(rowIndex++),
						cols
				);
				container.addRowToTable(row);
			}
		}
		
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec spec = inSpecs[0];
		DataColumnSpec columnSpec = spec.getColumnSpec(pharColumn.getStringValue());
		if (columnSpec == null || !columnSpec.getType().isCompatible(PharValue.class)) {
			for (DataColumnSpec col : spec) {
				if (col.getType().isCompatible(PharValue.class)) {
					setWarningMessage("Column '" + spec.getName() + "' automatically chosen as Pharmacophores column");
					pharColumn.setStringValue(col.getName());
					break;
				}
			}
			if (pharColumn.getStringValue() == "") {
				throw new InvalidSettingsException("Table contains no phar column");
			}
		}
		return new DataTableSpec[] { getOutputSpec() };
	}

	private DataTableSpec getOutputSpec() {
		return new DataTableSpec(
				new DataColumnSpecCreator("Pharmacophore identifier", StringCell.TYPE).createSpec(),
				new DataColumnSpecCreator("Pharmacophore type", StringCell.TYPE).createSpec(),
				new DataColumnSpecCreator("Coordinate", DoubleVectorCellFactory.TYPE).createSpec(),
				new DataColumnSpecCreator("Alpha", DoubleCell.TYPE).createSpec(),
				new DataColumnSpecCreator("Direction", DoubleVectorCellFactory.TYPE).createSpec()
		);
	}

}
