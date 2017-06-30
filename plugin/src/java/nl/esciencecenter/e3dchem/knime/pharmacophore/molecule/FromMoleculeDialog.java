package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import org.knime.chem.types.MolValue;
import org.knime.chem.types.SdfValue;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;

public class FromMoleculeDialog extends PharMoleculeDialog {
	@SuppressWarnings("unchecked")
	public FromMoleculeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(config.getColumn(), "Molecule (SDF/Mol) column", 0,
				SdfValue.class, MolValue.class));

		addElementsMappingGroup();
	}
}
