package nl.esciencecenter.e3dchem.knime.pharmacophore.writer;

import javax.swing.JFileChooser;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class WriterDialog extends DefaultNodeSettingsPane {
	@SuppressWarnings("unchecked")
	protected WriterDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(WriterModel.CFGKEY_PHAR, ""),
				"Pharmacophore column", 0, StringValue.class));

		addDialogComponent(new DialogComponentFileChooser(new SettingsModelString(WriterModel.CFGKEY_FILENAME, null),
				"phar_write", JFileChooser.OPEN_DIALOG, ".phar"));
	}
}
