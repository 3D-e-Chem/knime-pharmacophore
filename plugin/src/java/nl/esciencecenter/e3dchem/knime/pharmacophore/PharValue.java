package nl.esciencecenter.e3dchem.knime.pharmacophore;

import java.util.Objects;

import javax.swing.Icon;

import org.knime.core.data.DataValueComparator;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.data.StringValue;
import org.knime.core.data.StringValueComparator;
import org.knime.core.data.convert.DataValueAccessMethod;

public interface PharValue extends StringValue {
	public static final UtilityFactory UTILITY = new PharUtilityFactory();

	class PharUtilityFactory extends ExtensibleUtilityFactory {
		public PharUtilityFactory() {
			super(PharValue.class);
			addRendererFactory(new PharValueRenderer.Factory(), true);
		}

		private static final Icon ICON = ExtensibleUtilityFactory.loadIcon(PharValue.class, "PharValue.png");

		@Override
		public String getName() {
			return "Phar";
		}

		@Override
		public Icon getIcon() {
			return ICON;
		}

		@Override
		public String getGroupName() {
			return "Chemistry";
		}

		@Override
		protected DataValueComparator getComparator() {
			return new StringValueComparator();
		}
	}

	public static boolean equalContent(PharCell a, PharValue b) {
		return Objects.equals(a.getStringValue(), b.getStringValue());
	}

	public static int hashCode(PharCell pharCell) {
		return Objects.hashCode(pharCell.getStringValue());
	};

	@DataValueAccessMethod(name = "String (Phar)")
	Pharmacophore getPharmacophoreValue();
}
