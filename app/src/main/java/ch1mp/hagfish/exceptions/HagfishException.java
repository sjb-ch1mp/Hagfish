package ch1mp.hagfish.exceptions;

public abstract class HagfishException extends Exception {
    private String message;

    public HagfishException(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
