package com.phillips.phillipscs;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by surbhidhingra on 5/7/2016.
 */
public class BaseModel {

    private int responseCode;
    @JsonProperty("error")
    private boolean error;
    @JsonProperty("error_msg")
    private String errorMessage;
    @JsonProperty("user_details")
    private LoginModel loginModel;
    @JsonProperty("request_data")
    private List<RequestModel> requestModels;
    @JsonProperty("request_detail")
    private RequestModel requestModel;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public void setLoginModel(LoginModel loginModel) {
        this.loginModel = loginModel;
    }

    public List<RequestModel> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(List<RequestModel> requestModels) {
        this.requestModels = requestModels;
    }

    public RequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(RequestModel requestModel) {
        this.requestModel = requestModel;
    }
}
