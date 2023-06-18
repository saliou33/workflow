package com.innov.workflow.activiti.domain.editor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelInformation {
    private Long id;
    private String name;
    private Integer type;
}
