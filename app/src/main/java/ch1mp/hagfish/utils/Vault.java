package ch1mp.hagfish.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Vault implements Serializable {

    private static final long serialVersionUID = 42L;

    private ArrayList<Account> accounts;

    public Vault()
    {
        accounts = new ArrayList<>(0);
    }


}
