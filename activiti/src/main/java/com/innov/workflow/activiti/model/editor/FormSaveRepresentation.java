package com.innov.workflow.activiti.model.editor;


import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.activiti.model.editor.form.FormRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FormSaveRepresentation extends AbstractRepresentation {
    protected boolean reusable;
    protected boolean newVersion;
    protected String comment;
    protected String formImageBase64;
    protected FormRepresentation formRepresentation;

}
