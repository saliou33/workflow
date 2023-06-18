package com.innov.workflow.activiti.domain.editor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MissingModelInformation extends ModelInformation {
    private String usedIn;
}
