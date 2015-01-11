package vp.com.watchrooms.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vp.com.watchrooms.R;
import vp.com.watchrooms.models.Floor;
import vp.com.watchrooms.models.FloorList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class FloorsRecycleViewAdapter extends RecyclerView.Adapter<FloorsRecycleViewAdapter.FloorsListViewHolder> {

    private static final String TAG = FloorsRecycleViewAdapter.class.getSimpleName();
    private FloorList floorList;
    private FloorsClickListener listener;

    public FloorsRecycleViewAdapter(FloorsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public FloorsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_floors, viewGroup, false);

        // set the onClick listener
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView floorIdView = (TextView) v.findViewById(R.id.floor_id_text_view);
                Log.i(TAG, format("Floor with id: [%s] selected", floorIdView.getText()));
                listener.onFloorSelected(floorIdView.getText().toString());
            }
        });

        // set the view's size, margins, paddings and layout parameters
        FloorsListViewHolder vh = new FloorsListViewHolder(v);
        return vh;
    }


    public interface FloorsClickListener {
        public void onFloorSelected(String floorId);
    }

    @Override
    public void onBindViewHolder(FloorsListViewHolder viewHolder, int i) {
        Floor floor = floorList.getFloors().get(i);
        viewHolder.mFloorNameTextView.setText(floor.getName());
        viewHolder.mFloorIdTextView.setText(floor.getFloorId());
    }

    @Override
    public int getItemCount() {
        if (floorList != null) {
            return floorList.getFloors().size();
        } else {
            return 0;
        }
    }

    public FloorList getFloorList() {
        return floorList;
    }

    public void setFloorList(FloorList toSet) {
        this.floorList = toSet;
    }

    public static class FloorsListViewHolder extends RecyclerView.ViewHolder {

        public TextView mFloorNameTextView;
        public TextView mFloorIdTextView;

        public FloorsListViewHolder(View v) {
            super(v);
            mFloorNameTextView = (TextView) itemView.findViewById(R.id.floor_name_text_view);
            mFloorIdTextView = (TextView) itemView.findViewById(R.id.floor_id_text_view);
        }
    }

    public void addFloor(Floor toAdd) {
        this.floorList.getFloors().add(toAdd);
        notifyItemInserted(this.floorList.getFloors().size() - 1);
    }


}
