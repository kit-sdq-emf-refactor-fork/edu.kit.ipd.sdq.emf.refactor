package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;

import edu.kit.ipd.sdq.ecoregraph.util.EcoreHelper;
import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * Checks the number of equal EAttributes to other EClasses
 */
public final class MissingClassSharedProperties extends MetricBasedModelSmellFinderClass {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {

        double threshold = getLimit();
        LinkedList<LinkedList<EObject>> results = new LinkedList<>();

        // iterate all classes
        List<EClass> classes = DetectionHelper.getAllEClasses(root);
        for (int i = 0; i < classes.size(); i++) {
            EClass eClass = classes.get(i);

            // iterate all other classes
            for (int j = i + 1; j < classes.size(); j++) {
                EClass otherClass = classes.get(j);

                if (getSharedPropertiesCount(eClass, otherClass) >= threshold) {
                    LinkedList<EObject> result = new LinkedList<>();
                    result.add(eClass);
                    result.add(otherClass);
                    results.add(result);
                }
            }
        }
        return results;
    }

    private int getSharedPropertiesCount(EClass c1, EClass c2) {
        int sharedProperties = 0;
        sharedProperties += intersectionSize(c1.getEAttributes(), c2.getEAttributes(), this::typedElememEquals);
        sharedProperties += intersectionSize(c1.getEReferences(), c2.getEReferences(), this::typedElememEquals);
        sharedProperties += intersectionSize(c1.getESuperTypes(), c2.getESuperTypes(), EcoreHelper::eClassEquals);
        sharedProperties += intersectionSize(c1.getEOperations(), c2.getEOperations(), this::typedElememEquals);
        return sharedProperties;
    }

    private boolean typedElememEquals(ETypedElement a1, ETypedElement a2) {
        return haveEqualNames(a1, a2) && haveEqualTypes(a1, a2) && haveEqualMultiplicities(a1, a2);
    }

    private boolean haveEqualMultiplicities(ETypedElement e1, ETypedElement e2) {
        return ((e1.getLowerBound() == e2.getLowerBound()) && (e1.getUpperBound() == e2.getUpperBound()));
    }

    private boolean haveEqualTypes(ETypedElement e1, ETypedElement e2) {
        return ((e2.getEType() == null) && (e1.getEType() == null)) || ((e2.getEType() != null) && (e1.getEType() != null)) && (e2.getEType().equals(e1.getEType()));
    }

    private boolean haveEqualNames(ENamedElement e1, ENamedElement e2) {
        if (e1 == null || e2 == null)
            return false;
        if (e1.getName() == null || e2.getName() == null)
            return false;
        return e2.getName().equals(e1.getName());
    }

    private <E> int intersectionSize(List<E> l1, List<E> l2, BiPredicate<E, E> equalser) {
        int size = 0;
        for (E e1 : l1) {
            for (E e2 : l2) {
                if (equalser.test(e1, e2)) {
                    size++;
                }
            }
        }
        return size;
    }
}
