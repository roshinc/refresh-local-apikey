package dev.roshin.refresh;

// Payload class for steps 1 and 2
class AppPayload {
    String appName;
    String appType;
    String imageId;

    public AppPayload(String appName, String appType, String imageId) {
        this.appName = appName;
        this.appType = appType;
        this.imageId = imageId;
    }
}
