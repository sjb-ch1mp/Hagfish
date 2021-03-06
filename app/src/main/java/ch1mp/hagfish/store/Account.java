package ch1mp.hagfish.store;

import java.io.Serializable;
import java.util.Date;

import ch1mp.hagfish.exceptions.PasswordException;

/**
 * The Account class stores all the data associated with a given account.
 * It is the central unit for Hagfish.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class Account implements Serializable{

    private static final long serialVersionUID = 1986L;
    private String accountName;
    private String userName;
    private String password;
    private Date datePasswordChanged;
    private PasswordParameters passwordParameters;
    private String previousPassword;

    public Account(String accountName, String userName, String password)
    {
        this.accountName = accountName;
        this.userName = userName;
        this.password = password;
        datePasswordChanged = new Date();
        passwordParameters = new PasswordParameters();
    }

    /*==============
    * Getter methods
    * ==============*/
    public String getAccountName() { return accountName; }
    public String getUserName(){ return userName; }
    public String getPassword(){ return password; }
    public String getDatePasswordChanged(){ return datePasswordChanged.toString(); }
    public PasswordParameters getPasswordParameters(){ return passwordParameters; }
    public boolean hasPreviousPassword(){ return previousPassword != null; }

    /*==============
    * Setter methods
    * ==============*/
    public void changeAccountName(String accountName){ this.accountName = accountName; }
    public void changeUserName(String userName){ this.userName = userName; }
    public void setPasswordParameters(PasswordParameters passwordParameters){ this.passwordParameters = passwordParameters; }
    public void changePassword(String password)
    {
        /*
        * Whenever the password is changed -
        *   1. save the previous password, and
        *   2. save the current date-time.
        * */

        previousPassword = this.password;
        this.password = password;
        datePasswordChanged = new Date();
    }

    /**
     * Restore the previous password (deleting the current password in the process).
     *
     * @throws PasswordException - if there is no previous password.
     */
    public void restorePreviousPassword() throws PasswordException
    {
        if(previousPassword != null)
        {
            password = previousPassword;
            previousPassword = null;
        }
        else throw new PasswordException("There is no previous password");
    }
}
