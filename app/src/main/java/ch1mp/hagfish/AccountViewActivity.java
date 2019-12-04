package ch1mp.hagfish;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch1mp.hagfish.utils.Account;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.Memory;
import ch1mp.hagfish.utils.UserPreferences;
import ch1mp.hagfish.utils.Vault;

public class AccountViewActivity extends AppCompatActivity {

    //FIXME: When the application loses focus for a given time (e.g. 2 minutes) - close it
    //FIXME: When the application closes - save the Memory and burn the vault and crypter

    Vault vault;
    Crypter crypter;
    Account activeAccount;
    Toolbar mainToolbar;
    TextView textAccountName;
    PopupMenu accountMenu;
    TextView textUserName;
    TextView textPassword;
    TextView textModified;
    View selectedAccount;
    RecyclerView accountList;
    Toast debugToast;
    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        mainToolbar = findViewById(R.id.toolbarAccountView);
        setSupportActionBar(mainToolbar);

        vault = Vault.retrieveVault(getIntent());
        crypter = new Crypter(
                getIntent().getByteArrayExtra("password"),
                getIntent().getByteArrayExtra("seed")
        );
        userPreferences = (UserPreferences) getIntent().getSerializableExtra("userprefs");

        textAccountName = findViewById(R.id.textAccountName);
        accountMenu = new PopupMenu(AccountViewActivity.this, textAccountName);
        MenuInflater inflater = accountMenu.getMenuInflater();
        inflater.inflate(R.menu.account_menu, accountMenu.getMenu());
        accountMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.account_un_change:
                        changeUserName();
                        return true;
                    case R.id.account_pw_change:
                        changeAccountPassword();
                        return true;
                    case R.id.account_pw_generate:
                        generateNewAccountPassword();
                        return true;
                    case R.id.account_delete:
                        deleteAccount();
                        return true;
                    default:
                        return false;
                }
            }
        });

        textUserName = findViewById(R.id.textUserName);
        textPassword = findViewById(R.id.textPassword);
        textModified = findViewById(R.id.textModified);

        accountList = findViewById(R.id.accountList);
        setUpButtonsAndPopups();
        setUpRecyclableView();

        debugToast = new Toast(AccountViewActivity.this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
        builder.setMessage(R.string.log_out_prompt);
        builder.setPositiveButton(R.string.log_out_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Memory.saveMemory(AccountViewActivity.this, vault, crypter, userPreferences);
                vault = null;
                crypter = null;
                AccountViewActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.log_out_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_item_new_account:
                addNewAccount();
                return true;
            case R.id.menu_item_login:
                changeLogInAttempts();
                return true;
            case R.id.menu_item_change_hagfish_pw:
                changeHagfishPassword();
                return true;
            case R.id.menu_item_delete_vault:
                destroyVault();
                return true;
            case R.id.menu_item_about:
                aboutHagfish();
                return true;
            default:
                return false;
        }
    }

    private void setUpButtonsAndPopups() {

        textUserName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipBoard((TextView) view);
                return true;
            }
        });

        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassword();
            }
        });

        textPassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipBoard((TextView) view);
                return true;
            }
        });

        textAccountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textAccountName.getText().toString().equals(getString(R.string.no_accounts)))
                {
                    accountMenu.show();
                }
            }
        });

    }

    private void setUpRecyclableView() {

        //FIXME: need to figure this one out - use an adaptor?



    }

    private void toggleAccountDetailsVisibility(boolean currentlyVisible)
    {
        if(!currentlyVisible)
        {
            textUserName.setVisibility(View.VISIBLE);
            textPassword.setVisibility(View.VISIBLE);
            textModified.setVisibility(View.VISIBLE);
        }
        else
        {
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
        showSelectedOption("Showing password");
    }

    private void copyToClipBoard(TextView viewPressed)
    {
        //FIXME: copy the user name or password to the clipboard
        showSelectedOption("Copying to clipboard");
    }

    private void changeUserName()
    {
        //FIXME: change the user name
        showSelectedOption("Changing user name");
    }

    private void changeAccountPassword()
    {
        //FIXME: change the password for the account
        showSelectedOption("Changing account password");
    }

    private void generateNewAccountPassword()
    {
        //FIXME: generate a new password for the account
        showSelectedOption("Generating new password");
    }

    private void deleteAccount()
    {
        //FIXME: delete the account
        showSelectedOption("Deleting account");
    }

    private void addNewAccount()
    {
        //FIXME: add a new account
        showSelectedOption("Adding new account");
    }

    private void changeLogInAttempts()
    {
        //FIXME: change log-in attempts
        showSelectedOption("Changing Log in attempts allowed");
    }

    private void changeHagfishPassword()
    {
        //FIXME: change the Hagfish password
        showSelectedOption("Changing Hagfish password");
    }

    private void destroyVault()
    {
        //FIXME: destroy the current vault
        showSelectedOption("Destroying vault");
    }

    private void aboutHagfish()
    {
        //FIXME: show the user 'About' spheel
        showSelectedOption("Showing About");
    }

    private void showSelectedOption(String debugText)
    {
        Toast.makeText(AccountViewActivity.this, debugText, Toast.LENGTH_SHORT).show();
    }

}
