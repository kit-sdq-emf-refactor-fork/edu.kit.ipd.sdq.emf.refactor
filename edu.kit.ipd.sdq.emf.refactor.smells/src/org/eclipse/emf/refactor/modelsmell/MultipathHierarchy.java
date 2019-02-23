package org.eclipse.emf.refactor.modelsmell;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;

import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;
import edu.kit.ipd.sdq.ecoregraph.util.EClassLinkedSet;
import edu.kit.ipd.sdq.ecoregraph.util.MultipathHierarchyDetector;

/**
 * This class detects if the same EClass inherits from the same EClass over different paths.
 * 
 * @author renehahn
 * @author Amine Kechaou
 *
 */
public final class MultipathHierarchy implements IModelSmellFinder {

    @Override
    public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
        LinkedList<LinkedList<EObject>> results = new LinkedList<>();

        //EPackage ePackage = (EPackage) root;
        //EcoreGraph eGraph = EcoreGraphRegistry.INSTANCE.getEcoreGraph(ePackage);

        EcoreGraph graph = ModelSmellFinder.ecoreGraph;
        MultipathHierarchyDetector detector = new MultipathHierarchyDetector(graph);

        detector.findMultipathHierarchies();
        Collection<EClassLinkedSet> multipaths = detector.getMultipaths();
        for (EClassLinkedSet path : multipaths) {
            results.add(new LinkedList<EObject>(path));
        }

        return results;
    }
}