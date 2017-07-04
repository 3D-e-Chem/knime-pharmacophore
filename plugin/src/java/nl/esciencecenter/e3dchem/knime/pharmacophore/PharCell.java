package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.StringValue;

public class PharCell extends DataCell implements PharValue, StringValue {
	private static final long serialVersionUID = -1611319590828877125L;
	public static final DataType TYPE = DataType.getType(PharCell.class);
	private String value;

	public PharCell(String value) {
		if (value == null) {
			throw new NullPointerException("Phar value must not be null.");
		}
		this.value = value;
	}

	@Override
	public String getStringValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	protected boolean equalsDataCell(DataCell dc) {
		return value.equals(((PharCell) dc).getStringValue());
	}

	@Override
	protected boolean equalContent(DataValue otherValue) {
		return PharValue.equalContent(this, (PharValue) otherValue);
	}

	@Override
	public int hashCode() {
		return PharValue.hashCode(this);
	}

	public static class Serializer implements DataCellSerializer<PharCell> {

		@Override
		public void serialize(final PharCell cell, final DataCellDataOutput output) throws IOException {
			output.writeUTF(cell.getStringValue());
		}

		@Override
		public PharCell deserialize(final DataCellDataInput input) throws IOException {
			return new PharCell(input.readUTF());
		}
	}
}
