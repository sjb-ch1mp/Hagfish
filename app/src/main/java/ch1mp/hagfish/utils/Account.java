package ch1mp.hagfish.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * The Account class stores all the data associated with a given account.
 * It is the central unit for Hagfish.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 42L;
    private String accountName;
    private String userName;
    private String password;
    private Date datePasswordChanged;
    private ArrayList<PreviousPassword> previousPasswords;

    public Account(String accountName, String userName, String password)
    {
        this.accountName = accountName;
        this.userName = userName;
        this.password = password;
        datePasswordChanged = new Date();
        previousPasswords = new ArrayList<>(0);
    }

    //getters
    public String getAccountName(){ return accountName; }
    public String getUserName(){ return userName; }
    public String getPassword(){ return password; }
    public String getDatePasswordChanged(){ return datePasswordChanged.toString(); }

    //setters - whenever the password is changed, update the datePasswordChanged field
    public void changeAccountName(String accountName){ this.accountName = accountName; }
    public void changeUserName(String userName){ this.userName = userName; }
    public void changePassword(String password)
    {
        previousPasswords.add(new PreviousPassword(this.password));
        this.password = password;
        datePasswordChanged = new Date();
    }
    public void generateNewPassword(PasswordParameters passwordParameters)
    {
        Generator generator = new Generator(passwordParameters);
        password = generator.generatePassword();
        datePasswordChanged = new Date();
    }

    class PreviousPassword
    {
        String password;
        Date dateRetired;

        PreviousPassword(String password)
        {
            this.password = password;
            dateRetired = new Date();
        }

        public String getPassword(){ return password; }
        public Date getDateRetired(){ return dateRetired; }
    }
}
