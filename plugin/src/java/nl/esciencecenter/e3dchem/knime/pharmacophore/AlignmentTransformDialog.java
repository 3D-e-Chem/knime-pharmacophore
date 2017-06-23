package nl.esciencecenter.e3dchem.knime.pharmacophore;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "AlignmentTransform" Node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 */
public class AlignmentTransformDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring AlignmentTransform node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected AlignmentTransformDialog() {
        super();

        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    AlignmentTransformModel.CFGKEY_COUNT,
                    AlignmentTransformModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));

    }
}
