package com.phillips.phillipscs;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by surbhidhingra on 5/7/2016.
 */
public class LoginModel extends BaseModel{

    @JsonProperty("user_id")
    private String userID;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_email")
    private String userEmail;
    @JsonProperty("user_role")
    private String userRole;
    @JsonProperty("user_role_type")
    private String userRoleType;
    @JsonProperty("warehouse_id")
    private String wareHouseID;
    @JsonProperty("logistic_partner_id")
    private String partnerId;
    @JsonProperty("logistic_partner_company")
    private String partnerCompany;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(String userRoleType) {
        this.userRoleType = userRoleType;
    }

    public String getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(String wareHouseID) {
        this.wareHouseID = wareHouseID;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerCompany() {
        return partnerCompany;
    }

    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }
}
