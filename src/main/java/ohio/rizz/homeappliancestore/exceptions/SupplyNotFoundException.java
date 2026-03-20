package ohio.rizz.homeappliancestore.exceptions;

public class SupplyNotFoundException extends RuntimeException {
    public SupplyNotFoundException(String message) {
        super(message);
    }
}