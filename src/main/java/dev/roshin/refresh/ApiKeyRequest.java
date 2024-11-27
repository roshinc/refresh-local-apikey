package dev.roshin.refresh;

// Payload class for step 3
class ApiKeyRequest {
    private String appName;
    private String imageId;
    private String appType;
    private  String roles;

    public ApiKeyRequest(String appName, String imageId, String appType, String roles) {
        this.appName = appName;
        this.imageId = imageId;
        this.appType = appType;
        this.roles = roles;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}