package edu.kit.ipd.sdq.ecoregraph.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

public final class EcoreHelper {

    public static boolean equals(EClass eClass1, EClass eClass2) {

        if (eClass1 == eClass2)
            return true;

        if (eClass1 == null || eClass2 == null) {
            return false;
        }

        // names identical?
        String name1 = eClass1.getName();
        if (name1.equalsIgnoreCase(eClass2.getName())) {

            // packages and namespaces identical?
            if (isPackageHierarchyIdentical(eClass1.getEPackage(), eClass2.getEPackage())) {
                //System.out.println("The EClasses with the name " + name + " have same package and namespace hierarchies.");
                return true;
            } else {
                System.out.println("Different EClasses with the same name (" + name1 + ") were found!");
                System.out.println(name1 + ": Package=" + eClass1.getEPackage().getName() + " Resoruce=" + eClass1.eResource());
                System.out.println(name1 + ": Package=" + eClass2.getEPackage().getName() + " Resoruce=" + eClass2.eResource());
            }
        }

        return false;
    }

    public static boolean isPackageHierarchyIdentical(EPackage ePackage1, EPackage ePackage2) {
        // packages identical? (or both null?)
        if (ePackage1 == ePackage2)
            return true;

        // one of them null?
        if (ePackage1 == null || ePackage2 == null)
            return false;

        // other namespace?
        if (!ePackage1.getNsURI().equalsIgnoreCase(ePackage2.getNsURI()))
            return false;

        // identical namespace -> check rest of package hierarhcy
        return isPackageHierarchyIdentical(ePackage1.getESuperPackage(), ePackage2.getESuperPackage());
    }
}
