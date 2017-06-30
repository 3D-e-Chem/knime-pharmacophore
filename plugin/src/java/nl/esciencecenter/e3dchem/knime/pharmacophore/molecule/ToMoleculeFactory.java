package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class ToMoleculeFactory extends NodeFactory<ToMoleculeModel> {

	@Override
	public ToMoleculeModel createNodeModel() {
		return new ToMoleculeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<ToMoleculeModel> createNodeView(int viewIndex, ToMoleculeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new ToMoleculeDialog();
	}

}
