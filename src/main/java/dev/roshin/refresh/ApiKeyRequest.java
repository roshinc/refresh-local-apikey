package dev.roshin.refresh;

// Payload class for step 3
class ApiKeyRequest {
    String appName;
    String imageId;
    String appType;
    String roles;

    public ApiKeyRequest(String appName, String imageId, String appType, String roles) {
        this.appName = appName;
        this.imageId = imageId;
        this.appType = appType;
        this.roles = roles;
    }
}