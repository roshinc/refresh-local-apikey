package dev.roshin.refresh;

// Payload class for steps 1 and 2
class AppPayload {
    private String appName;
    private String appType;
    private String imageId;

    public AppPayload(String appName, String appType, String imageId) {
        this.appName = appName;
        this.appType = appType;
        this.imageId = imageId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
