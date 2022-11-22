package com.app.edithormobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.edithormobile.R;
import com.app.edithormobile.layouts.AddNote;
import com.app.edithormobile.models.NoteModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    Context context;
    ArrayList<NoteModel> notes;
    DatabaseReference removeRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    //Constructor
    public NoteAdapter(Context context, ArrayList<NoteModel> notes) {
        this.context = context;
        this.notes = notes;
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_note_item, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {
        NoteModel mNote = notes.get(position);
        holder.tvTitle.setText(mNote.getNotBaslik());
        holder.tvNote.setText(mNote.getNotIcerigi());
        holder.tvOlusturmaTarihi.setText(mNote.getNotOlusturmaTarihi());

        //Long press remove item
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        removeRef = FirebaseDatabase.getInstance()
                .getReference().child("Kullanicilar").child(user_id).child("Notlarim").child(mNote.getNoteID());

        //long delete
        holder.card.setOnLongClickListener(v -> {

            holder.card.setChecked(!holder.card.isChecked());

            // HashSet<String> dizi = new HashSet<>();

           if (holder.card.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Emin misiniz?");
                builder.setMessage("Notu silmek istediğinize emin misiniz?");
                builder.setNegativeButton("Hayır", (dialog, which) -> Toast.makeText(context, "Vazgeçildi.", Toast.LENGTH_SHORT).show());
                builder.setPositiveButton("Evet", (dialogInterface, i) -> {
                    removeRef.setValue(null);
                    notes.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Notunuz silindi.", Toast.LENGTH_SHORT).show();
                });
               builder.show();
            }


            return true;
        });

        //Veri alma
        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNote.class);
            intent.putExtra("baslik", mNote.getNotBaslik());
            intent.putExtra("icerik", mNote.getNotIcerigi());
            context.startActivity(intent);

            //Update

        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    //ViewHolder
    public static class NoteHolder extends RecyclerView.ViewHolder {

        TextView tvNote, tvTitle, tvOlusturmaTarihi;
        MaterialCardView card;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOlusturmaTarihi = itemView.findViewById(R.id.tvOlusturmaTarihi);

        }

    }


}
