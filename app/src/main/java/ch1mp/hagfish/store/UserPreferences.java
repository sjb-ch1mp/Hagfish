package ch1mp.hagfish.store;

import java.io.Serializable;

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

    public void setMaxAttempts(int maxAttempts){ this.maxAttempts = maxAttempts; }
    public void setMaxIdle(int maxIdle){ this.maxIdle = maxIdle; }
    public void setMaxPasswordShowTime(int maxPasswordShowTime){ this.maxPasswordShowTime = maxPasswordShowTime; }

    public int getMaxAttempts(){ return maxAttempts;}
    public int getMaxIdle(){ return maxIdle; }
    public int getMaxPasswordShowTime(){ return maxPasswordShowTime; }

}