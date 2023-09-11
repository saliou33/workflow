package com.innov.workflow.activiti.domain.editor;

import lombok.Data;

@Data
public class ModelInformation {
    private String id;
    private String name;
    private Integer type;

    public ModelInformation(String id, String name, Integer type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

}
