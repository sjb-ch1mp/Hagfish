package ch1mp.hagfish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ch1mp.hagfish.exceptions.PasswordException;
import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.Memory;
import ch1mp.hagfish.utils.PasswordParameters;
import ch1mp.hagfish.utils.Vault;

public class MainActivity extends AppCompatActivity {

    private Memory memory;
    private Vault vault;
    private Crypter crypter;
    private EditText txtPassword;
    private TextView labelPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPassword = findViewById(R.id.textPassword);
        labelPassword = findViewById(R.id.labelPassword);

        memory = Memory.getInstance(MainActivity.this);

        if(memory.isNew()) noAccountsExist();
        else accountsExist();

        txtPassword.requestFocus();
    }

    /**
     * This method is called if a memory has been loaded from the mem.dat file,
     * i.e. !memory.isNew().
     */
    private void accountsExist()
    {
        labelPassword.setText(
                getString(R.string.login_enter_pw)
                        .replace("{remaining_attempts}",
                                String.valueOf(memory.getRemainingAttempts())));

        txtPassword.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String password = txtPassword.getText().toString();
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                {
                    txtPassword.setText("");
                    crypter = new Crypter(password, memory.getSeed());
                    vault = memory.getVault(crypter);
                    if(vault != null)
                    {
                        openAccountViewer();
                    }
                    else
                    {
                        if(memory.getRemainingAttempts() <= 0)
                        {
                            clearMemory();
                            labelPassword.setText(getString(R.string.login_attempts_exceeded));
                            txtPassword.setEnabled(false);
                        }
                        else
                        {
                            labelPassword.setText(
                                    getString(R.string.login_wrong_pw)
                                            .replace("{remaining_attempts}",
                                                    String.valueOf(memory.getRemainingAttempts())));
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * This method is called if there is no mem.dat file and an empty memory
     * has been created. This can occur if the previous memory has been deleted due to
     * too many incorrect login attempts, or because it is the first time that
     * the application has been opened.
     */
    private void noAccountsExist()
    {
        labelPassword.setText(getString(R.string.login_first_time));
        txtPassword.requestFocus();
        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                {
                    String password = txtPassword.getText().toString();
                    txtPassword.setText("");
                    try
                    {
                        PasswordParameters.checkHagFishPassword(password);
                        runFirstTimeSetUp(password);
                        openAccountViewer();
                    }
                    catch(PasswordException e)
                    {
                        System.out.println(e.toString());
                        labelPassword.setText(e.getMessage());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void runFirstTimeSetUp(String password)
    {
        vault = new Vault();
        crypter = new Crypter(password);
    }

    private void openAccountViewer()
    {
        Intent intent = new Intent(MainActivity.this, AccountViewActivity.class);
        intent.putExtra("vault", vault);
        intent.putExtra("key", crypter.getKey());
        intent.putExtra("prefs", memory.getUserPreferences());
        clearMemory();
        startActivity(intent);
        finish();
    }

    private void clearMemory()
    {
        memory = null;
        MainActivity.this.deleteFile("mem.dat");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(memory != null && !memory.isNew())
            memory.saveMemory(this);
        super.onDestroy();
    }
}
