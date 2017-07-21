package nl.esciencecenter.e3dchem.knime.pharmacophore.reader;

import java.io.File;
import java.io.IOException;

import org.junit.After;
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

public class ReaderWorkflowTest {
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

	@After
	public void tearDown() {
		// The workflow writes pharmacophore.out.phar to cwd, clear it
		File file = new File("pharmacophore.out.phar");
		if (file.exists()) {
			file.delete();
		}
	}
	
	@Test
	public void test_read_and_write() throws IOException, InvalidSettingsException, CanceledExecutionException,
	        UnsupportedWorkflowVersionException, LockFailedException, InterruptedException {
	    File workflowDir = new File("src/knime/read-write-test");
	    runner.runTestWorkflow(workflowDir);
	}
}
