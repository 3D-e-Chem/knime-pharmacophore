package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.collection.ListDataValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.vector.doublevector.DoubleVectorValue;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharmacophorePoint;

public class FromPointsModel extends NodeModel {
	private static final DataColumnSpecCreator CREATOR = new DataColumnSpecCreator("Pharmacophore", PharCell.TYPE);
	private static final DataTableSpec SPEC = new DataTableSpec(CREATOR.createSpec());

	public static final String CFGKEY_IDENTIFIER = "pharId";
	public static final String CFGKEY_COORDINATE = "pharCoordinate";
	public static final String CFGKEY_TYPE = "pharType";
	public static final String CFGKEY_ALPHA = "pharAlpha";
	public static final String CFGKEY_DIRECTION = "pharDirection";

	private final SettingsModelColumnName pharId = new SettingsModelColumnName(CFGKEY_IDENTIFIER, "");
	private final SettingsModelString pharCoordinate = new SettingsModelString(CFGKEY_COORDINATE, "");
	private final SettingsModelString pharType = new SettingsModelString(CFGKEY_TYPE, "");
	private final SettingsModelString pharAlpha = new SettingsModelString(CFGKEY_ALPHA, "");
	private final SettingsModelString pharDirection = new SettingsModelString(CFGKEY_DIRECTION, "");

