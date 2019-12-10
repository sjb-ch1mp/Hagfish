package ch1mp.hagfish.utils;

/**
 * The PasswordParameters class
 */
public class PasswordParameters {

    private int length;
    private boolean lowerCase;
    private boolean upperCase;
    private boolean numeric;
    private boolean extendedSpecialCharacters;
    private Character[] limitedSpecialCharacters;

    public PasswordParameters()
    {
        length = 15;
        lowerCase = true;
        upperCase = true;
        numeric = true;
        extendedSpecialCharacters = true;
        limitedSpecialCharacters = null;
    }

    public PasswordParameters(int length, boolean lc, boolean uc, boolean nums, boolean esc, String lsc)
    {
        this.length = length;
        lowerCase = lc;
        upperCase = uc;
        numeric = nums;
        extendedSpecialCharacters = esc;
        if(lsc.length() > 0)
        {
            limitedSpecialCharacters = new Character[lsc.length()];
            for(int i= 0; i<lsc.length(); i++)
            {
                limitedSpecialCharacters[i] = lsc.charAt(i);
            }
        }
    }

    //setters
    public void setLength(int length){
        this.length = length;
    }
    public void setLowerCase(boolean lowerCase){
        this.lowerCase = lowerCase;
    }
    public void setUpperCase(boolean upperCase){
        this.upperCase = upperCase;
    }
    public void setNumeric(boolean numeric){
        this.numeric = numeric;
    }
    public void setExtendedSpecialCharacters(boolean extended){
        extendedSpecialCharacters = extended;
    }
    public void setLimitedSpecialCharacters(Character[] limitedSpecialCharacters){
        this.limitedSpecialCharacters = limitedSpecialCharacters;
    }

    //getters
    public int getLength(){
        return length;
    }
    public boolean lowerCaseAllowed(){
        return lowerCase;
    }
    public boolean upperCaseAllowed(){
        return upperCase;
    }
    public boolean numericAllowed(){
        return numeric;
    }
    public boolean extendedSpecialCharactersAllowed(){
        return extendedSpecialCharacters;
    }
    public Character[] getLimitedSpecialCharacters(){
        return limitedSpecialCharacters;
    }
    public String getLimitedSpecialCharactersAsString()
    {
        if(limitedSpecialCharacters != null && limitedSpecialCharacters.length > 0)
        {
            StringBuilder lsc = new StringBuilder();
            for(Character c : limitedSpecialCharacters)
            {
                lsc.append(c);
            }
            return lsc.toString();
        }
        return "";
    }
}
