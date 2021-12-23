package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.BankModel;
import com.nlpl.ui.ui.activities.ProfileAndRegistrationActivity;

import java.util.ArrayList;

public class BanksAdapter extends RecyclerView.Adapter<BanksAdapter.BankViewHolder> {

    private ArrayList<BankModel> bankList;
    private ProfileAndRegistrationActivity activity;

    public BanksAdapter(ProfileAndRegistrationActivity activity, ArrayList<BankModel> bankList) {
        this.bankList = bankList;
        this.activity = activity;
    }

    @Override
    public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_list, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BankViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BankModel obj = bankList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getAccountholder_name();
        Log.i("File Name:", name1);

        String accNumber = obj.getAccount_number();

        holder.list_title.setText(" " + name1);
        holder.list_acc_no.setText(accNumber);
//--------------------------------------------------------------------------------------------------
        holder.list_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getBankDetails(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public void updateData(ArrayList<BankModel> bankList) {
        this.bankList = bankList;
        notifyDataSetChanged();
    }

    public class BankViewHolder extends RecyclerView.ViewHolder {
        private TextView list_title, list_acc_no, list_edit;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.bankNameDone);
            list_acc_no = itemView.findViewById(R.id.accNoDone);
            list_edit = itemView.findViewById(R.id.editBankDetailsDone);
        }

    }
//--------------------------------------------------------------------------------------------------
}