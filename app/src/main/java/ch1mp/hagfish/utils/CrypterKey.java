package ch1mp.hagfish.utils;

import java.io.Serializable;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CrypterKey implements Serializable {

    private static final long serialVersionUID = 1986L;
    private byte[] password;
    private byte[] seed;

    public CrypterKey(byte[] password, byte[] seed)
    {
        this.password = password;
        this.seed = seed;
    }

    public SecretKey getKey()
    {
        return new SecretKeySpec(password, "AES");
    }

    public IvParameterSpec getIV()
    {
        return new IvParameterSpec(seed);
    }

    byte[] getPassword()
    {
        return password;
    }

    byte[] getSeed()
    {
        return seed;
    }
}
