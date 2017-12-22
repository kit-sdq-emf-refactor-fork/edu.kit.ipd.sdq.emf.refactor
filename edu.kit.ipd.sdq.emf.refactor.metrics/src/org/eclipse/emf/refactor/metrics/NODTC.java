package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;

public final class NODTC implements IMetricCalculator {
	
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		org.eclipse.emf.ecore.EClass cl = (org.eclipse.emf.ecore.EClass) context.get(0);
		double result = 0.0;
		
		result = cl.getEReferences().size();
		
		return result;
	}
}