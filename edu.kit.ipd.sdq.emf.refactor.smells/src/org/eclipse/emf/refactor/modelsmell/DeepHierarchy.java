package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.core.Metric;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;

/**
 * Checks if a hierarchy is excessively deep
 * 
 * @author renehahn
 *
 */
public final class DeepHierarchy extends MetricBasedModelSmellFinderClass {

    private String metricId = "org.eclipse.emf.refactor.metrics.ecore.maxditec";
    private Metric localMetric = Metric.getMetricInstanceFromId(metricId);

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
        LinkedList<EObject> rootList = new LinkedList<>();
        rootList.add(root);
        IMetricCalculator localCalculateClass = localMetric.getCalculateClass();
        double globalLimit = getLimit();
        LinkedList<LinkedList<EObject>> deepHierarchies = findSmellyObjectGroups(root, globalLimit, localCalculateClass);

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

    private LinkedList<LinkedList<EObject>> findSmellyObjectGroups(EObject object, double globalLimit, IMetricCalculator localCalculateClass) {
        String context = localMetric.getContext();
        LinkedList<LinkedList<EObject>> smellyEObjects = new LinkedList<>();
        String objectType = object.eClass().getInstanceClass().getSimpleName();
        if (objectType.equals(context)) {
            LinkedList<EObject> rootList = new LinkedList<>();
            rootList.add(object);
            localCalculateClass.setContext(rootList);
            double localValue = localCalculateClass.calculate();
            if (limitReached(localValue, globalLimit)) {
                LinkedList<EObject> currentObjects = new LinkedList<>();
                currentObjects.add(object);
                smellyEObjects.add(currentObjects);
            }
        } else {
            List<EObject> containedEObjects = object.eContents();
            for (EObject containedEObject : containedEObjects) {
                smellyEObjects.addAll(findSmellyObjectGroups(containedEObject, globalLimit, localCalculateClass));
            }
        }
        return smellyEObjects;
    }

    private boolean limitReached(double localValue, double globalLimit) {
        return localValue >= globalLimit;
    }
}