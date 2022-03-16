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

public class SelectVehicleType {

    public static void selectBodyType(Activity context, TextView setModel, TextView tobeClicked){
        ArrayAdapter<CharSequence> selectStateArray;
        Dialog selectTypeDialog = new Dialog(context);
        selectTypeDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTypeDialog.show();
        selectTypeDialog.setCancelable(true);

        ListView bodyList = (ListView) selectTypeDialog.findViewById(R.id.list_state);
        TextView title = selectTypeDialog.findViewById(R.id.dialog_spinner_title);
        title.setText("Select Body Type");

        selectStateArray = ArrayAdapter.createFromResource(context, R.array.array_body_type, R.layout.custom_list_row);
        bodyList.setAdapter(selectStateArray);

        bodyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                setModel.setText(selectStateArray.getItem(i)); //Set Selected Credentials
                selectTypeDialog.dismiss();
                tobeClicked.setText("");
                tobeClicked.performClick();
            }
        });
    }

    public static void selectLoadType(Activity context, String selectedModel, TextView setLoadType){
        ArrayAdapter<CharSequence> selectLoadTypeArray = null;

        Dialog selectLoadTypeDialog = new Dialog(context);
        selectLoadTypeDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectLoadTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectLoadTypeDialog.show();
        TextView title = selectLoadTypeDialog.findViewById(R.id.dialog_spinner_title);
        title.setText("Select Load Type");
        ListView loadTypeList = (ListView) selectLoadTypeDialog.findViewById(R.id.list_state);

        switch (selectedModel) {
            case "Container":
                selectLoadTypeArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_container, R.layout.custom_list_row);
                break;
            case "Trailers Flat Body":
                selectLoadTypeArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_trailers_flat_body, R.layout.custom_list_row);
                break;
            case "Trailers Dala Body":
                selectLoadTypeArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_trailers_dala_body, R.layout.custom_list_row);
                break;
            default:
                selectLoadTypeArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_open_close_tarpaulin, R.layout.custom_list_row);
                break;
        }
        loadTypeList.setAdapter(selectLoadTypeArray);

        ArrayAdapter<CharSequence> finalSelectDistrictArray = selectLoadTypeArray;
        loadTypeList.setOnItemClickListener((adapterView, view, i, l) -> {
            setLoadType.setText(finalSelectDistrictArray.getItem(i)); //Set Selected Credentials
            selectLoadTypeDialog.dismiss();
        });
    }
}
