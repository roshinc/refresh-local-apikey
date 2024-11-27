package dev.roshin.refresh;

// Class representing an application
class App {
    String appName;
    String appType;
    String desc;
    String product;
    String updated;
    String updatedBy;

    // Default constructor for Gson
    public App() {}

    public App(String appName, String appType, String desc, String product, String updated, String updatedBy) {
        this.appName = appName;
        this.appType = appType;
        this.desc = desc;
        this.product = product;
        this.updated = updated;
        this.updatedBy = updatedBy;
    }
}
