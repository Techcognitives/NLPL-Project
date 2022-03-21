package com.nlpl.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.ui.activities.DriverDetailsActivity;

public class SelectState {

    public static void selectState(Activity context, TextView setState, TextView tobeClicked){
        ArrayAdapter<CharSequence> selectStateArray, selectStateUnionCode;
        Dialog selectStateDialog = new Dialog(context);
        selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectStateDialog.show();
        selectStateDialog.setCancelable(true);
        ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

        selectStateArray = ArrayAdapter.createFromResource(context, R.array.array_indian_states, R.layout.custom_list_row);
        selectStateUnionCode = ArrayAdapter.createFromResource(context, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

        stateList.setAdapter(selectStateArray);


        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                setState.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                selectStateDialog.dismiss();
                tobeClicked.performClick();
            }
        });
    }
}
