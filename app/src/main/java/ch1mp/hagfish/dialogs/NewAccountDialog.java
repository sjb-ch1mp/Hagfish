package ch1mp.hagfish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;

public class NewAccountDialog extends DialogFragment {

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = this.requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.new_account, null);
        final EditText accName = dialogView.findViewById(R.id.new_account_name);
        final EditText usrName = dialogView.findViewById(R.id.new_user_name);
        final EditText pw = dialogView.findViewById(R.id.new_password);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(accName.getText().toString().equals("") || usrName.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(), R.string.dialog_fields_required, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            listener.onDialogAddAccount(
                                    accName.getText().toString(),
                                    usrName.getText().toString(),
                                    pw.getText().toString()
                            );
                        }
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

    public interface DialogListener {
        void onDialogAddAccount(String accountName, String userName, String password);
    }
}
