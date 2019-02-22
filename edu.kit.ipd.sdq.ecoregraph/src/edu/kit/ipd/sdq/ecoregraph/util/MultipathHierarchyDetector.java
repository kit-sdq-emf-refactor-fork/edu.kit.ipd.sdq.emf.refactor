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
    private List<EClassSet> multipaths;
    private EcoreGraph eGraph;

    public MultipathHierarchyDetector(EcoreGraph eGraph) {
        this.multipaths = new LinkedList<EClassSet>();
        this.eGraph = eGraph;
    }

    public void findMultipathHierarchies() {
        AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph = EcoreGraphUtil.hierarchySubGraph(eGraph);
        Set<EClass> startVertices = hierarchySubGraph.vertexSet().stream().filter(c -> hierarchySubGraph.outDegreeOf(c) >= 2).map(c -> (EClass) c).collect(Collectors.toSet());

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

    private void findAllPaths(EClass startVertex, EClass destination, AsSubgraph<EClassifier, DefaultEdge> hierarchySubGraph, Stack<EClass> path) {
        path.push(startVertex);
        if (startVertex == destination) { // path found
            this.multipaths.add(new EClassSet(path));
        } else {
            Set<DefaultEdge> outgoingEdges = hierarchySubGraph.outgoingEdgesOf(startVertex);
            Set<EClass> neighbors = outgoingEdges.stream().map(e -> (EClass) hierarchySubGraph.getEdgeTarget(e)).collect(Collectors.toSet());
            for (EClass neighbor : neighbors) {
                findAllPaths(neighbor, destination, hierarchySubGraph, path);
            }
        }
        path.pop();
    }

    public List<EClassSet> getMultipaths() {
        return multipaths;
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

    public void groupMultipaths() {
        boolean resultChanged;
        do {
            resultChanged = false;

            // iterate all multipaths
            int i = 0;
            while (i < multipaths.size()) {
                EClassSet ithPath = multipaths.get(i);

                // iterate all other multipaths
                int j = i + 1;
                while (j < multipaths.size()) {

                    EClassSet jthPath = multipaths.get(j);

                    // do paths share same start and end?
//                    if (samePathStart(ithPath, jthPath) && samePathEnd(ithPath, jthPath)) {

//                        System.out.println("consolidating (same start/end)" + print(ithPath, jthPath));

                    // might not be meaningful
                    // consolidate
//                        currentGroup.addAll(jthPath);
//                        multipaths.remove(j);
//                        resultChanged = true;
//                    } else if (isSuperSet(ithPath, jthPath)) {

                    if (isSuperSet(ithPath, jthPath)) {

//                        System.out.println("consolidating (is superpath)" + print(ithPath, jthPath));
                        // the other path is completely contained in the current path
                        multipaths.remove(j);
                        resultChanged = true;
                    } else if (isSuperSet(jthPath, ithPath)) {

//                        System.out.println("consolidating (is subpath)" + print(ithPath, jthPath));
                        // the other path completely contains this path
                        ithPath.addAll(jthPath);
                        multipaths.remove(j);
                        resultChanged = true;
                    } else {
//                        System.out.println("not consolidating " + print(ithPath, jthPath));
                        j++;
                    }
                }

                i++;
            }
        } while (resultChanged);
    }

    private boolean isSuperSet(EClassSet ithPath, EClassSet jthPath) {
        return ithPath.containsAll(jthPath);

//        for (EClass classFromSubSet : jthPath) {
//            boolean inSuperSet = classContained(ithPath, classFromSubSet);
//            if (!inSuperSet) {
//                // class from subset is not in superset
//                // => no superset relation! 
//                return false;
//            }
//        }
//
//        // all classes of the subset are in the superset
//        return true;
    }

    private boolean classContained(List<EClass> superSet, EClass classFromSubSet) {
        for (EClass classFromSuperSet : superSet) {
            if (EcoreHelper.equals(classFromSuperSet, classFromSubSet)) {
                return true;
            }
        }
        return false;
    }

    private String print(List<EClass> ithPath, List<EClass> jthPath) {
        String classNameList1 = ithPath.stream().map(c -> c.getName()).collect(Collectors.joining(","));
        String classNameList2 = jthPath.stream().map(c -> c.getName()).collect(Collectors.joining(","));
        return classNameList1 + " <-> " + classNameList2;
    }

    private boolean samePathStart(EClassSet ithPath, EClassSet jthPath) {
        EClass startClass1 = ithPath.iterator().next();
        EClass startClass2 = jthPath.iterator().next();
        return EcoreHelper.equals(startClass1, startClass2);
    }

    private boolean samePathEnd(EClassSet ithPath, EClassSet jthPath) {
        EClass endClass1 = ithPath.getLast();
        EClass endClass2 = jthPath.getLast();
        return EcoreHelper.equals(endClass1, endClass2);
    }
}
