package nl.esciencecenter.e3dchem.knime.pharmacophore.points;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class FromPointsFactory extends NodeFactory<FromPointsModel> {

	@Override
	public FromPointsModel createNodeModel() {
		return new FromPointsModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<FromPointsModel> createNodeView(int viewIndex, FromPointsModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new FromPointsDialog();
	}

}
