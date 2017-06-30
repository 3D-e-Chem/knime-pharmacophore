package nl.esciencecenter.e3dchem.knime.pharmacophore;

import org.knime.core.data.AdapterValue;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.renderer.AbstractDataValueRendererFactory;
import org.knime.core.data.renderer.DataValueRenderer;
import org.knime.core.data.renderer.MultiLineStringValueRenderer;

@SuppressWarnings("serial")
public class PharValueRenderer extends MultiLineStringValueRenderer {

	public PharValueRenderer(String description) {
		super(description);
	}

	@Override
	protected void setValue(Object value) {
		if (value instanceof PharValue) {
			super.setValue(((PharValue) value).getStringValue());
		} else if ((value instanceof AdapterValue) && ((AdapterValue) value).isAdaptable(PharValue.class)) {
			super.setValue(((AdapterValue) value).getAdapter(PharValue.class).getStringValue());
		} else {
			super.setValue(value);
		}
	}

	public static final class Factory extends AbstractDataValueRendererFactory {

		@Override
		public String getDescription() {
			return "Phar string";
		}

		@Override
		public DataValueRenderer createRenderer(DataColumnSpec colSpec) {
			return new PharValueRenderer(getDescription());
		}

	}
}
