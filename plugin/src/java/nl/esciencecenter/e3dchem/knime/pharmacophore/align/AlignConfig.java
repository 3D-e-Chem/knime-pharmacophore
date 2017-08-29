package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class AlignConfig {
    private static final String CFGKEY_QUERY = "queryColumn";
    private final SettingsModelString queryColumn = new SettingsModelString(CFGKEY_QUERY, "");

    private static final String CFGKEY_REFERENCE = "referencePharmacophore";
    private final SettingsModelString referencePharmacophore = new SettingsModelString(CFGKEY_REFERENCE, "");

    private static final String CFGKEY_CUTOFF = "cutoff";
    private final SettingsModelDouble cutoff = new SettingsModelDoubleBounded(CFGKEY_CUTOFF, 1.0, 0.0, 1000.0);

    private static final String CFGKEY_BREAKNUMCLIQUES = "cliqueBreak";
    private final SettingsModelInteger cliqueBreak = new SettingsModelIntegerBounded(CFGKEY_BREAKNUMCLIQUES, 3000, 1, 50000);

    private static final String CFGKEY_CLIQUES2ALIGN = "cliques2align";
    private final SettingsModelInteger cliques2align = new SettingsModelIntegerBounded(CFGKEY_CLIQUES2ALIGN, 3, 1, 100);

    public SettingsModelString getQueryColumn() {
        return queryColumn;
    }

    public SettingsModelString getReferencePharmacophore() {
        return referencePharmacophore;
    }

    public SettingsModelDouble getCutoff() {
        return cutoff;
    }

    public SettingsModelInteger getCliqueBreak() {
        return cliqueBreak;
    }

    public SettingsModelInteger getCliques2align() {
        return cliques2align;
    }

    public void saveSettingsTo(NodeSettingsWO settings) {
        queryColumn.saveSettingsTo(settings);
        referencePharmacophore.saveSettingsTo(settings);
        cutoff.saveSettingsTo(settings);
        cliqueBreak.saveSettingsTo(settings);
        cliques2align.saveSettingsTo(settings);
    }

    public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
        queryColumn.loadSettingsFrom(settings);
        referencePharmacophore.loadSettingsFrom(settings);
        cutoff.loadSettingsFrom(settings);
        cliqueBreak.loadSettingsFrom(settings);
        cliques2align.loadSettingsFrom(settings);
    }

    public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
        queryColumn.validateSettings(settings);
        referencePharmacophore.validateSettings(settings);
        cutoff.validateSettings(settings);
        cliqueBreak.validateSettings(settings);
        cliques2align.validateSettings(settings);
    }

}
