package info.androidhive.project3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;
    private ProgressDialog pDialog;
    private ListView lv;
    private Spinner sp;
    private Spinner sp2;

    // URLs to get JSON
    private static String url = "https://thawing-beach-68207.herokuapp.com/carmakes";
    private static String url2 = "https://thawing-beach-68207.herokuapp.com/carmodelmakes/";
    private static String url3 = "https://thawing-beach-68207.herokuapp.com/cars/";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = (Spinner) findViewById(R.id.spinner);
        sp2 = (Spinner) findViewById(R.id.spinner2);
        lv = (ListView) findViewById(R.id.list_item);
        //checking if the layout has the container(if it is tablet)
        if((findViewById(R.id.container))!=null){
            mTwoPane=true;
        }
        else
            mTwoPane=false;

        new GetCar().execute();
    }


    /**
     * Async task class to get json by making HTTP call
     */
    public class GetCar extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener{
        //Arraylists to store the json objects
        ArrayList<GetCar.Carcompany> carmakesList = new ArrayList<>();
        ArrayList<GetCar.Carmodel> carmodels;
        ArrayList<HashMap<String, String>> carids = new ArrayList<>();
        ArrayList<GetCar.Cardetails> cardetails = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray cardet = new JSONArray(jsonStr);

                    // looping through All json objects and adding them in the arraylist
                    for (int i = 0; i < cardet.length(); i++) {
                        JSONObject c = cardet.getJSONObject(i);
                        String id = c.getString("id");
                        String vehicle_make = c.getString("vehicle_make");
                        carmakesList.add(new Carcompany(vehicle_make,id));

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
          //setting the obtained data in the spinner
            sp.setOnItemSelectedListener(this);
            ArrayAdapter<Carcompany> adapter=new ArrayAdapter<Carcompany>(MainActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, carmakesList);
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);

        }

        //When the item in the spinner 1 (make) or spinner 2 (model) is selected
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch(parent.getId()) {
               //when the item in make spinner is selected
                case R.id.spinner:
                carmodels = new ArrayList<>();
                String idtemp = "";
                String text = sp.getSelectedItem().toString();
                //get the id for the selected make
                for (Carcompany a : carmakesList) {
                    if (a.vehicle_make.equals(text)) {
                        idtemp = a.id;
                    }
                }
                url2 = url2 + idtemp;
                //calling method to obtain the json objects for models
                loadSpinnerData(url2);
                url2 = "https://thawing-beach-68207.herokuapp.com/carmodelmakes/";
                break;

                //when the item in model spinner is selected
                case R.id.spinner2:
                    carids = new ArrayList<>();
                    String makeid = "";
                    String idtemp2 = "";
                    String text2 = sp2.getSelectedItem().toString();
                   //get the id and makeid for the selected model
                    for (Carmodel a : carmodels) {
                        if (a.model.equals(text2)) {
                            idtemp2 = a.id;
                            makeid = a.vehicle_make_id;
                        }
                    }
                    url3 = url3 + makeid + "/" + idtemp2 + "/" + "92603";
                    //calling the method to get the json objects in the list
                    loadListData(url3);
                    url3 = "https://thawing-beach-68207.herokuapp.com/cars/";
                    break;

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
              parent.setSelection(0);
        }
        private void loadSpinnerData(String url) {
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONArray jsonArray=new JSONArray(response);
                        // looping through All json objects and adding them in the arraylist
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String model=jsonObject1.getString("model");
                                String id=jsonObject1.getString("id");
                                String vehicle_make_id=jsonObject1.getString("vehicle_make_id");
                                carmodels.add(new Carmodel(id,model,vehicle_make_id));

                            }

                        sp2.setAdapter(new ArrayAdapter<Carmodel>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, carmodels));

                    }catch (JSONException e){e.printStackTrace();}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
            sp2.setOnItemSelectedListener(this);
        }
        private void loadListData(String url) {
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url3, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonObject=new JSONObject(response);
                      JSONArray jsonArray=jsonObject.getJSONArray("lists");
                        // looping through All json objects and adding them in the arraylist
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String id=jsonObject1.getString("id");
                            String vehicle_make=jsonObject1.getString("vehicle_make");
                            HashMap<String, String> listings = new HashMap<>();
                            listings.put("id",id);
                            listings.put("vehicle_make",vehicle_make);
                            carids.add(listings);

                        }
                        //setting the data in the list
                        ListAdapter adapter = new SimpleAdapter(
                                MainActivity.this, carids,
                                R.layout.list_item, new String[]{"vehicle_make","id",
                                }, new int[]{R.id.name,R.id.id
                                });

                           lv.setAdapter(adapter);
                    }catch (JSONException e){e.printStackTrace();}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
            lv.setOnItemClickListener(this);

        }
        public void loadcardetails(String url) {
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url3, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONArray jsonArray=new JSONArray(response);
                        // looping through All json objects and adding them in the arraylist
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String image_url=jsonObject1.getString("image_url");
                          String id=jsonObject1.getString("id");
                            String updated_at=jsonObject1.getString("updated_at");
                            String price=jsonObject1.getString("price");
                            String veh_description=jsonObject1.getString("veh_description");
                            String mileage = jsonObject1.getString("mileage");
                            cardetails.add(new Cardetails(id,image_url,updated_at,price,veh_description,mileage));

                        }
                        //if it is mobile view
                        if(!mTwoPane) {
                            String id="";
                            String image_url="";
                            String price="";
                            String updated_at="";
                            String veh_description="";
                            String mileage="";
                            for (Cardetails a : cardetails) {
                                  //  id = a.id;
                                    image_url = a.image_url;
                                    price = a.price;
                                    updated_at=a.updated_at;
                                    veh_description=a.veh_description;
                                    mileage=a.mileage;

                            }

                            Intent intent = new Intent(getApplicationContext(), ActivityDetail.class);
                            intent.putExtra("image_url",image_url);
                            intent.putExtra("price",price);
                            intent.putExtra("updated_at",updated_at);
                            intent.putExtra("veh_description",veh_description);
                            intent.putExtra("mileage",mileage);
                            startActivity(intent);
                        }
                        //if it is tablet view
                        else{
                            ContainerFragment fragment=new ContainerFragment();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("Data",cardetails);
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

                        }


                    }catch (JSONException e){e.printStackTrace();}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        }


        //when the item is the list is clicked
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selected = ((TextView) view.findViewById(R.id.id)).getText().toString();
            url3 = url3 + selected ;
            cardetails = new ArrayList<>();
            loadcardetails(url3);
            url3 = "https://thawing-beach-68207.herokuapp.com/cars/";

        }
        //the classes for storing the objects that are inserted in the arraylists.
        public class Carcompany {
            private String vehicle_make;
            private String id;

            public Carcompany() {
            }

            public Carcompany(String vehicle_make, String id) {
                this.vehicle_make = vehicle_make;
                this.id = id;
            }

            public String getCarcompany_name() {
                return vehicle_make;
            }

            public void setCarcompany_name(String vehicle_make) {
                this.vehicle_make = vehicle_make;
            }

            public String getCarcompany_id() {
                return id;
            }

            public void setCarcompany_id(String id) {
                this.id = id;
            }

            /**
             * Pay attention here, you have to override the toString method as the
             * ArrayAdapter will reads the toString of the given object for the name
             *
             * @return contact_name
             */
            @Override
            public String toString() {
                return vehicle_make;
            }
        }
        public class Carmodel {
            private String model;
            private String vehicle_make_id;
            private String id;

            public Carmodel() {
            }

        public Carmodel(String id,String model ,String vehicle_make_id) {
            this.id = id;
            this.model = model;
            this.vehicle_make_id = vehicle_make_id;

        }

        public String getCarmodel_name() {
            return model;
        }

        public void setCarmodel_name(String model) {
            this.model = model;
        }
        public String getCarmodel_vehicleid() {
            return vehicle_make_id;
        }

        public void setCarmodel_vehicleid(String vehicle_make_id) {
            this.vehicle_make_id = vehicle_make_id;
        }

        public String getCarmodel_id() {
            return id;
        }

        public void setCarmodel_id(String id) {
            this.id = id;
        }


        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        @Override
        public String toString() {
            return model;
        }
        }
        public class Cardetails {
            private String image_url;
            private String id;
            private String updated_at;
            private String price;
            private String mileage;
            private String veh_description;

            public Cardetails() {
            }

            public Cardetails(String id,String image_url,String updated_at,String price,String veh_description,String mileage) {
                this.id = id;
                this.image_url = image_url;
                this.mileage = mileage;
                this.updated_at = updated_at;
                this.price = price;
                this.veh_description = veh_description;

            }
            public String getCardetails_id() {
                return id;
            }

            public void setCardetails_id(String id) {
                this.id =id;
            }

            public String getCardetails_image_url() {
                return image_url;
            }

            public void setCardetails_image_url(String image_url) {
                this.image_url =image_url;
            }
            public String getCardetails_mileage() {
                return mileage;
            }

            public void setCardetails_mileage(String mileage) {
                this.mileage = mileage;
            }

            public String getCardetails_updated_at() {
                return updated_at;
            }

            public void setCardetails_updated_at(String updated_at) {
                this.updated_at= updated_at;
            }
            public String getCardetails_price() {
                return price;
            }

            public void setCardetails_price(String price) {
                this.price = price;
            }
            public String getCardetails_veh_description() {
                return veh_description;
            }

            public void setCardetails_veh_description(String veh_description) {
                this.veh_description = veh_description;
            }



            /**
             * Pay attention here, you have to override the toString method as the
             * ArrayAdapter will reads the toString of the given object for the name
             *
             * @return contact_name
             */
            @Override
            public String toString() {
                return id;
            }
        }


    }
}
