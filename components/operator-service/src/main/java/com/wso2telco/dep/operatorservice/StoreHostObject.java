/*******************************************************************************
 * Copyright  (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 * <p>
 * WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.operatorservice;

import java.sql.SQLException;
import java.util.*;

import com.wso2telco.dep.operatorservice.dao.WorkflowDAO;
import com.wso2telco.dep.operatorservice.exception.StoreHostObjectException;
import com.wso2telco.dep.operatorservice.model.*;
import com.wso2telco.dep.operatorservice.newOperatorModels.*;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import com.wso2telco.dep.operatorservice.dao.OperatorDAO;
import com.wso2telco.dep.operatorservice.service.OparatorService;
import org.wso2.carbon.apimgt.api.APIConsumer;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.apimgt.impl.workflow.*;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserStoreException;


import static org.wso2.carbon.apimgt.hostobjects.APIStoreHostObject.isStringArray;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;


public class StoreHostObject extends ScriptableObject {
	
	private static final String DEPLOYMENT_TYPE_SYSTEM_PARAM = "DEPLOYMENT_TYPE";

    /**
     *
     */
    private static final long serialVersionUID = 3642128192613608256L;

    /** The Constant log. */
    private static final Log log = LogFactory.getLog(StoreHostObject.class);

    /** The hostobject name. */
    private String hostobjectName = "StoreHostObject";

    private static final String TENANT_ID = "-1234";
    private static final String APPLICATION_CREATION_APPROVAL_PROCESS_NAME = "application_creation_approval_process";
    public static final String APPLICATION_NAME = "applicationName";
    private static final String APPLICATION_ID = "applicationId";
    private static final String WORKFLOW_REF_ID = "workflowRefId";
    private static final String CALL_BACK_URL = "callBackUrl";
    private static final String OPERATORS = "operators";
    private static final String DEPLOYMENT_TYPE = "deployment_type";

    public static final String TIER = "tier";
    private static final String DESCRIPTION = "description";
    private static final String TENANT_DOMAIN = "tenantDomain";
    public static final String USER_NAME = "userName";
    private static final String EXTERNAL_REFERENCE = "externalWorkflowReferenc";
    private static final String TIERS_STR = "tiersStr";
    private static final String ADMIN_USER = "adminUserName";
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String SERVICE_HOST = "service.host";
    private static final String SERVICE_URL = "serviceURL";
    private static final String MANDATE_SERVICE_HOST = "mandate.service.host";
    private static final String MANDATE_SERVICE_URL = "mandateServiceURL";
    private static final String OPERATOR_STATUS = "newOperatorList";
    private static final String NEW_OPERATOR_STATUS = "newOperatorTask";

    private static String serviceEndpoint;
    private static String username;
    private static String password;

    /*
     * (non-Javadoc)
     *
     * @see org.mozilla.javascript.ScriptableObject#getClassName()
     */
    @Override
    public String getClassName() {
        return hostobjectName;
    }

    /**
     * Instantiates a new api store host object.
     */
    public StoreHostObject() {
        if (log.isDebugEnabled()) {
            log.debug("Initialized HostObject StoreHostObject");
        }
    }

    /**
     *
     * @param cx
     * @param thisObj
     * @param args
     * @param funObj
     * @return
     * @throws StoreHostObjectException
     */
    public static List<Operator> jsFunction_retrieveOperatorList(Context cx,
                                                                 Scriptable thisObj,
                                                                 Object[] args,
                                                                 Function funObj) throws StoreHostObjectException {

        List<Operator> operatorList = null;

        try {
            OperatorSearchDTO searchDTO = new OperatorSearchDTO();
            operatorList = new OparatorService().loadOperators(searchDTO);

        } catch (Exception e) {
            handleException("Error occured while retrieving operator list. ", e);
        }

        return operatorList;
    }

    /**
     *
     * @param cx
     * @param thisObj
     * @param args
     * @param funObj
     * @return
     * @throws StoreHostObjectException
     */
    public static List<Operator> jsFunction_retrieveNewOperatorList(Context cx,
                                                                 Scriptable thisObj,
                                                                 Object[] args,
                                                                 Function funObj) throws StoreHostObjectException {

        Integer appId = (Integer)args[0];

        List<Operator> allOperators = null;
        List<Operator> existingAppOperators = null;
        List<Operator> newOperatorList = new ArrayList<Operator>();

        try {
            allOperators = new OparatorService().retrieveOperatorList();                        // get all the operatorsl
            existingAppOperators = new OparatorService().getAllApplicationOperators(appId);     // get all the operators

            boolean k;
            for(Operator opSet1 : allOperators){
                k=true;
                for(Operator opSet2 : existingAppOperators){
                    if(opSet1.getOperatorName().equals(opSet2.getOperatorName())){
                        k=false;
                    }
                }
                if(k)
                    newOperatorList.add(opSet1);
            }

        } catch (Exception e) {
            handleException("Error occured while retrieving operator list. ", e);
        }

        return newOperatorList;
    }


    /**
     *
     * @param cx
     * @param thisObj
     * @param args
     * @param funObj
     * @return
     * @throws StoreHostObjectException
     */
    public static String jsFunction_operatorApplicationApproval(Context cx,
                                                                    Scriptable thisObj,
                                                                    Object[] args,
                                                                    Function funObj) throws StoreHostObjectException, WorkflowException, UserStoreException {


        String status = null;
        if (args != null && args.length >= 9 && isStringArray(args)) {

            String appName = (String)args[0];
            String appId = (String)args[1];
            String usrName = (String)args[2];

            if (StringUtils.isEmpty(appName.trim())) {
                handleException("Application Name is empty.");
            }

            String tier1 = (String)args[3];
            if (StringUtils.isEmpty(tier1.trim())) {
                handleException("No tier is defined for the Application.");
            }

            String callbackUrl = (String)args[4];
            String appDescription = (String)args[5];
            String deploymentPattern = (String)args[6];
            String operatorList = (String) args[7];
            String tentDomain = (String) args[8];

            try {

                WorkflowExecutor appCreationWFExecutor = WorkflowExecutorFactory.getInstance().
                        getWorkflowExecutor(WorkflowConstants.WF_TYPE_AM_APPLICATION_CREATION);

                try{
                    username = appCreationWFExecutor.getClass().getMethod("getUsername").invoke(appCreationWFExecutor).toString();
                    password = appCreationWFExecutor.getClass().getMethod("getPassword").invoke(appCreationWFExecutor).toString();
                    serviceEndpoint = appCreationWFExecutor.getClass().getMethod("getServiceEndpoint").invoke(appCreationWFExecutor).toString();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                BusinessProcessApi api = Feign.builder()
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        //.errorDecoder(new WorkflowErrorDecoder())
                        .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                        .target(BusinessProcessApi.class, serviceEndpoint);

                ApiMgtDAO apiMgtDAO = ApiMgtDAO.getInstance();
                int applicationid = apiMgtDAO.getApplicationId(appName, usrName);

                CreateProcessInstanceRequest
                        processInstanceRequest = new CreateProcessInstanceRequest(APPLICATION_CREATION_APPROVAL_PROCESS_NAME,
                        TENANT_ID);
                processInstanceRequest.setBusinessKey(appCreationWFExecutor.generateUUID());

                // TODO: how to read 'deployment_type' / how to check if hub flow or hub-as-a-gateway flow??
                // currently this is read from a java system parameter
                APIConsumer consumer = APIManagerFactory.getInstance().getAPIConsumer();
                Set<Tier> tierSet = consumer.getTiers(APIConstants.TIER_APPLICATION_TYPE, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain(true));
                StringBuilder tiersStr = new StringBuilder();

                for (Iterator iterator = tierSet.iterator(); iterator.hasNext(); ) {
                    Tier tier = (Tier) iterator.next();
                    String tierName = tier.getName();
                    tiersStr.append(tierName + ',');
                }

                String ID = String.valueOf(appCreationWFExecutor.generateUUID());

                Map<String, String> workflowProperties = WorkflowProperties.loadWorkflowPropertiesFromXML();
                String serviceURLString = workflowProperties.get(SERVICE_HOST);
                String mandateServiceURLString = workflowProperties.get(MANDATE_SERVICE_HOST);

                Variable deploymentType = new Variable(DEPLOYMENT_TYPE, deploymentPattern);
                Variable applicationName = new Variable(APPLICATION_NAME, appName);
                Variable workflorRefId = new Variable(WORKFLOW_REF_ID, appCreationWFExecutor.generateUUID());
                Variable callBackUrl = new Variable(CALL_BACK_URL, appCreationWFExecutor.getCallbackURL());
                Variable applicationId = new Variable(APPLICATION_ID, String.valueOf(applicationid));  // this is not used for workflow
                Variable tier = new Variable(TIER, (String)args[3]);
                Variable description = new Variable(DESCRIPTION, appDescription);
                Variable tenantDomain = new Variable(TENANT_DOMAIN, tentDomain);
                Variable userName = new Variable(USER_NAME, (String)args[2]);
                Variable externalWorkflowReference = new Variable(EXTERNAL_REFERENCE, appCreationWFExecutor.generateUUID());
                Variable tiers = new Variable(TIERS_STR, tiersStr.toString());
                Variable serviceURL = new Variable(SERVICE_URL, serviceURLString);
                Variable mandateServiceURL = new Variable(MANDATE_SERVICE_URL, mandateServiceURLString);
                Variable adminUserName = new Variable(ADMIN_USER, CarbonContext
                        .getThreadLocalCarbonContext()
                        .getUserRealm()
                        .getRealmConfiguration().getAdminUserName());
                Variable adminPassword = new Variable(ADMIN_PASSWORD, CarbonContext
                        .getThreadLocalCarbonContext()
                        .getUserRealm()
                        .getRealmConfiguration().getAdminPassword());
                Variable operatorStatus = new Variable(OPERATOR_STATUS, NEW_OPERATOR_STATUS);


                Variable operators = new Variable(OPERATORS, operatorList.toLowerCase());
                if (operators == null) {
                    throw new WorkflowException("No operator(s) defined!!");
                }

                if (log.isDebugEnabled()) {
                    log.debug("Calling the operatorApplicationApproval in for newly onboarded operators");
                    log.debug(" deployment type: " + deploymentType + " applicationName: " + applicationName +
                            " workflow reference id: " + workflorRefId + " callback url: " + callBackUrl + " operators: " + operatorList +
                            " applicationId: " + applicationId + " tier: " + tier + " description: " + description +
                            " tenantDomain: " + tenantDomain + " userName: " + usrName + " externalWorkflowReference :" + externalWorkflowReference +
                            " tiers :" + tiers + " service endpoint: " + serviceURLString + " serviceURL: " + serviceURL +
                            " mandateServiceURL: " + mandateServiceURL + " adminUserName: " + adminUserName + " adminPassword: " + adminPassword +
                            " operatorStatus: " + operatorStatus );
                }

                List<Variable> variables = new ArrayList<Variable>();

                variables.add(deploymentType);
                variables.add(applicationName);
                variables.add(workflorRefId);
                variables.add(callBackUrl);
                variables.add(operators);
                variables.add(applicationId);
                variables.add(tier);
                variables.add(description);
                variables.add(tenantDomain);
                variables.add(userName);
                variables.add(externalWorkflowReference);
                variables.add(tiers);
                variables.add(serviceURL);
                variables.add(adminUserName);
                variables.add(adminPassword);
                variables.add(mandateServiceURL);
                variables.add(operatorStatus);
                processInstanceRequest.setVariables(variables);
                CreateProcessInstanceResponse processInstanceResponse;

                try {
                    processInstanceResponse = api.createProcessInstance(processInstanceRequest);
                } catch (WorkflowExtensionException e) {
                    throw new WorkflowException("WorkflowException: " + e.getMessage(), e);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Process definition url: " + processInstanceResponse.getProcessDefinitionUrl());
                }
                log.info("Application Creation approval process instance task with business key " +
                        appCreationWFExecutor.generateUUID() + " created successfully");

                status = "Success";
            }
            catch (APIManagementException e) {
                log.error("Error in obtaining APIConsumer", e);
                throw new WorkflowException("Error in obtaining APIConsumer", e);
            } catch (UserStoreException e) {
                log.error("Error in obtaining APIConsumer", e);
                throw new WorkflowException("Error in obtaining APIConsumer", e);
            }

        } else {
            handleException("Missing parameters.");
            status = "Failed";
        }

        return status;
    }



    /**
     *
     * @param cx
     * @param thisObj
     * @param args
     * @param funObj
     * @return
     * @throws StoreHostObjectException
     */
    public static boolean jsFunction_persistSubOperatorList(Context cx,
                                                            Scriptable thisObj,
                                                            Object[] args,
                                                            Function funObj) throws StoreHostObjectException {

        boolean status = false;

        String apiName = (String) args[0];
        String apiVersion = (String) args[1];
        String apiProvider = (String) args[2];
        int appId = ((Double) args[3]).intValue();
        String operatorList = (String) args[4];

        try {
            new OperatorDAO().persistOperators(apiName, apiVersion, apiProvider, appId, operatorList);

        } catch (Exception e) {
            handleException("Error occured while retrieving operator list. ", e);
        }

        return status;
    }
    
    public static String jsFunction_getDeploymentType(){
    	
    	return System.getProperty(DEPLOYMENT_TYPE_SYSTEM_PARAM, "hub");
    			
    }

    public static void jsFunction_removeAPISubscription(Context cx,
                                                        Scriptable thisObj,
                                                        Object[] args,
                                                        Function funObj) {
        OperatorDAO operatorDAO = new OperatorDAO();
        String applicationId = (String) args[0];
        String apiName= (String) args[1];
        try {
            operatorDAO.removeAPISubscription(applicationId,apiName);
        } catch (SQLException e) {
            log.error("database operation error in remove API Subscription : ", e);
        }
    }

    public static void jsFunction_removeAPISubscriptionFromStatDB(Context cx,
                                                                  Scriptable thisObj,
                                                                  Object[] args,
                                                                  Function funObj) {
        OperatorDAO operatorDAO = new OperatorDAO();
        String applicationId = (String) args[0];
        String apiName= (String) args[1];
        String version = (String) args[2];
        try {
            operatorDAO.removeAPISubscriptionFromStatDB(applicationId,apiName,version);
        } catch (SQLException e) {
            log.error("database operation error in remove API Subscription : ", e);
        }
    }

    public static void jsFunction_removeApplication(Context cx,
                                                    Scriptable thisObj,
                                                    Object[] args,
                                                    Function funObj) {
        OperatorDAO operatorDAO = new OperatorDAO();
        String applicationId = (String) args[0];
        try {
            operatorDAO.removeApplication(applicationId);
        } catch (SQLException e) {
            log.error("database operation error in remove application : ", e);
        }
    }

    public static void jsFunction_removeSubApprovalOperators(Context cx,
                                                    Scriptable thisObj,
                                                    Object[] args,
                                                    Function funObj) {
        OperatorDAO operatorDAO = new OperatorDAO();
        String applicationId = (String) args[0];
        try {
            operatorDAO.removeSubApprovalOperators(applicationId);
        } catch (SQLException e) {
            log.error("database operation error in remove application : ", e);
        }
    }

    public static WorkflowReferenceDTO jsFunction_getWorkflowRef(Context cx,
                                                      Scriptable thisObj,
                                                      Object[] args,
                                                      Function funObj){
        WorkflowReferenceDTO workflow=null;
        WorkflowDAO workflowDAO=new WorkflowDAO();
        String apiName = (String) args[1];
        String applicationId = (String) args[0];
        String apiVersion = (String) args[2];
        try {
            workflow=workflowDAO.findWorkflow(apiName, applicationId, apiVersion);
        } catch (Exception e) {
            log.error("database operation error in get workflow ref : ", e);
        }
        return workflow;

    }

    public static boolean jsFunction_getAppStatus(Context cx,Scriptable thisObj,Object[] args,Function funObj){

        String appId = (String) args[0];
        String operator = (String) args[1];
        boolean status=false;
        WorkflowDAO workflowDAO=new WorkflowDAO();

        try {
            status=workflowDAO.operatorAppsIsActive(Integer.parseInt(appId),operator);
        } catch (Exception e) {
            log.error("database operation error in get workflow ref : ", e);
        }
        return status;

    }
    
    public static NativeObject jsFunction_getOperatorApprovedSubscriptionsByApplicationId(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) throws StoreHostObjectException {

		NativeObject resultObject = new NativeObject();
		NativeArray historyArray = new NativeArray(0);

		String appId = args[0].toString();

		OparatorService oparatorService = new OparatorService();

        if(jsFunction_getDeploymentType().equals("hub") || jsFunction_getDeploymentType().equals("external_gateway")){
            try {

                Map<Integer, Map<String, Map<String,String>>> subDetails = oparatorService.getOperatorApprovedSubscriptionsByApplicationId(Integer.parseInt(appId));
                log.debug("getOperatorApprovedSubscriptionsByApplicationId : " + subDetails);

                if (!subDetails.isEmpty()) {

                    int j = 0;
                    for (Map.Entry<Integer, Map<String, Map<String,String>>> sub : subDetails.entrySet()) {

                        Map<String, Map<String,String>> subInfo = sub.getValue();

                        NativeArray historyDataArray = new NativeArray(0);
                        int z = 0;

                        for (Map.Entry<String, Map<String,String>> sb : subInfo.entrySet()) {

                            String apiName = sb.getKey();
                            Map<String,String> s = sb.getValue();

                            NativeObject subData = new NativeObject();
                            subData.put("apiName", subData, apiName);
                            subData.put("substatus", subData, s.get("substatus"));
                            subData.put("operatorname", subData, s.get("operatorname"));

                            historyDataArray.put(z, historyDataArray, subData);
                            z++;
                        }

                        historyArray.put(j, historyArray, historyDataArray);
                        j++;
                    }
                } else {

                    log.debug("subscription details unavalible for application id : " + appId);
                }
            } catch (Exception e) {

                log.error("error occurred in getOperatorApprovedSubscriptionsByApplicationId : ", e);
                handleException(e.getMessage(), e);
            }

        }

		resultObject.put("operatorSubsApprovedHistory", resultObject, historyArray);

		return resultObject;
	}

    /**
     * Handle exception.
     *
     * @param msg
     *            the msg
     * @throws StoreHostObjectException
     *             the API management exception
     */
    private static void handleException(String msg) throws StoreHostObjectException {
        log.error(msg);
        throw new StoreHostObjectException(msg);
    }

    /**
     * Handle exception.
     *
     * @param msg
     *            the msg
     * @param t
     *            the t
     * @throws StoreHostObjectException
     *             the API management exception
     */
    private static void handleException(String msg, Throwable t) throws StoreHostObjectException {
        log.error(msg, t);
        throw new StoreHostObjectException(msg, t);
    }
}
