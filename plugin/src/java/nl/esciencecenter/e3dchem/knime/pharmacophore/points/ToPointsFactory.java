package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class ToPointsFactory extends NodeFactory<ToPointsModel> {

	@Override
	public ToPointsModel createNodeModel() {
		return new ToPointsModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<ToPointsModel> createNodeView(int viewIndex, ToPointsModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new ToPointsDialog();
	}

}
