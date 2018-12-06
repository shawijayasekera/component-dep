package com.wso2telco.dep.operatorservice.newOperatorModels;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BusinessProcessApi {
    @RequestLine("POST runtime/process-instances")
    @Headers("Content-Type: application/json")
    CreateProcessInstanceResponse createProcessInstance(CreateProcessInstanceRequest
                                                                createProcessInstanceRequest) throws WorkflowExtensionException;

    @RequestLine("GET runtime/process-instances?businessKey={businessKey}")
    ProcessInstanceData getProcessInstances(@Param("businessKey") String businessKey) throws WorkflowExtensionException;

    @RequestLine("DELETE runtime/process-instances/{processInstanceId}")
    void deleteProcessInstance(@Param("processInstanceId") String processInstanceId) throws WorkflowExtensionException;

}
