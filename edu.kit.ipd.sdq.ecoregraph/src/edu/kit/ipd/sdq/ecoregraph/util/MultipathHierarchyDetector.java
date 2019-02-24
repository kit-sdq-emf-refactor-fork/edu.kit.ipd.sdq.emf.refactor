package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
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

        trimPaths(hierarchySubGraph);
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

    private void trimPaths(AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph) {
        // find paths with same start and end
        for (EClassLinkedSet path : multipaths) {

            EClass destination = path.getLast();
            EClass start = path.getFirst();

            // list holds all paths with same start and end
            List<EClassLinkedSet> similarPaths = findPathsWithSameStartAndDestination(path, destination);

            // try to trim until there is no more change 
            boolean pathChanged;
            do {
                pathChanged = false;

                ensureDestinationIsInheritanceSink(path, destination);
                ensureStartIsInheritanceSource(path, start);

                pathChanged = trimPathDestination(similarPaths, destination, hierarchySubGraph);
                if (pathChanged) {
                    // update destination
                    destination = path.getLast();
                    continue;
                }

                pathChanged = trimPathStart(similarPaths, start, hierarchySubGraph);
                if (pathChanged) {
                    // update destination
                    start = path.getFirst();
                }

            } while (pathChanged);
        }
    }

    private boolean trimPathStart(List<EClassLinkedSet> paths, EClass start, AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph) {
        // is path start linear?
        int superClassesInPath = getWidthOfFirstPathSegment(start, paths);
        if (superClassesInPath == 1) {

            // for each of the similar paths
            for (EClassLinkedSet similarPath : paths) {

                System.out.println("Trimming start of: " + similarPath);

                // trim start
                similarPath.remove(start);
            }

            return true;
        }
        return false;
    }

    private int getWidthOfFirstPathSegment(EClass start, List<EClassLinkedSet> paths) {
        int superClassesInPath;
        superClassesInPath = 0;
        EList<EClass> superClasses = start.getESuperTypes();
        for (EClass superClass : superClasses) {
            if (partOfSomePath(superClass, paths)) {
                superClassesInPath++;
            }
        }
        return superClassesInPath;
    }

    private boolean trimPathDestination(List<EClassLinkedSet> paths, EClass destination, AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph) {
        // check whether destination path is linear
        Set<DefaultEdge> incomingEdgesToDestination = hierarchySubGraph.incomingEdgesOf(destination);
        Set<EClass> subclassesOfDestination = incomingEdgesToDestination.stream().map(e -> (EClass) hierarchySubGraph.getEdgeSource(e)).collect(Collectors.toSet());
        int widthOfLastSegment = 0;

        // for each subclass of the destination
        for (EClass destinationSubClass : subclassesOfDestination) {

            // check wether it is part of a path
//            if (partOfSomePath(destinationSubClass))
            if (partOfSomePath(destinationSubClass, paths))
                widthOfLastSegment++;
        }

        // is the destination only reachable trough one inheritance?
        if (widthOfLastSegment == 1) {

            // for each of the similar paths
            for (EClassLinkedSet similarPath : paths) {

                System.out.println("Trimming destination of: " + similarPath);

                // trim destination
                similarPath.remove(destination);
            }

            return true;
        }
        return false;
    }

    private void ensureStartIsInheritanceSource(EClassLinkedSet path, EClass first) {
        for (EClass eClass : path) {
            if (eClass != first && first.isSuperTypeOf(eClass)) {
                throw new RuntimeException("This is not a multipath start! It has incoming inheritances from the path.");
            }
        }
    }

    private void ensureDestinationIsInheritanceSink(EClassLinkedSet path, EClass destination) {
        for (EClass destinationSuperClass : destination.getEAllSuperTypes()) {
            if (path.contains(destinationSuperClass)) {
                throw new RuntimeException("This is not a multipath destination! It has outgoing inheritances into the path.");
            }
        }
    }

    private boolean partOfSomePath(EClass destinationSubClass, List<EClassLinkedSet> paths) {
//        for (EClassLinkedSet otherPath : multipaths) {
        for (EClassLinkedSet otherPath : paths) {
            if (otherPath.contains(destinationSubClass)) {
                return true;
            }
        }
        return false;
    }

    private List<EClassLinkedSet> findPathsWithSameStartAndDestination(EClassLinkedSet path, EClass detination) {
        List<EClassLinkedSet> similarPaths = new ArrayList<EClassLinkedSet>();
        similarPaths.add(path);

        for (EClassLinkedSet otherPath : multipaths) {
            if (path == otherPath) {
                continue;
            }

            if (samePathStart(path, otherPath) && samePathDestination(detination, otherPath)) {
                similarPaths.add(otherPath);
            }
        }
        return similarPaths;
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
