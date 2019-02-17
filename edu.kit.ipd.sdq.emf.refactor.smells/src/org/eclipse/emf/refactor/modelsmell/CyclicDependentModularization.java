package org.eclipse.emf.refactor.modelsmell;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;

import edu.kit.ipd.sdq.ecoregraph.EcoreGraph;
import edu.kit.ipd.sdq.ecoregraph.EcoreGraphRegistry;
import edu.kit.ipd.sdq.ecoregraph.util.EcoreGraphUtil;

/**
 * This class checks if there is a cycle in the hierarchy
 * @author renehahn
 * @author Amine Kechaou
 *
 */
public final class CyclicDependentModularization implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		//EPackage ePackage = (EPackage) root;
		
		//EcoreGraph graph = EcoreGraphRegistry.INSTANCE.getEcoreGraph(ePackage);
		EcoreGraph graph = ModelSmellFinder.ecoreGraph;
		Collection<List<EClassifier>> cycles = EcoreGraphUtil.findSimpleCycles(graph);
		
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		for (List<EClassifier> cycle : cycles) {
			results.add(new LinkedList<EObject>(cycle));
		}
		
		return results;
	}
	
}