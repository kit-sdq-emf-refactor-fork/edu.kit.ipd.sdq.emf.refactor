package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class checks if a EEnum is unused
 * @author renehahn
 *
 */
public final class UnutilizedAbstraction_UnusedEnumeration implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EEnum> enums = DetectionHelper.getAllEEnums(root);
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		for(EEnum currentEnum : enums)
		{
			if(!isUsedAsType(currentEnum, classes))
			{
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(currentEnum);
				results.add(result);
			}
		}		
		
		return results;
	}
	
	private boolean isUsedAsType(EEnum currentEnum, List<EClass> allClasses)
	{
		for(EClass clazz : allClasses)
		{
			/*for(EReference reference : clazz.getEAllReferences())
			{
				if(reference.getEType().equals(currentEnum))
				{
					return true;
				}				
			}*/
			for(EAttribute attribute : clazz.getEAllAttributes())
			{
				if(attribute.getEAttributeType().equals(currentEnum))
				{
					return true;
				}
			}
		}
		return false;
	}
}