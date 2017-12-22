package edu.kit.ipd.sdq.emf.refactor.tests.util;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * Helper class to build ecore models.
 * @author René Hahn
 *
 */
public class EcoreBuilder 
{
	/**
	 * Adds an attribute to an EClass.
	 * @param context EClass, where to add the attribute.
	 * @param name Name of the attribute.
	 * @param type Type of the attribute.
	 * @param isId Is attribute an ID?
	 * @param lowerBound 
	 * @param upperBound
	 */
	public static void addAttribute(EClass context, String name,
			EClassifier type, boolean isId, int lowerBound, int upperBound)
	{
		final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		context.getEStructuralFeatures().add(attribute);
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setID(isId);
		attribute.setLowerBound(lowerBound);
		attribute.setUpperBound(upperBound);
		//attribute.set
	}
	
	/**
	 * Adds a reference to an EClass.
	 * @param context EClass, where to add the reference.
	 * @param name Name of the reference.
	 * @param type Type of the reference.
	 * @param containment Is this reference a containment?
	 * @param lowerBound
	 * @param upperBound
	 */
	public static void addReference(EClass context, String name,
			EClassifier type, boolean containment, int lowerBound, int upperBound)
	{
		final EReference reference = EcoreFactory.eINSTANCE.createEReference();
		context.getEStructuralFeatures().add(reference);
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(containment);
		reference.setLowerBound(lowerBound);
		reference.setUpperBound(upperBound);
	}
	
	public static void makeOppositeReference(EClass firstClass, String firstReferenceName, EClass secondClass, String secondReferenceName) 
	{
		EReference firstReference = null;
		EReference secondReference = null;
		
		for(EReference currentReference : firstClass.getEAllReferences())
		{
			if(currentReference.getName().equals(firstReferenceName))
			{
				firstReference = currentReference;
			}
		}
		
		for(EReference currentReference : secondClass.getEAllReferences())
		{
			if(currentReference.getName().equals(secondReferenceName))
			{
				secondReference = currentReference;
			}
		}
		
		if(firstReference != null && secondReference != null)
		{
			firstReference.setEOpposite(secondReference);
			secondReference.setEOpposite(firstReference);
		}	
	}
	
	/**
	 * Adds a EEnumLiteral to an EEnum
	 * @param context EEnum, where to add the literal.
	 * @param name Name of the literal.
	 * @param value Value of the literal.
	 */
	public static void addEEnumLiteral(EEnum context, String name,
			int value)
	{
		final EEnumLiteral enumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
		context.getELiterals().add(enumLiteral);
		enumLiteral.setName(name);
		enumLiteral.setValue(value);
	}
	
	/**
	 * Creates a new EPackage.
	 * @param name Name of EPackage to create.
	 * @param prefix Prefix of EPackage.
	 * @param uri URI of EPackage.
	 * @return EPackage with specified parameters.
	 */
	public static EPackage createPackage(final String name, final String prefix,
	        final String uri)
	{
	    final EPackage epackage = EcoreFactory.eINSTANCE.createEPackage();
	    epackage.setName(name);
	    epackage.setNsPrefix(prefix);
	    epackage.setNsURI(uri);
	    return epackage;
	}
	
	/**
	 * Creates a new EClass.
	 * @param name Name of EClass to create.
	 * @return EClass with specified name.
	 */
    public static EClass createEClass(final String name)
    {
        final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(name);
        return eClass;
    }
    
    /**
     * Create a new EEnum.
     * @param name Name of EEnum to create.
     * @return EEnum with specified name.
     */
    public static EEnum createEEnum(final String name)
    {
    	final EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
    	eEnum.setName(name);
    	return eEnum;
    }  
	
    /**
     * Adds a supertype to an EClass.
     * @param context EClass, where the supertype is added.
     * @param superTypePackage EPackage which contains the supertype
     * @param className Name of supertype
     */
    public static void addSuperType(EClass context, EPackage superTypePackage,
            String className)
    {
         final EClass eSuperClass = (EClass) superTypePackage.getEClassifier(className);
         context.getESuperTypes().add(eSuperClass);
    }
	 
    /**
     * Initializes EMF Registry to work outside of Eclipse. Not required if running inside Eclipse.
     */
    public static void initStandalone()
    {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    // NOTE: It is very important that you DO NOT make the call to any
	    // EPackage.eINSTANCE's here.  Unlike in the FAQ example, we want to be sure
	    // that all non-plugin EPackages are loaded directly from ecore files, not generated
	    // Java classes.
    }
   
    /**
     * Saves an Package to file.
     * @param packageToSave EPackage, which will be saved.
     * @param filePath Path to file.
     */
	public static void savePackageToFile(EPackage packageToSave, String filePath)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource outputRes = resourceSet.createResource(URI.createFileURI(filePath));
		outputRes.getContents().add(packageToSave);
		try 
		{
			// to File
			outputRes.save(Collections.emptyMap());
			// to std out
			outputRes.save(System.out, Collections.emptyMap());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
