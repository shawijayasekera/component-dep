package com.wso2telco.dep.operatorservice.newOperatorModels;

import java.util.List;

public class CreateProcessInstanceRequest {

    private String processDefinitionKey;

    private String businessKey;

    private List<Variable> variables;

    public CreateProcessInstanceRequest () {}

    public CreateProcessInstanceRequest (String processDefinitionKey, String tenantId) {
        this.processDefinitionKey = processDefinitionKey;
        //this.tenantId = tenantId;
    }


    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    /*public String getTenantId() {
        return tenantId;
    }*/

    /*public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }*/

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

}
