package vp.com.watchrooms.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vp.com.watchrooms.R;
import vp.com.watchrooms.models.Building;
import vp.com.watchrooms.models.BuildingList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class BuildingsRecycleViewAdapter extends RecyclerView.Adapter<BuildingsRecycleViewAdapter.BuildingsListViewHolder> {

    private static final String TAG = BuildingsRecycleViewAdapter.class.getSimpleName();
    private BuildingList buildingList;
    private BuildingsClickListener listener;

    public BuildingsRecycleViewAdapter(BuildingsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public BuildingsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_buildings, viewGroup, false);

        // set the onClick listener
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView buildingIdView = (TextView) v.findViewById(R.id.building_id_text_view);
                Log.v(TAG, format("Building with id: [%s] selected", buildingIdView.getText()));
                listener.onBuildingSelect(buildingIdView.getText().toString());
            }
        });

        // set the view's size, margins, paddings and layout parameters
        BuildingsListViewHolder vh = new BuildingsListViewHolder(v);
        return vh;
    }

    public interface BuildingsClickListener {
        public void onBuildingSelect(String buildingId);
    }

    @Override
    public void onBindViewHolder(BuildingsListViewHolder viewHolder, int i) {
        Building building = buildingList.getBuildings().get(i);
        viewHolder.mBuildingNameTextView.setText(building.getName());
        viewHolder.mBuildingAddressTextView.setText(building.getAddress());
        viewHolder.mBuildingIdTextView.setText(building.getBuildingId());
    }

    @Override
    public int getItemCount() {
        if (buildingList != null) {
            return buildingList.getBuildings().size();
        } else {
            return 0;
        }
    }

    public BuildingList getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(BuildingList toSet) {
        this.buildingList = toSet;
    }

    public static class BuildingsListViewHolder extends RecyclerView.ViewHolder {

        public TextView mBuildingNameTextView;
        public TextView mBuildingAddressTextView;
        public TextView mBuildingIdTextView;

        public BuildingsListViewHolder(View v) {
            super(v);
            mBuildingNameTextView = (TextView) itemView.findViewById(R.id.building_name_text_view);
            mBuildingAddressTextView = (TextView) itemView.findViewById(R.id.building_address_text_view);
            mBuildingIdTextView = (TextView) itemView.findViewById(R.id.building_id_text_view);
        }
    }

    public void addBuilding(Building toAdd) {
        this.buildingList.getBuildings().add(toAdd);
        notifyItemInserted(this.buildingList.getBuildings().size() - 1);
    }


}
