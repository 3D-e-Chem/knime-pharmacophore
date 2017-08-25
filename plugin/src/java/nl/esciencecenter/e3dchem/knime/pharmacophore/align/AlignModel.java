package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.MissingCell;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.DoubleCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

/**
 * This is the model implementation of AlignModel.
 *
 */
public class AlignModel extends NodeModel {
	static final int QUERY_PORT = 0;
	static final int REFERENCE_PORT = 1;

	static final String CFGKEY_QUERY = "queryColumn";
	protected final SettingsModelString queryColumn = new SettingsModelString(CFGKEY_QUERY, "");

	static final String CFGKEY_REFERENCE = "referenceColumn";
	protected final SettingsModelString referenceColumn = new SettingsModelString(CFGKEY_REFERENCE, "");

	static final String CFGKEY_CUTOFF = "cutoff";
	protected final SettingsModelDouble cutoff = new SettingsModelDoubleBounded(CFGKEY_CUTOFF, 1.0, 0.0, 1000.0);

	static final String CFGKEY_BREAKNUMCLIQUES = "cliqueBreak";
	protected final SettingsModelInteger cliqueBreak = new SettingsModelIntegerBounded(CFGKEY_BREAKNUMCLIQUES, 3000, 0,
			5000);

	private static final DataTableSpec outputSpec = new DataTableSpec(
			new DataColumnSpecCreator("Aligned pharmacophore", PharCell.TYPE).createSpec(),
			new DataColumnSpecCreator("Transformation matrix", DoubleVectorCellFactory.TYPE).createSpec(),
			new DataColumnSpecCreator("RMSD", DoubleCell.TYPE).createSpec());

	/**
	 * Constructor for the node model.
	 */
	protected AlignModel() {
		super(2, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		Pharmacophore referencePharmacophore = getReferencePharmacophore(inData);

		BufferedDataTable queryData = inData[QUERY_PORT];
		DataTableSpec outSpec = new DataTableSpecCreator(queryData.getSpec()).addColumns(outputSpec).createSpec();
		BufferedDataContainer container = exec.createDataContainer(outSpec);
		int queryIndex = queryData.getSpec().findColumnIndex(queryColumn.getStringValue());

		double cutoff = this.cutoff.getDoubleValue();
		int cliqueBreak = this.cliqueBreak.getIntValue();

		Pharmacophore current;
		Pharmacophore aligned;
		long currentRow = 0;
		long size = queryData.size();
		for (DataRow queryRow : queryData) {
			current = ((PharValue) queryRow.getCell(queryIndex)).getPharmacophoreValue();
			try {
				CliqueAligner aligner = Aligner.align(current, referencePharmacophore, cutoff, cliqueBreak);
				aligned = aligner.getAligned();

				container.addRowToTable(new AppendedColumnRow(queryRow, new PharCell(aligned),
						DoubleVectorCellFactory.createCell(aligner.getMatrixAsArray()),
						new DoubleCell(aligner.getRMSD())));
			} catch (NoOverlapFoundException e) {
				MissingCell miss = new MissingCell(e.getMessage());
				container.addRowToTable(new AppendedColumnRow(queryRow, miss, miss, miss));
				setWarningMessage("Some pharmacophore could not be aligned");
			}

			exec.setProgress((double) currentRow++ / size);
			exec.checkCanceled();
		}

		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	protected Pharmacophore getReferencePharmacophore(final BufferedDataTable[] inData) {
		Pharmacophore referencePharmacophore = null;
		BufferedDataTable referenceData = inData[REFERENCE_PORT];
		int referenceIndex = referenceData.getSpec().findColumnIndex(referenceColumn.getStringValue());
		for (DataRow referenceRow : referenceData) {
			referencePharmacophore = ((PharValue) referenceRow.getCell(referenceIndex)).getPharmacophoreValue();
		}
		if (referencePharmacophore == null) {
			throw new IllegalArgumentException("No reference pharmacophore found");
		}
		return referencePharmacophore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		configureQuery(inSpecs);
		configureReference(inSpecs);
		DataTableSpec outSpec = new DataTableSpecCreator(inSpecs[QUERY_PORT]).addColumns(outputSpec).createSpec();
		return new DataTableSpec[] { outSpec };
	}

	private void configureReference(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec refSpec = inSpecs[REFERENCE_PORT];
		DataColumnSpec refColumnSpec = refSpec.getColumnSpec(referenceColumn.getStringValue());
		if (refColumnSpec == null || !refColumnSpec.getType().isCompatible(PharValue.class)) {
			for (DataColumnSpec col : refSpec) {
				if (col.getType().isCompatible(PharValue.class)) {
					setWarningMessage("Column '" + col.getName()
							+ "' automatically chosen as phar column as reference pharmacophore");
					referenceColumn.setStringValue(col.getName());
					break;
				}
			}
			if (referenceColumn.getStringValue() == "") {
				throw new InvalidSettingsException("Table on second port contains no phar column");
			}
		}
	}

	private void configureQuery(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec querySpec = inSpecs[QUERY_PORT];
		DataColumnSpec queryColumnSpec = querySpec.getColumnSpec(queryColumn.getStringValue());
		if (queryColumnSpec == null || !queryColumnSpec.getType().isCompatible(PharValue.class)) {
			for (DataColumnSpec col : querySpec) {
				if (col.getType().isCompatible(PharValue.class)) {
					setWarningMessage("Column '" + col.getName()
							+ "' automatically chosen as phar column as query pharmacophore");
					queryColumn.setStringValue(col.getName());
					break;
				}
			}
			if (queryColumn.getStringValue() == "") {
				throw new InvalidSettingsException("Table on first port contains no phar column");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		queryColumn.saveSettingsTo(settings);
		referenceColumn.saveSettingsTo(settings);
		cutoff.saveSettingsTo(settings);
		cliqueBreak.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		queryColumn.loadSettingsFrom(settings);
		referenceColumn.loadSettingsFrom(settings);
		cutoff.loadSettingsFrom(settings);
		;
		cliqueBreak.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		queryColumn.validateSettings(settings);
		referenceColumn.validateSettings(settings);
		cutoff.validateSettings(settings);
		cliqueBreak.validateSettings(settings);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internals to load
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internals to save
	}

}
