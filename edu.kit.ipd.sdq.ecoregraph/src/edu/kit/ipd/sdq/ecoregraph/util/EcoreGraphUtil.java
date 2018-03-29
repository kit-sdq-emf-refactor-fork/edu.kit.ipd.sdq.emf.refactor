package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;


public class EcoreGraphUtil {
	
	public static Collection<List<EClassifier>> findSimpleCycles(EcoreGraph eGraph) {
		DefaultDirectedGraph<EClassifier, DefaultEdge> graph = eGraph.getGraph();
		
		JohnsonSimpleCycles<EClassifier, DefaultEdge> cycleDetector = new JohnsonSimpleCycles<EClassifier, DefaultEdge>(graph);
		Collection<List<EClassifier>> cycles = cycleDetector.findSimpleCycles();
		
		return cycles;
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
