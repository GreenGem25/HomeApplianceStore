package ohio.rizz.homeappliancestore.enums;

public enum OrderStatus {
    IN_PROGRESS("В сборке"),
    COMPLETED("Выполнен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}