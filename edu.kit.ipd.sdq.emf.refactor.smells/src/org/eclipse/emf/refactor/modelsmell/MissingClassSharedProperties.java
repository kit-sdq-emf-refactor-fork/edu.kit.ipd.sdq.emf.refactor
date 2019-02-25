package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;

import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * Checks the number of equal EAttributes to other EClasses
 * 
 * @author renehahn
 */
public final class MissingClassSharedProperties extends MetricBasedModelSmellFinderClass {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {

        double threshold = getLimit();
//	    AsSubgraph<EClassifier, DefaultEdge> hierarchyGraph = EcoreGraphUtil.hierarchySubGraph(ModelSmellFinder.ecoreGraph);
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

    private int getSharedPropertiesCount(EClass eClass, EClass otherClass) {
        int sharedProperties = 0;
        sharedProperties += intersectionSize(eClass.getEAttributes(), otherClass.getEAttributes(), (EAttribute a1, EAttribute a2) -> {
            return attributesEqual(a1, a2);
        });
        //TODO
        return sharedProperties;
    }

    private boolean attributesEqual(EAttribute a1, EAttribute a2) {
        return haveEqualNames(a1, a2) && haveEqualTypes(a1, a2) && haveEqualMultiplicities(a1, a2);
    }

    private boolean haveEqualMultiplicities(EAttribute attr, EAttribute att) {
        return ((attr.getLowerBound() == att.getLowerBound()) && (attr.getUpperBound() == att.getUpperBound()));
    }

    private boolean haveEqualTypes(EAttribute attr, EAttribute att) {
        return ((att.getEType() == null) && (attr.getEType() == null)) || ((att.getEType() != null) && (attr.getEType() != null)) && (att.getEType().equals(attr.getEType()));
    }

    private boolean haveEqualNames(EAttribute attr, EAttribute att) {
        if (attr == null || att == null)
            return false;
        if (attr.getName() == null || att.getName() == null)
            return false;
        return att.getName().equals(attr.getName());
    }

    private <E> int intersectionSize(List<E> c1, List<E> c2, BiPredicate<E, E> equalser) {
        int size = 0;
        for (E e1 : c1) {
            for (E e2 : c2) {
                if (equalser.test(e1, e2)) {
                    size++;
                }
            }
        }
        return size;
    }
}
