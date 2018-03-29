package edu.kit.ipd.sdq.ecoregraph;

import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

public class EcoreGraphRegistry {

	
	public static EcoreGraphRegistry INSTANCE = new EcoreGraphRegistry();
	private HashMap<EPackage, EcoreGraph> registry;
	private HashMap<EPackage, EPackage> versionTracker;
	
	private EcoreGraphRegistry() {
		registry = new HashMap<EPackage, EcoreGraph>();
		versionTracker = new HashMap<EPackage, EPackage>();
	}
	
	public void registerEPackage(EPackage ePackage) {
		//System.out.println("hkj");
		if (!registry.containsKey(ePackage)) {
			//System.out.println("Not registered");
			EcoreGraph graph = new EcoreGraph(ePackage);
			registry.put(ePackage, graph);
			
			EcoreUtil.Copier copier = new Copier();
	    	EPackage copy = (EPackage) copier.copy(ePackage);
	    	copier.copyReferences();
	    	
	    	versionTracker.put(ePackage, copy);
			
		}
		else {
			EPackage versionedEPackage = versionTracker.get(ePackage);
			
			if (!new EcoreUtil.EqualityHelper().equals(ePackage, versionedEPackage)) {
				//System.out.println("Registered but has been modified");
				EcoreGraph graph = new EcoreGraph(ePackage);
				registry.put(ePackage, graph);
				
				EPackage copy = copyEObject(ePackage);
		    	
				versionTracker.put(ePackage, copy);
			}
			else {
				//System.out.println("Registered and up to date");
			}
			
		}
	}
	
	public EcoreGraph getEcoreGraph(EPackage ePackage) {
		//System.out.println("hbkjl");
		if (!registry.containsKey(ePackage)) {
			//System.out.println("Not registered");
			return null;
		}
		else {
			EPackage versionedEPackage = versionTracker.get(ePackage);
			
			if (!new EcoreUtil.EqualityHelper().equals(ePackage, versionedEPackage)) {
				//System.out.println("Registered but has been modified");
				EcoreGraph graph = new EcoreGraph(ePackage);
				registry.put(ePackage, graph);
				
				EPackage copy = copyEObject(ePackage);
				versionTracker.put(ePackage, copy);
			}
			else {
				//System.out.println("Registered and up to date");
			}
		}
		return registry.get(ePackage);
	}
	
	private <T extends EObject> T copyEObject(T eObject) {
		EcoreUtil.Copier copier = new Copier();
	   	@SuppressWarnings("unchecked")
		T copy = (T) copier.copy(eObject);
    	copier.copyReferences();
    	return copy;
	}
}
