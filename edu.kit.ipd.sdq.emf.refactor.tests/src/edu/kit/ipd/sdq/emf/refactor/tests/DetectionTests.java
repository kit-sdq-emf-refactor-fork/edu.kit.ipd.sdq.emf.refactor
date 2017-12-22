package edu.kit.ipd.sdq.emf.refactor.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.kit.ipd.sdq.emf.refactor.tests.util.EcoreBuilder;
import edu.kit.ipd.sdq.emf.refactor.tests.util.SmellFinder;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.Result;
import org.eclipse.emf.refactor.modelsmell.*;

public class DetectionTests {

	@Test
	public void validateWideHierarchyDetection_DifferentLimits()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType1 = EcoreBuilder.createEClass("SubType1");
		testPackage.getEClassifiers().add(subType1);
		EcoreBuilder.addSuperType(subType1, testPackage, superType.getName());
		
		EClass subType2 = EcoreBuilder.createEClass("SubType2");
		testPackage.getEClassifiers().add(subType2);
		EcoreBuilder.addSuperType(subType2, testPackage, superType.getName());
		
		EClass subType3 = EcoreBuilder.createEClass("SubType3");
		testPackage.getEClassifiers().add(subType3);
		EcoreBuilder.addSuperType(subType3, testPackage, superType.getName());
		
		EClass subType4 = EcoreBuilder.createEClass("SubType4");
		testPackage.getEClassifiers().add(subType4);
		EcoreBuilder.addSuperType(subType4, testPackage, superType.getName());
		
		EClass subType5 = EcoreBuilder.createEClass("SubType5");
		testPackage.getEClassifiers().add(subType5);
		EcoreBuilder.addSuperType(subType5, testPackage, superType.getName());
		
		EcoreBuilder.savePackageToFile(testPackage, "WideHierarchy.ecore");	
				
		Result result = SmellFinder.findMetricSmellWithLimit(new WideHierarchy(), 5, testPackage);
		assertNotNull(result);
		assertEquals(1,	result.getModelelements().size()); 	
		
