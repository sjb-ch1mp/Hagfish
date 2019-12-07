package ch1mp.hagfish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;

public class WarningDialog extends DialogFragment {

    private DialogListener listener;
    private Action action;

    public WarningDialog(Action action)
    {
        this.action = action;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        final TextView textWarning = dialogView.findViewById(R.id.dialog_warning);

        switch(action)
        {
            case GO_BACK:
                textWarning.setText(R.string.warning_log_out);
                break;
            case DELETE_ACCOUNT:
                textWarning.setText(R.string.warning_delete_account);
                break;
            case BURN_VAULT:
                textWarning.setText(R.string.warning_burn_vault);
        }

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.doAction(action);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            listener = (DialogListener) context;
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
    }

    public interface DialogListener
    {
        void doAction(Action action);
    }

    public enum Action { GO_BACK, DELETE_ACCOUNT, BURN_VAULT }
}
