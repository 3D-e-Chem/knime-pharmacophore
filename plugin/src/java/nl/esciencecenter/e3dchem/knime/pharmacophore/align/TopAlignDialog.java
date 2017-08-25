package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

public class TopAlignDialog extends AlignDialog {

	@Override
	protected void buildComponents() {
		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(TopAlignModel.CFGKEY_BESTCLIQUECOUNT, 3, 0, 1000),
				"Maximum number of alignments per query pharmacophore", 1
				)
		);
		super.buildComponents();
	}
}
