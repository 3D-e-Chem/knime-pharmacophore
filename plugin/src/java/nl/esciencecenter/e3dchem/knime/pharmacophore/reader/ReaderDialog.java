package nl.esciencecenter.e3dchem.knime.pharmacophore.reader;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "PharmacophoreReader" Node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 */
public class ReaderDialog extends DefaultNodeSettingsPane {
    /**
     * New pane for configuring PharmacophoreReader node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected ReaderDialog() {
        super();

        addDialogComponent(new DialogComponentFileChooser(
                new SettingsModelString(
                    ReaderModel.CFGKEY_FILENAME, null
                 ), "phar_read", JFileChooser.OPEN_DIALOG, ".phar"
        ));

    }
}
