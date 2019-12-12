package ch1mp.hagfish.utils;

import java.util.ArrayList;
import java.util.Random;

import ch1mp.hagfish.store.PasswordParameters;

/**
 * The Generator class generates passwords IAW the password's parameters (e.g. length,
 * legal characters).
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 */
public class Generator {

    private PasswordParameters passwordParameters;

    /**
     * Constructor that uses user-defined password parameters
     * @param passwordParameters - the current password parameters for the account
     */
    public Generator(PasswordParameters passwordParameters) {
        this.passwordParameters = passwordParameters;
    }

    /**
     * Default constructor using default password parameters. Called when the
     * user creates an account without defining a password.
     */
    public Generator()
    {
        passwordParameters = new PasswordParameters();
    }

    /*=============
    * Setter method
    * =============*/
    public void updatePasswordParameters(PasswordParameters passwordParameters) {
        this.passwordParameters = passwordParameters;
    }

    /**
     * Generates a new password IAW the user-defined parameters. To do so, it:
     *  1. Builds an arrayList of arrays of legal characters
     *  2. For each character in the password:
     *      a. Randomly selects an array of legal characters, then
     *      b. Randomly selects a character from the array.
     * @return - An automatically generated password.
     */
    public String generatePassword() {

        StringBuilder password = new StringBuilder();
        ArrayList<Character[]> legalChars = getLegalChars();
        Random rand = new Random();

        while(password.length() < passwordParameters.getLength())
        {
            Character[] randomCharArray = legalChars.get(rand.nextInt(legalChars.size()));
            password.append(randomCharArray[rand.nextInt(randomCharArray.length)]);
        }

        return password.toString();
    }

    /**
     * Builds an ArrayList of arrays of legal characters IAW the user-defined password parameters
     *
     * @return - ArrayList of arrays of legal characters.
     */
    private ArrayList<Character[]> getLegalChars()
    {
        ArrayList<Character[]> legalChars = new ArrayList<>(0);

        if(passwordParameters.lowerCaseAllowed()) legalChars.add(getLowerCase());
        if(passwordParameters.upperCaseAllowed()) legalChars.add(getUpperCase());
        if(passwordParameters.numericAllowed()) legalChars.add(getNumeric());

        if(passwordParameters.extendedSpecialCharactersAllowed()) legalChars.add(getExtendedSpecialCharacters());
        else
        {
            if(passwordParameters.getLimitedSpecialCharacters() != null)
            {
                legalChars.add(passwordParameters.getLimitedSpecialCharacters());
            }
        }

        return legalChars;
    }

    /**
     * Builds a Character array of all lower case alphabetical characters to be
     * used by the generator.
     *
     * @return - Character[] (lower case letters)
     */
    private Character[] getLowerCase()
    {
        Character[] lc = new Character[26];
        for(int i=97; i<123; i++)
        {
            lc[i-97] = (char) i;
        }
        return lc;
    }

    /**
     * Builds a Character array of all upper case alphabetical characters to be
     * used by the generator.
     *
     * @return - Character[] (upper case letters)
     */
    private Character[] getUpperCase()
    {
        Character[] uc = new Character[26];
        for(int i=65; i<91; i++)
        {
            uc[i-65] = (char) i;
        }
        return uc;
    }

    /**
     * Builds a Characters array of all numbers to be used by the generator.
     *
     * @return - Character[] (numbers)
     */
    private Character[] getNumeric()
    {
        Character[] num = new Character[10];
        for(int i=48; i<58; i++)
        {
            num[i - 48] = (char) i;
        }
        return num;
    }

    /**
     * Builds a Character array of all ASCII characters that are:
     *  1. Not alphabetical
     *  2. Not numeric, and
     *  3. Not control characters.
     * @return - Character[] (special characters)
     */
    private Character[] getExtendedSpecialCharacters()
    {
        int len = (48 - 33)
                + (65 - 58)
                + (97 - 91)
                + (127 - 123);
        int idx = 0;
        Character[] esc = new Character[len];

        for(int i=33; i<48; i++) esc[idx++] = (char) i;
        for(int i=58; i<65; i++) esc[idx++] = (char) i;
        for(int i=91; i<97; i++) esc[idx++] = (char) i;
        for(int i=123; i<127; i++) esc[idx++] = (char) i;

        return esc;
    }
}
