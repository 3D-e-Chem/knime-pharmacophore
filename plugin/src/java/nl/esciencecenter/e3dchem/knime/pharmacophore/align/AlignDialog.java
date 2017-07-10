package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

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
     * New pane for configuring AlignmentTransform node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    @SuppressWarnings("unchecked")
	protected AlignDialog() {
        super();
        
        addDialogComponent(new DialogComponentColumnNameSelection(
        		new SettingsModelString(AlignModel.CFGKEY_QUERY, ""), 
        		"Query Pharmacophore column (table 1)", AlignModel.QUERY_PORT, StringValue.class
        ));

        addDialogComponent(new DialogComponentColumnNameSelection(
        		new SettingsModelString(AlignModel.CFGKEY_REFERENCE, ""), 
        		"Reference Pharmacophore column (table 2)", AlignModel.REFERENCE_PORT, StringValue.class
        ));
    }
}
