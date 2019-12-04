package ch1mp.hagfish.utils;

import java.io.Serializable;

public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1986L;
    private int maxAttempts;

    public UserPreferences()
    {
        maxAttempts = 3;
    }

    public void setMaxAttempts(int maxAttempts){ this.maxAttempts = maxAttempts; }
    public int getMaxAttempts(){ return maxAttempts;}
}
