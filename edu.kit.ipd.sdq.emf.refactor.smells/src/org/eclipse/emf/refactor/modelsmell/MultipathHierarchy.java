package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class detects if the same EClass inherits from the same EClass over different pathes.
 * @author renehahn
 *
 */
public final class MultipathHierarchy implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		for (EClass cl : classes) 
		{
			// investigate only if more than one supertype is available
			if (cl.getESuperTypes().size() > 1) 
			{
				for (int i = 0; i < cl.getESuperTypes().size(); i++) 
				{
					EClass cl1 = cl.getESuperTypes().get(i);
					// int j = (i+j) is required to not investigate the same supertype again.
					// Otherwise the result contains the same smell twice.
					for (int j = (i + 1); j < cl.getESuperTypes().size(); j++) 
					{
						EClass cl2 = cl.getESuperTypes().get(j);
						
						List<EClass> superclasses1 = DetectionHelper.getAllESuperclasses(cl1);
						// add supertype itself also to the list, as the hierarchy must not have the form of a diamond
						superclasses1.add(cl1);
						List<EClass> superclasses2 = DetectionHelper.getAllESuperclasses(cl2);
						superclasses2.add(cl2);
						for (EClass superclass : superclasses1) 
						{
							if (superclasses2.contains(superclass)) 
							{
								LinkedList<EObject> result = new LinkedList<EObject>();
								// add class itself, the starting classes of the different pathes and the endpoint to the result
								result.add(cl);
								if(!result.contains(cl1))
								{
									result.add(cl1);
								}
								if(!result.contains(cl2))
								{
									result.add(cl2);
								}						
								if(!result.contains(superclass))
								{
									result.add(superclass);
								}								
								results.add(result);
							}
						}
					}
				}
			}
		}
		return results;
	}
}