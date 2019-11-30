package ch1mp.hagfish.utils;

public class Crypter {

    private byte[] password;

    public Crypter(byte[] password)
    {
        this.password = password;
    }

    public Vault decryptVault(byte[] encryptedVault)
    {
        return null;
    }

    public byte[] encryptVault(Vault decryptedVault)
    {
        return null;
    }

    public void changePassword(byte[] password)
    {
        this.password = password;
    }
}
