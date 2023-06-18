package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LightAppRepresentation extends AbstractRepresentation {
    private Long id;
    private String name;
    private String description;
    private String theme;
    private String icon;

}
