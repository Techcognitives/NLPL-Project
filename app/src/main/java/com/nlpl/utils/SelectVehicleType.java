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
        ArrayAdapter<CharSequence> selectDistrictArray = null;

        Dialog selectDistrictDialog = new Dialog(context);
        selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectDistrictDialog.show();
        TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
        title.setText("Select Load Type");
        ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

        switch (selectedModel) {
            case "Container":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_container, R.layout.custom_list_row);
                break;
            case "Trailers Flat Body":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_trailers_flat_body, R.layout.custom_list_row);
                break;
            case "Trailers Dala Body":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_trailers_dala_body, R.layout.custom_list_row);
                break;
            default:
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_load_type_open_close_tarpaulin, R.layout.custom_list_row);
                break;
        }
        districtList.setAdapter(selectDistrictArray);

        ArrayAdapter<CharSequence> finalSelectDistrictArray = selectDistrictArray;
        districtList.setOnItemClickListener((adapterView, view, i, l) -> {
            setLoadType.setText(finalSelectDistrictArray.getItem(i)); //Set Selected Credentials
            selectDistrictDialog.dismiss();
        });
    }
}
