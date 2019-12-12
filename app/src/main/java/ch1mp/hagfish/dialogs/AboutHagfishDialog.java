package ch1mp.hagfish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;


/**
 *
 * Dialog Fragment for viewing general information about the Hagfish application.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class AboutHagfishDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_about_hagfish,  null))
                .setNegativeButton(R.string.dialog_close, null);
        return builder.create();
    }
}
