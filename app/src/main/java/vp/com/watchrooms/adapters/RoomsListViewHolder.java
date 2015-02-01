package vp.com.watchrooms.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import vp.com.watchrooms.R;

/**
 * Created by vinaypothnis on 2015-02-01.
 */
public class RoomsListViewHolder extends RecyclerView.ViewHolder {

    public TextView mRoomNameTextView;
    public TextView mRoomStatusTextView;
    public TextView mRoomGenderTextView;
    public TextView mRoomIdTextView;
    public ToggleButton mSubscriptionToggleButton;
    public ToggleButton mRoomStatusToggleButton;

    public RoomsListViewHolder(View v) {
        super(v);
        mRoomNameTextView = (TextView) itemView.findViewById(R.id.room_name_text_view);
        mRoomStatusTextView = (TextView) itemView.findViewById(R.id.room_status_text_view);
        mRoomGenderTextView = (TextView) itemView.findViewById(R.id.room_gender_text_view);
        mRoomIdTextView = (TextView) itemView.findViewById(R.id.room_id_text_view);
        mSubscriptionToggleButton = (ToggleButton) itemView.findViewById(R.id.subscription_toggle_button);
        mRoomStatusToggleButton = (ToggleButton) itemView.findViewById(R.id.room_status_toggle_button);
    }
}

