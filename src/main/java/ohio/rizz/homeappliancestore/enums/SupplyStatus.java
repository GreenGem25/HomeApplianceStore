package ohio.rizz.homeappliancestore.enums;

public enum SupplyStatus {
    PENDING("Ожидание"),
    COMPLETED("Завершена"),
    CANCELLED("Отменена");

    private final String displayName;

    SupplyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}