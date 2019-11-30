package ch1mp.hagfish;

import java.util.Iterator;

import ch1mp.hagfish.utils.Account;
import ch1mp.hagfish.utils.Vault;

public class TestUtilities {

    public static Vault createVault()
    {
        Vault vault = new Vault();
        vault.addAccount(new Account("Netflix",
                "username@domain.com",
                "password1234"));
        vault.addAccount(new Account("Stan",
                "username@domain.com",
                "password1234"));
        vault.addAccount(new Account("Disney+",
                "username@domain.com",
                "password1234"));
        return vault;
    }

    public static boolean testVaultContents(Vault vault, String expectedContents)
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
