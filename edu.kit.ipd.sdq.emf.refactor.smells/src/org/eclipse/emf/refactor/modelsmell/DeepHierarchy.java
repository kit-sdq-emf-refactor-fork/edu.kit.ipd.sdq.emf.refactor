package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.core.Metric;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;

/**
 * Checks if a hierarchy is excessively deep
 * @author renehahn
 *
 */
public final class DeepHierarchy extends MetricBasedModelSmellFinderClass {

	private String metricId = "org.eclipse.emf.refactor.metrics.ecore.maxditec";
	private Metric localMetric = Metric.getMetricInstanceFromId(metricId);
	
	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<EObject> rootList = new LinkedList<EObject>();
		rootList.add(root);
		IMetricCalculator localCalculateClass = localMetric.getCalculateClass();
		double globalLimit = this.getLimit();
		return findSmellyObjectGroups(root, globalLimit, localCalculateClass);
	}
	
	private LinkedList<LinkedList<EObject>> findSmellyObjectGroups(EObject object, double globalLimit, 
				IMetricCalculator localCalculateClass) {
		String context = localMetric.getContext();
		LinkedList<LinkedList<EObject>> smellyEObjects = new LinkedList<LinkedList<EObject>>();		
		String objectType = object.eClass().getInstanceClass().getSimpleName();
		if(objectType.equals(context)) {
			LinkedList<EObject> rootList = new LinkedList<EObject>();
			rootList.add(object);
			localCalculateClass.setContext(rootList);
			double localValue = localCalculateClass.calculate();
			if (limitReached(localValue, globalLimit)) {
				LinkedList<EObject> currentObjects = new LinkedList<EObject>();
				currentObjects.add(object);
				smellyEObjects.add((currentObjects));
			}
		} else {
			List<EObject> containedEObjects = object.eContents();
			for(EObject containedEObject : containedEObjects) {
				smellyEObjects.addAll(findSmellyObjectGroups(containedEObject, globalLimit, localCalculateClass));
			}
		}
		return smellyEObjects;
	}
	
	private boolean limitReached(double localValue, double globalLimit) {
		return (localValue >= globalLimit);
	}	
}