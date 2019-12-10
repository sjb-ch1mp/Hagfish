package ch1mp.hagfish.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Generator class generates passwords.
 * It takes parameters that characterise the password (e.g. length, legal characters, etc.)
 */
public class Generator {

    private PasswordParameters passwordParameters;

    public Generator() {
        passwordParameters = new PasswordParameters();
    }

    public void updatePasswordParameters(PasswordParameters passwordParameters) {
        this.passwordParameters = passwordParameters;
    }

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

    private Character[] getLowerCase()
    {
        Character[] lc = new Character[26];
        for(int i=97; i<123; i++)
        {
            lc[i-97] = (char) i;
        }
        return lc;
    }

    private Character[] getUpperCase()
    {
        Character[] uc = new Character[26];
        for(int i=65; i<91; i++)
        {
            uc[i-65] = (char) i;
        }
        return uc;
    }

    private Character[] getNumeric()
    {
        Character[] num = new Character[10];
        for(int i=48; i<58; i++)
        {
            num[i - 48] = (char) i;
        }
        return num;
    }

    private Character[] getExtendedSpecialCharacters()
    {
        int len = (47 - 33)
                + (64 - 58)
                + (96 - 91)
                + (126 - 123);
        int idx = 0;
        Character[] esc = new Character[len];

        for(int i=33; i<48; i++) esc[idx++] = (char) i;
        for(int i=58; i<65; i++) esc[idx++] = (char) i;
        for(int i=91; i<97; i++) esc[idx++] = (char) i;
        for(int i=123; i<126; i++) esc[idx++] = (char) i;

        return esc;
    }
}
