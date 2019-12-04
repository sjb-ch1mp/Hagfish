package ch1mp.hagfish.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch1mp.hagfish.R;

public class AccountAdapter extends RecyclerView.Adapter {

    private Vault vault;

    public static class AccountView extends RecyclerView.ViewHolder
    {
        TextView textView;

        public AccountView(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }

    public AccountAdapter(Vault vault)
    {
        this.vault = vault;
    }

    @NonNull
    @Override
    public AccountAdapter.AccountView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_account_view, parent, false);
        AccountView av = new AccountView(tv);
        return av;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AccountView) holder).textView.setText(vault.get(position).getAccountName());
    }

    @Override
    public int getItemCount() {
        return vault.size();
    }

    public Account getAccount(int position)
    {
        return vault.get(position);
    }
}
