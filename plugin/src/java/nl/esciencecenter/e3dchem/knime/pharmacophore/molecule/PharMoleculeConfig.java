package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class PharMoleculeConfig {
	public static final List<String> ALLOWED_ELEMENTS = Arrays.asList("S", "N", "C", "F", "BR", "O", "P", "CL", "B");

	private SettingsModelString column = new SettingsModelString("Column", "");
	private List<SettingsModelString> elements = Arrays.asList(new SettingsModelString("AROM", "S"),
			new SettingsModelString("HDON", "N"), new SettingsModelString("HACC", "O"),
			new SettingsModelString("LIPO", "C"), new SettingsModelString("POSC", "P"),
			new SettingsModelString("NEGC", "F"), new SettingsModelString("HYBH", "CL"),
			new SettingsModelString("HYBL", "BR"), new SettingsModelString("EXCL", "B"));

	public void saveSettingsTo(NodeSettingsWO settings) {
		column.saveSettingsTo(settings);
	}

	public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		column.validateSettings(settings);
	}

	public void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		column.loadSettingsFrom(settings);
	}

	public SettingsModelString getColumn() {
		return column;
	}

	public Map<String, String> getPhar2elementMap() {
		Map<String, String> map = new HashMap<>();
		for (SettingsModelString element : elements) {
			map.put(element.getKey(), element.getStringValue());
		}
		return map;
	}

	public List<SettingsModelString> getElements() {
		return elements;
	}

	public Map<String, String> getElement2PharMap() {
		Map<String, String> map = new HashMap<>();
		for (SettingsModelString element : elements) {
			map.put(element.getStringValue(), element.getKey());
		}
		return map;
	}

}
