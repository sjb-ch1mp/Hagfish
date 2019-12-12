package ch1mp.hagfish.exceptions;

/**
 * Abstract class for any Hagfish-specific exceptions.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
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
