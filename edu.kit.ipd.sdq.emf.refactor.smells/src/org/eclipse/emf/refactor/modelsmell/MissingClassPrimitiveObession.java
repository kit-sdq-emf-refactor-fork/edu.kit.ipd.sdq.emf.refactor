package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.core.Metric;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;

/**
 * Checks if a EClass contains a lot of EAttributes of primitive types.
 * @author renehahn
 *
 */
public final class MissingClassPrimitiveObession extends MetricBasedModelSmellFinderClass {

	private String metricId = "org.eclipse.emf.refactor.metrics.naptc";
	private Metric localMetric = Metric.getMetricInstanceFromId(metricId);
	
	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<EObject> rootList = new LinkedList<>();
		rootList.add(root);
		IMetricCalculator localCalculateClass = localMetric.getCalculateClass();
		double globalLimit = getLimit();
		return findSmellyObjectGroups(root, globalLimit, localCalculateClass);
	}
	
	private LinkedList<LinkedList<EObject>> findSmellyObjectGroups(EObject object, double globalLimit, 
				IMetricCalculator localCalculateClass) {
		String context = localMetric.getContext();
		LinkedList<LinkedList<EObject>> smellyEObjects = new LinkedList<>();		
		String objectType = object.eClass().getInstanceClass().getSimpleName();
		if(objectType.equals(context)) {
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
			for(EObject containedEObject : containedEObjects) {
				smellyEObjects.addAll(findSmellyObjectGroups(containedEObject, globalLimit, localCalculateClass));
			}
		}
		return smellyEObjects;
	}
	
	private boolean limitReached(double localValue, double globalLimit) {
		return localValue >= globalLimit;
	}	
}