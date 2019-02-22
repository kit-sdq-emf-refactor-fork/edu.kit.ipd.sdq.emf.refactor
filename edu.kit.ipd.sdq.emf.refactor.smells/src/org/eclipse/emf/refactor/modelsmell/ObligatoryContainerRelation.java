package org.eclipse.emf.refactor.modelsmell;

public final class ObligatoryContainerRelation extends RedundantContainerRelation {

    public ObligatoryContainerRelation() {
        super();
        wantedType = ContainerRelationType.obligatory;
    }
}