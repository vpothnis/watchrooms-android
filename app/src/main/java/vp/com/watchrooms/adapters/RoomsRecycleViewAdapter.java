package vp.com.watchrooms.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import vp.com.watchrooms.R;
import vp.com.watchrooms.models.Room;
import vp.com.watchrooms.models.RoomList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class RoomsRecycleViewAdapter extends RecyclerView.Adapter<RoomsRecycleViewAdapter.RoomsListViewHolder> {

    private static final String TAG = RoomsRecycleViewAdapter.class.getSimpleName();
    private RoomList roomList;
    private SubscriptionToggleClickListener listener;

    public RoomsRecycleViewAdapter(SubscriptionToggleClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RoomsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_rooms, viewGroup, false);

        // set the view's size, margins, paddings and layout parameters
        RoomsListViewHolder vh = new RoomsListViewHolder(v);
        return vh;
    }

    /**
     * Listen to subscription status changes and then call back so that the choice is persisted to db
     */
    public interface SubscriptionToggleClickListener {

        public void onSubscriptionStatusChanged(String roomId, boolean subscribed);
    }

    @Override
    public void onBindViewHolder(RoomsListViewHolder viewHolder, int i) {
        Room room = roomList.getRooms().get(i);
        viewHolder.mRoomNameTextView.setText(room.getName());
        viewHolder.mRoomGenderTextView.setText(room.getCurrentStatus().toString());
        viewHolder.mRoomStatusTextView.setText(room.getGender().toString());
        viewHolder.mRoomIdTextView.setText(room.getRoomId());
        if (room.isSubscribed()) {
            viewHolder.mSubscriptionToggleButton.setChecked(true);
            viewHolder.mSubscriptionToggleButton.setText("Subscribed!");
        } else {
            viewHolder.mSubscriptionToggleButton.setChecked(false);
            viewHolder.mSubscriptionToggleButton.setText("Subscribe Now!");
        }
        viewHolder.mSubscriptionToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View) v.getParent();
                TextView roomIdView = (TextView) parent.findViewById(R.id.room_id_text_view);
                ToggleButton subscriptionToggleButton = (ToggleButton) v;
                boolean currentSubscriptionStatus = subscriptionToggleButton.isChecked();
                Log.i(TAG, format("User has chosen change subscription status of room [%s] to [%s]", roomIdView.getText(), currentSubscriptionStatus));
                listener.onSubscriptionStatusChanged(roomIdView.getText().toString(), currentSubscriptionStatus);
                String newStatus = currentSubscriptionStatus ? "Subscribed!" : "Subscribe Now!";
                subscriptionToggleButton.setText(newStatus);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (roomList != null) {
            return roomList.getRooms().size();
        } else {
            return 0;
        }
    }

    public RoomList getRoomList() {
        return roomList;
    }

    public void setRoomList(RoomList toSet) {
        this.roomList = toSet;
    }

    public static class RoomsListViewHolder extends RecyclerView.ViewHolder {

        public TextView mRoomNameTextView;
        public TextView mRoomStatusTextView;
        public TextView mRoomGenderTextView;
        public TextView mRoomIdTextView;
        public ToggleButton mSubscriptionToggleButton;

        public RoomsListViewHolder(View v) {
            super(v);
            mRoomNameTextView = (TextView) itemView.findViewById(R.id.room_name_text_view);
            mRoomStatusTextView = (TextView) itemView.findViewById(R.id.room_status_text_view);
            mRoomGenderTextView = (TextView) itemView.findViewById(R.id.room_gender_text_view);
            mRoomIdTextView = (TextView) itemView.findViewById(R.id.room_id_text_view);
            mSubscriptionToggleButton = (ToggleButton) itemView.findViewById(R.id.subscription_toggle_button);
        }
    }

    public void addRoom(Room toAdd) {
        this.roomList.getRooms().add(toAdd);
        notifyItemInserted(this.roomList.getRooms().size() - 1);
    }


}
