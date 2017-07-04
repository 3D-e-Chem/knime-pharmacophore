package nl.esciencecenter.e3dchem.knime.pharmacophore.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
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

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

public class WriterModel extends NodeModel {
	public static final String CFGKEY_FILENAME = "phar_filename";
	public static final String CFGKEY_PHAR = "pharColumn";

	private final SettingsModelString pharFilename = new SettingsModelString(CFGKEY_FILENAME, null);
	private final SettingsModelString pharColumn = new SettingsModelString(CFGKEY_PHAR, "");

	protected WriterModel() {
		super(1, 0);
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		String filename = pharFilename.getStringValue();
		File file = new File(filename);;
		checkFile(file);
		try {
			URL url = new URL(filename);
			if ("file".equals(url.getProtocol())) {
				file = FileUtil.getFileFromURL(url);
				checkFile(file);
			}
		} catch (MalformedURLException e) {
			file = new File(filename);
		}
		int index = inData[0].getSpec().findColumnIndex(pharColumn.getStringValue());
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(file));
			write(inData[0], index, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return new BufferedDataTable[] { };
	}

	private void write(BufferedDataTable table, int index, PrintStream out) {
		String phar;
		for (DataRow row : table) {
			phar = ((PharValue) row.getCell(index)).getStringValue();
			out.print(phar);
		}
		out.flush();
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
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
		
		return new DataTableSpec[] { };
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

	private void checkFile(File f) throws InvalidSettingsException {
		if (f.exists()) {
			throw new InvalidSettingsException("File '" + f.getAbsolutePath() + "' already exists.");
		}
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internals to save
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		pharFilename.saveSettingsTo(settings);
		pharColumn.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		pharFilename.loadSettingsFrom(settings);
		pharColumn.loadSettingsFrom(settings);
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
		pharColumn.validateSettings(settings);
	}


	@Override
	protected void reset() {
		// no internal state to reset
	}

}
