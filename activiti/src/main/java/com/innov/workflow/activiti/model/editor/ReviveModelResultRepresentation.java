package com.innov.workflow.activiti.model.editor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviveModelResultRepresentation {
    private List<UnresolveModelRepresentation> unresolvedModels = new ArrayList();

    public ReviveModelResultRepresentation() {
    }

    public List<UnresolveModelRepresentation> getUnresolvedModels() {
        return this.unresolvedModels;
    }

    public void setUnresolvedModels(List<UnresolveModelRepresentation> unresolvedModels) {
        this.unresolvedModels = unresolvedModels;
    }

    public static class UnresolveModelRepresentation {
        private final String id;
        private final String name;
        private final String createdBy;

        public UnresolveModelRepresentation(String id, String name, String createdBy) {
            this.id = id;
            this.name = name;
            this.createdBy = createdBy;
        }


    }
}
