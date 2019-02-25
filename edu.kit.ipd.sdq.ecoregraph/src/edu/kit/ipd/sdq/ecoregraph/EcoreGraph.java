package edu.kit.ipd.sdq.ecoregraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.kit.ipd.sdq.ecoregraph.Dependency.DependencyType;

public class EcoreGraph {
    private EPackage root;
    private DefaultDirectedGraph<EClassifier, DefaultEdge> graph;
    private Map<EClassifier, Set<Dependency>> dependencies;
    private Queue<EClassifier> queuedEClassifiers;
    private Set<EClassifier> visitedEClassifiers;

    public EcoreGraph(EPackage root) {
        this.root = root;

        this.graph = new DefaultDirectedGraph<EClassifier, DefaultEdge>(DefaultEdge.class);
        this.dependencies = new HashMap<EClassifier, Set<Dependency>>();
        this.visitedEClassifiers = new HashSet<EClassifier>();

        this.queuedEClassifiers = new LinkedList<EClassifier>();
        this.queuedEClassifiers.addAll(getAllEClassifiers(root));

        computeGraph();
    }

    public Map<EClassifier, Set<Dependency>> getDependencies() {
        return dependencies;
    }

    public DefaultDirectedGraph<EClassifier, DefaultEdge> getGraph() {
        return graph;
    }

    public EPackage getRootEPackage() {
        return root;
    }

    private void computeGraph() {
        while (!queuedEClassifiers.isEmpty()) {
            EClassifier eClassifier = queuedEClassifiers.poll();

            // Add vertex to graph
            addVertex(eClassifier);

            //The ETypeParameter dependency is available for the types EClass and EDataType
            eClassifier.getETypeParameters().forEach(x -> visitTypeParam(x, eClassifier));

            //The following dependencies are exclusively available for the type EClass
            //If eClassifier is not an instance of EClass, move then to the next one
            if (!(eClassifier instanceof EClass))
                continue;

            // Now the case where eClassifier is an instance of EClass
            EClass eClass = (EClass) eClassifier;

            eClass.getESuperTypes().forEach(x -> addEdge(new Dependency(eClass, x, DependencyType.E_SUPER_TYPE)));

            eClass.getEReferences().forEach(x -> {
                addEdge(new Dependency(eClass, x.getEType(), DependencyType.E_REFERENCE));
                visitGenericRef(x.getEGenericType(), eClass);
            });

            eClass.getEAttributes().forEach(x -> {
                addEdge(new Dependency(eClass, x.getEType(), DependencyType.E_ATTRIBUTE));
                visitGenericRef(x.getEGenericType(), eClass);
            });

            eClass.getEGenericSuperTypes().forEach(x -> visitGenericSuperType(x, eClass));

            eClass.getEOperations().forEach(x -> {
                EClassifier operationType = x.getEType();
                if (operationType != null) {
                    addEdge(new Dependency(eClass, operationType, DependencyType.E_OPERATION_RETURN_TYPE));
                }

                x.getEParameters().forEach(y -> {
                    addEdge(new Dependency(eClass, y.getEType(), DependencyType.E_OPERATION_PARAMETER));
                    visitGenericType(y.getEGenericType(), eClass);
                });

                visitGenericType(x.getEGenericType(), eClass);

                x.getETypeParameters().forEach(y -> visitTypeParam(y, eClass));

            });
            visitedEClassifiers.add(eClassifier);
        }

    }

    /**
     * Adds a vertex to the graph and creates an entry in the dependencies map
     * 
     * @param eClassifier
     *            the EClassifier for which a vertex will be created
     */
    private void addVertex(EClassifier eClassifier) {
        if (!dependencies.containsKey(eClassifier)) {
            graph.addVertex(eClassifier);
            dependencies.put(eClassifier, new HashSet<Dependency>());
        }
    }

    private void addEdge(Dependency dependency) {
        EClassifier source = dependency.getSource();
        EClassifier target = dependency.getTarget();

        addVertex(target);
        if (!visitedEClassifiers.contains(target) && !queuedEClassifiers.contains(target))
            queuedEClassifiers.add(target);

        graph.addEdge(source, target);
        dependencies.get(source).add(dependency);
    }

    public static List<EClassifier> getAllEClassifiers(EPackage ePackage) {
        List<EClassifier> result = new ArrayList<EClassifier>();
        result.addAll(ePackage.getEClassifiers());
        ePackage.getESubpackages().forEach(x -> result.addAll(getAllEClassifiers(x)));
        return result;
    }

    /**
     * visits all bounds of the typeParam
     * 
     * @param typeParam
     * @param source
     *            dependency's origin
     */
    private void visitTypeParam(ETypeParameter typeParam, EClassifier source) {
        typeParam.getEBounds().forEach(bound -> visitGenericType(bound, source));
    }

    /**
     * visits genericSuperType if it has type arguments
     * 
     * @param genericSuperType
     * @param source
     *            dependency's origin
     */
    private void visitGenericSuperType(EGenericType genericSuperType, EClassifier source) {
        if (genericSuperType.getETypeArguments().size() > 0)
            visitGenericType(genericSuperType, source);
    }

    /**
     * visits genericTypeOfRef if it has type arguments
     * 
     * @param genericTypeOfRef
     * @param source
     *            dependency's origin
     */
    private void visitGenericRef(EGenericType genericTypeOfRef, EClassifier source) {
        if (genericTypeOfRef.getETypeArguments().size() > 0)
            visitGenericType(genericTypeOfRef, source);
    }

    /**
     * Makes a call to addDependency on the EClassifier of genericType and visits the upper and
     * lower bounds as well as the type arguments and the type parameter
     * 
     * @param genericType
     * @param source
     *            dependency's origin
     */
    private void visitGenericType(EGenericType genericType, EClassifier source) {
        if (genericType == null)
            return;

        EClassifier eClassifier = genericType.getEClassifier();
        if (eClassifier != null) {
            addEdge(new Dependency(source, eClassifier, DependencyType.E_GENERIC_TYPE));
        }

        visitGenericType(genericType.getEUpperBound(), source);
        visitGenericType(genericType.getELowerBound(), source);
        genericType.getETypeArguments().forEach(t -> visitGenericType(t, source));

        ETypeParameter typeParam = genericType.getETypeParameter();
        if (typeParam != null) {
            visitTypeParam(typeParam, source);
        }
    }

    public void print() {
        System.out.println("---");
        System.out.println("Dependencies :");
        System.out.println("---");
        for (EClassifier eClassifier : dependencies.keySet()) {
            System.out.println(eClassifier.getName());
            for (Dependency d : dependencies.get(eClassifier)) {
                System.out.println("\t => " + d.getTarget().getName() + " : " + d.getType().toString());
            }
        }
        System.out.println("---");
    }

}
