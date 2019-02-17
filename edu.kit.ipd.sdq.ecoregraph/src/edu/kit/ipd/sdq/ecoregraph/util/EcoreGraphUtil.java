package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.Dependency;
import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;
import edu.kit.ipd.sdq.ecoregraph.Dependency.DependencyType;


public class EcoreGraphUtil {
	
	public static Collection<List<EClassifier>> findSimpleCycles(EcoreGraph eGraph) {
		DefaultDirectedGraph<EClassifier, DefaultEdge> graph = eGraph.getGraph();
		
		JohnsonSimpleCycles<EClassifier, DefaultEdge> cycleDetector = new JohnsonSimpleCycles<EClassifier, DefaultEdge>(graph);
		Collection<List<EClassifier>> cycles = cycleDetector.findSimpleCycles();
		
		return cycles;
	}
	
	public static AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph(EcoreGraph eGraph) {
		DefaultDirectedGraph<EClassifier, DefaultEdge> graph = eGraph.getGraph();
		Map<EClassifier, Set<Dependency>> dependencies = eGraph.getDependencies();

		Set<EClass> vertexSubset = graph.vertexSet().stream().filter(x -> x instanceof EClass).map(x -> (EClass) x)
				.collect(Collectors.toSet());
		Set<DefaultEdge> edgeSubset = new HashSet<DefaultEdge>();


		for (EClass c : vertexSubset) {
			Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(c);
			Set<DefaultEdge> allowedIncomingEdges = incomingEdges.stream()
					.filter(e -> dependencies.get(graph.getEdgeSource(e)).stream()
							.anyMatch(d -> d.getTarget() == c && d.getType().equals(DependencyType.E_SUPER_TYPE)))
					.collect(Collectors.toSet());
			edgeSubset.addAll(allowedIncomingEdges);

			Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(c);
			Set<DefaultEdge> allowedOutgoingEdges = outgoingEdges
					.stream().filter(
							e -> dependencies.get(c).stream()
									.anyMatch(d -> d.getTarget() == graph.getEdgeTarget(e)
											&& d.getType().equals(DependencyType.E_SUPER_TYPE)))
					.collect(Collectors.toSet());
			edgeSubset.addAll(allowedOutgoingEdges);


		}

		AsSubgraph<EClassifier, DefaultEdge> asSubgraph = new AsSubgraph<EClassifier, DefaultEdge>(graph, vertexSubset,
				edgeSubset);

		return asSubgraph;
	}
	
	public static void printGraph(Graph<EClassifier, DefaultEdge> graph) {
    	System.out.println("---");
		System.out.println("Vertices:");
    	System.out.println("---");
    	for (EClassifier v : graph.vertexSet()) {
    		System.out.println(v.getName());
    	}
    	System.out.println("---");
    	for (DefaultEdge e : graph.edgeSet()) {
    		System.out.println(graph.getEdgeSource(e).getName() + "=>" + graph.getEdgeTarget(e).getName());
    	}
    	System.out.println("---");    		
	}
	
}
