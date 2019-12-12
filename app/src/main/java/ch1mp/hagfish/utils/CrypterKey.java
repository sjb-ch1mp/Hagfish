package ch1mp.hagfish.utils;

import java.io.Serializable;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class that holds the hashed password of the user and the
 * iv seed that was used in the most recent decryption. Allows this data to be easily
 * passed between activities as it is serializable.
 * Contains utility methods getKey() and getIV(), which return a SecretKey and Initialization Vector
 * for the Crypter to use in encryption and decryption.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 */
public class CrypterKey implements Serializable {

    private static final long serialVersionUID = 1986L;
    private byte[] password;
    private byte[] seed;

    CrypterKey(byte[] password, byte[] seed)
    {
        this.password = password;
        this.seed = seed;
    }

    /**
     * Utility method for creating a SecretKey for AES encryption using the current password hash.
     * @return - SecretKey
     */
    SecretKey getKey()
    {
        return new SecretKeySpec(password, "AES");
    }

    /**
     * Utility method for creating an Initialization Vector for AES encryption using the current
     * IV seed.
     * @return - IvParameterSpec
     */
    IvParameterSpec getIV()
    {
        return new IvParameterSpec(seed);
    }

    /*==============
    * Getter methods
    * ==============*/
    public byte[] getPassword()
    {
        return password;
    }
    public byte[] getSeed()
    {
        return seed;
    }

    /*==============
    * Setter Methods
    * ==============*/
    void setSeed(byte[] seed)
    {
        this.seed = seed;
    }
}
