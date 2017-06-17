package org.openecomp.sdc.be.components.impl;

import org.openecomp.sdc.be.components.ArtifactsResolver;
import org.openecomp.sdc.be.datatypes.enums.ComponentTypeEnum;
import org.openecomp.sdc.be.model.ArtifactDefinition;
import org.openecomp.sdc.be.model.Component;
import org.openecomp.sdc.be.model.ComponentInstance;
import org.openecomp.sdc.be.model.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Component("artifact-resolver")
public class ArtifactResolverImpl implements ArtifactsResolver {

    @Override
    public ArtifactDefinition findArtifactOnComponent(Component component, ComponentTypeEnum componentType, String artifactId) {
        List<ArtifactDefinition> allComponentsArtifacts = getAllComponentsArtifacts(component, componentType);
        return findById(allComponentsArtifacts, artifactId);

    }

    @Override
    public ArtifactDefinition findArtifactOnComponentInstance(ComponentInstance componentInstance, String artifactId) {
        List<ArtifactDefinition> allInstanceArtifacts = getAllInstanceArtifacts(componentInstance);
        return findById(allInstanceArtifacts, artifactId);
    }

    private ArtifactDefinition findById(List<ArtifactDefinition> artifacts, String artifactId) {
        return artifacts.stream()
                        .filter(artifact -> artifact.getUniqueId().equals(artifactId))
                        .findFirst().orElse(null);
    }

    private List<ArtifactDefinition> getAllComponentsArtifacts(Component component, ComponentTypeEnum componentType) {
        Map<String, ArtifactDefinition> deploymentArtifacts = Optional.ofNullable(component.getDeploymentArtifacts()).orElse(Collections.emptyMap());
        Map<String, ArtifactDefinition> artifacts = Optional.ofNullable(component.getArtifacts()).orElse(Collections.emptyMap());
        Map<String, ArtifactDefinition> serviceApiArtifacts = Collections.emptyMap();
        if (componentType.equals(ComponentTypeEnum.SERVICE)) {
            serviceApiArtifacts = Optional.ofNullable(((Service) component).getServiceApiArtifacts()).orElse(Collections.emptyMap());
        }
        return appendAllArtifacts(deploymentArtifacts, artifacts, serviceApiArtifacts);
    }

    private List<ArtifactDefinition> getAllInstanceArtifacts(ComponentInstance instance) {
        Map<String, ArtifactDefinition> deploymentArtifacts = Optional.ofNullable(instance.getDeploymentArtifacts()).orElse(Collections.emptyMap());
        Map<String, ArtifactDefinition> artifacts = Optional.ofNullable(instance.getArtifacts()).orElse(Collections.emptyMap());
        return appendAllArtifacts(deploymentArtifacts, artifacts);
    }

    @SafeVarargs
    private final List<ArtifactDefinition> appendAllArtifacts(Map<String, ArtifactDefinition>... artifacts) {
        List<ArtifactDefinition> allArtifacts = new ArrayList<>();
        Arrays.stream(artifacts).forEach(a -> allArtifacts.addAll(a.values()));
        return allArtifacts;
    }

}