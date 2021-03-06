package nl.esciencecenter.e3dchem.knime.pharmacophore.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
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
import org.knime.core.util.FileUtil;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

/**
 * This is the model implementation of PharmacophoreReader.
 *
 */
public class ReaderModel extends NodeModel {
	public static final String CFGKEY_FILENAME = "phar_filename";
	private static final DataColumnSpecCreator CREATOR = new DataColumnSpecCreator("Pharmacophore", PharCell.TYPE);
	private static final DataTableSpec SPEC = new DataTableSpec(CREATOR.createSpec());

	private final SettingsModelString pharFilename = new SettingsModelString(CFGKEY_FILENAME, null);

	/**
	 * Constructor for the node model.
	 */
	protected ReaderModel() {
		super(0, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		BufferedDataContainer container = exec.createDataContainer(SPEC);

		String filename = pharFilename.getStringValue();
		InputStream inStream;
		try {
			inStream = new URL(filename).openStream();
		} catch (MalformedURLException e) {
			inStream = new FileInputStream(filename);
		}

		for (Pharmacophore phar : Pharmacophore.fromStream(inStream)) {
			exec.checkCanceled();
			addPharmacophore(phar, container);
		}

		// once we are done, we close the container and return its table
		inStream.close();
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	private void addPharmacophore(Pharmacophore phar, BufferedDataContainer container) {
		DataRow row = new DefaultRow(new RowKey(phar.getIdentifier()), new PharCell(phar.toString()));
		container.addRowToTable(row);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		String filename = pharFilename.getStringValue();
		if (filename == null) {
			throw new InvalidSettingsException("No *.phar file specified");
		}

		try {
			URL url = new URL(filename);
			if ("file".equals(url.getProtocol())) {
				checkFile(FileUtil.getFileFromURL(url));
			}
		} catch (MalformedURLException e) {
			checkFile(new File(filename));
		}

		return new DataTableSpec[] { SPEC };
	}

	private void checkFile(File f) throws InvalidSettingsException {
		if (!f.exists()) {
			throw new InvalidSettingsException("File '" + f.getAbsolutePath() + "' does not exist.");
		}
		if (!f.isFile()) {
			throw new InvalidSettingsException("The path '" + f.getAbsolutePath() + "' is not a file.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		pharFilename.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		pharFilename.loadSettingsFrom(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		pharFilename.validateSettings(settings);
		if (settings.getString(CFGKEY_FILENAME) == null) {
			throw new InvalidSettingsException("No phar file specified");
		}
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		String filename = pharFilename.getStringValue();
		if (filename == null) {
			return;
		}

		try {
			URL url = new URL(filename);
			if ("file".equals(url.getProtocol())) {
				checkFile(FileUtil.getFileFromURL(url));
			}
		} catch (MalformedURLException e) {
			try {
				checkFile(new File(filename));
			} catch (InvalidSettingsException e1) {
				setWarningMessage(e.getMessage());
			}
		} catch (InvalidSettingsException e) {
			setWarningMessage(e.getMessage());
		}
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Auto-generated method stub

	}

	@Override
	protected void reset() {
		// Auto-generated method stub
	}
}
