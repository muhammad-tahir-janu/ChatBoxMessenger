package com.tahir70108.chatboxmessenger;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

     private FirebaseFirestore firebaseFirestore;
     LinearLayoutManager linearLayoutManager;
     FirebaseAuth firebaseAuth;

     ImageView ivUserImage;

     FirestoreRecyclerAdapter<FirebaseModel,NoteViewHodler> chatAdapter;

     RecyclerView rvChat;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_chat, container, false);
      firebaseAuth= FirebaseAuth.getInstance();
      firebaseFirestore =FirebaseFirestore.getInstance();
        rvChat =view.findViewById(R.id.rvChat);

        Query query =firebaseFirestore.collection("Users");
        FirestoreRecyclerOptions<FirebaseModel> allUserName = new FirestoreRecyclerOptions
                .Builder<FirebaseModel>()
                .setQuery(query,FirebaseModel.class)
                .build();
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHodler>(allUserName) {
            @Override
            protected void onBindViewHolder(@NonNull  NoteViewHodler holder, int position,  FirebaseModel model) {
                holder.particularUserName.setText(model.getName());
                String uri = model.getImage();
                Picasso.get().load(uri).into(ivUserImage);

                if(model.getStatus().equals("Online")){
                    holder.userStatus.setText(model.getStatus());
                    holder.userStatus.setTextColor(Color.GREEN);

                }else {
                    holder.userStatus.setText(model.getStatus());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Item Is Clicked ", Toast.LENGTH_SHORT).show();

                    }
                });


            }


            @NonNull
            @Override
            public NoteViewHodler onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_chat_view,parent,false);
                return  new NoteViewHodler(view1);
            }
        };
        rvChat.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapter);

        return view;
    }

    public class NoteViewHodler extends RecyclerView.ViewHolder{
        private TextView particularUserName;
        private TextView userStatus;

        public NoteViewHodler(@NonNull  View itemView) {
            super(itemView);
            particularUserName = itemView.findViewById(R.id.tvShowUserName);
            userStatus = itemView.findViewById(R.id.tvUserStatus);
            ivUserImage = itemView.findViewById(R.id.ivUserProfileImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null){
            chatAdapter.stopListening();
        }
    }
}