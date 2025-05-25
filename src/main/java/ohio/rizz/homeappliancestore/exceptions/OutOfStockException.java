package ohio.rizz.homeappliancestore.exceptions;

public class OutOfStockException extends RuntimeException{
    public OutOfStockException(String message){
        super(message);
    }
}
