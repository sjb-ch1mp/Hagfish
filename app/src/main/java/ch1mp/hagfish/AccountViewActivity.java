package ch1mp.hagfish;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import ch1mp.hagfish.dialogs.PasswordParametersDialog;
import ch1mp.hagfish.dialogs.SettingsDialog;
import ch1mp.hagfish.dialogs.WarningDialog;
import ch1mp.hagfish.exceptions.PasswordException;
import ch1mp.hagfish.store.Account;
import ch1mp.hagfish.utils.AccountAdapter;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.CrypterKey;
import ch1mp.hagfish.utils.Generator;
import ch1mp.hagfish.store.Memory;
import ch1mp.hagfish.store.PasswordParameters;
import ch1mp.hagfish.store.UserPreferences;
import ch1mp.hagfish.store.Vault;

/**
 * The Activity in which the user views all the saved Accounts and can conduct the following
 * actions:
 *
 *  1. Add an account,
 *  2. Delete an account,
 *  3. View an account,
 *  4. Edit an account,
 *      a. Change its name,
 *      b. Change its user name,
 *      c. Change its password,
 *      d. Automatically generate a new password,
 *      e. Restore the previous password,
 *      f. Set the parameters for automatic password generation.
 *  5. Change the user preferences,
 *      a. Change the number of attempts allowed before the vault is deleted,
 *      b. Change the length of time the application is allowed to idle before automatic logout
 *      c. Change the length of time a password is shown when the user touches it
 *  6. Delete all accounts at once (i.e. burn vault)
 *  7. Change the login password for Hagfish
 *  8. Log out
 *
 *  When AccountViewActivity starts, an idle timer begins. If no activities (generally menu clicks) have
 *  occurred by the time it finishes, Hagfish automatically logs out. Whenever the AccountViewActivity
 *  finishes (either by logging out or pausing) - the vault is saved as a new Memory (or mem.dat file).
 *  If the AccountViewActivity is swiped shut, the onPause() callback ensures that the vault is saved.
 *
 *  The AccountViewActivity implements a unique DialogListener interface for any DialogFragment that
 *  must pass unique data back to this context.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class AccountViewActivity
        extends AppCompatActivity
        implements NewAccountDialog.DialogListener,
        SettingsDialog.DialogListener,
        WarningDialog.DialogListener,
        ChangeFieldDialog.DialogListener,
        PasswordParametersDialog.DialogListener {

    Vault vault;
    Crypter crypter;
    Account activeAccount;
    Toolbar mainToolbar;
    TextView textAccountName;
    PopupMenu accountMenu;
    TextView textUserName;
    TextView textPassword;
    TextView textModified;
    ImageView imgLogo;
    UserPreferences userPreferences;
    ListView accountList;
    ArrayAdapter<Account> adapter;
    CountDownTimer idleTimer;
    boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        setUpMainToolbar();
        setUpStores();
        setUpLabels();
        setUpListView();
        resetIdleTimer();
        setUpAccountMenu();
        toggleAccountButton(vault.size());
        saved = false;
    }

    /**
     * Enables the Action Bar using res/menu/toolbar_menu.xml
     */
    private void setUpMainToolbar()
    {
        mainToolbar = findViewById(R.id.toolbarAccountView);
        setSupportActionBar(mainToolbar);
    }

    /**
     * Retrieve saved data from the mem.dat file from the Intent:
     *  1. Vault
     *  2. CrypterKey (hashed password and iv)
     *  3. UserPreferences
     */
    private void setUpStores()
    {
        vault = Vault.retrieveVault((ArrayList<Account>) getIntent().getSerializableExtra("vault"));
        crypter = new Crypter((CrypterKey) getIntent().getSerializableExtra("key"));
        userPreferences = (UserPreferences) getIntent().getSerializableExtra("prefs");
    }

    /**
     * Load the label views for showing Account data, and attach
     * the 'About Hagfish' Dialog Fragment to the logo.
     */
    private void setUpLabels()
    {
        textAccountName = findViewById(R.id.textAccountName);
        textUserName = findViewById(R.id.textUserName);

        textModified = findViewById(R.id.textModified);

        textPassword = findViewById(R.id.textPassword);
        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetIdleTimer();
                showPassword();
            }
        });

        imgLogo = findViewById(R.id.imageLogoHeader);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutHagfish();
            }
        });
    }

    /**
     * Inflate the account menu (res/menu/account_menu.xml) and attach it to
     * the Account header (textAccountName).
     */
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
                    case R.id.account_name_change:
                        changeAccountName();
                        return true;
                    case R.id.account_un_change:
                        changeUserName();
                        return true;
                    case R.id.account_pw_change:
                        changeAccountPassword();
                        return true;
                    case R.id.account_pw_generate:
                        generateNewAccountPassword();
                        return true;
                    case R.id.account_pw_params:
                        getPasswordParameters();
                        return true;
                    case R.id.account_delete:
                        deleteAccountWarning();
                        return true;
                    case R.id.account_undo:
                        undoChangePassword();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    /**
     * Toggles the account button depending upon whether the vault is empty.
     * If the vault is empty - textAccountName should launch the new account Dialog Fragment.
     * If the vault is NOT empty - textAccountName should open the account menu.
     *
     * @param vaultSize - the size of the current Vault
     */
    private void toggleAccountButton(int vaultSize)
    {
        resetIdleTimer();
        if(vaultSize <= 0)
        {
            textAccountName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewAccount();
                }
            });
        }
        else
        {
            textAccountName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accountMenu.show();
                }
            });
        }
    }

    /**
     * Sets up the listView that holds the saved Accounts.
     */
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

    /**
     * Called when the user carries out an action. The current timer (if it exists) is
     * cancelled and a new one is started.
     */
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

    /**
     * Inflates the Action Bar menu (res/menu/toolbar_menu.xml)
     * @param menu - reference to the menu being inflated
     * @return - true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Handler for when the Action bar menu is clicked (res/menu/toolbar_menu.xml)
     * @param item - the selected MenuItem
     * @return - true if option exists, false otherwise
     */
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

    /**
     * Automatically generates a new password IAW the user-defined password parameters for
     * the active account.
     */
    private void generateNewAccountPassword()
    {
        activeAccount.changePassword(new Generator(activeAccount.getPasswordParameters()).generatePassword());
        showToast(R.string.dialog_acc_pw_updated);
        showPassword();
    }

    /**
     * Deletes all accounts.
     */
    private void burnVault()
    {
        vault.clear();
        adapter.notifyDataSetChanged();
        activeAccount = null;
        clearAccountDetails();
        textAccountName.setText(R.string.ava_first_time);
        toggleAccountButton(vault.size());
    }

    /**
     * Unhides the password of the activeAccount by starting a CountDownTimer.
     * The length of time for which the password is shown is defined by the user.
     */
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

    /**
     * Deletes the activeAccount
     */
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
        toggleAccountButton(vault.size());
    }

    /**
     * Utility method for showing a short Toast
     * @param resId - the resource id for the string
     */
    private void showToast(int resId)
    {
        Toast.makeText(AccountViewActivity.this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Utility method for showing a short Toast
     * @param message - the string to be shown
     */
    private void showToast(String message)
    {
        Toast.makeText(AccountViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * The logOut() method:
     *  1. tells the user that Hagfish is logging out
     *  2. cancels the timer
     *  3. saves the current memory
     *  4. closes this activity, and
     *  5. launches the main activity
     */
    public void logOut()
    {
        showToast(R.string.warning_idle_out);

        idleTimer.cancel();

        if(!saved)
            saved = Memory.saveMemory(this, vault, crypter.scramble(), userPreferences);

        Intent intent = new Intent(AccountViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Toggles the visibility of the labels that show the details of an account.
     * @param currentlyVisible - whether the account details are currently visible
     */
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

    /**
     * Public utility method that allows the AccountAdapter to set the activeAccount
     * in an onClickListener.
     *
     * @param account - The account being saved as active.
     */
    public void setActiveAccount(Account account)
    {
        activeAccount = account;
    }

    /**
     * Populates the labels with the details of the activeAccount.
     */
    public void showAccountDetails()
    {
        textAccountName.setText(activeAccount.getAccountName());
        textUserName.setText(activeAccount.getUserName());
        textPassword.setText(hidePassword());
        textModified.setText(activeAccount.getDatePasswordChanged());

        toggleAccountDetailsVisibility(false);
    }

    /**
     * Clears the labels of all their content.
     */
    public void clearAccountDetails()
    {
        textAccountName.setText("");
        textUserName.setText("");
        textPassword.setText("");
        textModified.setText("");

        toggleAccountDetailsVisibility(true);
    }

    /**
     * Returns a string of asterisks with the same length as the
     * password of the activeAccount.
     * @return - a String of asterisks.
     */
    private String hidePassword()
    {
        StringBuilder hiddenPassword = new StringBuilder();
        while(hiddenPassword.length() < activeAccount.getPassword().length()) hiddenPassword.append("*");
        return hiddenPassword.toString();
    }

    /**
     * Ensures that whenever the AccountViewActivity loses focus - the memory is saved.
     *
     * The saved field stores whether the vault has already been saved so that it is not
     * saved twice when logging out.
     *
     */
    @Override
    protected void onPause() {
        if(!saved)
            saved = Memory.saveMemory(this, vault, crypter.scramble(), userPreferences);
        super.onPause();
    }

    /**
     * Ensures that if Hagfish loses focus, but DOES NOT LOG OUT - that the saved field is
     * changed back to false so that subsequent changes are saved.
     */
    @Override
    protected void onRestart() {
        saved = false;
        super.onRestart();
    }

    /*==================================================================
     * DIALOG FRAGMENTS
     *  - These methods all simply launch the applicable Dialog Fragment
     * =================================================================*/
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

    private void getPasswordParameters()
    {
        DialogFragment df = new PasswordParametersDialog(activeAccount.getPasswordParameters());
        df.show(getSupportFragmentManager(), "PasswordParametersDialog");
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

    private void changeAccountName()
    {
        DialogFragment df = new ChangeFieldDialog(ChangeFieldDialog.Field.ACCOUNT_NAME);
        df.show(getSupportFragmentManager(), "ChangeFieldDialog_Acc_Name");
    }

    private void undoChangePassword()
    {
        if(activeAccount.hasPreviousPassword())
        {
            DialogFragment df = new WarningDialog(WarningDialog.Action.UNDO_CHANGE_PASSWORD);
            df.show(getSupportFragmentManager(), "WarningDialog_Undo_Change_PW");
        }
        else showToast(R.string.warning_no_prev_pw);
    }

    @Override
    public void onBackPressed() {
        DialogFragment df = new WarningDialog(WarningDialog.Action.GO_BACK);
        df.show(getSupportFragmentManager(), "WarningDialog_GoBack");
    }

    /*====================================================================
     * LISTENERS
     *  - These methods all handle the callbacks from the Dialog Fragments
     * ===================================================================*/
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
                break;
            case UNDO_CHANGE_PASSWORD:
                try
                {
                    activeAccount.restorePreviousPassword();
                    showAccountDetails();
                    showToast(R.string.warning_pw_restored);
                }
                catch(PasswordException e)
                {
                    showToast(e.toString());
                }
        }
    }

    public void updateField(String newValue, ChangeFieldDialog.Field field) //LISTENER
    {
        switch(field)
        {
            case ACCOUNT_NAME:
                if(!vault.contains(newValue))
                {
                    activeAccount.changeAccountName(newValue);
                    vault.alphabetize();
                    adapter.notifyDataSetChanged();
                    showAccountDetails();
                    showToast(R.string.dialog_account_name_changed);
                }
                else showToast(R.string.warning_account_exists);
                break;
            case USER_NAME:
                if(!activeAccount.getUserName().equals(newValue))
                {
                    activeAccount.changeUserName(newValue);
                    showAccountDetails();
                    showToast(R.string.dialog_user_updated);
                }
                else showToast(R.string.warning_un_same);
                break;
            case ACCOUNT_PASSWORD:
                if(!activeAccount.getPassword().equals(newValue))
                {
                    activeAccount.changePassword(newValue);
                    showAccountDetails();
                    showToast(R.string.dialog_acc_pw_updated);
                }
                else showToast(R.string.warning_pw_same);
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
            vault.alphabetize();
            adapter.notifyDataSetChanged();
            setActiveAccount(account);
            showAccountDetails();
        }
        toggleAccountButton(vault.size());
    }

    public void updateSettings(int loginAttempts, int idleTime, int showPWTime) //listener
    {
        userPreferences.setMaxAttempts(loginAttempts);
        userPreferences.setMaxIdle(idleTime * 60000);
        userPreferences.setMaxPasswordShowTime(showPWTime * 1000);
        showToast(R.string.warning_prefs_changed);
    }

    public void updateParameters(int length, boolean lc, boolean uc, boolean num, boolean esc, String lsc)
    {
        if(!lc && !uc && !num && !esc && lsc.equals(""))
        {
            showToast(R.string.warning_params_all_false);
        }
        else
        {
            activeAccount.setPasswordParameters(new PasswordParameters(length, lc, uc, num, esc, lsc));
            showToast(R.string.warning_params_updated);
        }
    }
}
