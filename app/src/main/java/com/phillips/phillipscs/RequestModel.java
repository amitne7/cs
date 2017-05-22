package com.phillips.phillipscs;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by surbhidhingra on 5/7/2016.
 */
public class RequestModel extends BaseModel implements Serializable {

    @JsonProperty("request_id")
    private String requestID;
    @JsonProperty("request_reference_number")
    private String requestNumber;
    @JsonProperty("request_status")
    private String status;
    @JsonProperty("request_date")
    private String date;
    @JsonProperty("customer_id")
    private String customerID;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("customer_phone")
    private String customerPhone;
    @JsonProperty("customer_email")
    private String customerEmail;
    @JsonProperty("customer_address")
    private String customerAddress;
    @JsonProperty("logistic_partner_id")
    private String logPartnerID;
    @JsonProperty("logistic_partner_company")
    private String logCompany;
    @JsonProperty("logistic_partner_email")
    private String logEmail;
    @JsonProperty("logistic_partner_phone")
    private String logPhone;
    @JsonProperty("ready_for_pickup_date")
    private String pickupDate;
    @JsonProperty("ready_for_pickup_time")
    private String pickupTime;
    @JsonProperty("logistic_partner_user_id")
    private String logUserId;
    @JsonProperty("movement_type")
    private String movementType;
    @JsonProperty("request_action_name")
    private String requestActionName;
    @JsonProperty("request_action_id")
    private int requestActionId;
    @JsonProperty("proof_of_status_required")
    private int proofOfStatusRequired;
    @JsonProperty("remark_for_delay_required")
    private int remarkForDelayRequired;
    @JsonProperty("request_status_id")
    private int requestStatusID;
    @JsonProperty("datetime_required")
    private int dateTimeRequired;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getLogPartnerID() {
        return logPartnerID;
    }

    public void setLogPartnerID(String logPartnerID) {
        this.logPartnerID = logPartnerID;
    }

    public String getLogCompany() {
        return logCompany;
    }

    public void setLogCompany(String logCompany) {
        this.logCompany = logCompany;
    }

    public String getLogEmail() {
        return logEmail;
    }

    public void setLogEmail(String logEmail) {
        this.logEmail = logEmail;
    }

    public String getLogPhone() {
        return logPhone;
    }

    public void setLogPhone(String logPhone) {
        this.logPhone = logPhone;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getLogUserId() {
        return logUserId;
    }

    public void setLogUserId(String logUserId) {
        this.logUserId = logUserId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getRequestActionName() {
        return requestActionName;
    }

    public void setRequestActionName(String requestActionName) {
        this.requestActionName = requestActionName;
    }

    public int getRequestActionId() {
        return requestActionId;
    }

    public void setRequestActionId(int requestActionId) {
        this.requestActionId = requestActionId;
    }

    public int getProofOfStatusRequired() {
        return proofOfStatusRequired;
    }

    public void setProofOfStatusRequired(int proofOfStatusRequired) {
        this.proofOfStatusRequired = proofOfStatusRequired;
    }

    public int getRemarkForDelayRequired() {
        return remarkForDelayRequired;
    }

    public void setRemarkForDelayRequired(int remarkForDelayRequired) {
        this.remarkForDelayRequired = remarkForDelayRequired;
    }

    public int getRequestStatusID() {
        return requestStatusID;
    }

    public void setRequestStatusID(int requestStatusID) {
        this.requestStatusID = requestStatusID;
    }

    public int getDateTimeRequired() {
        return dateTimeRequired;
    }

    public void setDateTimeRequired(int dateTimeRequired) {
        this.dateTimeRequired = dateTimeRequired;
    }
}
