package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;

/**
 * The MultipathHierarchyDetector class implements the logic of detecting multipath hierarchies,
 * i.e. when a class inherits (indirectly) from another class over two or more paths
 * 
 * @author Amine Kechaou
 *
 */
public class MultipathHierarchyDetector {
    private List<EClassLinkedSet> multipaths;
    private EcoreGraph eGraph;

    public MultipathHierarchyDetector(EcoreGraph eGraph) {
        this.multipaths = new LinkedList<EClassLinkedSet>();
        this.eGraph = eGraph;
    }

    public void findMultipathHierarchies() {
        AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph = EcoreGraphUtil.hierarchySubGraph(eGraph);

        // begin search at classes that have more than 1 direct superclass
        Set<EClass> startVertices = hierarchySubGraph.vertexSet().stream().filter(c -> hierarchySubGraph.outDegreeOf(c) >= 2).map(c -> (EClass) c).collect(Collectors.toSet());
        for (EClass startVertex : startVertices) {
            List<EClass> superTypes = startVertex.getESuperTypes();
            Set<EClass> destinations = new HashSet<EClass>();

            // iterate superclasses
            for (EClass superType : superTypes) {
                List<EClass> otherSuperTypes = new ArrayList<EClass>();
                otherSuperTypes.addAll(superTypes);
                otherSuperTypes.remove(superType);

                // superclasses of this superclass are potential candidates
                Set<EClass> potentialCandidates = new HashSet<EClass>();
                potentialCandidates.addAll(superType.getEAllSuperTypes());
                potentialCandidates.add(superType);

                // iterate other superclasses
                for (EClass otherSuperType : otherSuperTypes) {
                    List<EClass> allOtherSuperSuperTypes = otherSuperType.getEAllSuperTypes();

                    // iterate candidates
                    for (EClass potentialCandidate : potentialCandidates) {

                        // candidate is multipath destination if it is also a superclass of another superclass
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

        removeLinearPathEnds();
        groupMultipaths();
    }

    private void findAllPaths(EClass startVertex, EClass destination, AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph, Stack<EClass> path) {
        path.push(startVertex);
        if (startVertex == destination) { // path found
            this.multipaths.add(new EClassLinkedSet(path));
        } else {
            Set<DefaultEdge> outgoingEdges = hierarchySubGraph.outgoingEdgesOf(startVertex);
            Set<EClass> neighbors = outgoingEdges.stream().map(e -> (EClass) hierarchySubGraph.getEdgeTarget(e)).collect(Collectors.toSet());
            for (EClass neighbor : neighbors) {
                findAllPaths(neighbor, destination, hierarchySubGraph, path);
            }
        }
        path.pop();
    }

    private void removeLinearPathEnds() {
        // find paths with same start and end
        for (EClassLinkedSet path : multipaths) {

            EClass detination = path.getLast();

            // list holds all paths with same start and end
            List<EClassLinkedSet> similarPaths = new ArrayList<EClassLinkedSet>();

            // iterate all other paths
            for (EClassLinkedSet otherPath : multipaths) {
                if (path == otherPath) {
                    continue;
                }

                if (samePathStart(path, otherPath) && samePathDestination(detination, otherPath)) {
                    similarPaths.add(otherPath);
                }
            }

            // check whether destination path is linear
            //TODO
        }
    }

//    //Former version (from Amine?)
//    public Collection<Set<EClass>> groupMultipaths() {
//        int i = 0, j = 1;
//        Collection<Set<EClass>> groupedMultipaths = new LinkedList<Set<EClass>>();
//        while (i < multipaths.size()) {
//            List<EClass> ithPath = multipaths.get(i);
//            Set<EClass> currentGroup = new HashSet<EClass>(ithPath);
//            while (j < multipaths.size() && samePathStart(j, ithPath) && samePathEnd(j, ithPath)) {
//                currentGroup.addAll(multipaths.get(j));
//                ++j;
//            }
//            i = j;
//            ++j;
//            groupedMultipaths.add(currentGroup);
//        }
//        return groupedMultipaths;
//    }

    private void groupMultipaths() {
        boolean resultChanged;
        do {
            resultChanged = false;

            // iterate all multipaths
            int i = 0;
            while (i < multipaths.size()) {
                EClassLinkedSet ithPath = multipaths.get(i);
                EClass ithDestination = ithPath.getLast();

                // iterate all other multipaths
                int j = i + 1;
                while (j < multipaths.size()) {

                    EClassLinkedSet jthPath = multipaths.get(j);

                    // do paths share same start and end?
                    if (samePathStart(ithPath, jthPath) && samePathDestination(ithDestination, jthPath)) {

                        // consolidate
                        ithPath.inject(jthPath);
                        multipaths.remove(j);
                        resultChanged = true;
                    } else if (ithPath.containsAll(jthPath)) {

                        // the other path is completely contained in the current path
                        multipaths.remove(j);
                        resultChanged = true;
                    } else if (jthPath.containsAll(ithPath)) {

                        // the other path completely contains this path
                        ithPath.clear();
                        ithPath.addAll(jthPath);
                        multipaths.remove(j);
                        resultChanged = true;
                    } else {
                        j++;
                    }
                }

                i++;
            }
        } while (resultChanged);
    }

    private boolean samePathStart(EClassLinkedSet path, EClassLinkedSet otherPath) {
        return EcoreHelper.equals(path.getFirst(), otherPath.getFirst());
    }

    private boolean samePathDestination(EClass ithDestination, EClassLinkedSet otherPath) {
        return EcoreHelper.equals(ithDestination, otherPath.getLast());
    }

    public List<EClassLinkedSet> getMultipaths() {
        return multipaths;
    }
}
