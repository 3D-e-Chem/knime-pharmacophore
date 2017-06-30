package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class FromMoleculeFactory extends NodeFactory<FromMoleculeModel> {

	@Override
	public FromMoleculeModel createNodeModel() {
		return new FromMoleculeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<FromMoleculeModel> createNodeView(int viewIndex, FromMoleculeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new FromMoleculeDialog();
	}
}
