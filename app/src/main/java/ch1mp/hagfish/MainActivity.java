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
import ch1mp.hagfish.utils.Prompt;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPassword = findViewById(R.id.textPassword);
        labelPassword = findViewById(R.id.labelPassword);

        memory = Memory.getInstance(MainActivity.this);
        if(memory.isNew())
        {//run set up
            labelPassword.setText(Prompt.SET_UP.getMessage());
            txtPassword.requestFocus();
            txtPassword.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                    {
                        password = txtPassword.getText().toString();
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
            labelPassword.setText(Prompt.ENTER_PASSWORD.getMessage());

            txtPassword.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    password = txtPassword.getText().toString();
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                    {
                        crypter = new Crypter(password);
                        vault = memory.getVault(new Crypter(password));
                        if(vault != null)
                        {

                        }
                        else
                        {
                            txtPassword.setText("");
                            labelPassword.setText(Prompt.WRONG_PASSWORD.getMessage().replace("{remaining_attempts}", String.valueOf(memory.getRemainingAttempts())));
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
        Intent intent = new Intent(MainActivity.this, AccountViewActivity.class);
        //intent.putExtra("Vault", vault);
        //intent.putExtra("Crypter", crypter);
        startActivity(intent);
    }



}
