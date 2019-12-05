package ch1mp.hagfish.dialogs;

import ch1mp.hagfish.utils.UserAction;

public interface GeneralDialogListener {
    void onDialogPositiveClick(UserAction userAction);
    void onDialogNegativeClick(UserAction userAction);
}
