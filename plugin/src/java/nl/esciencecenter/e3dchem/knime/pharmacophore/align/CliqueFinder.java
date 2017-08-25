package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import org.ejml.simple.SimpleMatrix;

import nl.esciencecenter.e3dchem.knime.pharmacophore.Pharmacophore;

public class CliqueFinder {
	private Pharmacophore probe;
	private Pharmacophore reference;
	private Map<PointPair, List<PointPair>> pairs = new HashMap<>();
	private Set<PointPair> nodes = new HashSet<>();
	private double cutoff = 1.0;
	private int cliqueBreak = 3000;
	private boolean searchComplete;
	private int cliqueCount;
	private Deque<List<PointPair>> bestCliques;

	public CliqueFinder(Pharmacophore probe, Pharmacophore reference, double cutoff, int cliqueBreak, int bestCliqueCount)
			throws NoOverlapFoundException {
		this.probe = probe;
		this.reference = reference;
		this.cutoff = cutoff;
		this.cliqueBreak = cliqueBreak;
		this.bestCliques = new LinkedBlockingDeque<>(bestCliqueCount);
		candidatePairs();
		findBestCliques();
	}

	/**
	 * Code was copied from Kripo yasara plugin written by Dave Wood, August 8th
	 * 2008 and was translated from Python to Java/ejml/oo
	 * @throws NoOverlapFoundException 
	 */
	private void candidatePairs() throws NoOverlapFoundException {
		SimpleMatrix probeDistances = probe.getDistancesBetweenPoints();
		SimpleMatrix refDistances = reference.getDistancesBetweenPoints();

		// IDENTIFY EACH PAIR IN PHARMACOPHORE A
		for (int a = 0; a < reference.size() - 1; a++) {
			for (int b = a + 1; b < reference.size(); b++) {
				// RECORD THE FEATURE TYPES AND THE DISTANCE BETWEEN THE
				// FEATURES
				String typeAA = reference.get(a).type;
				String typeAB = reference.get(b).type;
				double distA = refDistances.get(a, b);
				// CHECK WHETHER FEATURE PAIR IS SYMMETRICAL
				boolean symmetrical = typeAA.equals(typeAB);

				// IDENTIFY FIRST FEATURE FOR POSSIBLE PAIR IN PHARMACOPHORE B
				for (int c = 0; c < probe.size() - 1; c++) {
					// CHECK WHETHER THE FIRST FEATURE MATCHES EITHER OR BOTH
					// OF THE PPHORE A FEATURE PAIR. CONTINUE IF NOT
					String typeBA = probe.get(c).type;
					int matchA = 0;
					if (typeBA.equals(typeAA)) {
						matchA = 1;
					} else if (typeBA.equals(typeAB)) {
						matchA = 2;
					} else {
						continue;
					}

					// IDENITFY SECOND FEATURE FOR PPHORE B FEATURE PAIR
					for (int d = c + 1; d < probe.size(); d++) {
						// CHECK WHETHER FEATURES MATCH THE FEATURE PAIR IN
						// PHARMACOPHORE A
						// IF NOT THEN CONTINUE
						String typeBB = probe.get(d).type;
						boolean pairMatch1 = ((matchA == 1) && (typeAB.equals(typeBB)));
						boolean pairMatch2 = ((matchA == 2) && (typeAA.equals(typeBB)));
						if (!(pairMatch1 || pairMatch2)) {
							continue;
						}

						// CALCULATE DISTANCE BETWEEN THE PPHORE B FEATURE PAIR
						double distB = probeDistances.get(c, d);

						// CHECK WHETHER DISTANCE MATCHES
						if (Math.abs(distA - distB) < cutoff) {
							storeCandidatePair(a, b, c, d, symmetrical, matchA);
						}
					}
				}
			}
		}
		
		if (nodes.isEmpty()) {
			throw new NoOverlapFoundException();
		}
	}

	private void storeCandidatePair(int a, int b, int c, int d, boolean symmetrical, int matchA) {
		// IF IT DOES, RECORD THE PAIR OF PAIRS AS BEING
		// COMPATIBLE
		PointPair pairAB = new PointPair(a, b);
		PointPair pairBA = new PointPair(b, a);
		PointPair pairCD = new PointPair(c, d);
		PointPair pairDC = new PointPair(d, c);
		PointPair pairAC = new PointPair(a, c);
		PointPair pairBD = new PointPair(b, d);
		PointPair pairAD = new PointPair(a, d);
		PointPair pairBC = new PointPair(b, c);

		// MAKE SURE YOU PAIR THE FEATURES THE RIGHT WAY
		// ROUND
		if (matchA == 1) {
			if (!pairs.containsKey(pairAB)) {
				pairs.put(pairAB, new ArrayList<PointPair>());
				pairs.put(pairBA, new ArrayList<PointPair>());
			}
			// ADD PPHORE B PAIR TO THE COMPATIBLE PAIRS
			// FOR PPHORE A PAIR
			pairs.get(pairAB).add(pairCD);
			pairs.get(pairBA).add(pairDC);
			nodes.add(pairAC);
			nodes.add(pairBD);
			if (symmetrical) {
				nodes.add(pairAD);
				nodes.add(pairBC);
			}
			// FEATURES PAIRED THE OTHER WAY ROUND
		} else if (matchA == 2) {
			if (!pairs.containsKey(pairAB)) {
				pairs.put(pairAB, new ArrayList<PointPair>());
				pairs.put(pairBA, new ArrayList<PointPair>());
			}
			// ADD PPHORE B PAIR TO THE COMPATIBLE PAIRS
			// FOR PPHORE A PAIR
			pairs.get(pairAB).add(pairDC);
			pairs.get(pairBA).add(pairCD);
			nodes.add(pairAD);
			nodes.add(pairBC);
		}
	}

