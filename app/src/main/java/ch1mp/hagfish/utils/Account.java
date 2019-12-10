package ch1mp.hagfish.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * The Account class stores all the data associated with a given account.
 * It is the central unit for Hagfish.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 1986L;
    private String accountName;
    private String userName;
    private String password;
    private Date datePasswordChanged;
    private PasswordParameters passwordParameters;

    public Account(String accountName, String userName, String password)
    {
        this.accountName = accountName;
        this.userName = userName;
        this.password = password;
        datePasswordChanged = new Date();
        passwordParameters = new PasswordParameters();
    }

    //getters
    public String getAccountName(){ return accountName; }
    public String getUserName(){ return userName; }
    public String getPassword(){ return password; }
    public String getDatePasswordChanged(){ return datePasswordChanged.toString(); }
    public PasswordParameters getPasswordParameters(){ return passwordParameters; }

    //setters - whenever the password is changed, update the datePasswordChanged field
    public void changeAccountName(String accountName){ this.accountName = accountName; }
    public void changeUserName(String userName){ this.userName = userName; }
    public void changePassword(String password)
    {
        this.password = password;
        datePasswordChanged = new Date();
    }
    public void setPasswordParameters(PasswordParameters passwordParameters)
    {
        this.passwordParameters = passwordParameters;
    }
}
