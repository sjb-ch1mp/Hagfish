package ch1mp.hagfish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch1mp.hagfish.R;

/**
 *
 * Dialog Fragment for changing:
 *  1. an account name
 *  2. an account user name
 *  3. an account password
 *  4. the Hagfish password
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 *
 */
public class ChangeFieldDialog extends DialogFragment {

    private DialogListener listener;
    private Field field;

    /**
     * Constructor must be informed of which field is being changed.
     * @param field - The Field being changed.
     */
    public ChangeFieldDialog(Field field)
    {
        this.field = field;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_field, null);
        final EditText editText = dialogView.findViewById(R.id.change_field);

        /*
        * Change the hint and input type, depending upon the field being changed.
        * Default input type is 'text password'.
        * */
        switch(field)
        {
            case ACCOUNT_NAME:
                editText.setHint(R.string.dialog_new_name);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case USER_NAME:
                editText.setHint(R.string.dialog_new_un);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case ACCOUNT_PASSWORD:
                editText.setHint(R.string.dialog_new_acc_pw);
                break;
            case HAGFISH_PASSWORD:
                editText.setHint(R.string.dialog_new_hf_pw);
        }

        builder.setView(dialogView)
            .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!editText.getText().toString().equals(""))
                    {
                        listener.updateField(editText.getText().toString(), field);
                    }
                }
            })
            .setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    /**
     * Attach AccountViewActivity as a DialogListeenr
     *
     * @param context - AccountViewActivity
     */
    @Override
    public void onAttach(Context context) {
        listener = (DialogListener) context;
        super.onAttach(context);
    }

    public interface DialogListener
    {
        void updateField(String newValue, Field field);
    }

    /**
     * Enum for the field that is being changed.
     */
    public enum Field
    {
        USER_NAME,
        ACCOUNT_PASSWORD,
        HAGFISH_PASSWORD,
        ACCOUNT_NAME
    }
}
