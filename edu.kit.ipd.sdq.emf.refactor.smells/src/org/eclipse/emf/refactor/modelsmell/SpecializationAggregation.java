package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This clas investigates if a subclass defines a specialization relation to a already refered type.
 * @author renehahn
 *
 */
public final class SpecializationAggregation implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		for(EClass currentClass : classes)
		{
			if(hasClassSpecializationAggregation(currentClass))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentClass);
				results.add(result);
			}
		}		
		
		return results;
	}	
	
	/*private boolean hasClassSpecializationAggregation(EClass currentClass)
	{
		for(EReference reference : currentClass.getEAllReferences())
		{
			for(EReference localReference: currentClass.getEReferences())
			{
				if(reference != localReference)
				{
					EClass referenceClass = reference.getEReferenceType();
					EClass localReferenceClass = localReference.getEReferenceType();
					
					if(referenceClass.equals(localReferenceClass))
					{
						return true;
					}
					
					if(referenceClass.isSuperTypeOf(localReferenceClass))
					{
						return true;
					}
				}
			}
		}
		return false;
	}*/
	
	private boolean hasClassSpecializationAggregation(EClass currentClass)
	{
		LinkedList<EReference> inheritedReferences = new LinkedList<EReference>();
		
		for(EReference reference : currentClass.getEAllReferences())
		{
			if(!currentClass.getEReferences().contains(reference))
			{
				inheritedReferences.add(reference);
			}
		}
		
		
		for(EReference reference : inheritedReferences)
		{
			for(EReference localReference: currentClass.getEReferences())
			{
				if(reference != localReference)
				{
					EClass referenceClass = reference.getEReferenceType();
					EClass localReferenceClass = localReference.getEReferenceType();
					
					if(referenceClass.equals(localReferenceClass))
					{
						return true;
					}
					
					if(referenceClass.isSuperTypeOf(localReferenceClass))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}