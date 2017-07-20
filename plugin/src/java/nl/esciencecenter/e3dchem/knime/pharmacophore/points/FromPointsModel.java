package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
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

	private final SettingsModelString pharId = new SettingsModelString(CFGKEY_IDENTIFIER, "");
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
		RowKey currentKey = RowKey.createRowKey(0L);
		for (DataRow pointIn : pointsIn) {
			String identifier = ((StringValue) pointIn.getCell(idIndex)).getStringValue();
			String type = ((StringValue) pointIn.getCell(typeIndex)).getStringValue();
			double alpha;
			if (alphaIndex == -1) {
				alpha = PharmacophorePoint.getDefaultAlpha(type);
			} else {
				alpha = ((DoubleValue) pointIn.getCell(alphaIndex)).getDoubleValue();
			}
			PharmacophorePoint point;
			if (dirIndex == -1) {
				point = new PharmacophorePoint(type, ((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(0),
						((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(1),
						((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(2), alpha);
			} else {
				point = new PharmacophorePoint(type, ((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(0),
						((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(1),
						((DoubleVectorValue) pointIn.getCell(coordIndex)).getValue(2), alpha,
						((DoubleVectorValue) pointIn.getCell(dirIndex)).getValue(0),
						((DoubleVectorValue) pointIn.getCell(dirIndex)).getValue(1),
						((DoubleVectorValue) pointIn.getCell(dirIndex)).getValue(2));
			}
			currentPoints.add(point);
			currentKey = pointIn.getKey();
			if (!identifier.equals(currentIdentifier) && currentIdentifier != null) {
				Pharmacophore phar = new Pharmacophore(currentIdentifier, currentPoints);
				DataRow row = new DefaultRow(currentKey, new PharCell(phar.toString()));
				container.addRowToTable(row);
				currentPoints.clear();
			}
			currentIdentifier = identifier;
		}
		if (!currentPoints.isEmpty()) {
			Pharmacophore phar = new Pharmacophore(currentIdentifier, currentPoints);
			DataRow row = new DefaultRow(currentKey, new PharCell(phar.toString()));
			container.addRowToTable(row);
		}

		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		// TODO auto select input columns
		return new DataTableSpec[] { SPEC };
	}

}
