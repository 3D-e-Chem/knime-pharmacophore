package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.vector.doublevector.DoubleVectorCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

/**
 * This is the model implementation of AlignModel.
 *
 */
public class AlignModel extends NodeModel {
    static final int QUERY_PORT = 0;

    private final AlignConfig config = new AlignConfig();

    /**
     * Constructor for the node model.
     */
    protected AlignModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {

        BufferedDataTable table = inData[QUERY_PORT];
        DataTableSpec outSpec = tableSpec(table.getSpec());
        BufferedDataContainer container = exec.createDataContainer(outSpec);
        int queryIndex = table.getSpec().findColumnIndex(config.getQueryColumn().getStringValue());

        double cutoff = config.getCutoff().getDoubleValue();
        int cliqueBreak = config.getCliqueBreak().getIntValue();
        int cliques2align = config.getCliques2align().getIntValue();

        long rowCounter = 0L;
        long size = table.size();
        String refPharBlock = config.getReferencePharmacophore().getStringValue();
        Pharmacophore refPhar = new Pharmacophore(refPharBlock);

        for (DataRow row : table) {
            DataCell queryCell = row.getCell(queryIndex);
            Pharmacophore queryPhar = ((PharValue) queryCell).getPharmacophoreValue();

            try {
                List<CliqueAligner> alignments = Aligner.align(queryPhar, refPhar, cutoff, cliqueBreak, cliques2align);
                int cliqueIndex = 0;
                for (CliqueAligner alignment : alignments) {
                    addAlignment(row, alignment, cliqueIndex, container);
                    cliqueIndex++;
                }
            } catch (NoOverlapFoundException e) {
                handleNoOverlap(container, row, e);
            }

            exec.setProgress((double) rowCounter++ / size);
            exec.checkCanceled();
        }

        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[] { out };
    }

    private void addAlignment(DataRow queryRow, CliqueAligner alignment, int cliqueIndex, BufferedDataContainer container) {
        RowKey rowKey = new RowKey(queryRow.getKey().getString() + " - " + cliqueIndex);
        DataRow row = new AppendedColumnRow(rowKey, queryRow, new PharCell(alignment.getAligned()),
                DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()), new DoubleCell(alignment.getRMSD()),
                new IntCell(cliqueIndex), new IntCell(alignment.getCliqueSize()));
        container.addRowToTable(row);
    }

    private void handleNoOverlap(BufferedDataContainer container, DataRow queryRow, NoOverlapFoundException e) {
        MissingCell miss = new MissingCell(e.getMessage());
        DataRow row = new AppendedColumnRow(queryRow, miss, miss, miss, miss, miss);
        container.addRowToTable(row);
        setWarningMessage("Some pharmacophore could not be aligned");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        configureQuery(inSpecs);
        configureReference(inSpecs);
        DataTableSpec outSpec = tableSpec(inSpecs[QUERY_PORT]);
        return new DataTableSpec[] { outSpec };
    }

    private void configureReference(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        String pharBlock = config.getReferencePharmacophore().getStringValue();
        Pharmacophore phar = new Pharmacophore(pharBlock);
        if (phar.size() < 1) {
            throw new InvalidSettingsException("Reference pharmacophore contains no points or unable to parse");
        }
    }

    private void configureQuery(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec querySpec = inSpecs[QUERY_PORT];
        DataColumnSpec queryColumnSpec = querySpec.getColumnSpec(config.getQueryColumn().getStringValue());
        if (queryColumnSpec == null || !queryColumnSpec.getType().isCompatible(PharValue.class)) {
            for (DataColumnSpec col : querySpec) {
                if (col.getType().isCompatible(PharValue.class)) {
                    setWarningMessage(
                            "Column '" + col.getName() + "' automatically chosen as phar column as query pharmacophore");
                    config.getQueryColumn().setStringValue(col.getName());
                    break;
                }
            }
            if (config.getQueryColumn().getStringValue() == "") {
                throw new InvalidSettingsException("Table contains no phar column");
            }
        }
    }

    private DataTableSpec tableSpec(DataTableSpec inSpec) {
        DataTableSpec commonSpec = new DataTableSpec(
                new DataColumnSpecCreator("Aligned pharmacophore", PharCell.TYPE).createSpec(),
                new DataColumnSpecCreator("Transformation matrix", DoubleVectorCellFactory.TYPE).createSpec(),
                new DataColumnSpecCreator("RMSD", DoubleCell.TYPE).createSpec(),
                new DataColumnSpecCreator("Clique index", IntCell.TYPE).createSpec(),
                new DataColumnSpecCreator("Clique size", IntCell.TYPE).createSpec());

        return new DataTableSpecCreator(inSpec).addColumns(commonSpec).createSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        config.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        config.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        config.validateSettings(settings);
    }

    @Override
    protected void loadInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException, CanceledExecutionException {
        // no internals to load
    }

    @Override
    protected void saveInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException, CanceledExecutionException {
        // no internals to save
    }

}
