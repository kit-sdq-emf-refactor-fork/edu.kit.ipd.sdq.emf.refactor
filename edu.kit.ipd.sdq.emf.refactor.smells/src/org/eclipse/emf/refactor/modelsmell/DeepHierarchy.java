package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.util.EClassLinkedSet;
import edu.kit.ipd.sdq.ecoregraph.util.EcoreGraphUtil;
import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * Checks if a hierarchy is excessively deep
 * 
 * @author renehahn
 *
 */
public final class DeepHierarchy extends MetricBasedModelSmellFinderClass {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {

        AsSubgraph<EClassifier, DefaultEdge> hierarchyGraph = EcoreGraphUtil.hierarchySubGraph(ModelSmellFinder.ecoreGraph);
        List<EClass> eClasses = DetectionHelper.getAllEClasses(root);
        double globalLimit = getLimit();

        LinkedList<LinkedList<EObject>> deepHierarchies = new LinkedList<LinkedList<EObject>>();

        // iterate all classes in metamodel
        for (EClass eClass : eClasses) {

            // iterate all superclasses
            for (EClass superClass : eClass.getEAllSuperTypes()) {

                // iterate all paths to superclasses
                List<EClassLinkedSet> paths = EcoreGraphUtil.findAllPaths(eClass, superClass, hierarchyGraph);
                for (EClassLinkedSet path : paths) {

                    // if path exceeds limit
                    if (path.size() >= globalLimit) {

                        // add to results
                        deepHierarchies.add(new LinkedList<EObject>(path));
                    }
                }
            }
        }

        removeSubsets(deepHierarchies);

        return deepHierarchies;
    }

    private void removeSubsets(LinkedList<LinkedList<EObject>> paths) {
        for (int i = 0; i < paths.size();) {
            LinkedList<EObject> deepHierarchy = paths.get(i);
            boolean iDeleted = false;

            for (int j = i + 1; j < paths.size();) {
                LinkedList<EObject> otherDeepHierarchy = paths.get(j);

                if (deepHierarchy.containsAll(otherDeepHierarchy)) {
                    paths.remove(j);
                } else if (otherDeepHierarchy.containsAll(deepHierarchy)) {
                    paths.remove(i);
                    iDeleted = true;
                    break;
                } else {
                    j++;
                }
            }

            if (!iDeleted) {
                i++;
            }
        }
    }
}