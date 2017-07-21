package nl.esciencecenter.e3dchem.knime.pharmacophore.molecule;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.util.LockFailedException;
import org.knime.testing.core.TestrunConfiguration;

import nl.esciencecenter.e3dchem.knime.testing.TestFlowRunner;

public class MoleculeWorkflowTest {
	@Rule
	public ErrorCollector collector = new ErrorCollector();
	private TestFlowRunner runner;

	@Before
	public void setUp() {
	    TestrunConfiguration runConfiguration = new TestrunConfiguration();
	    runConfiguration.setLoadSaveLoad(false);
		runConfiguration.setTestDialogs(true);
	    runner = new TestFlowRunner(collector, runConfiguration);
	}

	@Test
	public void test_toMolecule() throws IOException, InvalidSettingsException, CanceledExecutionException,
	        UnsupportedWorkflowVersionException, LockFailedException, InterruptedException {
	    File workflowDir = new File("src/knime/to-molecule-test");
	    runner.runTestWorkflow(workflowDir);
	}

	@Test
	public void test_fromMolecule() throws IOException, InvalidSettingsException, CanceledExecutionException,
	        UnsupportedWorkflowVersionException, LockFailedException, InterruptedException {
	    File workflowDir = new File("src/knime/from-molecule-test");
	    runner.runTestWorkflow(workflowDir);
	}
}
