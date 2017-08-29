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
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
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
    static final int REFERENCE_PORT = 1;

    private final AlignConfig config = new AlignConfig();

    /**
     * Constructor for the node model.
     */
    protected AlignModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {

        BufferedDataTable queryData = inData[QUERY_PORT];
        BufferedDataTable refData = inData[REFERENCE_PORT];
        DataTableSpec outSpec = tableSpec(queryData.getSpec());
        BufferedDataContainer container = exec.createDataContainer(outSpec);
        int queryIndex = queryData.getSpec().findColumnIndex(config.getQueryColumn().getStringValue());
        int referenceIndex = refData.getSpec().findColumnIndex(config.getReferenceColumn().getStringValue());

        long currentInRow = 0L;
        long size = refData.size() * queryData.size();
        long currentOutRow = 0L;
        for (DataRow refRow : refData) {
            DataCell refCell = refRow.getCell(referenceIndex);
            for (DataRow queryRow : queryData) {
                currentOutRow = align(container, queryIndex, currentOutRow, refCell, queryRow);

                exec.setProgress((double) currentInRow++ / size);
                exec.checkCanceled();
            }
        }

        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[] { out };
    }

    private long align(BufferedDataContainer container, int queryIndex, long currentOutRow, DataCell refCell, DataRow queryRow) {
        Pharmacophore referencePharmacophore = ((PharValue) refCell).getPharmacophoreValue();
        double cutoff = config.getCutoff().getDoubleValue();
        int cliqueBreak = config.getCliqueBreak().getIntValue();
        boolean all = config.getAllalignments().getBooleanValue();
        int cliques2align = config.getCliques2align().getIntValue();

        DataCell queryCell = queryRow.getCell(queryIndex);
        Pharmacophore queryPhar = ((PharValue) queryCell).getPharmacophoreValue();
        try {
            List<CliqueAligner> alignments = Aligner.align(queryPhar, referencePharmacophore, cutoff, cliqueBreak, cliques2align);
            if (all) {
                currentOutRow = addAllAlignments(container, currentOutRow, refCell, queryCell, alignments);
            } else {
                addBestAlignment(container, refCell, queryRow, alignments);
            }
        } catch (NoOverlapFoundException e) {
            currentOutRow = handleNoOverlap(container, currentOutRow, refCell, queryRow, queryCell, e);
        }
        return currentOutRow;
    }

    private void addBestAlignment(BufferedDataContainer container, DataCell refCell, DataRow queryRow,
            List<CliqueAligner> alignments) {
        boolean includeRef = config.getIncludeReference().getBooleanValue();
        CliqueAligner alignment = alignments.get(0);
        Pharmacophore alignedPhar = alignment.getAligned();
        if (includeRef) {
            container.addRowToTable(new AppendedColumnRow(queryRow, new PharCell(alignedPhar),
                    DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()), new DoubleCell(alignment.getRMSD()),
                    refCell));
        } else {
            container.addRowToTable(new AppendedColumnRow(queryRow, new PharCell(alignedPhar),
                    DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()),
                    new DoubleCell(alignment.getRMSD())));
        }
    }

    private long addAllAlignments(BufferedDataContainer container, long currentOutRow, DataCell refCell,
            DataCell queryCell, List<CliqueAligner> alignments) {
        boolean includeRef = config.getIncludeReference().getBooleanValue();
        for (CliqueAligner alignment : alignments) {
            if (includeRef) {
                container.addRowToTable(new DefaultRow(RowKey.createRowKey(currentOutRow++), queryCell,
                        new PharCell(alignment.getAligned()),
                        DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()),
                        new DoubleCell(alignment.getRMSD()), refCell));
            } else {
                container.addRowToTable(new DefaultRow(RowKey.createRowKey(currentOutRow++), queryCell,
                        new PharCell(alignment.getAligned()),
                        DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()),
                        new DoubleCell(alignment.getRMSD())));
            }
        }
        return currentOutRow;
    }

    private long handleNoOverlap(BufferedDataContainer container, long currentOutRow, DataCell refCell, DataRow queryRow,
            DataCell queryCell, NoOverlapFoundException e) {
        MissingCell miss = new MissingCell(e.getMessage());
        boolean includeRef = config.getIncludeReference().getBooleanValue();
        boolean all = config.getAllalignments().getBooleanValue();
        if (all) {
            if (includeRef) {
                container.addRowToTable(
                        new DefaultRow(RowKey.createRowKey(currentOutRow++), queryCell, miss, miss, miss, refCell));
            } else {
                container.addRowToTable(new DefaultRow(RowKey.createRowKey(currentOutRow++), queryCell, miss, miss, miss));
            }
        } else {
            if (includeRef) {
                container.addRowToTable(new AppendedColumnRow(queryRow, miss, miss, miss, refCell));
            } else {
                container.addRowToTable(new AppendedColumnRow(queryRow, miss, miss, miss));
            }
        }
        setWarningMessage("Some pharmacophore could not be aligned");
        return currentOutRow;
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
        DataTableSpec refSpec = inSpecs[REFERENCE_PORT];
        DataColumnSpec refColumnSpec = refSpec.getColumnSpec(config.getReferenceColumn().getStringValue());
        if (refColumnSpec == null || !refColumnSpec.getType().isCompatible(PharValue.class)) {
            for (DataColumnSpec col : refSpec) {
                if (col.getType().isCompatible(PharValue.class)) {
                    setWarningMessage(
                            "Column '" + col.getName() + "' automatically chosen as phar column as reference pharmacophore");
                    config.getReferenceColumn().setStringValue(col.getName());
                    break;
                }
            }
            if (config.getReferenceColumn().getStringValue() == "") {
                throw new InvalidSettingsException("Table on second port contains no phar column");
            }
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
                throw new InvalidSettingsException("Table on first port contains no phar column");
            }
        }
    }

    private DataTableSpec tableSpec(DataTableSpec inSpec) {
        DataTableSpec commonSpec = new DataTableSpec(
                new DataColumnSpecCreator("Aligned pharmacophore", PharCell.TYPE).createSpec(),
                new DataColumnSpecCreator("Transformation matrix", DoubleVectorCellFactory.TYPE).createSpec(),
                new DataColumnSpecCreator("RMSD", DoubleCell.TYPE).createSpec());

        boolean includeRef = config.getIncludeReference().getBooleanValue();
        if (includeRef) {
            DataColumnSpec refSpec = new DataColumnSpecCreator("Reference pharmacophore", PharCell.TYPE).createSpec();
            commonSpec = new DataTableSpecCreator(commonSpec).addColumns(refSpec).createSpec();
        }

        boolean all = config.getAllalignments().getBooleanValue();
        if (all) {
            DataTableSpec querySpec = new DataTableSpec(
                    new DataColumnSpecCreator("Query pharmacophore", PharCell.TYPE).createSpec());
            return new DataTableSpecCreator(querySpec).addColumns(commonSpec).createSpec();
        } else {
            return new DataTableSpecCreator(inSpec).addColumns(commonSpec).createSpec();
        }
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
