package edu.kit.ipd.sdq.emf.refactor.smells.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;

public class DetectionHelper 
{
	public static List<EClass> getAllEClasses(EObject root) {
		List<EClass> classes = new ArrayList<EClass>();
		TreeIterator<EObject> iter = root.eAllContents();
		while (iter.hasNext()) {
			EObject eObject = iter.next();
			if (eObject instanceof EClass) {
				EClass cl = (EClass) eObject;
				classes.add(cl);
			}
		}
		return classes;
	}
	
	public static List<EClass> getAllESuperclasses(EClass cl) {
		List<EClass> superclasses = new ArrayList<EClass>();
		if (cl.getESuperTypes().isEmpty()) return superclasses;
		superclasses.addAll(cl.getESuperTypes());
		for (EClass superclass : cl.getESuperTypes()) {
			superclasses.addAll(getAllESuperclasses(superclass));
		}
		return superclasses;
	}
	
	public static List<EEnum> getAllEEnums(EObject root) {
		List<EEnum> enums = new ArrayList<EEnum>();
		TreeIterator<EObject> iter = root.eAllContents();
		while (iter.hasNext()) {
			EObject eObject = iter.next();
			if (eObject instanceof EEnum) {
				EEnum enumeration = (EEnum) eObject;
				enums.add(enumeration);
			}
		}
		return enums;
	}
	
}
