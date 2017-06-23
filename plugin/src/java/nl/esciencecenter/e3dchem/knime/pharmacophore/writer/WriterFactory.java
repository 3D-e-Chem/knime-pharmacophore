package nl.esciencecenter.e3dchem.knime.pharmacophore.writer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class WriterFactory  extends NodeFactory<WriterModel>{

	@Override
	public WriterModel createNodeModel() {
		return new WriterModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<WriterModel> createNodeView(int viewIndex, WriterModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new WriterDialog();
	}

}
