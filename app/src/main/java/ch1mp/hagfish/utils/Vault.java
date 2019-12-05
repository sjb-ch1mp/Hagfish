package ch1mp.hagfish.utils;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import ch1mp.hagfish.exceptions.AccountException;

public class Vault extends ArrayList<Account> implements Serializable {

    //FIXME: Implement Parcelable so that this can be passed from MainActivity to AccountViewActivity

    private static final long serialVersionUID = 1986L;

    public void addAccount(Account account) throws AccountException
    {
        if(!this.contains(account.getAccountName()))
        {
            this.add(account);
        }
        else throw new AccountException("Account already exists!");
    }

    public void deleteAccount(String accountName)
    {
        int idx = 0;
        while(!this.get(idx).getAccountName().equals(accountName))
            idx++;
        remove(idx);
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

    public boolean contains(String accountName)
    {
        for(Account a : this)
        {
            if(a.getAccountName().equals(accountName))
                return true;
        }
        return false;
    }

    public static Vault retrieveVault(Intent intent)
    {
        Vault vault = new Vault();
        Account account;
        int idx = 0;

        while((account = (Account) intent.getSerializableExtra("account" + idx)) != null)
        {
            vault.add(account);
        }

        return vault;
    }
}
