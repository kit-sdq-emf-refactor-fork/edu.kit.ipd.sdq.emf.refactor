package org.eclipse.emf.refactor.modelsmell;

public final class ObligatoryContainerRelation extends ContainerRelation {

    public ObligatoryContainerRelation() {
        super();
        wantedType = ContainerRelationType.obligatory;
    }
}