	private void findBestCliques() throws NoOverlapFoundException {
		searchComplete = false;
		cliqueCount = 0;
		List<PointPair> potential_clique = new ArrayList<>();
		List<PointPair> candidates = new ArrayList<>();
		List<PointPair> already_found = new ArrayList<>();
		candidates.addAll(nodes);

		findCliques(potential_clique, candidates, already_found);
	}

	/**
	 * BronKerbosch clique detection algorithm Based on the pseudo code in
	 * 'Reporting maximal cliques: new insights into an old problem' Cazaals and
	 * Chinmay 2007
	 * 
	 * @param potential_clique
	 * @param candidates
	 * @param already_found
	 */
	private void findCliques(List<PointPair> potential_clique, List<PointPair> candidates,
			List<PointPair> already_found) {
		// IF SATISFACTORY CLIQUE HAS BEEN FOUND THEN BREAK OUT OF THE LOOP
		if (searchComplete) {
			return;
			// SET COMPLETION IF BREAK POINT REACHED
		} else if (cliqueBreak != 0 && cliqueCount > cliqueBreak) {
			searchComplete = true;
			return;
			// COUNT THE NUMBER OF CLIQUES RECORDED SO FAR
		} else if (candidates.isEmpty() && already_found.isEmpty()) {
			cliqueCount++;
			// RECORD CLIQUE IF IT IS THE LARGEST FOUND SO FAR
			if (bestCliques.isEmpty() || potential_clique.size() > bestCliques.peek().size()) {
				List<PointPair> bestClique = potential_clique;
				if (!bestCliques.offerFirst(bestClique)) {
					bestCliques.removeLast();
					bestCliques.addFirst(bestClique);
				}

				// IF CLIQUE IS LARGER THAN HALF THE SIZE OF THE SMALLEST
				// PHARMACOPHORE SET COMPLETE
				if (!searchComplete && potential_clique.size() >= (Math.min(reference.size(), probe.size()) / 2)) {
					searchComplete = true;
				}
			}
			// DO THE BRON KERBOSCH THING
		} else {
			// FOR EACH POSSIBLE PAIR OF FEATURES
			while (!candidates.isEmpty()) {
				PointPair u_i = candidates.remove(0);

				// MAKE COPY OF MATCHED PAIRS
				List<PointPair> r_new = new ArrayList<>(potential_clique);
				r_new.add(u_i);

				// MAKE COPY OF UNTESTED PAIRS
				List<PointPair> p_new = new ArrayList<>(candidates);

				// CHECK WHETHER UNTESTED FEATURE PAIRS ARE COMPATIBLE WITH
				// CURRENT CLIQUE
				int j = 0;
				while (j < p_new.size()) {
					PointPair p_i = p_new.get(j);
					PointPair up_i = new PointPair(u_i.first, p_i.first);
					if ((!pairs.containsKey(up_i))
							|| (!(pairs.get(up_i).contains(new PointPair(u_i.second, p_i.second))))) {
						p_new.remove(j);
						j--;
					}
					j++;
				}

				// CHECK WHETHER NEW CLIQUE IS PART OF PREVIOUSLY TESTED CLIQUE
				List<PointPair> x_new = new ArrayList<>(already_found);
				j = 0;
				while (j < x_new.size()) {
					PointPair x_i = x_new.get(j);
					PointPair ux_i = new PointPair(u_i.first, x_i.first);
					if ((!pairs.containsKey(ux_i))
							|| (!(pairs.get(ux_i).contains(new PointPair(u_i.second, x_i.second))))) {
						x_new.remove(j);
						j--;
					}
					j++;
				}

				// PROGRESS A LEVEL IN THE SEARCH TREE
				findCliques(r_new, p_new, x_new);

				// IF THE MAX CLIQUE HAS BEEN FOUND THEN BACKTRACK THROUGH THE
				// SEARCH TREE
				if (searchComplete) {
					break;
				}

				// ADD FEATURE PAIR TO CURRENT CLIQUE
				already_found.add(u_i);
			}
		}
	}

	public Queue<List<PointPair>> getBestCliques() {
		return bestCliques;
	}
}
