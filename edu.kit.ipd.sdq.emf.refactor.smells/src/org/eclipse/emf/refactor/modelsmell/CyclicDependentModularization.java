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
 * This class checks if there is a cycle in the hierarchy
 * @author renehahn
 *
 */
public final class CyclicDependentModularization implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		
		for(EClass currentClass : classes)
		{
			if(hasClassACyclicModularization(currentClass))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentClass);
				results.add(result);
			}
		}
		
		return results;
	}
	
	private boolean hasClassACyclicModularization(EClass currentClass)
	{
		LinkedList<EClass> visitedClasses = new LinkedList<EClass>();
		
		for(EClass referencedClass : getAllReferencesClasses(currentClass, visitedClasses))
		{
			if(referencedClass.equals(currentClass))
			{
				return true;
			}
		}
				
		return false;
	}
	
	private LinkedList<EClass> getAllReferencesClasses(EClass currentClass, LinkedList<EClass> visitedClasses)
	{
		LinkedList<EClass> referencedTypes = new LinkedList<EClass>();
		
		for(EClass referencedClass : getReferencedClasses(currentClass))
		{
			if(!visitedClasses.contains(referencedClass))
			{
				visitedClasses.add(referencedClass);
				referencedTypes.add(referencedClass);
				referencedTypes.addAll(getAllReferencesClasses(referencedClass, visitedClasses));
			}
		}
		
		return referencedTypes;
	}
	
	private LinkedList<EClass> getReferencedClasses(EClass currentClass)
	{
		LinkedList<EClass> referencedClasses = new LinkedList<EClass>();
		
		for(EReference reference : currentClass.getEAllReferences())
		{
			EClass referenceClass = reference.getEReferenceType();
			if(!referencedClasses.contains(referenceClass))
			{
				referencedClasses.add(referenceClass);
			}
		}
		
		return referencedClasses;
	}
}