		result = SmellFinder.findMetricSmellWithLimit(new WideHierarchy(), 6, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size()); 	
	}
	
	@Test
	public void validateDeepHierarchyDetection_DifferentLimits()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType = EcoreBuilder.createEClass("SubType");
		testPackage.getEClassifiers().add(subType);
		EcoreBuilder.addSuperType(subType, testPackage, superType.getName());
		
		EClass subSubType = EcoreBuilder.createEClass("SubSubType");
		testPackage.getEClassifiers().add(subSubType);
		EcoreBuilder.addSuperType(subSubType, testPackage, subType.getName());
		
		EClass subSubSubType = EcoreBuilder.createEClass("SubSubSubType");
		testPackage.getEClassifiers().add(subSubSubType);
		EcoreBuilder.addSuperType(subSubSubType, testPackage, subSubType.getName());
		
		EcoreBuilder.savePackageToFile(testPackage, "DeepHierarchy.ecore");	
		
		Result result = SmellFinder.findMetricSmellWithLimit(new DeepHierarchy(), 3, testPackage);
		assertNotNull(result);
		assertEquals(1,	result.getModelelements().size()); 	
		
		result = SmellFinder.findMetricSmellWithLimit(new DeepHierarchy(), 4, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size()); 	
	}
	
	@Test
	public void validateMissingAbstraction_DataClumpsAttributesDetection() 
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		EcoreBuilder.addAttribute(firstClass, "firstAttribute", EcorePackage.Literals.EDOUBLE, false, 0, 1);
		EcoreBuilder.addAttribute(firstClass, "secondAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		EcoreBuilder.addAttribute(secondClass, "firstAttribute", EcorePackage.Literals.EDOUBLE, false, 0, 1);
		EcoreBuilder.addAttribute(secondClass, "secondAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "MissingAbstractionDataClumps.ecore");
		
		Result result = SmellFinder.findMetricSmellWithLimit(new MissingAbstraction_DataClumpsAttributes(), 2, testPackage);
		assertNotNull(result);
		assertEquals(2,	result.getModelelements().size());
		
		result = SmellFinder.findMetricSmellWithLimit(new MissingAbstraction_DataClumpsAttributes(), 3, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size());
	}
	
	@Test
	public void validateMissingAbstraction_PrimitiveObessionPrimitiveTypesDetection() 
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		EcoreBuilder.addAttribute(firstClass, "firstAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		EcoreBuilder.addAttribute(firstClass, "secondAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		EcoreBuilder.addAttribute(secondClass, "firstAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		EcoreBuilder.addAttribute(secondClass, "secondAttribute", EcorePackage.Literals.EINT, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "MissingAbstractionPrimitiveObsessionPrimitiveTypes.ecore");
		
		Result result = SmellFinder.findMetricSmellWithLimit(new MissingAbstraction_PrimitiveObessionPrimitiveTypes(), 2, testPackage);
		assertNotNull(result);
		assertEquals(2,	result.getModelelements().size());
		
		result = SmellFinder.findMetricSmellWithLimit(new MissingAbstraction_PrimitiveObessionPrimitiveTypes(), 3, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size());
	}
	
	@Test
	public void validateMultipathHierarchyDetection() 
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass supersuperClass = EcoreBuilder.createEClass("SuperSuperClass");
		testPackage.getEClassifiers().add(supersuperClass);
		
		EClass firstSuperClass = EcoreBuilder.createEClass("FirstSuperClass");
		testPackage.getEClassifiers().add(firstSuperClass);
		EcoreBuilder.addSuperType(firstSuperClass, testPackage, "SuperSuperClass");
		
		EClass secondSuperClass = EcoreBuilder.createEClass("SecondSuperClass");
		testPackage.getEClassifiers().add(secondSuperClass);
		EcoreBuilder.addSuperType(secondSuperClass, testPackage, "SuperSuperClass");
		
		EClass derivedClass = EcoreBuilder.createEClass("DerivedClass");
		testPackage.getEClassifiers().add(derivedClass);
		EcoreBuilder.addSuperType(derivedClass, testPackage, "FirstSuperClass");
		EcoreBuilder.addSuperType(derivedClass, testPackage, "SecondSuperClass");
		
		EcoreBuilder.savePackageToFile(testPackage, "MultipathHierarchy.ecore");
				
		Result result = SmellFinder.findSmell(new MultipathHierarchy(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
		assertEquals(4, result.getModelelements().get(0).size());
	}
	
	@Test
	public void validateMultipathHierarchyDetection_OneDirectOneIndirect() 
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass supersuperClass = EcoreBuilder.createEClass("SuperSuperClass");
		testPackage.getEClassifiers().add(supersuperClass);
		
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		EcoreBuilder.addSuperType(superClass, testPackage, "SuperSuperClass");
				
		EClass derivedClass = EcoreBuilder.createEClass("DerivedClass");
		testPackage.getEClassifiers().add(derivedClass);
		EcoreBuilder.addSuperType(derivedClass, testPackage, "SuperClass");
		EcoreBuilder.addSuperType(derivedClass, testPackage, "SuperSuperClass");
		
		EcoreBuilder.savePackageToFile(testPackage, "MultipathHierarchy2.ecore");
				
		Result result = SmellFinder.findSmell(new MultipathHierarchy(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
		assertEquals(3, result.getModelelements().get(0).size());
	}
	
	@Test
	public void validateUnutilizedAbstractionUnusedClassDetection_UnusedClasses_BothClassesAreDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		
		EClass subClass = EcoreBuilder.createEClass("SubClass");
		testPackage.getEClassifiers().add(subClass);
		
		EcoreBuilder.savePackageToFile(testPackage, "UnutilizedAbstraction_UnusedClass1.ecore");
		
		Result result = SmellFinder.findSmell(new UnutilizedAbstraction_UnusedClasses(), testPackage);
		assertNotNull(result);
		assertEquals(2, result.getModelelements().size());
		assertEquals(1, result.getModelelements().get(0).size());
		assertEquals(1, result.getModelelements().get(1).size());
	}
	
	@Test
	public void validateUnutilizedAbstractionUnusedClassDetection_UsedAsSuperType_NoClassDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		
		EClass subClass = EcoreBuilder.createEClass("SubClass");
		testPackage.getEClassifiers().add(subClass);
		EcoreBuilder.addSuperType(subClass, testPackage, "SuperClass");
		
		Result result = SmellFinder.findSmell(new UnutilizedAbstraction_UnusedClasses(), testPackage);
		assertNotNull(result);
		assertEquals(0, result.getModelelements().size());
	}
	
	@Test
	public void validateUnutilizedAbstractionUnusedClassDetection_UsedAsType_ReferencingClassDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		
		EClass subClass = EcoreBuilder.createEClass("SubClass");
		testPackage.getEClassifiers().add(subClass);
		EcoreBuilder.addReference(subClass, "testReference", superClass, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "UnutilizedAbstraction_UnusedClass2.ecore");
		
		Result result = SmellFinder.findSmell(new UnutilizedAbstraction_UnusedClasses(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateUnutilizedAbstractionUnusedEnumDetection_Unused_EnumDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EEnum eEnum = EcoreBuilder.createEEnum("TestEnum");
		EcoreBuilder.addEEnumLiteral(eEnum, "firstLiteral", 0);
		testPackage.getEClassifiers().add(eEnum);
		
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		
		EcoreBuilder.savePackageToFile(testPackage, "UnutilizedAbstraction_UnusedEnum.ecore");
		
		Result result = SmellFinder.findSmell(new UnutilizedAbstraction_UnusedEnumeration(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateUnutilizedAbstractionUnusedEnumDetection_UsedAsType_NoEnumDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EEnum eEnum = EcoreBuilder.createEEnum("TestEnum");
		EcoreBuilder.addEEnumLiteral(eEnum, "firstLiteral", 0);
		testPackage.getEClassifiers().add(eEnum);
		
		EClass superClass = EcoreBuilder.createEClass("SuperClass");
		testPackage.getEClassifiers().add(superClass);
		EcoreBuilder.addAttribute(superClass, "testEnum", eEnum, false, 0, 1);
		
		Result result = SmellFinder.findSmell(new UnutilizedAbstraction_UnusedEnumeration(), testPackage);
		assertNotNull(result);
		assertEquals(0, result.getModelelements().size());
	}
	
	@Test
	public void validateRedundantContainerRelation_ExplicitReference_ClassIsDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		
		EcoreBuilder.addReference(firstClass, "testReference", secondClass, true, 0, 1);
		EcoreBuilder.addReference(secondClass, "referencingReference", firstClass, false, 0, 1);
		EcoreBuilder.makeOppositeReference(firstClass, "testReference", secondClass, "referencingReference");		
		
		EcoreBuilder.savePackageToFile(testPackage, "RedundantContainerRelation.ecore");
		
		Result result = SmellFinder.findSmell(new RedundantContainerRelation(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateObligatoryContainerRelation_ExplicitReference_ClassIsDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		
		EcoreBuilder.addReference(firstClass, "testReference", secondClass, true, 0, 1);
		EcoreBuilder.addReference(secondClass, "referencingReference", firstClass, false, 1, 1);
		EcoreBuilder.makeOppositeReference(firstClass, "testReference", secondClass, "referencingReference");		
		
		EcoreBuilder.savePackageToFile(testPackage, "ObligatoryContainerRelation.ecore");
		
		Result result = SmellFinder.findSmell(new ObligatoryContainerRelation(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateSpecializationAggregration_SpecialAggrToSubClass_ClassIsDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		
		EClass firstSubClass = EcoreBuilder.createEClass("FirstSubClass");
		testPackage.getEClassifiers().add(firstSubClass);
		EcoreBuilder.addSuperType(firstSubClass, testPackage, "FirstClass");
		
		EClass secondSubClass = EcoreBuilder.createEClass("SecondSubClass");
		testPackage.getEClassifiers().add(secondSubClass);
		EcoreBuilder.addSuperType(secondSubClass, testPackage, "SecondClass");
		
		EcoreBuilder.addReference(firstClass, "testReference", secondClass, false, 0, 1);
		EcoreBuilder.addReference(firstSubClass, "specializationReference", secondSubClass, false, 0, 1);	
		
		EcoreBuilder.savePackageToFile(testPackage, "SpecializationAggregation1.ecore");
		
		Result result = SmellFinder.findSmell(new SpecializationAggregation(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateSpecializationAggregration_SpecialAggrToSuperClass_ClassIsDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		
		EClass firstSubClass = EcoreBuilder.createEClass("FirstSubClass");
		testPackage.getEClassifiers().add(firstSubClass);
		EcoreBuilder.addSuperType(firstSubClass, testPackage, "FirstClass");
		
		EClass secondSubClass = EcoreBuilder.createEClass("SecondSubClass");
		testPackage.getEClassifiers().add(secondSubClass);
		EcoreBuilder.addSuperType(secondSubClass, testPackage, "SecondClass");
		
		EcoreBuilder.addReference(firstClass, "testReference", secondClass, false, 0, 1);
		EcoreBuilder.addReference(firstSubClass, "specializationReference", secondClass, false, 0, 1);	
		
		EcoreBuilder.savePackageToFile(testPackage, "SpecializationAggregation2.ecore");
		
		Result result = SmellFinder.findSmell(new SpecializationAggregation(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateSpecializationAggregration_NoSpecialAggr_NoClassIsDetected()
	{
		EcoreBuilder.initStandalone();
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass firstClass = EcoreBuilder.createEClass("FirstClass");
		testPackage.getEClassifiers().add(firstClass);
		
		EClass secondClass = EcoreBuilder.createEClass("SecondClass");
		testPackage.getEClassifiers().add(secondClass);
		
		EClass firstSubClass = EcoreBuilder.createEClass("FirstSubClass");
		testPackage.getEClassifiers().add(firstSubClass);
		EcoreBuilder.addSuperType(firstSubClass, testPackage, "FirstClass");
		
		EClass secondSubClass = EcoreBuilder.createEClass("SecondSubClass");
		testPackage.getEClassifiers().add(secondSubClass);
		EcoreBuilder.addSuperType(secondSubClass, testPackage, "SecondClass");
		
		EcoreBuilder.addReference(firstClass, "testReference", secondClass, false, 0, 1);
		
		Result result = SmellFinder.findSmell(new SpecializationAggregation(), testPackage);
		assertNotNull(result);
		assertEquals(0, result.getModelelements().size());
	}
	
	@Test
	public void validateHubLikeModularizationIncomingDependencies_DifferentLimits()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass hubClass = EcoreBuilder.createEClass("HubClass");
		testPackage.getEClassifiers().add(hubClass);
		
		EClass class1 = EcoreBuilder.createEClass("Class1");
		testPackage.getEClassifiers().add(class1);
		EcoreBuilder.addReference(class1, "testReference", hubClass, false, 0, 1);
		
		EClass class2 = EcoreBuilder.createEClass("Class2");
		testPackage.getEClassifiers().add(class2);
		EcoreBuilder.addReference(class2, "testReference", hubClass, false, 0, 1);
		
		EClass class3 = EcoreBuilder.createEClass("Class3");
		testPackage.getEClassifiers().add(class3);
		EcoreBuilder.addReference(class3, "testReference", hubClass, false, 0, 1);
		
		EClass class4 = EcoreBuilder.createEClass("Class4");
		testPackage.getEClassifiers().add(class4);
		EcoreBuilder.addReference(class4, "testReference", hubClass, false, 0, 1);
		
		EClass class5 = EcoreBuilder.createEClass("Class5");
		testPackage.getEClassifiers().add(class5);
		EcoreBuilder.addReference(class5, "testReference", hubClass, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "HubLike_Incoming.ecore");	
				
		Result result = SmellFinder.findMetricSmellWithLimit(new HubLikeModularization_IncomingDependencies(), 5, testPackage);
		assertNotNull(result);
		assertEquals(1,	result.getModelelements().size()); 	
		
		result = SmellFinder.findMetricSmellWithLimit(new HubLikeModularization_IncomingDependencies(), 6, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size()); 	
	}
	
	@Test
	public void validateHubLikeModularizationOutgoingDependencies_DifferentLimits()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass hubClass = EcoreBuilder.createEClass("HubClass");
		testPackage.getEClassifiers().add(hubClass);
		
		EClass class1 = EcoreBuilder.createEClass("Class1");
		testPackage.getEClassifiers().add(class1);
		EcoreBuilder.addReference(hubClass, "testReference1", class1, false, 0, 1);
		
		EClass class2 = EcoreBuilder.createEClass("Class2");
		testPackage.getEClassifiers().add(class2);
		EcoreBuilder.addReference(hubClass, "testReference2", class2, false, 0, 1);
		
		EClass class3 = EcoreBuilder.createEClass("Class3");
		testPackage.getEClassifiers().add(class3);
		EcoreBuilder.addReference(hubClass, "testReference3", class3, false, 0, 1);
		
		EClass class4 = EcoreBuilder.createEClass("Class4");
		testPackage.getEClassifiers().add(class4);
		EcoreBuilder.addReference(hubClass, "testReference4", class4, false, 0, 1);
		
		EClass class5 = EcoreBuilder.createEClass("Class5");
		testPackage.getEClassifiers().add(class5);
		EcoreBuilder.addReference(hubClass, "testReference5", class5, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "HubLike_Outgoing.ecore");	
				
		Result result = SmellFinder.findMetricSmellWithLimit(new HubLikeModularization_OutgoingDependencies(), 5, testPackage);
		assertNotNull(result);
		assertEquals(1,	result.getModelelements().size()); 	
		
		result = SmellFinder.findMetricSmellWithLimit(new HubLikeModularization_OutgoingDependencies(), 6, testPackage);
		assertNotNull(result);
		assertEquals(0,	result.getModelelements().size()); 	
	}
	
	@Test
	public void validateCyclicHierarchyDetection()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType = EcoreBuilder.createEClass("SubType");
		testPackage.getEClassifiers().add(subType);
		EcoreBuilder.addSuperType(subType, testPackage, superType.getName());
		
		EClass subSubType = EcoreBuilder.createEClass("SubSubType");
		testPackage.getEClassifiers().add(subSubType);
		EcoreBuilder.addSuperType(subSubType, testPackage, subType.getName());
		
		EcoreBuilder.addReference(superType, "knownDerived", subSubType, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "CyclicHierarchy.ecore");	
		
		Result result = SmellFinder.findSmell(new CyclicHierarchy(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
	
	@Test
	public void validateCyclicDependentModularizationDetection()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType = EcoreBuilder.createEClass("SubType");
		testPackage.getEClassifiers().add(subType);
		EcoreBuilder.addSuperType(subType, testPackage, superType.getName());
					
		EClass anotherType = EcoreBuilder.createEClass("AnotherType");
		testPackage.getEClassifiers().add(anotherType);
		
		EClass anotherType2 = EcoreBuilder.createEClass("AnotherType2");
		testPackage.getEClassifiers().add(anotherType2);
		
		EcoreBuilder.addReference(superType, "TestReference", anotherType, false, 0, 1);
		EcoreBuilder.addReference(anotherType, "TestReference", anotherType2, false, 0, 1);
		EcoreBuilder.addReference(anotherType2, "TestReference", subType, false, 0, 1);
		
		EcoreBuilder.savePackageToFile(testPackage, "CyclicDependentModularizationHierarchy.ecore");	
		
		Result result = SmellFinder.findSmell(new CyclicDependentModularization(), testPackage);
		assertNotNull(result);
		assertEquals(3, result.getModelelements().size());
	}
	
	@Test
	public void validateCyclicDependentModularizationDetection_NoCycle()
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType = EcoreBuilder.createEClass("SubType");
		testPackage.getEClassifiers().add(subType);
		EcoreBuilder.addSuperType(subType, testPackage, superType.getName());
					
		EClass anotherType = EcoreBuilder.createEClass("AnotherType");
		testPackage.getEClassifiers().add(anotherType);
		
		EClass anotherType2 = EcoreBuilder.createEClass("AnotherType2");
		testPackage.getEClassifiers().add(anotherType2);
		
		EcoreBuilder.addReference(superType, "TestReference", anotherType, false, 0, 1);
		EcoreBuilder.addReference(anotherType, "TestReference", anotherType2, false, 0, 1);
		
		Result result = SmellFinder.findSmell(new CyclicDependentModularization(), testPackage);
		assertNotNull(result);
		assertEquals(0, result.getModelelements().size());
	}
	
	@Test
	public void validateLateHierachyDetection() 
	{
		EcoreBuilder.initStandalone();
		
		EPackage testPackage = EcoreBuilder.createPackage("testPackage", "testPackage", "http://testPackage");
		
		EClass superType = EcoreBuilder.createEClass("SuperType");
		testPackage.getEClassifiers().add(superType);
		
		EClass subType = EcoreBuilder.createEClass("SubType");
		testPackage.getEClassifiers().add(subType);
		subType.setAbstract(true);
		EcoreBuilder.addSuperType(subType, testPackage, superType.getName());
		
		EcoreBuilder.savePackageToFile(testPackage, "LateHierarchy.ecore");	
		
		Result result = SmellFinder.findSmell(new LateHierarchy(), testPackage);
		assertNotNull(result);
		assertEquals(1, result.getModelelements().size());
	}
}
