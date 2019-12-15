package ch1mp.hagfish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;
import ch1mp.hagfish.store.PasswordParameters;

/**
 *
 * DialogListener for updating the PasswordParameters of an account.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class PasswordParametersDialog extends DialogFragment {

    private DialogListener listener;
    private PasswordParameters pp;

    /**
     * Constructor requires the current PasswordParameters to build the
     * dialog correctly.
     * @param pp - The current PasswordParameters for the account
     */
    public PasswordParametersDialog(PasswordParameters pp)
    {
        this.pp = pp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password_params, null);

        final SeekBar pwLength = dialogView.findViewById(R.id.seekBar_password_length);
        final TextView pwLenStatus = dialogView.findViewById(R.id.params_seekbar_status);
        final CheckBox cbLowerCase = dialogView.findViewById(R.id.checkBox_lc);
        final CheckBox cbUpperCase = dialogView.findViewById(R.id.checkBox_uc);
        final CheckBox cbNumbers = dialogView.findViewById(R.id.checkBox_nums);
        final CheckBox cbExtSpecChar = dialogView.findViewById(R.id.checkBox_esc);
        final LinearLayout lscContainer = dialogView.findViewById(R.id.lsc_container);
        final EditText etLimSpecChar = dialogView.findViewById(R.id.editText_lsc);

        /*
        * Set up the dialog to reflect the current settings.
        * */
        pwLength.setProgress(pp.getLength());
        pwLenStatus.setText(String.valueOf(pwLength.getProgress() + 1));
        pwLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pwLenStatus.setText(String.valueOf(seekBar.getProgress() + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });

        cbLowerCase.setChecked(pp.lowerCaseAllowed());
        cbUpperCase.setChecked(pp.upperCaseAllowed());
        cbNumbers.setChecked(pp.numericAllowed());
        cbExtSpecChar.setChecked(pp.extendedSpecialCharactersAllowed());
        cbExtSpecChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    lscContainer.setVisibility(View.INVISIBLE);
                }
                else
                {
                    lscContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        etLimSpecChar.setText(pp.getLimitedSpecialCharactersAsString());
        if(cbExtSpecChar.isChecked()) lscContainer.setVisibility(View.INVISIBLE);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.updateParameters(
                                pwLength.getProgress(),
                                cbLowerCase.isChecked(),
                                cbUpperCase.isChecked(),
                                cbNumbers.isChecked(),
                                cbExtSpecChar.isChecked(),
                                etLimSpecChar.getText().toString()
                        );
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    /**
     * Attach the AccountViewActivity as a DialogListener
     * @param context - AccountViewActivity
     */
    @Override
    public void onAttach(Context context) {
        listener = (DialogListener) context;
        super.onAttach(context);
    }

    /**
     * The interface by which the new values for the PasswordParameters are passed
     * to AccountViewActivity
     */
    public interface DialogListener
    {
        void updateParameters(int length, boolean lc, boolean uc, boolean num, boolean esc, String lsc);
    }
}
