package ch1mp.hagfish.utils;

//import android.util.Log;

import java.security.MessageDigest;

public class Crypter {

    private static final String TAG = "Crypter";
    private byte[] password;

    public Crypter(String password)
    {
        this.password = hashPassword(password);
    }

    public Vault decryptVault(byte[] encryptedVault)
    {
        return null;
    }

    public byte[] encryptVault(Vault decryptedVault)
    {
        return null;
    }

    public void changePassword(String password)
    {
        this.password = hashPassword(password);
    }

    public byte[] hashPassword(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA256");
            md.update(password.getBytes());
            return md.digest();
        }
        catch(Exception e)
        {
         //   Log.d(TAG, e.toString());
            return null;
        }
    }
}
