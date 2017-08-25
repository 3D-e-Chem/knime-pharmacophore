package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

/**
 * Align points of probe pharmacophore to points of reference pharmacophore
 *
 * Algorithm:
 * <ol>
 * <li>Find candidate pairs points between probe and reference</li>
 * <li>Find biggest network of pairs, using <a href=
 * "https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm">BronKerbosch
 * clique detection algorithm</a></li>
 * <li>Calculate optimal translation and rotation using
 * <a href="https://en.wikipedia.org/wiki/Kabsch_algorithm">Kabsch algorithm
 * </a>.</li>
 * </ol>
 */
public class Aligner {
	private Aligner() {
		throw new IllegalStateException("Utility class");
	}

	public static CliqueAligner align(Pharmacophore probe, Pharmacophore reference, double cutoff, int cliqueBreak)
			throws NoOverlapFoundException {
		CliqueFinder cliqueFinder = new CliqueFinder(probe, reference, cutoff, cliqueBreak, 1);
		List<PointPair> clique = cliqueFinder.getBestCliques().poll();
		return new CliqueAligner(probe, reference, clique);
	}

	public static List<CliqueAligner> align(Pharmacophore probe, Pharmacophore reference, double cutoff,
			int cliqueBreak, int bestCliqueCount) throws NoOverlapFoundException {
		CliqueFinder cliqueFinder = new CliqueFinder(probe, reference, cutoff, cliqueBreak, bestCliqueCount);
		Queue<List<PointPair>> cliques = cliqueFinder.getBestCliques();
		List<PointPair> clique;
		List<CliqueAligner> alignments = new ArrayList<>(cliques.size());
		while ((clique = cliques.poll()) != null) {
			alignments.add(new CliqueAligner(probe, reference, clique));
		}
		// lowest rmsd first
		Collections.sort(alignments, (a, b) -> Double.compare(a.getRMSD(), b.getRMSD()));
		return alignments;
	}
}
