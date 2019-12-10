package ch1mp.hagfish.utils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Crypter {

    private static final String TAG = "Crypter";
    private CrypterKey key;

    /**
     * Invoked when a fresh instance of Hagfish is being made
     * @param password
     */
    public Crypter(String password)
    {
        key = new CrypterKey(hashPassword(password), generateIvSeed());
    }

    /**
     * Invoked after passing key from MainActivity to AccountViewActivity
     * @param key
     */
    public Crypter(CrypterKey key)
    {
        this.key = key;
    }

    /**
     * Invoked when attempting to decrypt the vault
     * @param password
     * @param seed
     */
    public Crypter(String password, byte[] seed)
    {
        this.key = new CrypterKey(hashPassword(password), seed);
    }

    public Crypter scramble()
    {
        key.setSeed(generateIvSeed());
        return this;
    }

    public Vault decryptVault(byte[] encryptedVault)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key.getKey(), key.getIV());
            byte[] decryptedVault = cipher.doFinal(encryptedVault);
            ByteArrayInputStream bais = new ByteArrayInputStream(decryptedVault);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Vault) ois.readObject();
        }
        catch(Exception e)
        {
            Log.d(this.getClass().getSimpleName(), e.toString());
            return null;
        }
    }

    public byte[] encryptVault(Vault vault)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(vault);
            byte[] unencryptedVault = baos.toByteArray();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), key.getIV());
            return cipher.doFinal(unencryptedVault);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] generateIvSeed()
    {
        try
        {
            Random rand = new Random();
            byte[] ivSeed = new byte[Cipher.getInstance("AES/CBC/PKCS5Padding").getBlockSize()];
            for(int i=0; i<ivSeed.length; i++)
            {
                ivSeed[i] = (byte) rand.nextInt();
            }
            return ivSeed;
        }
        catch(NoSuchAlgorithmException e)
        {
            return null;
        }
        catch(NoSuchPaddingException e)
        {
            return null;
        }
    }

    private byte[] hashPassword(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return md.digest();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean changePassword(String password)
    {
        if(!key.getPassword().equals(hashPassword(password)))
        {
            key = new CrypterKey(hashPassword(password), generateIvSeed());
            return true;
        }
        return false;
    }

    public CrypterKey getKey()
    {
        return key;
    }

}
