package nl.esciencecenter.e3dchem.knime.pharmacophore.align;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class Aligner {
	private List<String> probeTypes;
	private List<String> refTypes;
	private DMatrixRMaj probePoints;
	private DMatrixRMaj refPoints;
	private Map<PointPair, List<PointPair>> pairs = new HashMap<>();
	private Set<PointPair> nodes = new HashSet<>();
	private double cutoff = 1.0;
	private int cliqueBreak = 3000;
	private boolean searchComplete;
	private int cliqueCount;
	private List<PointPair> bestClique;

	public Aligner(String probe, String reference, double cutoff) throws NoOverlapFoundException {
		Entry<List<String>, DMatrixRMaj> probeParsed = parse(probe);
		probeTypes = probeParsed.getKey();
		probePoints = probeParsed.getValue();
		Entry<List<String>, DMatrixRMaj> refParsed = parse(reference);
		refTypes = refParsed.getKey();
		refPoints = refParsed.getValue();
		this.cutoff = cutoff;
		transformation();
	}

	private Entry<List<String>, DMatrixRMaj> parse(String pharBlock) {
		List<String> types = new ArrayList<>();
		List<Double> rawPointsX = new ArrayList<>();
		List<Double> rawPointsY = new ArrayList<>();
		List<Double> rawPointsZ = new ArrayList<>();
		for (String line : pharBlock.split("\\r?\\n")) {
			if (line.startsWith("$$$$")) {
				break;
			} else if (line.startsWith("#")) {
				// Skip comments
			} else {
				String[] cols = line.split("\\s+");
				if (cols.length < 9) {
					// pharmacophore name
				} else {
					types.add(cols[0]);
					rawPointsX.add((double) Float.parseFloat(cols[1]));
					rawPointsY.add((double) Float.parseFloat(cols[2]));
					rawPointsZ.add((double) Float.parseFloat(cols[3]));
				}
			}
		}
		DMatrixRMaj points = new DMatrixRMaj(rawPointsX.size(), 3);
		for (int i = 0; i < rawPointsX.size(); i++) {
			points.set(i, 0, rawPointsX.get(i));
			points.set(i, 1, rawPointsY.get(i));
			points.set(i, 2, rawPointsZ.get(i));
		}
		return new SimpleEntry<List<String>, DMatrixRMaj>(types, points);
	}

	private DMatrixRMaj computeDistances(DMatrixRMaj points) {
		SimpleMatrix simplePoints = SimpleMatrix.wrap(points);
		DMatrixRMaj distances = new DMatrixRMaj(points.getNumRows(), points.getNumRows());
		int nrPoints = points.getNumRows();
		for (int i = 0; i < nrPoints; i++) {
			for (int j = 0; j < nrPoints; j++) {
				if (i < j) {
					double a = Math.sqrt(simplePoints.extractVector(true, i).elementPower(2).elementSum());
					double b = Math.sqrt(simplePoints.extractVector(true, j).elementPower(2).elementSum());
					double dist = Math.abs(a - b);
					distances.set(i, j, dist);
					distances.set(j, i, dist);
				}
			}
		}
		return distances;
	}

	private void candidatePairs() {
		DMatrixRMaj probeDistances = computeDistances(probePoints);
		DMatrixRMaj refDistances = computeDistances(refPoints);

		// IDENTIFY EACH PAIR IN PHARMACOPHORE A
		for (int a = 0; a < refPoints.getNumRows() - 1; a++) {
			for (int b = a + 1; b < probePoints.getNumRows(); b++) {
				// RECORD THE FEATURE TYPES AND THE DISTANCE BETWEEN THE
				// FEATURES
				String typeAA = refTypes.get(a);
				String typeAB = probeTypes.get(b);
				double distA = refDistances.get(a, b);
				// CHECK WHETHER FEATURE PAIR IS SYMMETRICAL
				boolean symmetrical = typeAA.equals(typeAB);

				// IDENTIFY FIRST FEATURE FOR POSSIBLE PAIR IN PHARMACOPHORE B
				for (int c = 0; c < probePoints.getNumRows() - 1; c++) {
					// CHECK WHETHER THE FIRST FEATURE MATCHES EITHER OR BOTH
					// OF THE PPHORE A FEATURE PAIR. CONTINUE IF NOT
					String typeBA = probeTypes.get(c);
					int matchA = 0;
					if (typeBA.equals(typeAA)) {
						matchA = 1;
					} else if (typeBA.equals(typeAB)) {
						matchA = 2;
					} else {
						continue;
					}

					// IDENITFY SECOND FEATURE FOR PPHORE B FEATURE PAIR
					for (int d = c + 1; d < probePoints.getNumRows(); d++) {
						// CHECK WHETHER FEATURES MATCH THE FEATURE PAIR IN
						// PHARMACOPHORE A
						// IF NOT THEN CONTINUE
						String typeBB = probeTypes.get(d);
						if (!(((matchA == 1 && typeAB == typeBB) || (matchA == 2 && typeAA == typeBB)))) {
							continue;
						}

						// CALCULATE DISTANCE BETWEEN THE PPHORE B FEATURE PAIR
						double distB = probeDistances.get(c, d);

						// CHECK WHETHER DISTANCE MATCHES
						if (Math.abs(distA - distB) < cutoff) {
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
					}
				}
			}
		}
	}

	private void getBestClique() throws NoOverlapFoundException {
		bestClique = new ArrayList<>();
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
			if (potential_clique.size() > bestClique.size()) {
				bestClique = potential_clique;

				// IF CLIQUE IS LARGER THAN HALF THE SIZE OF THE SMALLEST
				// PHARMACOPHORE SET COMPLETE
				if (!searchComplete && potential_clique
						.size() >= (Math.min(refPoints.getNumRows(), probePoints.getNumRows()) / 2)) {
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

	private SimpleMatrix filterPointsByBestClique(Matrix points) {
		DMatrixRMaj cliquePoints = new DMatrixRMaj(bestClique.size(), 3);
		for (int i = 0; i < bestClique.size(); i++) {
			cliquePoints.add(i, 0, refPoints.get(bestClique.get(i).first, 0));
			cliquePoints.add(i, 1, refPoints.get(bestClique.get(i).first, 1));
			cliquePoints.add(i, 2, refPoints.get(bestClique.get(i).first, 2));
		}
		return SimpleMatrix.wrap(cliquePoints);
	}

	private SimpleMatrix getCentroid(SimpleMatrix points) {
		DMatrixRMaj refSum = org.ejml.dense.row.CommonOps_DDRM.sumCols(points.matrix_F64(), null);
		SimpleMatrix refCentroid = SimpleMatrix.wrap(refSum).divide(points.numRows());
		return refCentroid;
	}

	private void transformation() throws NoOverlapFoundException {
		candidatePairs();
		getBestClique();

		if (bestClique == null) {
			throw new NoOverlapFoundException();
		}

		SimpleMatrix refCliquePoints = filterPointsByBestClique(refPoints);
		SimpleMatrix probeCliquePoints = filterPointsByBestClique(probePoints);
		SimpleMatrix refCentroid = getCentroid(refCliquePoints);
		SimpleMatrix probeCentroid = getCentroid(probeCliquePoints);
		SimpleMatrix refCentered = refCliquePoints.minus(refCentroid);
		SimpleMatrix probeCentered = probeCliquePoints.minus(probeCentroid);

		SimpleMatrix cov = refCentered.transpose().mult(probeCentered);
		SimpleSVD<SimpleMatrix> svd = cov.svd();
		boolean d = svd.getU().determinant() * svd.getW().determinant() < 0.0;
		if (d) {
			// not right handed, change it
		}
		SimpleMatrix translate = refCentroid.minus(probeCentroid);
		SimpleMatrix probeAlignedPoints = probeCentered.mult(svd.getU()).plus(translate);
		// TODO complete it
	}

	public double[] getMatrix() {
		return new double[] {};
	}

	public double getRMSD() {
		return 0;
	}

	public String aligned() {
		return "";
	}
}
