package ch1mp.hagfish.exceptions;

public class PasswordException extends Exception {

    private String message;

    public PasswordException(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message + "\nSTACK TRACE: " + super.toString();
    }

    public String getMessage()
    {
        return message;
    }
}
