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
import ch1mp.hagfish.store.UserPreferences;

/**
 *
 * Dialog Fragment for changing the settings of Hagfish.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class SettingsDialog extends DialogFragment {

    private DialogListener listener;
    private UserPreferences up;

    /**
     * The constructor requires the current UserPreferences in order to build
     * the dialog correctly.
     * @param up - The current UserPreferences for Hagfish
     */
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

        /*
        * Set up the dialog to reflect the current settings
        * */
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
     * The interface that contains the method by which the new settings are
     * passed to the AccountViewActivity
     */
    public interface DialogListener
    {
        void updateSettings(int loginAttempts, int idleTime, int showPWTime);
    }

}
