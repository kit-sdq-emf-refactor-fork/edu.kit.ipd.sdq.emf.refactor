package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

public class ContainerRelation implements IModelSmellFinder {

    protected ContainerRelationType wantedType = ContainerRelationType.normal;

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
        LinkedList<LinkedList<EObject>> results = new LinkedList<>();
        List<EClass> classes = DetectionHelper.getAllEClasses(root);
        for (EClass currentClass : classes) {
            findRedundantContainerRelations(currentClass, results);
        }

        return results;
    }

    private void findRedundantContainerRelations(EClass currentClass, LinkedList<LinkedList<EObject>> results) {
        for (EReference reference : currentClass.getEReferences()) {
            if (reference.isContainer()) {

                boolean isSmellOccurrence = wantedType == ContainerRelationType.normal && reference.getLowerBound() == 0
                        || wantedType == ContainerRelationType.obligatory && reference.getLowerBound() == 1;
                if (isSmellOccurrence) {
                    LinkedList<EObject> result = new LinkedList<>();
                    result.add(currentClass);
                    result.add(reference.getEType());
                    results.add(result);
                }
            }
        }
    }

    protected static enum ContainerRelationType {
        normal, obligatory;
    }
}
