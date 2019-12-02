package ch1mp.hagfish.utils;

import java.io.Serializable;
import java.util.HashSet;

import ch1mp.hagfish.exceptions.AccountException;

public class Vault extends HashSet<Account> implements Serializable {

    //FIXME: Implement Parcelable so that this can be passed from MainActivity to AccountViewActivity

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

    public Account getAccount(String accountName) throws AccountException
    {
        for(Account a : this)
        {
            if(a.getAccountName().equals(accountName))
                return a;
        }
        throw new AccountException("Account does not exist");
    }
}
