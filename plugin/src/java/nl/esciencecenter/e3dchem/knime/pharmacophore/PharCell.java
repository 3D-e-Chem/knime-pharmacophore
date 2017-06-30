package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.io.IOException;
import java.util.Objects;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;

public class PharCell extends DataCell implements PharValue {
	private static final long serialVersionUID = -1611319590828877125L;
	public static final DataType TYPE = DataType.getType(PharCell.class);
	private String value;

	public PharCell(String value) {
		super();
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
	public int hashCode() {
		return Objects.hashCode(value);
	}

	class Serializer implements DataCellSerializer<PharCell> {

		@Override
		public void serialize(PharCell cell, DataCellDataOutput output) throws IOException {
			output.writeUTF(cell.getStringValue());
		}

		@Override
		public PharCell deserialize(DataCellDataInput input) throws IOException {
			return new PharCell(input.readUTF());
		}
	}
}
