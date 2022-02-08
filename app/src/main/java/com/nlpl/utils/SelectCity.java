package com.nlpl.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nlpl.R;

public class SelectCity {
    
    public static void selectCity(Activity context, String selectedState, TextView setCity){
        ArrayAdapter<CharSequence> selectDistrictArray = null;
        
        Dialog selectDistrictDialog = new Dialog(context);
        selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectDistrictDialog.show();
        TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
        title.setText("Select City");
        ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

        switch (selectedState) {
            case "AP":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                break;
            case "AR":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                break;
            case "AS":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_assam_districts, R.layout.custom_list_row);
                break;
            case "BR":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_bihar_districts, R.layout.custom_list_row);
                break;
            case "CG":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                break;
            case "GA":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_goa_districts, R.layout.custom_list_row);
                break;
            case "GJ":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_gujarat_districts, R.layout.custom_list_row);
                break;
            case "HR":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_haryana_districts, R.layout.custom_list_row);
                break;
            case "HP":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                break;
            case "JH":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_jharkhand_districts, R.layout.custom_list_row);
                break;
            case "KA":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_karnataka_districts, R.layout.custom_list_row);
                break;
            case "KL":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_kerala_districts, R.layout.custom_list_row);
                break;
            case "MP":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                break;
            case "MH":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_maharashtra_districts, R.layout.custom_list_row);
                break;
            case "MN":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_manipur_districts, R.layout.custom_list_row);
                break;
            case "ML":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_meghalaya_districts, R.layout.custom_list_row);
                break;
            case "MZ":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_mizoram_districts, R.layout.custom_list_row);
                break;
            case "NL":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_nagaland_districts, R.layout.custom_list_row);
                break;
            case "OD":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_odisha_districts, R.layout.custom_list_row);
                break;
            case "PB":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_punjab_districts, R.layout.custom_list_row);
                break;
            case "RJ":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_rajasthan_districts, R.layout.custom_list_row);
                break;
            case "SK":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_sikkim_districts, R.layout.custom_list_row);
                break;
            case "TN":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                break;
            case "TS":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_telangana_districts, R.layout.custom_list_row);
                break;
            case "TR":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_tripura_districts, R.layout.custom_list_row);
                break;
            case "UP":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                break;
            case "UK":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                break;
            case "WB":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_west_bengal_districts, R.layout.custom_list_row);
                break;
            case "AN":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                break;
            case "CH/PB":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_chandigarh_districts, R.layout.custom_list_row);
                break;
            case "DD":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                break;
            case "DD2":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_daman_diu_districts, R.layout.custom_list_row);
                break;
            case "DL":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_delhi_districts, R.layout.custom_list_row);
                break;
            case "JK":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                break;
            case "LD":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                break;
            case "LA":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_ladakh_districts, R.layout.custom_list_row);
                break;
            case "PY":
                selectDistrictArray = ArrayAdapter.createFromResource(context,
                        R.array.array_puducherry_districts, R.layout.custom_list_row);
                break;
        }
        districtList.setAdapter(selectDistrictArray);

        ArrayAdapter<CharSequence> finalSelectDistrictArray = selectDistrictArray;
        districtList.setOnItemClickListener((adapterView, view, i, l) -> {
            setCity.setText(finalSelectDistrictArray.getItem(i)); //Set Selected Credentials
            selectDistrictDialog.dismiss();
        });
    }
}
