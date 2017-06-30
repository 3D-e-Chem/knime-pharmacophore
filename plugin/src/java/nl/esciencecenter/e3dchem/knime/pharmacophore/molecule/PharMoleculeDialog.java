package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class PharMoleculeDialog extends DefaultNodeSettingsPane {

	protected PharMoleculeConfig config;

	public PharMoleculeDialog() {
		super();
		config = new PharMoleculeConfig();
	}

	protected void addElementsMappingGroup() {
		createNewGroup("Pharmacophore type 2 element mapping");
		for (SettingsModelString element : config.getElements()) {
			addDialogComponent(
					new DialogComponentStringSelection(element, element.getKey(), PharMoleculeConfig.ALLOWED_ELEMENTS));
		}
		closeCurrentGroup();
	}

}