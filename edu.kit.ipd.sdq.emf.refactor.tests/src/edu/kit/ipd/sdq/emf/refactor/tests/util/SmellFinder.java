package edu.kit.ipd.sdq.emf.refactor.tests.util;

import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.Result;

public class SmellFinder 
{
	public static Result findSmell(IModelSmellFinder smellDetector, EObject rootObject)
	{
		if(smellDetector == null)
		{
			throw new NullPointerException();
		}
		return findModelSmell(smellDetector, rootObject);
	}
	
	public static Result findMetricSmellWithLimit(MetricBasedModelSmellFinderClass smellDetector, double limit, EObject rootObject)
	{
		if(smellDetector == null)
		{
			throw new NullPointerException();
		}
		
		smellDetector.setLimit(limit);
		return findModelSmell(smellDetector, rootObject);
	}
	
	private static Result findModelSmell(IModelSmellFinder modelSmellFinder, EObject rootObject)
	{
		ModelSmell modelSmell = new ModelSmell("name", "description", "metamodel", modelSmellFinder, "id");
		LinkedList<ModelSmell> modelSmells = new LinkedList<ModelSmell>();
		modelSmells.add(modelSmell);
		LinkedList<Result> results = ModelSmellFinder.findModelSmells(modelSmells, rootObject);
		return results.getFirst();
	}
}
