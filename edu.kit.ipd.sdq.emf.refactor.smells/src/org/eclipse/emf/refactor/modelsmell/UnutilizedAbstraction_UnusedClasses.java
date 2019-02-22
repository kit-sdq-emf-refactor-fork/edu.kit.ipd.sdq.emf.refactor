package org.eclipse.emf.refactor.modelsmell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class checks if a EClass is unused
 * 
 * @author renehahn
 *
 */
public final class UnutilizedAbstraction_UnusedClasses implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<>();

		List<EClass> classes = DetectionHelper.getAllEClasses(root);
		for (EClass currentClass : classes) {
			if (classes.stream().noneMatch(c -> c.getEReferences().stream()
					.anyMatch(r -> r.isContainment() && (r.getEReferenceType().equals(currentClass)
							|| currentClass.getEAllSuperTypes().stream()
									.anyMatch(sup -> r.getEReferenceType().equals(sup))
							|| getAllSubTypes(currentClass, classes).stream()
									.anyMatch(sub -> r.getEReferenceType().equals(sub) || sub.getEAllSuperTypes()
											.stream().anyMatch(sup -> r.getEReferenceType().equals(sup))))))) {
				LinkedList<EObject> result = new LinkedList<>();
				result.add(currentClass);
				results.add(result);
			}

		}

		return results;
	}

	List<EClass> getAllSubTypes(EClass superClass, List<EClass> classes) {
		List<EClass> result = new ArrayList<>();
		for (EClass c : classes) {
			if (c.getEAllSuperTypes().contains(superClass)) {
				result.add(c);
			}
		}
		return result;
	}
}