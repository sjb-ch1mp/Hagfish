package ch1mp.hagfish.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;
import ch1mp.hagfish.utils.UserPreferences;

public class SettingsDialog extends DialogFragment {

    private DialogListener listener;
    private UserPreferences up;

    public SettingsDialog(UserPreferences up)
    {
        this.up = up;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = this.requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_settings, null);
        final SeekBar sbLogin = dialogView.findViewById(R.id.seekBar_login_attempts);
        final SeekBar sbIdle = dialogView.findViewById(R.id.seekBar_idle_timer);
        final SeekBar sbPassword = dialogView.findViewById(R.id.seekBar_password_timer);

        sbLogin.setProgress(up.getMaxAttempts() - 1);
        sbIdle.setProgress((up.getMaxIdle()/60000) - 1);
        sbPassword.setProgress((up.getMaxPasswordShowTime()/1000) - 1);


        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.updateSettings(sbLogin.getProgress() + 1,
                                sbIdle.getProgress() + 1,
                                sbPassword.getProgress() + 1);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        try
        {
            listener = (DialogListener) context;
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
        super.onAttach(context);
    }

    public interface DialogListener
    {
        void updateSettings(int loginAttempts, int idleTime, int showPWTime);
    }

}
