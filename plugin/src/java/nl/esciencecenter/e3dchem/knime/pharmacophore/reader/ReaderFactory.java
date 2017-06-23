package nl.esciencecenter.e3dchem.knime.pharmacophore.reader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PharmacophoreReader" Node.
 *
 */
public class ReaderFactory
        extends NodeFactory<ReaderModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ReaderModel createNodeModel() {
        return new ReaderModel();
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
    public NodeView<ReaderModel> createNodeView(final int viewIndex,
            final ReaderModel nodeModel) {
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
        return new ReaderDialog();
    }

}
