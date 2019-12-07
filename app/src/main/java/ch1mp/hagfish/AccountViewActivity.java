package ch1mp.hagfish;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import ch1mp.hagfish.dialogs.AboutHagfishDialog;
import ch1mp.hagfish.dialogs.ChangeFieldDialog;
import ch1mp.hagfish.dialogs.NewAccountDialog;
import ch1mp.hagfish.dialogs.SettingsDialog;
import ch1mp.hagfish.dialogs.WarningDialog;
import ch1mp.hagfish.utils.Account;
import ch1mp.hagfish.utils.AccountAdapter;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.CrypterKey;
import ch1mp.hagfish.utils.Generator;
import ch1mp.hagfish.utils.Memory;
import ch1mp.hagfish.utils.UserPreferences;
import ch1mp.hagfish.utils.Vault;

public class AccountViewActivity
        extends AppCompatActivity
        implements NewAccountDialog.DialogListener,
        SettingsDialog.DialogListener,
        WarningDialog.DialogListener,
        ChangeFieldDialog.DialogListener {

    Vault vault;
    Crypter crypter;
    Account activeAccount;
    Toolbar mainToolbar;
    TextView textAccountName;
    PopupMenu accountMenu;
    TextView textUserName;
    TextView textPassword;
    TextView textModified;
    UserPreferences userPreferences;
    ListView accountList;
    ArrayAdapter<Account> adapter;
    CountDownTimer idleTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        setUpMainToolbar();
        setUpStores();
        setUpLabels();
        setUpAccountMenu();
        setUpPressListeners();
        setUpListView();
        resetIdleTimer();
    }

    /*============
    * INIT METHODS
    * ============*/
    private void setUpMainToolbar()
    {
        mainToolbar = findViewById(R.id.toolbarAccountView);
        setSupportActionBar(mainToolbar);
    }

    private void setUpStores()
    {
        vault = Vault.retrieveVault((ArrayList<Account>) getIntent().getSerializableExtra("vault"));
        crypter = new Crypter((CrypterKey) getIntent().getSerializableExtra("key"));
        userPreferences = (UserPreferences) getIntent().getSerializableExtra("prefs");
    }

    private void setUpLabels()
    {
        textAccountName = findViewById(R.id.textAccountName);
        textUserName = findViewById(R.id.textUserName);
        textPassword = findViewById(R.id.textPassword);
        textModified = findViewById(R.id.textModified);
    }

    private void setUpAccountMenu()
    {
        accountMenu = new PopupMenu(AccountViewActivity.this, textAccountName);
        MenuInflater inflater = accountMenu.getMenuInflater();
        inflater.inflate(R.menu.account_menu, accountMenu.getMenu());
        accountMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                resetIdleTimer();
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
                        deleteAccountWarning();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setUpPressListeners() {

        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetIdleTimer();
                showPassword();
            }
        });

        textAccountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetIdleTimer();
                if(!textAccountName.getText().toString().equals(getString(R.string.ava_first_time)))
                {
                    accountMenu.show();
                }
            }
        });

    }

    private void setUpListView()
    {
        adapter = new AccountAdapter(this, vault);
        accountList = findViewById(R.id.accountList);
        accountList.setAdapter(adapter);

        if(vault.size() == 0)
        {
            toggleAccountDetailsVisibility(true);
            textAccountName.setText(R.string.ava_first_time);
        }
        else
        {
            activeAccount = vault.get(0);
            showAccountDetails();
        }
    }

    public void resetIdleTimer()
    {
        if(idleTimer != null) idleTimer.cancel();

        idleTimer = new CountDownTimer(userPreferences.getMaxIdle(), 1000) {
            @Override
            public void onTick(long l) {
                //do nothing
            }

            @Override
            public void onFinish() {
                logOut();
            }
        };
        idleTimer.start();
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
        resetIdleTimer();
        switch(item.getItemId())
        {
            case R.id.menu_item_new_account:
                addNewAccount();
                return true;
            case R.id.menu_settings:
                changeUserPreferences();
                return true;
            case R.id.menu_item_delete_vault:
                deleteAllAccountsWarning();
                return true;
            case R.id.menu_item_about:
                aboutHagfish();
                return true;
            case R.id.menu_item_logout:
                logOut();
                return true;
            case R.id.menu_item_change_password:
                changeHagfishPassword();
                return true;
            default:
                return false;
        }
    }


    /*================
    * DIALOG FRAGMENTS
    * ================*/
    private void changeUserName()
    {
        DialogFragment df = new ChangeFieldDialog(ChangeFieldDialog.Field.USER_NAME);
        df.show(getSupportFragmentManager(), "ChangeFieldDialog_UserName");
    }

    private void changeAccountPassword()
    {
        DialogFragment df = new ChangeFieldDialog(ChangeFieldDialog.Field.ACCOUNT_PASSWORD);
        df.show(getSupportFragmentManager(), "ChangeFieldDialog_Acc_PW");
    }

    private void generateNewAccountPassword()
    {
        //FIXME: Password Generation
    }

    private void deleteAccountWarning()
    {
        DialogFragment df = new WarningDialog(WarningDialog.Action.DELETE_ACCOUNT);
        df.show(getSupportFragmentManager(), "WarningDialog_Delete_Acc");
    }

    private void changeUserPreferences()
    {
        DialogFragment df = new SettingsDialog(userPreferences);
        df.show(getSupportFragmentManager(), "SettingsDialog");
    }

    private void addNewAccount()
    {
        DialogFragment df = new NewAccountDialog();
        df.show(getSupportFragmentManager(), "NewAccountDialog");
    }

    private void deleteAllAccountsWarning()
    {
        DialogFragment df = new WarningDialog(WarningDialog.Action.BURN_VAULT);
        df.show(getSupportFragmentManager(), "WarningDialog_Burn_Vault");
    }

    private void aboutHagfish()
    {
        DialogFragment df = new AboutHagfishDialog();
        df.show(getSupportFragmentManager(), "AboutHagfishDialog");
    }

    private void changeHagfishPassword()
    {
        DialogFragment df = new ChangeFieldDialog(ChangeFieldDialog.Field.HAGFISH_PASSWORD);
        df.show(getSupportFragmentManager(), "ChangeFieldDialog_HF_PW");
    }

    @Override
    public void onBackPressed() {
        DialogFragment df = new WarningDialog(WarningDialog.Action.GO_BACK);
        df.show(getSupportFragmentManager(), "WarningDialog_GoBack");
    }

    /*=========
    * LISTENERS
    * =========*/
    public void doAction(WarningDialog.Action action) //listener
    {
        switch(action)
        {
            case GO_BACK:
                logOut();
                break;
            case DELETE_ACCOUNT:
                deleteAccount();
                break;
            case BURN_VAULT:
                burnVault();
        }
    }

    public void updateField(String newValue, ChangeFieldDialog.Field field) //LISTENER
    {
        switch(field)
        {
            case USER_NAME:
                activeAccount.changeUserName(newValue);
                showAccountDetails();
                showToast(R.string.dialog_user_updated);
                break;
            case ACCOUNT_PASSWORD:
                activeAccount.changePassword(newValue);
                showAccountDetails();
                showToast(R.string.dialog_acc_pw_updated);
                break;
            case HAGFISH_PASSWORD:
                crypter.changePassword(newValue);
                showToast(R.string.dialog_hf_pw_updated);
        }
    }

    public void onDialogAddAccount(String accName, String usrName, String pw) //listener
    {
        if(vault.contains(accName))
        {
            showToast(R.string.warning_account_exists);
        }
        else
        {
            Account account = new Account(
                    accName,
                    usrName,
                    (pw.equals(""))?new Generator().generatePassword():pw
            );
            vault.add(account);
            adapter.notifyDataSetChanged();
            setActiveAccount(account);
            showAccountDetails();
        }
    }

    public void updateSettings(int loginAttempts, int idleTime, int showPWTime) //listener
    {
        userPreferences.setMaxAttempts(loginAttempts);
        userPreferences.setMaxIdle(idleTime * 60000);
        userPreferences.setMaxPasswordShowTime(showPWTime * 1000);
        showToast(R.string.warning_prefs_changed);
    }

    /*============
     * UTIL METHODS
     * ============*/
    private void burnVault()
    {
        vault.clear();
        adapter.notifyDataSetChanged();
        activeAccount = null;
        clearAccountDetails();
        textAccountName.setText(R.string.ava_first_time);
    }

    private void showPassword()
    {
        textPassword.setText(activeAccount.getPassword());
        CountDownTimer timer = new CountDownTimer(userPreferences.getMaxPasswordShowTime(), 100) {
            @Override
            public void onTick(long l) {
                //do nothing
            }

            @Override
            public void onFinish() {
                textPassword.setText(hidePassword());
            }
        };
        timer.start();
    }

    private void deleteAccount()
    {
        vault.deleteAccount(activeAccount.getAccountName());
        adapter.notifyDataSetChanged();
        if(vault.size() == 0)
        {
            activeAccount = null;
            clearAccountDetails();
            textAccountName.setText(R.string.ava_first_time);
        }
        else
        {
            activeAccount = vault.get(vault.size() - 1);
            showAccountDetails();
        }
    }

    private void showToast(int resId)
    {
        Toast.makeText(AccountViewActivity.this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void logOut()
    {
        showToast(R.string.warning_idle_out);

        idleTimer.cancel();

        Memory.saveMemory(this, vault, crypter, userPreferences);

        Intent intent = new Intent(AccountViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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

    public void setActiveAccount(Account account)
    {
        activeAccount = account;
    }

    public void showAccountDetails()
    {
        textAccountName.setText(activeAccount.getAccountName());
        textUserName.setText(activeAccount.getUserName());
        textPassword.setText(hidePassword());
        textModified.setText(activeAccount.getDatePasswordChanged());

        toggleAccountDetailsVisibility(false);
    }

    public void clearAccountDetails()
    {
        textAccountName.setText("");
        textUserName.setText("");
        textPassword.setText("");
        textModified.setText("");

        toggleAccountDetailsVisibility(true);
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
}
