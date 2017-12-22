package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;

public final class NIDTC implements IMetricCalculator {
	
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		EClass cl = (EClass) context.get(0);
		double result = 0.0;
		
		EList<org.eclipse.emf.ecore.EClass> otherClasses = getOtherClassses(cl);
		for(EClass otherClass : otherClasses)
		{
			for(EReference reference : otherClass.getEReferences())
			{
				if(reference.getEType().equals(cl))
				{
					result++;
				}
			}
		}
		
		return result;
	}

	private EList<EClass> getOtherClassses(EClass cl) {
		EList<EClass> otherClasses = new BasicEList<EClass>();
		TreeIterator<EObject> iter = getRoot(context.get(0)).eAllContents();
		while (iter.hasNext()) {
			EObject eObject = iter.next();
			if (eObject instanceof EClass) {
				EClass otherClass = (EClass) eObject;
				if (!otherClass.equals(cl)) {
					otherClasses.add(otherClass);
				}
			}
		}
		return otherClasses;
	}

	private EObject getRoot(EObject eObject) {
		if (eObject.eContainer() == null) return eObject;
		else return getRoot(eObject.eContainer());
	}
}