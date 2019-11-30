package ch1mp.hagfish.utils;

//import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypter {

    private static final String TAG = "Crypter";
    private SecretKey key;
    private IvParameterSpec iv;

    public Crypter(String password)
    {
        key = generateKey(password);
        iv = generateIV();
    }

    public Vault decryptVault(byte[] encryptedVault)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedVault = cipher.doFinal(encryptedVault);
            ByteArrayInputStream bais = new ByteArrayInputStream(decryptedVault);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Vault) ois.readObject();
        }
        catch(Exception e)
        {
            e.printStackTrace();
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
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(unencryptedVault);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void changePassword(String password)
    {
        key = generateKey(password);
    }

    private IvParameterSpec generateIV()
    {
        Random rand = new Random();
        try
        {
            byte[] iv = new byte[Cipher.getInstance("AES/CBC/PKCS5Padding").getBlockSize()];
            for(int i=0; i<iv.length; i++)
            {
                iv[i] = (byte) rand.nextInt();
            }
            return new IvParameterSpec(iv);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey generateKey(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            return new SecretKeySpec(md.digest(), "AES");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
