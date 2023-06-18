package com.innov.workflow.activiti.model.editor;


import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class FormFieldValuesRepresentation extends AbstractRepresentation {
    protected Long formId;
    protected String formName;
    protected List<FormFieldSummaryRepresentation> fields = new ArrayList();

}
