package enigmastudios.fridgebuddy;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evankardos on 3/8/18.
 */

public class FavoritesFragment extends Fragment {

    ListView ls;
    CustomAdapter ca;

    ArrayList<FoodItem> values = new ArrayList<FoodItem>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Produce");
    final static String TAG_FOOD = "FRIDGE.BUDDY.FOOD.POSITION";
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.food_fragment, container, false);
        ls = rootView.findViewById(R.id.listThing);
        ca =  new CustomAdapter(getActivity(),R.layout.food_card_view,values);

        String[] production = {FoodItem.COLUMN_NAME};

        mDatabase = new SaveFavoritesDataBase(this.getContext()).getReadableDatabase();

        mCursor = mDatabase.query(FoodItem.TABLE_NAME,
                production,
                null,
                null,
                null,
                null,
                null);

        mCursor.moveToFirst();
        if(mCursor.getCount() > 0) {
            do {
                String name = mCursor.getString(0);
                FoodItem foodItem = new FoodItem();
                foodItem.setName(name);
                values.add(foodItem);
            } while (mCursor.moveToNext());
        }
        ls.setAdapter(ca);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(),FoodDisplayInfo.class);
//                intent.putExtra(TAG_FOOD,values.get(position));
//                startActivity(intent);
            }
        });
        return rootView;
    }

    public class CustomAdapter extends ArrayAdapter<FoodItem>{
        private final List<FoodItem> foods;

        public CustomAdapter(Context context, int resource, ArrayList<FoodItem> foods) {
            super(context, resource, foods);
            this.foods = foods;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            FoodItem food = foods.get(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = inflater.inflate(R.layout.food_card_view, null);
// Set the text
            TextView textView = (TextView) row.findViewById(R.id.rowText);
            textView.setText(food.getName());

// Set the image
            ImageView iv = row.findViewById(R.id.rowImage);
            new DownLoadImageTask(iv).execute(food.getImage());
            return row;
        }
    }
}