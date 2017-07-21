package nl.esciencecenter.e3dchem.knime.pharmacophore;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellFactory.FromComplexString;
import org.knime.core.data.DataCellFactory.FromSimpleString;
import org.knime.core.data.DataType;
import org.knime.core.data.convert.DataCellFactoryMethod;

public class PharCellFactory implements FromSimpleString, FromComplexString {

	public DataType getDataType() {
	       return PharCell.TYPE;
	}
	
	@DataCellFactoryMethod(name = "String")
	@Override
	public DataCell createCell(final String input) {
		return new PharCell(input);
	}
}
