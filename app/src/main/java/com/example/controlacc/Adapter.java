package com.example.controlacc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.controlacc.model.MessagesData;

import java.util.ArrayList;

/**
 * Clase adaptadora que hereda de RecyclerView la cual permite
 * agregar mensajes a la pantalla principal
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

    private ArrayList<MessagesData> mMessagesData;

    public Adapter(ArrayList<MessagesData> arrayList){
        mMessagesData=arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_enter,viewGroup,false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        MessagesData data=mMessagesData.get(i);
        holder.heading.setText(data.getHeading());
        holder.messages.setText(data.getMessages());


    }

    @Override
    public int getItemCount() {
        return mMessagesData.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView heading,messages;

        public Holder(@NonNull View itemView) {
            super(itemView);

            heading=itemView.findViewById(R.id.headingEnter);
            messages=itemView.findViewById(R.id.messageBodyEnter);
        }
    }

}
