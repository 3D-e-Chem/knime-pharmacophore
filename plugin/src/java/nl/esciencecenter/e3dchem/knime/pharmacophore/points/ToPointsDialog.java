package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

public class ToPointsDialog extends DefaultNodeSettingsPane {

	@SuppressWarnings("unchecked")
	public ToPointsDialog() {
		super();
		addDialogComponent(
				new DialogComponentColumnNameSelection(
						new SettingsModelString(ToPointsModel.CFGKEY_PHAR, ""),
						"Pharmacophore column",
						0, 
						PharValue.class
				)
		);
	}

}
