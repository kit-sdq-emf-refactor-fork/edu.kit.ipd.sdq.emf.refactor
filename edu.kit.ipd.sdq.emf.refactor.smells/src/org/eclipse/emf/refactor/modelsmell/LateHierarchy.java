package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class checks if a hierarchy is realized too late.
 * @author renehahn
 *
 */
public final class LateHierarchy implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		
		for(EClass currentClass : classes)
		{
			if(hasClassLateHierarchy(currentClass))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentClass);
				results.add(result);
			}
		}
		
		return results;
	}
	
	private boolean hasClassLateHierarchy(EClass currentClass)
	{
		if(currentClass.isAbstract())
		{
			for(EClass superType : currentClass.getESuperTypes())
			{
				if(!superType.isAbstract())
				{
					return true;
				}
			}
		}
		return false;
	}
	
}