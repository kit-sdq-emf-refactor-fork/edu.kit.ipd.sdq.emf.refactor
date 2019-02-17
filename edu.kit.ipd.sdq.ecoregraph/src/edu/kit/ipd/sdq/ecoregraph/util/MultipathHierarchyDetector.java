package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.Dependency;
import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;
import edu.kit.ipd.sdq.ecoregraph.Dependency.DependencyType;

/**
 * The MultipathHierarchyDetector class implements the logic of detecting
 * multipath hierarchies, i.e. when a class inherits (indirectly) from another
 * class over two or more paths
 * 
 * @author Amine Kechaou
 *
 */
public class MultipathHierarchyDetector {
	private List<List<EClass>> multipaths;
	private EcoreGraph eGraph;

	public MultipathHierarchyDetector(EcoreGraph eGraph) {
		this.multipaths = new LinkedList<List<EClass>>();
		this.eGraph = eGraph;
		findMultipathHierarchies();
	}

	private void findMultipathHierarchies() {
		AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph = EcoreGraphUtil.hierarchySubGraph(eGraph);
		Set<EClass> startVertices = hierarchySubGraph.vertexSet().stream()
				.filter(c -> hierarchySubGraph.outDegreeOf(c) >= 2).map(c -> (EClass) c).collect(Collectors.toSet());

		for (EClass startVertex : startVertices) {
			List<EClass> superTypes = startVertex.getESuperTypes();
			Set<EClass> destinations = new HashSet<EClass>();
			for (EClass superType : superTypes) {
				List<EClass> otherSuperTypes = new ArrayList<EClass>();
				otherSuperTypes.addAll(superTypes);
				otherSuperTypes.remove(superType);

				Set<EClass> potentialCandidates = new HashSet<EClass>();
				potentialCandidates.addAll(superType.getEAllSuperTypes());
				potentialCandidates.add(superType);

				for (EClass otherSuperType : otherSuperTypes) {
					List<EClass> allOtherSuperSuperTypes = otherSuperType.getEAllSuperTypes();
					for (EClass potentialCandidate : potentialCandidates) {
						if (allOtherSuperSuperTypes.contains(potentialCandidate)) {
							destinations.add(potentialCandidate);
						}
					}
				}
			}

			for (EClass destination : destinations) {
				findAllPaths(startVertex, destination, hierarchySubGraph, new Stack<EClass>());
			}
		}
	}

	private void findAllPaths(EClass startVertex, EClass destination,
			AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph, Stack<EClass> path) {
		path.push(startVertex);
		if (startVertex == destination) { // path found
			this.multipaths.add(new LinkedList<EClass>(path));
		} else {
			Set<DefaultEdge> outgoingEdges = hierarchySubGraph.outgoingEdgesOf(startVertex);
			Set<EClass> neighbors = outgoingEdges.stream().map(e -> (EClass) hierarchySubGraph.getEdgeTarget(e))
					.collect(Collectors.toSet());
			for (EClass neighbor : neighbors) {
				findAllPaths(neighbor, destination, hierarchySubGraph, path);
			}
		}
		path.pop();
	}

	public List<List<EClass>> getMultipaths() {
		return multipaths;
	}

	public Collection<Set<EClass>> groupMultipaths() {
		int i = 0, j = 1;
		Collection<Set<EClass>> groupedMultipaths = new LinkedList<Set<EClass>>();
		while (i < multipaths.size()) {
			List<EClass> ithPath = multipaths.get(i);
			Set<EClass> currentGroup = new HashSet<EClass>(ithPath);
			while (j < multipaths.size() && ithPath.get(0) == multipaths.get(j).get(0)
					&& ithPath.get(ithPath.size() - 1) == multipaths.get(j).get(multipaths.get(j).size() - 1)) {
				currentGroup.addAll(multipaths.get(j));
				++j;
			}
			i = j;
			++j;
			groupedMultipaths.add(currentGroup);
		}
		return groupedMultipaths;
	}
}
