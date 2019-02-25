package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.util.EcoreGraphUtil;
import edu.kit.ipd.sdq.emf.refactor.smells.util.DetectionHelper;

/**
 * This class checks if a hierarchy is realized too late.
 * 
 * @author renehahn
 *
 */
public final class ConcreteAbstractClass implements IModelSmellFinder {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {

        AsSubgraph<EClassifier, DefaultEdge> hierarchyGraph = EcoreGraphUtil.hierarchySubGraph(ModelSmellFinder.ecoreGraph);

        LinkedList<LinkedList<EObject>> results = new LinkedList<>();

        // iterate all classes
        List<EClass> classes = DetectionHelper.getAllEClasses(root);
        for (EClass eClass : classes) {

            // is class concrete?
            if (!eClass.isAbstract()) {

                // class has abstract subclasses?
                Set<EClass> abstractSubclasses = EcoreGraphUtil.getAbstractSubclasses(eClass, hierarchyGraph);
                if (abstractSubclasses.size() > 0) {
                    LinkedList<EObject> result = new LinkedList<>();
                    result.add(eClass);
                    result.addAll(abstractSubclasses);
                    results.add(result);
                }
            }
        }

        return results;
    }
}