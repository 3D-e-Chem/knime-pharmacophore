package nl.esciencecenter.e3dchem.knime.pharmacophore;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "AlignmentTransform" Node.
 *
 */
public class AlignmentTransformFactory
        extends NodeFactory<AlignmentTransformModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AlignmentTransformModel createNodeModel() {
        return new AlignmentTransformModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<AlignmentTransformModel> createNodeView(final int viewIndex,
            final AlignmentTransformModel nodeModel) {
        return new AlignmentTransformView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new AlignmentTransformDialog();
    }

}
