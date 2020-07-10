package info.androidhive.project3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView imageview=findViewById(R.id.image);
        TextView location=findViewById(R.id.location);
        TextView price=findViewById(R.id.price);
        TextView model=findViewById(R.id.model);
        TextView createdat=findViewById(R.id.last_update);
       //setting the data received from the main activity
        String imageurl=(getIntent().getExtras().getString("image_url"));
        model.setText("mileage : "+getIntent().getExtras().getString("mileage"));
        location.setText("Description : "+getIntent().getExtras().getString("veh_description"));
        price.setText("Price : "+getIntent().getExtras().getString("price"));
        createdat.setText("Last Update : "+getIntent().getExtras().getString("updated_at"));
        Picasso.get().load(imageurl).into(imageview);
    }
}