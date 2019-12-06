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

    private String password;
    private Memory memory;
    private Vault vault;
    private Crypter crypter;
    private EditText txtPassword;
    private TextView labelPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //MainActivity.this.deleteFile("mem.dat");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPassword = findViewById(R.id.textPassword);
        labelPassword = findViewById(R.id.labelPassword);

        memory = Memory.getInstance(MainActivity.this);
        if(memory.isNew())
        {//run set up
            labelPassword.setText(getString(R.string.login_first_time));
            txtPassword.requestFocus();
            txtPassword.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                    {
                        password = txtPassword.getText().toString();
                        txtPassword.setText("");
                        try
                        {
                            PasswordParameters.checkHagFishPassword(password);
                            runFirstTimeSetUp();
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
        else
        {//memory successfully loaded - enter password
            labelPassword.setText(getString(R.string.login_enter_pw).replace("{remaining_attempts}", String.valueOf(memory.getRemainingAttempts())));

            txtPassword.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    password = txtPassword.getText().toString();
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                    {
                        txtPassword.setText("");
                        if(memory.hasSeed())
                        {
                            crypter = new Crypter(password, memory.getSeed());
                        }
                        else
                        {
                            crypter = new Crypter(password);
                        }
                        vault = memory.getVault(new Crypter(password));
                        if(vault != null)
                        {

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
                                labelPassword.setText(getString(R.string.login_wrong_pw).replace("{remaining_attempts}", String.valueOf(memory.getRemainingAttempts())));
                            }
                        }
                    }
                    return false;
                }
            });
            txtPassword.requestFocus();
        }
    }

    private void runFirstTimeSetUp()
    {
        vault = new Vault();
        crypter = new Crypter(password);
        openAccountViewer();
    }

    private void openAccountViewer()
    {
        Intent intent = new Intent(MainActivity.this, AccountViewActivity.class);
        intent.putExtra("vault", vault);
        intent.putExtra("password", password);
        //intent.putExtra("password", crypter.getHash());
        intent.putExtra("seed", crypter.getSeed());
        intent.putExtra("userprefs", memory.getUserPreferences());
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
    protected void onDestroy() {
        if(memory != null) memory.saveMemory(this);
        super.onDestroy();
    }
}
