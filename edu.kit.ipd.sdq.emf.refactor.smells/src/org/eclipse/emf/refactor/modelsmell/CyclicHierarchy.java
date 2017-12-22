package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class checks if there is a cylce in the hierarchy
 * @author renehahn
 *
 */
public final class CyclicHierarchy implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		
		for(EClass currentClass : classes)
		{
			if(hasClassACyclicHierarchy(currentClass))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentClass);
				results.add(result);
			}
		}
		
		return results;
	}
	
	private boolean hasClassACyclicHierarchy(EClass currentClass)
	{
		for(EReference reference : currentClass.getEReferences())
		{
			EClass referencedClass = reference.getEReferenceType();
			for(EClass superType : referencedClass.getEAllSuperTypes())
			{
				if(superType.equals(currentClass))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}