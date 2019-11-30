package ch1mp.hagfish;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

import ch1mp.hagfish.utils.Account;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.Vault;

public class CrypterTest {

    private static final String PASSWORD = "password1234";
    private Vault vault;

    @Before
    public void setUpVault()
    {
        vault = new Vault();
        vault.addAccount(new Account("Netflix",
                "username@domain.com",
                "password1234"));
        vault.addAccount(new Account("Stan",
                "username@domain.com",
                "password1234"));
        vault.addAccount(new Account("Disney+",
                "username@domain.com",
                "password1234"));
    }

    @Test
    public void testCrypter()
    {
        Crypter crypter = new Crypter(PASSWORD);
        byte[] encryptedVault = crypter.encryptVault(vault);
        assertNotNull("encryptedVault is null", encryptedVault);
        Vault decryptedVault = crypter.decryptVault(encryptedVault);
        assertNotNull("decryptedVault is null", decryptedVault);
        assertTrue(testVaultContents(decryptedVault, "StanDisney+Netflix"));
    }

    @Test
    public void testVaultAddAndDelete()
    {
        assertTrue(testVaultContents(vault, "StanDisney+Netflix"));
        vault.deleteAccount("Stan");
        assertTrue(testVaultContents(vault, "Disney+Netflix"));
        vault.deleteAccount("Disney+");
        assertTrue(testVaultContents(vault, "Netflix"));
        vault.deleteAccount("Netflix");
        assertTrue(testVaultContents(vault, ""));
        vault.addAccount(new Account("Netflix",
                "username@domain.com",
                "password1234"));
        assertTrue(testVaultContents(vault, "Netflix"));
        vault.addAccount(new Account("Stan",
                "username@domain.com",
                "password1234"));
        assertTrue(testVaultContents(vault, "NetflixStan"));
        vault.addAccount(new Account("Disney+",
                "username@domain.com",
                "password1234"));
        assertTrue(testVaultContents(vault, "NetflixStanDisney+"));
    }

    private boolean testVaultContents(Vault vault, String expectedContents)
    {
        Iterator<Account> iterator = vault.iterator();

        if(!iterator.hasNext()) return expectedContents.equals("");

        while(iterator.hasNext())
        {
            if(!expectedContents.contains(iterator.next().getAccountName()))
                return false;
        }
        return true;
    }
}
