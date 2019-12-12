package ch1mp.hagfish.store;

import java.io.Serializable;

/**
 *
 * The preferences of the user regarding:
 *  1. How many attempts are allowed before the vault is deleted,
 *  2. How long the application can idle before auto-logout,
 *  3. How long the password is shown when it is clicked.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1986L;
    private int maxAttempts;
    private int maxIdle;
    private int maxPasswordShowTime;

    UserPreferences()
    {
        maxAttempts = 3;
        maxIdle = 60000; //milliseconds (1 min)
        maxPasswordShowTime = 1000; //milliseconds (1 sec)
    }

    /*==============
     * Setter methods
     * ==============*/
    public void setMaxAttempts(int maxAttempts){ this.maxAttempts = maxAttempts; }
    public void setMaxIdle(int maxIdle){ this.maxIdle = maxIdle; }
    public void setMaxPasswordShowTime(int maxPasswordShowTime){ this.maxPasswordShowTime = maxPasswordShowTime; }

    /*==============
     * Getter methods
     * ==============*/
    public int getMaxAttempts(){ return maxAttempts;}
    public int getMaxIdle(){ return maxIdle; }
    public int getMaxPasswordShowTime(){ return maxPasswordShowTime; }

}
