package ch1mp.hagfish.utils;

/**
 * The Generator class generates passwords.
 * It takes parameters that characterise the password (e.g. length, legal characters, etc.)
 */
public class Generator {

    private PasswordParameters passwordParameters;

    public Generator(PasswordParameters passwordParameters)
    {
        this.passwordParameters = passwordParameters;
    }

    public Generator()
    {
        this.passwordParameters = null;
    }

    public void updatePasswordParameters(PasswordParameters passwordParameters)
    {
        this.passwordParameters = passwordParameters;
    }

    public String generatePassword()
    {
        if(passwordParameters == null)
        {
            return "passwordWithoutParameters";
        }
        else
        {
            return "passwordWithParameters";
        }
    }
}