	protected FromPointsModel() {
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
		pharId.saveSettingsTo(settings);
		pharCoordinate.saveSettingsTo(settings);
		pharType.saveSettingsTo(settings);
		pharAlpha.saveSettingsTo(settings);
		pharDirection.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		pharId.validateSettings(settings);
		pharCoordinate.validateSettings(settings);
		pharType.validateSettings(settings);
		pharAlpha.validateSettings(settings);
		pharDirection.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		pharId.loadSettingsFrom(settings);
		pharCoordinate.loadSettingsFrom(settings);
		pharType.loadSettingsFrom(settings);
		pharAlpha.loadSettingsFrom(settings);
		pharDirection.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {
		// Auto-generated method stub
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		BufferedDataContainer container = exec.createDataContainer(SPEC);

		BufferedDataTable pointsIn = inData[0];
		int idIndex = pointsIn.getSpec().findColumnIndex(pharId.getStringValue());
		int coordIndex = pointsIn.getSpec().findColumnIndex(pharCoordinate.getStringValue());
		int typeIndex = pointsIn.getSpec().findColumnIndex(pharType.getStringValue());
		int alphaIndex = pointsIn.getSpec().findColumnIndex(pharAlpha.getStringValue());
		int dirIndex = pointsIn.getSpec().findColumnIndex(pharDirection.getStringValue());

		String currentIdentifier = null;
		List<PharmacophorePoint> currentPoints = new ArrayList<>();
		long counter = 0L;
		for (DataRow pointIn : pointsIn) {
			String identifier;
			if (pharId.useRowID()) {
				identifier = pointIn.getKey().getString();
			} else {
				identifier = ((StringValue) pointIn.getCell(idIndex)).getStringValue();
			}
			if (!identifier.equals(currentIdentifier) && currentIdentifier != null) {
				Pharmacophore phar = new Pharmacophore(currentIdentifier, currentPoints);
				DataRow row = new DefaultRow(RowKey.createRowKey(counter++), new PharCell(phar));
				container.addRowToTable(row);
				currentPoints.clear();
			}
			addPoint(pointIn,typeIndex, alphaIndex, coordIndex, dirIndex, currentPoints);
			currentIdentifier = identifier;
			exec.checkCanceled();
		}
		if (!currentPoints.isEmpty()) {
			Pharmacophore phar = new Pharmacophore(currentIdentifier, currentPoints);
			DataRow row = new DefaultRow(RowKey.createRowKey(counter++), new PharCell(phar));
			container.addRowToTable(row);
		}

		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	private void addPoint(DataRow pointIn, int typeIndex, int alphaIndex, int coordIndex, int dirIndex, List<PharmacophorePoint> currentPoints) throws InvalidSettingsException {
		String type = ((StringValue) pointIn.getCell(typeIndex)).getStringValue();
		double alpha = extractAlpha(pointIn, alphaIndex, type);
		try {
			PharmacophorePoint point;
			DataCell coordCell = pointIn.getCell(coordIndex);
			double[] coordOut = extractDoubleArray(coordCell);
			if (dirIndex == -1 || pointIn.getCell(dirIndex).isMissing()) {
				point = new PharmacophorePoint(type, coordOut[0], coordOut[1], coordOut[2], alpha);
			} else {
				double[] dirOut = extractDoubleArray(pointIn.getCell(dirIndex));
				point = new PharmacophorePoint(type, coordOut[0], coordOut[1], coordOut[2], alpha, dirOut[0],
						dirOut[1], dirOut[2]);
			}
			currentPoints.add(point);
		} catch (IllegalArgumentException e) {
			setWarningMessage(e.getMessage());
		}
	}
	
	private double extractAlpha(DataRow pointIn, int alphaIndex, String type) {
		double alpha;
		if (alphaIndex == -1 || pointIn.getCell(alphaIndex).isMissing()) {
			alpha = PharmacophorePoint.getDefaultAlpha(type);
		} else {
			alpha = ((DoubleValue) pointIn.getCell(alphaIndex)).getDoubleValue();
		}
		return alpha;
	}

	private double[] extractDoubleArray(DataCell coordCell) throws InvalidSettingsException {
		double[] coordOut;
		DataType coordType = coordCell.getType();
		InvalidSettingsException wrongCoordException = new InvalidSettingsException(
				"Coordinate should be DoubleVector or List column type with 3 doubles");
		if (coordType.isCompatible(ListDataValue.class)) {
			ListDataValue coordIn = (ListDataValue) coordCell;
			if (coordIn.getElementType().isCompatible(DoubleValue.class) && coordIn.size() == 3) {
				if (coordIn.get(0).isMissing() || coordIn.get(1).isMissing() || coordIn.get(2).isMissing()) {
					throw wrongCoordException;
				}
				coordOut = new double[] { ((DoubleValue) coordIn.get(0)).getDoubleValue(),
						((DoubleValue) coordIn.get(1)).getDoubleValue(),
						((DoubleValue) coordIn.get(2)).getDoubleValue() };
			} else {
				throw wrongCoordException;
			}
		} else if (coordType.isCompatible(DoubleVectorValue.class)) {
			DoubleVectorValue coordIn = (DoubleVectorValue) coordCell;
			if (coordIn.getLength() == 3) {
				coordOut = new double[] { coordIn.getValue(0), coordIn.getValue(1), coordIn.getValue(2) };
			} else {
				throw wrongCoordException;
			}
		} else {
			throw wrongCoordException;
		}
		return coordOut;
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		// TODO auto select input columns
		// if (!coordIn.getElementType().isAdaptable(DoubleValue.class)) {
		// throw new IllegalArgumentException("Coordinate column is wrong");
		// }
		DataTableSpec inSpec = inSpecs[0];
		verifyCoordinateColumn(inSpec, pharCoordinate.getStringValue(), "Point coordinate");
		verifyCoordinateColumn(inSpec, pharDirection.getStringValue(), "Direction coordinate");

		return new DataTableSpec[] { SPEC };
	}

	private void verifyCoordinateColumn(DataTableSpec inSpec, String columnName, String what)
			throws InvalidSettingsException {
		if (columnName != null && !columnName.isEmpty() && inSpec.findColumnIndex(columnName) != -1) {
			DataType coordType = inSpec.getColumnSpec(columnName).getType();
			if (coordType.isCollectionType() && !coordType.getCollectionElementType().isCompatible(DoubleValue.class)) {
				throw new InvalidSettingsException(what + " should be DoubleVector or List column type with 3 doubles");
			}
		}
	}

}
