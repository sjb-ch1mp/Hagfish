package ch1mp.hagfish.store;

import java.io.Serializable;
import java.util.ArrayList;

import ch1mp.hagfish.exceptions.AccountException;

/**
 * The vault class is an ArrayList with utility methods that holds
 * Accounts.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class Vault extends ArrayList<Account> implements Serializable {

    private static final long serialVersionUID = 1986L;

    /**
     * Add a new account to the vault.
     * @param account - the Account to be added.
     * @throws AccountException - If the account already exists
     */
    public void addAccount(Account account) throws AccountException
    {
        if(!this.contains(account.getAccountName()))
        {
            this.add(account);
        }
        else throw new AccountException("Account already exists!");
    }

    /**
     * Find an account by its name and delete it.
     * @param accountName - the name of the Account to be deleted
     */
    public void deleteAccount(String accountName)
    {
        int idx = 0;
        while(!this.get(idx).getAccountName().equals(accountName))
            idx++;
        remove(idx);
    }

    /**
     * Find an account by its name and return it
     * @param accountName - the name of the desired Account
     * @return Account - the desired account
     * @throws AccountException - if the account does not exist
     */
    public Account getAccount(String accountName) throws AccountException
    {
        for(Account a : this)
        {
            if(a.getAccountName().equals(accountName))
                return a;
        }
        throw new AccountException("Account does not exist");
    }

    /**
     * Check whether an account exists in the vault
     * @param accountName - the Account being searched for.
     * @return boolean - true if account exists, false otherwise
     */
    public boolean contains(String accountName)
    {
        for(Account a : this)
        {
            if(a.getAccountName().equals(accountName))
                return true;
        }
        return false;
    }

    /**
     * Build a Vault from an ArrayList of Accounts.
     * @param accountList - the ArrayList of Accounts being transformed into a vault
     * @return Vault - the ArrayList of Accounts as a vault.
     */
    public static Vault retrieveVault(ArrayList<Account> accountList)
    {
        Vault vault = new Vault();
        vault.addAll(accountList);
        return vault;
    }

    /**
     * Sort the Accounts into alphabetical order.
     */
    public void alphabetize()
    {
        for(int i=0; i<this.size(); i++){
            for(int j=i; j<this.size(); j++){
                if(this.get(i).getAccountName().toUpperCase().compareTo(this.get(j).getAccountName().toUpperCase()) > 0){
                    Account hold = this.get(i);
                    this.set(i, this.get(j));
                    this.set(j, hold);
                }
            }
        }
    }


}
