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

import ch1mp.hagfish.store.Vault;

/**
 * The Crypter class encrypts and decrypts the Vault using AES encryption.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 */
public class Crypter {

    private static final String TAG = "Crypter";
    private CrypterKey key;

    /**
     * Invoked when a fresh instance of Hagfish is being made
     * @param password - the password entered by the user
     */
    public Crypter(String password)
    {
        key = new CrypterKey(hashPassword(password), generateIvSeed());
    }

    /**
     * Invoked after passing key from MainActivity to AccountViewActivity
     * @param key - the key passed from MainActivity
     */
    public Crypter(CrypterKey key)
    {
        this.key = key;
    }

    /**
     * Invoked when attempting to decrypt the vault
     * @param password - the password entered by the current user
     * @param seed - the IV saved from the previous encryption
     */
    public Crypter(String password, byte[] seed)
    {
        this.key = new CrypterKey(hashPassword(password), seed);
    }

    /**
     * Generates a new IV seed. Called whenever a Memory is saved to ensure
     * that a different IV is used each time the vault is encrypted.
     *
     * @return - A Crypter with a new IV seed.
     */
    public Crypter scramble()
    {
        key.setSeed(generateIvSeed());
        return this;
    }

    /**
     * Attempts to decrypt the vault with the password provided by the user.
     * This is done by decrypting the byte[] array and reading a Vault object
     * from it.
     *
     * If the password is correct - a decrypted vault is returned.
     * If the password is incorrect - null is returned.
     * @param encryptedVault - the encrypted vault from the mem.dat file
     * @return - Vault (decrypted vault), or null (if wrong password)
     */
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

    /**
     * Attempts the encrypt the Vault. This is done by writing the Vault to a byte[]
     * array and then encrypting the byte[] array using AES encryption.
     *
     * @param vault - the Vault to be encrypted
     * @return - byte[] array (encrypted vault)
     */
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

    /**
     * Generates a randomized byte[] array with the necessary length for an AES block.
     * This is used to create an Initialization Vector.
     *
     * @return - byte[] array with randomized content
     */
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

    /**
     * Creates a SHA-256 hash of the user's password to ensure that
     * it is always the correct length for encryption/decryption.
     *
     * @param password - the user's password
     * @return - byte[] array (SHA-256 hash)
     */
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

    /**
     * Saves a new password and generates a new IV seed for the
     * current CypterKey.
     *
     * @param password - the user's password
     * @return - true if the password is different to the currently saved one
     */
    public boolean changePassword(String password)
    {
        if(!key.getPassword().equals(hashPassword(password)))
        {
            key = new CrypterKey(hashPassword(password), generateIvSeed());
            return true;
        }
        return false;
    }

    /*==============
    * Setter Methods
    * ==============*/
    public CrypterKey getKey()
    {
        return key;
    }

}
