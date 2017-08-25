package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class TopAlignFactory extends NodeFactory<TopAlignModel>{

	@Override
	public TopAlignModel createNodeModel() {
		return new TopAlignModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<TopAlignModel> createNodeView(int viewIndex, TopAlignModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new TopAlignDialog();
	}
}
