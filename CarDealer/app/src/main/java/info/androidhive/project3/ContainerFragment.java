package info.androidhive.project3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ContainerFragment extends Fragment {


    ArrayList<MainActivity.GetCar.Cardetails> a;
    public ContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Bundle bundle=getArguments();
       if(bundle.getSerializable("Data")!=null){
           a=(ArrayList<MainActivity.GetCar.Cardetails>)bundle.getSerializable("Data");
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_container, container, false);
        ImageView imageview=view.findViewById(R.id.image);
        TextView location=view.findViewById(R.id.location);
        TextView price=view.findViewById(R.id.price);
        TextView model=view.findViewById(R.id.model);
        TextView createdat=view.findViewById(R.id.last_update);
        //extracting the data from the cardetails
        String imageurl=a.get(0).getCardetails_image_url();
        location.setText(a.get(0).getCardetails_veh_description());
        model.setText("mileage: "+a.get(0).getCardetails_mileage());
        price.setText("Price: "+a.get(0).getCardetails_price());
        createdat.setText("Last updated at: "+a.get(0).getCardetails_updated_at());
        Picasso.get().load(imageurl).into(imageview);
        return view;
    }
}