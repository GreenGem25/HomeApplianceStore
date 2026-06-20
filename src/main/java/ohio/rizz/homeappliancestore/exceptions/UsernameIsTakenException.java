package ohio.rizz.homeappliancestore.exceptions;

public class UsernameIsTakenException extends RuntimeException{
    public UsernameIsTakenException(){
        super("Пользователь с таким логином уже существует");
    }
}
