package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.vector.doublevector.DoubleVectorCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

import nl.esciencecenter.e3dchem.knime.pharmacophore.PharCell;
import nl.esciencecenter.e3dchem.knime.pharmacophore.PharValue;
import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

public class TopAlignModel extends AlignModel {
	static final String CFGKEY_BESTCLIQUECOUNT = "bestCliqueCount";
	private final SettingsModelInteger bestCliqueCount = new SettingsModelIntegerBounded(CFGKEY_BREAKNUMCLIQUES, 3, 0,
			100);
	
	private static final DataTableSpec outputSpec = new DataTableSpec(
			new DataColumnSpecCreator("Query pharmacophore", PharCell.TYPE).createSpec(),
			new DataColumnSpecCreator("Aligned pharmacophore", PharCell.TYPE).createSpec(),
			new DataColumnSpecCreator("Transformation matrix", DoubleVectorCellFactory.TYPE).createSpec(),
			new DataColumnSpecCreator("RMSD", DoubleCell.TYPE).createSpec());
	
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		Pharmacophore referencePharmacophore = getReferencePharmacophore(inData);

		BufferedDataTable queryData = inData[QUERY_PORT];
		DataTableSpec outSpec = new DataTableSpecCreator(outputSpec).createSpec();
		BufferedDataContainer container = exec.createDataContainer(outSpec);
		int queryIndex = queryData.getSpec().findColumnIndex(queryColumn.getStringValue());
		
		double cutoff = this.cutoff.getDoubleValue();
		int cliqueBreak = this.cliqueBreak.getIntValue();

		long i = 0L;
		for (DataRow queryRow : queryData) {
			DataCell cell = queryRow.getCell(queryIndex);
			Pharmacophore current = ((PharValue) cell).getPharmacophoreValue();
			List<CliqueAligner> alignments = Aligner.align(current, referencePharmacophore, cutoff, cliqueBreak, bestCliqueCount.getIntValue());
			for (CliqueAligner alignment : alignments) {
				container.addRowToTable(
					new DefaultRow(
						RowKey.createRowKey(i++),
						cell,
						new PharCell(alignment.getAligned()),
						DoubleVectorCellFactory.createCell(alignment.getMatrixAsArray()),
						new DoubleCell(alignment.getRMSD())
					)
				);
				exec.checkCanceled();
			}
		}
		
		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}
	
	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		super.configure(inSpecs);
		return new DataTableSpec[] { outputSpec };
	}
	
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		super.saveSettingsTo(settings);
		bestCliqueCount.saveSettingsTo(settings);
	}
	
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		super.loadValidatedSettingsFrom(settings);
		bestCliqueCount.loadSettingsFrom(settings);
	}
	
	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		super.validateSettings(settings);
		bestCliqueCount.validateSettings(settings);
	}
	
	
}
