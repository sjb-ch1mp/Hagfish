package ch1mp.hagfish.store;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ch1mp.hagfish.utils.Crypter;

/**
 * The Memory class holds an encrypted vault and tracks how many attempts have been made
 * to decrypt it. Constructors are private to ensure that only a single Memory exists at a time
 * (Singleton pattern).
 *
 * @author Samuel Brookes (sjb-ch1mp)
 *
 */
public class Memory implements Serializable {

    private static final long serialVersionUID = 1986L;
    private static final String TAG = "Memory";
    private static final String MEMORY = "mem.dat";
    private byte[] encryptedVault;
    private byte[] ivSeed;
    private int currentAttempts;
    private UserPreferences userPreferences;

    /**
     * Private constructor. Used in the event that there is no memory saved at all (i.e.
     * no mem.dat file).
     */
    private Memory()
    {
        currentAttempts = 0;
        encryptedVault = null;
        userPreferences = new UserPreferences();
    }

    /**
     * Private constructor. Used to create a new Memory that has the current
     * user preferences in the saveMemory() method.
     *
     * @param userPreferences - The current user preferences.
     */
    private Memory(UserPreferences userPreferences)
    {
        currentAttempts = 0;
        encryptedVault = null;
        this.userPreferences = userPreferences;
    }

    /**
     * Attempts to load the currently saved memory. If the file 'mem.dat' is not
     * found, then a new Memory is returned.
     *
     * @param context - MainActivity
     * @return Memory - the saved memory, or a new one.
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
     * @param userPreferences - the current user preferences
     * @return boolean - true if successful
     */
    public static boolean saveMemory(Context context, Vault vault, Crypter crypter, UserPreferences userPreferences)
    {
        Memory memory = new Memory(userPreferences);
        memory.ivSeed = crypter.getKey().getSeed();
        vault.alphabetize();

        try
        {
            memory.encryptedVault = crypter.encryptVault(vault);
            FileOutputStream fos = context.openFileOutput(MEMORY, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(memory);
            return true;
        }
        catch(Exception e)
        {
            Log.d(TAG, e.toString());
            return false;
        }
    }

    /**
     * This method is used to save the currently loaded memory on the log in screen.
     * This ensures that the login attempts are tracked.
     * @param context - MainActivity
     */
    public void saveMemory(Context context)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(MEMORY, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        }
        catch(Exception e)
        {
            Log.d(TAG, e.toString());
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
     * @return Vault (or null)
     */
    public Vault getVault(Crypter crypter)
    {
        if(encryptedVault == null) return null;

        currentAttempts++;

        if(currentAttempts > userPreferences.getMaxAttempts())
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

    /*=========================
    * Getter and Setter methods
    * =========================*/
    public int getRemainingAttempts(){ return userPreferences.getMaxAttempts() - currentAttempts; }
    public UserPreferences getUserPreferences(){ return userPreferences; }
    public boolean isNew(){ return encryptedVault == null; }
    public byte[] getSeed(){ return ivSeed; }

    /*===============
    * Testing methods
    * ===============*/
    /**
     * Overloaded getInstance method for testing purposes.
     *
     * @param fileName - the name of the file which is being loaded
     * @return Memory
     */
    public static Memory getInstance(String fileName)
    {
        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            return (Memory) objectInputStream.readObject();
        }
        catch(FileNotFoundException e)
        {
            //Log.d(TAG, MEMORY + " not found. Creating new Memory.");
            System.out.println("File not found. Creating new Memory");
            return new Memory();
        }
        catch(Exception e)
        {
            //Log.d(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Overloaded save method for testing
     * @param fileName - the name of the file being saved to
     * @param vault - the current vault
     * @param crypter - the current crypter
     * @return boolean - true if successful, false otherwise
     */
    public boolean saveMemory(String fileName, Vault vault, Crypter crypter)
    {
        try
        {
            encryptedVault = crypter.encryptVault(vault);
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            return true;
        }
        catch(Exception e)
        {
            //Log.d(TAG, e.toString());
            e.printStackTrace();
            return false;
        }
    }
}
