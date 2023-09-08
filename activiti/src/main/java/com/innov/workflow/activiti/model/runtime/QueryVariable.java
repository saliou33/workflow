package com.innov.workflow.activiti.model.runtime;

public class QueryVariable {
    private String name;
    private String operation;
    private Object value;
    private String type;

    public QueryVariable() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QueryVariableOperation getVariableOperation() {
        return this.operation == null ? null : QueryVariable.QueryVariableOperation.forFriendlyName(this.operation);
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum QueryVariableOperation {
        EQUALS("equals"),
        NOT_EQUALS("notEquals"),
        EQUALS_IGNORE_CASE("equalsIgnoreCase"),
        NOT_EQUALS_IGNORE_CASE("notEqualsIgnoreCase"),
        LIKE("like"),
        GREATER_THAN("greaterThan"),
        GREATER_THAN_OR_EQUALS("greaterThanOrEquals"),
        LESS_THAN("lessThan"),
        LESS_THAN_OR_EQUALS("lessThanOrEquals");

        private final String friendlyName;

        QueryVariableOperation(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public static QueryVariableOperation forFriendlyName(String friendlyName) {
            QueryVariableOperation[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                QueryVariableOperation type = arr[i];
                if (type.friendlyName.equals(friendlyName)) {
                    return type;
                }
            }

            return null;
        }

        public String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
