package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "AlignmentTransform" Node.
 *
 */
public class AlignFactory
        extends NodeFactory<AlignModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AlignModel createNodeModel() {
        return new AlignModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<AlignModel> createNodeView(final int viewIndex,
            final AlignModel nodeModel) {
        return null;
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
        return new AlignDialog();
    }

}
