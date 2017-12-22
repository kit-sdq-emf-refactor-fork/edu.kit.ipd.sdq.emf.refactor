package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;


public final class ObligatoryContainerRelation implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		for(EClass currentClass : classes)
		{
			if(hasObligatoryContainerRelation(currentClass))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentClass);
				results.add(result);
			}
		}		
		
		return results;
	}
	
	private boolean hasObligatoryContainerRelation(EClass currentClass)
	{
		for(EReference reference : currentClass.getEReferences())
		{
			if(reference.isContainment())
			{
				if(reference.getEOpposite() != null)
				{
					if(reference.getEOpposite().getLowerBound() == 1)
					{
						return true;
					}
				}
			}
		}
		return false;
	}	
}