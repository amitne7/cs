package com.phillips.phillipscs;

/**
 * Created by surbhidhingra on 5/4/2016.
 */
public class UserModel {

    private String userID;
    private String userName;
    private String userEmail;
    private String userRole;
    private String userRoleType;
    private String warehouseID;
    private String logisticsPartnerID;
    private String logisticsPartnerCompany;

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

    public String getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getLogisticsPartnerID() {
        return logisticsPartnerID;
    }

    public void setLogisticsPartnerID(String logisticsPartnerID) {
        this.logisticsPartnerID = logisticsPartnerID;
    }

    public String getLogisticsPartnerCompany() {
        return logisticsPartnerCompany;
    }

    public void setLogisticsPartnerCompany(String logisticsPartnerCompany) {
        this.logisticsPartnerCompany = logisticsPartnerCompany;
    }
}
