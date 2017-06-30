package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

public class ToMoleculeDialog extends PharMoleculeDialog {

	@SuppressWarnings("unchecked")
	public ToMoleculeDialog() {
		super();

		addDialogComponent(
				new DialogComponentColumnNameSelection(config.getColumn(), "Pharmacophore column", 0, PharValue.class));

		addElementsMappingGroup();
	}

}
