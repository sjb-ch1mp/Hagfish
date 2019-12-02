package ch1mp.hagfish;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

import ch1mp.hagfish.exceptions.AccountException;
import ch1mp.hagfish.utils.Account;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.Vault;

public class AccountViewActivity extends AppCompatActivity {

    //FIXME: When the application loses focus for a given time (e.g. 2 minutes) - close it
    //FIXME: When the application closes - save the Memory and burn the vault and crypter

    Vault vault;
    Crypter crypter;
    Account activeAccount;

    Button buttonUserName;
    Button buttonPassword;
    TextView textAccountName;
    TextView textUserName;
    TextView textPassword;
    TextView textModified;
    View selectedAccount;
    PopupMenu popupUserName;
    PopupMenu popupPassword;
    RecyclerView accountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        //vault = getIntent().getParcelableExtra("Vault");
        //crypter = getIntent().getParcelableExtra("Crypter");

        accountList = findViewById(R.id.accountList);
        textAccountName = findViewById(R.id.textAccountName);
        textUserName = findViewById(R.id.textUserName);
        textPassword = findViewById(R.id.textPassword);
        textModified = findViewById(R.id.textModified);

        setUpButtonsAndPopups();
        setUpRecyclableView();
    }

    private void setUpButtonsAndPopups() {
        buttonUserName = findViewById(R.id.buttonUserName);
        popupUserName = new PopupMenu(AccountViewActivity.this, buttonUserName);
        buttonUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupUserName.show();
            }
        });
        popupUserName.getMenu().add("Change");
        popupUserName.getMenu().add("Copy");
        //FIXME: Set up OnMenuItemClickListeners for all popupUserName menu items

        buttonPassword = findViewById(R.id.buttonPassword);
        popupPassword = new PopupMenu(AccountViewActivity.this, buttonPassword);
        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupPassword.show();
            }
        });
        popupPassword.getMenu().add("Show");
        popupPassword.getMenu().add("Change");
        popupPassword.getMenu().add("Generate New");
        popupPassword.getMenu().add("Copy");
        //FIXME: Set up OnMenuItemClickListeners for all popupPassword menu items.

    }

    private void setUpRecyclableView() {

        ArrayList<View> focusables = new ArrayList<>(0);

        if(vault != null)
        {
            Iterator<Account> iterator = vault.iterator();

            while (iterator.hasNext()) {
                Account account = iterator.next();
                selectedAccount = new TextView(AccountViewActivity.this);
                ((TextView) selectedAccount).setText(account.getAccountName());
                selectedAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(buttonPassword.getVisibility() == View.INVISIBLE)
                        {
                            toggleAccountDetailsVisibility(false);
                        }

                        selectedAccount = view;
                        try
                        {
                            activeAccount = vault.getAccount(((TextView) selectedAccount).getText().toString());
                            showAccountDetails();
                        }
                        catch(AccountException e)
                        {
                            System.out.println(e);
                        }
                    }
                });
            }
            selectedAccount = null;
        }

        if (focusables.size() > 0) {
            accountList.addFocusables(focusables, View.FOCUS_UP);
            try {
                activeAccount = vault.getAccount(((TextView) focusables.get(0)).getText().toString());
                showAccountDetails();
            } catch (AccountException e) {
                System.out.println(e);
            }
        }
        else
        {
            //there are no accounts yet
            textAccountName.setText("Add an account.");
            toggleAccountDetailsVisibility(buttonPassword.getVisibility() == View.VISIBLE);
        }
    }

    private void toggleAccountDetailsVisibility(boolean currentlyVisible)
    {
        if(!currentlyVisible)
        {
            buttonPassword.setVisibility(View.VISIBLE);
            buttonUserName.setVisibility(View.VISIBLE);
            textUserName.setVisibility(View.VISIBLE);
            textPassword.setVisibility(View.VISIBLE);
            textModified.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonPassword.setVisibility(View.INVISIBLE);
            buttonUserName.setVisibility(View.INVISIBLE);
            textUserName.setVisibility(View.INVISIBLE);
            textPassword.setVisibility(View.INVISIBLE);
            textModified.setVisibility(View.INVISIBLE);
        }
    }

    private void showAccountDetails()
    {
        textAccountName.setText(activeAccount.getAccountName());
        textUserName.setText(activeAccount.getUserName());
        textPassword.setText(hidePassword());
        textModified.setText(activeAccount.getDatePasswordChanged());
    }

    private String hidePassword()
    {
        String hiddenPassword = "";
        for(char c : activeAccount.getPassword().toCharArray())
        {
            hiddenPassword += "*";
        }
        return hiddenPassword;
    }

    private void showPassword()
    {
        //FIXME: temporarily show the password

    }

    private void copyToClipBoard(Button buttonPressed)
    {
        //FIXME: copy the user name or password to the clipboard
    }
}
