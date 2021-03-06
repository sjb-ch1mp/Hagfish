package ch1mp.hagfish.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ch1mp.hagfish.AccountViewActivity;
import ch1mp.hagfish.R;
import ch1mp.hagfish.store.Account;

/**
 * The Adapter for the ListView in AccountViewActivity.
 * Whenever an account from the list is selected:
 *  1. the idle timer is reset
 *  2. the account is made 'active'
 *  3. the labels are populated with its details.
 *
 * @author Samuel J. Brookes (sjb-ch1mp)
 */
public class AccountAdapter extends ArrayAdapter<Account> {

    Context context;

    public AccountAdapter(@NonNull Context context, @NonNull ArrayList<Account> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Account account = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
        }

        ((TextView) convertView).setText(account.getAccountName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountViewActivity) context).resetIdleTimer();
                ((AccountViewActivity) context).cancelShowPWTimer();
                ((AccountViewActivity) context).setActiveAccount(account);
                ((AccountViewActivity) context).showAccountDetails();

            }
        });

        return convertView;
    }
}
