package com.wso2telco.dep.operatorservice.newOperatorModels;

import java.util.List;

public class ProcessInstanceData {

    private List<CreateProcessInstanceResponse> data;

    public List<CreateProcessInstanceResponse> getData() {
        return data;
    }

    public void setData(List<CreateProcessInstanceResponse> data) {
        this.data = data;
    }
}
