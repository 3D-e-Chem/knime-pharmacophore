package nl.esciencecenter.e3dchem.knime.pharmacophore;

import javax.swing.Icon;

import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.data.StringValue;

public interface PharValue extends StringValue {
	public static final UtilityFactory UTILITY = new PharUtilityFactory();
			
	class PharUtilityFactory extends ExtensibleUtilityFactory {
		public PharUtilityFactory() {
			super(PharValue.class);
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
		
	};
}
