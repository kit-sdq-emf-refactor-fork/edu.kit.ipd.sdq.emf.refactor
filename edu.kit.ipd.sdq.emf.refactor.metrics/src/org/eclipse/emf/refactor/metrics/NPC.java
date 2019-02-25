package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;

/**
 * Implements the number of properties of class (NPC) metric. A property is an attribute,
 * inheritance, reference, or operation.
 */
public final class NPC implements IMetricCalculator {

    private List<EObject> context;

    @Override
    public void setContext(List<EObject> context) {
        this.context = context;
    }

    @Override
    public double calculate() {
        EClass eClass = (EClass) context.get(0);
        return (double) eClass.getEAttributes().size() + eClass.getEReferences().size() + eClass.getESuperTypes().size() + eClass.getOperationCount();
    }
}
