package ch1mp.hagfish.utils;

import java.io.Serializable;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CrypterKey implements Serializable {

    private static final long serialVersionUID = 1986L;
    private byte[] password;
    private byte[] seed;

    CrypterKey(byte[] password, byte[] seed)
    {
        this.password = password;
        this.seed = seed;
    }

    SecretKey getKey()
    {
        return new SecretKeySpec(password, "AES");
    }

    IvParameterSpec getIV()
    {
        return new IvParameterSpec(seed);
    }

    public byte[] getPassword()
    {
        return password;
    }

    public byte[] getSeed()
    {
        return seed;
    }

    void setSeed(byte[] seed){ this.seed = seed; }
}
