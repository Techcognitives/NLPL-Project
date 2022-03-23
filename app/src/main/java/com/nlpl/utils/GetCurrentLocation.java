package com.nlpl.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Html;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nlpl.R;
import com.nlpl.ui.activities.PersonalDetailsActivity;
import com.nlpl.ui.activities.PostALoadActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GetCurrentLocation {

    public static void getCurrentLocation(Activity activity, EditText address, EditText pinCode) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                        try {
                            String latitudeCurrent, longitudeCurrent, countryCurrent, stateCurrent, cityCurrent, subCityCurrent, addressCurrent, pinCodeCurrent;
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLatitude()));
                            longitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLongitude()));
                            countryCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getCountryName()));
                            stateCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAdminArea()));
                            cityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLocality()));
                            subCityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getSubLocality()));
                            addressCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAddressLine(0)));
                            pinCodeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getPostalCode()));

                            address.setText(addressCurrent);
                            pinCode.setText(pinCodeCurrent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void getCurrentLocationMaps(Activity activity, EditText address, EditText pinCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Dialog chooseDialog = new Dialog(activity);
            chooseDialog.setContentView(R.layout.dialog_choose);
            chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
            lp2.copyFrom(chooseDialog.getWindow().getAttributes());
            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp2.gravity = Gravity.BOTTOM;

            chooseDialog.show();
            chooseDialog.getWindow().setAttributes(lp2);

            ImageView currentLocation = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
            currentLocation.setImageResource(R.drawable.google_location_small);
            ImageView searchFromMaps = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
            searchFromMaps.setImageResource(R.drawable.google_address_small);

            TextView currentText = chooseDialog.findViewById(R.id.dialog_camera_text);
            currentText.setText(activity.getString(R.string.Current_Location));
            TextView fromMapText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
            fromMapText.setText(activity.getString(R.string.Search));

            currentLocation.setOnClickListener(view -> {
                chooseDialog.dismiss();
                    GetCurrentLocation.getCurrentLocation(activity, address, pinCode);
            });

            searchFromMaps.setOnClickListener(view -> {
                chooseDialog.dismiss();

                Places.initialize(activity.getApplicationContext(), "AIzaSyDAAes8x5HVKYB5YEIGBmdnCdyBrAHUijM");
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(activity);
                activity.startActivityForResult(intent, 100);
            });

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void setAddressAndPin(Activity activity, Intent data, EditText address, EditText pin) {
        try {
            Place place = Autocomplete.getPlaceFromIntent(data);
//            String[] addressField = place.getAddress().split(",");
//            address.setText(addressField[0]);

            List<Address> addresses;
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

//                Log.e("Address1: ", "" + address1);
//                Log.e("Address2: ", "" + address2);
//                Log.e("AddressCity: ", "" + city);
//                Log.e("AddressState: ", "" + state);
//                Log.e("AddressCountry: ", "" + country);
//                Log.e("AddressPostal: ", "" + postalCode);
//                Log.e("AddressLatitude: ", "" + place.getLatLng().latitude);
//                Log.e("AddressLongitude: ", "" + place.getLatLng().longitude);

                char[] chars = address1.toCharArray();
                StringBuilder sb = new StringBuilder();
                for (char c : chars) {
                    if (!Character.isDigit(c)) {
                        sb.append(c);
                    }
                }
//                System.out.println("String "+sb);

                String[] addressFieldWithoutCountry = address1.split(country);
                String[] addressFieldWithoutState = addressFieldWithoutCountry[0].split(state);
                String[] addressFiledWithoutCity = addressFieldWithoutState[0].split(city);

                try {
                    address.setText(addressFiledWithoutCity[0]);
                    pin.setText(postalCode);
                } catch (Exception e) {
                    address.setText(address1);
                    pin.setText(postalCode);
                }

                address.setText(address1);
                pin.setText(postalCode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //setMarker(latLng);
        }
    }

}
