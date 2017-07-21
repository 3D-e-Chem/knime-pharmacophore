package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.collection.ListDataValue;
import org.knime.core.data.vector.doublevector.DoubleVectorValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class FromPointsDialog extends DefaultNodeSettingsPane {

	@SuppressWarnings("unchecked")
	public FromPointsDialog() {
		super();
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(FromPointsModel.CFGKEY_IDENTIFIER, ""), "Pharmacophore identifier column",
				0, StringValue.class));
		addDialogComponent(
				new DialogComponentColumnNameSelection(new SettingsModelString(FromPointsModel.CFGKEY_COORDINATE, ""),
						"Point coordinate column", 0, DoubleVectorValue.class, ListDataValue.class));

		addDialogComponent(
				new DialogComponentColumnNameSelection(new SettingsModelString(FromPointsModel.CFGKEY_TYPE, ""),
						"Pharmacophore type column", 0, StringValue.class));

		addDialogComponent(
				new DialogComponentColumnNameSelection(new SettingsModelString(FromPointsModel.CFGKEY_ALPHA, ""),
						"Alpha column", 0, false, true, DoubleValue.class));
		addDialogComponent(
				new DialogComponentColumnNameSelection(new SettingsModelString(FromPointsModel.CFGKEY_DIRECTION, ""),
						"Direction coordinate column", 0, false, true, DoubleVectorValue.class, ListDataValue.class));
	}
}
