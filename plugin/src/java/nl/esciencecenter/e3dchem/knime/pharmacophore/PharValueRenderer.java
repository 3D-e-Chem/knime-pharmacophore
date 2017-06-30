package nl.esciencecenter.e3dchem.knime.pharmacophore;

import org.knime.core.data.AdapterValue;
import org.knime.core.data.renderer.MultiLineStringValueRenderer;

@SuppressWarnings("serial")
public class PharValueRenderer extends MultiLineStringValueRenderer {
	public PharValueRenderer() {
		super("Phar string");
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

}
