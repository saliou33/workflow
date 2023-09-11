package com.innov.workflow.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONObject;

public class RestService implements JavaDelegate {
    private Expression url;

    @Override
    public void execute(DelegateExecution execution) {

        String formData = (String) execution.getVariable("formData");

        if (formData == null) {
            makeRestAPIcALL(execution, null);
        } else {
            JSONObject formDataJson = new JSONObject(formData);
            System.out.println("Inside Delegate Method");
            System.out.println(formDataJson.get("body"));
            System.out.println("URL: " + url.getValue(execution));
        }
    }

    private void makeRestAPIcALL(DelegateExecution execution, JSONObject data) {
        System.out.println("Inside Make Rest Call");
    }
}
