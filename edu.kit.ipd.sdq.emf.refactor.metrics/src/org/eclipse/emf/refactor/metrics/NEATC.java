package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;

public final class NEATC implements IMetricCalculator {
	
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		EClass cl = (EClass) context.get(0);
		double result = 0.0;
		
		EList<EClass> otherClasses = getOtherClassses(cl);
		if (otherClasses.isEmpty()) return result;
		Double[] maxEqualAttributes = new Double[otherClasses.size()];
		for (int i=0; i < maxEqualAttributes.length; i++) maxEqualAttributes[i] = 0.0;
		for (EAttribute attr : cl.getEAttributes()) {
			for (int i=0; i < otherClasses.size(); i++) {
				if (containsEqualAttribute(otherClasses.get(i), attr)) {
					maxEqualAttributes[i]++;
				}
			}
		}	
		result = max(maxEqualAttributes);
		
		return result;
	}
	
	private double max(Double[] doubleArray) {
		Double result = 0.0;
		for (int i=0; i < doubleArray.length; i++) {
			if (doubleArray[i] > result) {
				result = doubleArray[i];
			}
		}
		return result;
	}

	private boolean containsEqualAttribute(EClass c, EAttribute attr) {
		boolean contains = false;
		for (EAttribute att : c.getEAttributes()) {
			if (haveEqualNames(attr, att) 
				&& haveEqualTypes(attr, att)
				&& haveEqualMultiplicities(attr, att)) {
					contains = true;
					break;
				}
		}
		return contains;
	}

	private boolean haveEqualMultiplicities(EAttribute attr, EAttribute att) {
		return ((attr.getLowerBound() == att.getLowerBound()) && (attr.getUpperBound() == att.getUpperBound()));
	}

	private boolean haveEqualTypes(EAttribute attr, EAttribute att) {
		return ((att.getEType() == null) && (attr.getEType() == null)) || 
				((att.getEType() != null) && (attr.getEType() != null)) && (att.getEType().equals(attr.getEType()));
	}

	private boolean haveEqualNames(EAttribute attr, EAttribute att) {
		if (attr == null || att == null) return false;
		if (attr.getName() == null || att.getName() == null) return false;
		return att.getName().equals(attr.getName());
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