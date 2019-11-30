package ch1mp.hagfish.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The Memory class holds an encrypted vault and tracks how many attempts have been made
 * to decrypt it.
 *
 * @author Samuel Brookes (sjb-ch1mp)
 */
public class Memory implements Serializable {

    private static final String TAG = "Memory";
    private static final String MEMORY = "mem.dat";
    private int maxAttempts;
    private byte[] encryptedVault;
    private int currentAttempts;

    /**
     * Private constructor. Only called if there is no currently saved Memory.
     * Default maximum attempts is 5.
     */
    private Memory()
    {
        maxAttempts = 5;
        currentAttempts = 0;
        encryptedVault = null;
    }

    /**
     * Attempts to load the currently saved memory. If the file 'mem.dat' is not
     * found, then a new Memory is returned.
     *
     * @param context - the Context in which the method is being called
     * @return the saved memory, or a new Memory object
     */
    public static Memory getInstance(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput(MEMORY);
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            return (Memory) objectInputStream.readObject();
        }
        catch(FileNotFoundException e)
        {
            Log.d(TAG, MEMORY + " not found. Creating new Memory.");
            return new Memory();
        }
        catch(Exception e)
        {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    /**
     * Encrypts the vault, and saves this memory to the private file directory.
     * Returns true if successful, false if there was an exception.
     *
     * @param context - the Context in which this method is being called
     * @param vault - the current vault
     * @param crypter - the Crypter holding the user's password
     * @return boolean - true if successful
     */
    public boolean saveMemory(Context context, Vault vault, Crypter crypter)
    {
        try
        {
            encryptedVault = crypter.encryptVault(vault);
            FileOutputStream fos = context.openFileOutput(MEMORY, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            return true;
        }
        catch(Exception e)
        {
            Log.d(TAG, e.toString());
            return false;
        }
    }

    /**
     * Takes a password from the user and attempts to decrypt the encrypted Vault.
     * If the password is correct, the decrypted Vault is returned, otherwise this method returns
     * null to signify that the attempt failed.
     *
     * If the user's attempts exceed maxAttempts, the vault is deleted. If the user
     * is successful, currentAttempts resets to 0.
     *
     * @param crypter - the Crypter object that has stored the user's password.
     * @return Vault, or null
     */
    public Vault getVault(Crypter crypter)
    {
        currentAttempts++;
        if(currentAttempts > maxAttempts)
        {
            encryptedVault = null;
            return null;
        }
        else
        {
            Vault vault = crypter.decryptVault(encryptedVault);
            if(vault == null) return null;
            else
            {
                currentAttempts = 0;
                return vault;
            }
        }
    }

    //Getters and setters
    public void setMaxAttempts(int maxAttempts){ this.maxAttempts = maxAttempts; }
    public int getMaxAttempts(){ return maxAttempts; }
    public int getCurrentAttempts(){ return currentAttempts; }
    public int getRemainingAttempts(){ return maxAttempts - currentAttempts; }
}
