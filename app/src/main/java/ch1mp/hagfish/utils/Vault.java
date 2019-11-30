package ch1mp.hagfish.utils;

import java.io.Serializable;
import java.util.HashSet;

public class Vault extends HashSet<Account> implements Serializable {

    private static final long serialVersionUID = 42L;

    public boolean addAccount(Account account)
    {
        return this.add(account);
    }

    public boolean deleteAccount(String accountName)
    {
        for(Account a : this)
        {
            if(a.getAccountName().equals(accountName))
                return remove(a);
        }
        return false;
    }

}
