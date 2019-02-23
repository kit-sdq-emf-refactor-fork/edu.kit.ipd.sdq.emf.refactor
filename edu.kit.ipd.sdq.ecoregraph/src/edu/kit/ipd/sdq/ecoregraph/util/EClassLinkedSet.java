package edu.kit.ipd.sdq.ecoregraph.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

public class EClassLinkedSet extends LinkedHashSet<EClass> {

    private static final long serialVersionUID = 6141321221905984295L;

    private final boolean forgiving;

    public EClassLinkedSet() {
        this(false);
    }

    public EClassLinkedSet(int size) {
        this(size, false);
    }

    public EClassLinkedSet(Collection<? extends EClass> otherSet) {
        this(otherSet, false);
    }

    public EClassLinkedSet(boolean forgiving) {
        super();
        this.forgiving = forgiving;
    }

    public EClassLinkedSet(int size, boolean forgiving) {
        super(size);
        this.forgiving = forgiving;
    }

    public EClassLinkedSet(Collection<? extends EClass> otherSet, boolean forgiving) {
        super(otherSet);
        this.forgiving = forgiving;
    }

    @Override
    public boolean add(EClass eClass) {

        if (eClass == null) {
            String message = "Tried to add null to an eClass set. There are problems in the metamodel.";
            if (forgiving) {
                System.out.println("WARNING: " + message);
                return false;
            } else {
                throw new RuntimeException(message);
            }
        }

        if (eClass.getName() == null) {
            String message = "Tried to add an unresolved reference to an eClass set: " + eClass + '.';
            if (forgiving) {
                System.out.println("WARNING: " + message + " Ignoring.");
                return false;
            } else {
                throw new RuntimeException(message);
            }
        }

        if (contains(eClass)) {
            return false;
        }
        return super.add(eClass);
    }

    @Override
    public boolean contains(Object o) {

        if (o == null) {
            return false;
        }

        // if object already present => true
        if (super.contains(o)) {
            return true;
        }

        // not an EClass => false
        if (!(o instanceof EClass)) {
            return false;
        }
        EClass eClass = (EClass) o;
        String name = eClass.getName();

        // duplicate might still exist in the set
        for (EClass containedEClass : this) {
            if (containedEClass.getName().equalsIgnoreCase(name)) {
                // check if packages and namespaces are identical
                if (isPackageHierarchyIdentical(eClass.getEPackage(), containedEClass.getEPackage())) {
//                    System.out.println("The EClasses with the name " + name + " have same package and namespace hierarchies.");
                    return true;
                } else {
                    System.out.println("Different EClasses with the same name (" + name + ") were found! "
                            + "You will need to write a 'disti' class matching exception, or else these classes cannot be correctly matched in the other metamodel. "
                            + "This is relevant for the modes: Model (exception file in base dir of project), Modification (only for entry classes, specified in the mode files) and Extension (only for extended classes, not implemented as not yet needed).");
                    System.out.println(name + ": Package=" + eClass.getEPackage().getName() + " Resoruce=" + eClass.eResource());
                    System.out.println(name + ": Package=" + containedEClass.getEPackage().getName() + " Resoruce=" + containedEClass.eResource());
                }
            }
        }

        return false;
    }

    private boolean isPackageHierarchyIdentical(EPackage ePackage1, EPackage ePackage2) {
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

    public EClass getLast() {
        EClass last = null;
        Iterator<EClass> iterator = iterator();
        while (iterator.hasNext())
            last = iterator.next();
        return last;
    }

    /**
     * Adds the content of otherSet to this set. Ensures that the last element stays at the end of
     * the list.
     * 
     * @param otherSet
     */
    public void inject(EClassLinkedSet otherSet) {
        Iterator<EClass> iterator = iterator();
        while (iterator.hasNext())
            iterator.next();
        iterator.remove();
        addAll(otherSet);
    }
}
