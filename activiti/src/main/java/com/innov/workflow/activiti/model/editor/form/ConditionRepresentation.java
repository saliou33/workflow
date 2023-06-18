package com.innov.workflow.activiti.model.editor.form;

public class ConditionRepresentation {
    private String leftFormFieldId;
    private String leftRestResponseId;
    private String operator;
    private Object rightValue;
    private String rightType;
    private String rightFormFieldId;
    private String rightRestResponseId;
    private String nextConditionOperator;
    private ConditionRepresentation nextCondition;

    public ConditionRepresentation() {
    }

    public String getLeftFormFieldId() {
        return this.leftFormFieldId;
    }

    public ConditionRepresentation setLeftFormFieldId(String leftFormFieldId) {
        this.leftFormFieldId = leftFormFieldId;
        return this;
    }

    public String getLeftRestResponseId() {
        return this.leftRestResponseId;
    }

    public void setLeftRestResponseId(String leftRestResponseId) {
        this.leftRestResponseId = leftRestResponseId;
    }

    public String getOperator() {
        return this.operator;
    }

    public ConditionRepresentation setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public Object getRightValue() {
        return this.rightValue;
    }

    public ConditionRepresentation setRightValue(Object value) {
        this.rightValue = value;
        return this;
    }

    public String getRightType() {
        return this.rightType;
    }

    public ConditionRepresentation setRightType(String rightType) {
        this.rightType = rightType;
        return this;
    }

    public String getRightFormFieldId() {
        return this.rightFormFieldId;
    }

    public ConditionRepresentation setRightFormFieldId(String rightFormFieldId) {
        this.rightFormFieldId = rightFormFieldId;
        return this;
    }

    public String getRightRestResponseId() {
        return this.rightRestResponseId;
    }

    public void setRightRestResponseId(String rightRestResponseId) {
        this.rightRestResponseId = rightRestResponseId;
    }

    public String getNextConditionOperator() {
        return this.nextConditionOperator;
    }

    public ConditionRepresentation getNextCondition() {
        return this.nextCondition;
    }

    public ConditionRepresentation setNextCondition(String operator, ConditionRepresentation nextCondition) {
        this.nextConditionOperator = operator;
        this.nextCondition = nextCondition;
        return this;
    }
}