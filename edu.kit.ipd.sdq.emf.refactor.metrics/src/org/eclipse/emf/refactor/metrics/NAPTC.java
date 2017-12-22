package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;

public final class NAPTC implements IMetricCalculator {
		
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		EClass in = (EClass) context.get(0);
		double ret = 0.0;
		
		for(EAttribute attribute : in.getEAttributes())
		{
			if(attribute.getEAttributeType() != null)
			{
				if(isPrimitiveType(attribute.getEAttributeType()))
				{
					ret++;
				}
			}
		}
		
		return ret;
	}
	
	private boolean isPrimitiveType(EDataType dataType)
	{
		if(dataType.getInstanceClass() != null)
		{
			if(dataType.getInstanceClass().isPrimitive()) { return true; }
		}
		
		return false;
	}
}