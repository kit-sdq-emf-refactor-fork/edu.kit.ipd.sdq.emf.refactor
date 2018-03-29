package org.eclipse.emf.refactor.modelsmell;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;
import edu.kit.ipd.sdq.ecoregraph.EcoreGraphRegistry;
import edu.kit.ipd.sdq.ecoregraph.util.MultipathHierarchyDetector;

/**
 * This class detects if the same EClass inherits from the same EClass over different pathes.
 * @author renehahn
 *
 */
public final class MultipathHierarchy implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		
		EPackage ePackage = (EPackage) root;
		EcoreGraph eGraph = EcoreGraphRegistry.INSTANCE.getEcoreGraph(ePackage);
		
		MultipathHierarchyDetector detector = new MultipathHierarchyDetector(eGraph);
		
		Collection<Set<EClass>> multipaths = detector.groupMultipaths();
		for (Set<EClass> path : multipaths) {
			results.add(new LinkedList<EObject>(path));
		}
		
//		List<List<EClass>> multipaths = detector.getMultipaths();
//		for (List<EClass> path : multipaths) {
//			results.add(new LinkedList<EObject>(path));
//		}
		
		return results;
	}
}