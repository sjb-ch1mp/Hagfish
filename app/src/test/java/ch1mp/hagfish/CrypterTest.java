package ch1mp.hagfish;

import org.junit.Before;
import org.junit.Test;

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
        vault = TestUtilities.createVault();
    }

    @Test
    public void testCrypter()
    {
        Crypter crypter = new Crypter(PASSWORD);
        byte[] encryptedVault = crypter.encryptVault(vault);
        assertNotNull("encryptedVault is null", encryptedVault);
        Vault decryptedVault = crypter.decryptVault(encryptedVault);
        assertNotNull("decryptedVault is null", decryptedVault);
        assertTrue(TestUtilities.testVaultContents(decryptedVault, "StanDisney+Netflix"));

        //test changing passwords
        crypter.changePassword("newpassword");
        decryptedVault = crypter.decryptVault(encryptedVault);
        assertNull(decryptedVault);
        crypter.changePassword(PASSWORD);
        decryptedVault = crypter.decryptVault(encryptedVault);
        assertNotNull(decryptedVault);
        assertTrue(TestUtilities.testVaultContents(decryptedVault, "StanDisney+Netflix"));
    }

    @Test
    public void testVaultAddAndDelete()
    {
        assertTrue(TestUtilities.testVaultContents(vault, "StanDisney+Netflix"));
        vault.deleteAccount("Stan");
        assertTrue(TestUtilities.testVaultContents(vault, "Disney+Netflix"));
        vault.deleteAccount("Disney+");
        assertTrue(TestUtilities.testVaultContents(vault, "Netflix"));
        vault.deleteAccount("Netflix");
        assertTrue(TestUtilities.testVaultContents(vault, ""));
        vault.addAccount(new Account("Netflix",
                "username@domain.com",
                "password1234"));
        assertTrue(TestUtilities.testVaultContents(vault, "Netflix"));
        vault.addAccount(new Account("Stan",
                "username@domain.com",
                "password1234"));
        assertTrue(TestUtilities.testVaultContents(vault, "NetflixStan"));
        vault.addAccount(new Account("Disney+",
                "username@domain.com",
                "password1234"));
        assertTrue(TestUtilities.testVaultContents(vault, "NetflixStanDisney+"));
    }

}
