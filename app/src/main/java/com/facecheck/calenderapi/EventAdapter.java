package com.facecheck.calenderapi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.title.setText(eventList.get(position).getTittle());
        holder.time.setText(eventList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView title, time;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTittle);
            time = itemView.findViewById(R.id.textTime);
        }
    }
}