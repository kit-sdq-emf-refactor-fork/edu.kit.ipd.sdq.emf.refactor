package org.eclipse.emf.refactor.modelsmell;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This clas investigates if a subclass defines a specialization relation to a already refered type.
 * 
 * @author renehahn
 *
 */
public final class SpecializedRelation implements IModelSmellFinder {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
        LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
        List<EClass> classes = DetectionHelper.getAllEClasses(root);
        for (EClass currentClass : classes) {
            findClassSpecializationAggregation(currentClass, results);
        }
        return results;
    }

    private void findClassSpecializationAggregation(EClass currentClass, LinkedList<LinkedList<EObject>> results) {

        EList<EReference> localReferences = currentClass.getEReferences();

        // iterate all inherited references
        Set<EReference> inheritedReferences = getInheritedReferences(currentClass);
        for (EReference inheritedReference : inheritedReferences) {

            // iterate all local references
            for (EReference localReference : localReferences) {
                if (inheritedReference != localReference) {

                    // is local reference target a subclass of the inherited class? 
                    EClass inheritedReferenceTarget = inheritedReference.getEReferenceType();
                    EClass localReferenceTarget = localReference.getEReferenceType();
                    if (inheritedReferenceTarget.isSuperTypeOf(localReferenceTarget) && !inheritedReferenceTarget.equals(localReferenceTarget)) {

                        // occurrence found
                        LinkedList<EObject> result = new LinkedList<EObject>();
                        result.add(currentClass);
                        result.add(localReferenceTarget);
                        results.add(result);
                    }
                }
            }
        }
    }

    private Set<EReference> getInheritedReferences(EClass currentClass) {
        Collection<EReference> ownReferences = currentClass.getEReferences();
        HashSet<EReference> inheritedReferences = new HashSet<>();
        for (EReference reference : currentClass.getEAllReferences()) {
            if (!ownReferences.contains(reference)) {
                inheritedReferences.add(reference);
            }
        }
        return inheritedReferences;
    }
}
