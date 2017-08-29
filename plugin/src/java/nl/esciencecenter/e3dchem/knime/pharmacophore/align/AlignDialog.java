package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;

/**
 * <code>NodeDialog</code> for the "AlignmentTransform" Node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 */
public class AlignDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring AlignmentTransform node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected AlignDialog() {
		super();

		buildComponents();
	}

	@SuppressWarnings("unchecked")
	protected void buildComponents() {
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(AlignModel.CFGKEY_QUERY, ""),
				"Query Pharmacophore column (table 1)", AlignModel.QUERY_PORT, PharValue.class));

		addDialogComponent(
				new DialogComponentColumnNameSelection(new SettingsModelString(AlignModel.CFGKEY_REFERENCE, ""),
						"Reference Pharmacophore column (table 2)", AlignModel.REFERENCE_PORT, PharValue.class));

		createNewTab("Advanced");

		addDialogComponent(
				new DialogComponentNumber(new SettingsModelDoubleBounded(AlignModel.CFGKEY_CUTOFF, 1.0, 0.0, 1000.0),
						"Tolerance threshold for considering two distances to be equivalent", 0.1));

		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(AlignModel.CFGKEY_BREAKNUMCLIQUES, 3000, 0, 5000),
				"Break when set number of cliques is found", 1));
		
		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(AlignModel.CFGKEY_CLIQUES2ALIGN, 3, 1, 100),
				"Number of cliques of each query pharmacophore to align", 1
		));
		
		addDialogComponent(new DialogComponentBoolean(
				new SettingsModelBoolean(AlignModel.CFGKEY_ALLALIGNMENTS, false), "All alignments of cliques"
				)
		);
	}
}
