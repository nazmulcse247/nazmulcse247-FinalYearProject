package com.nazmul.finalyearproject;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nazmul.finalyearproject.Interface.IFirebaseLoadDone;
import com.nazmul.finalyearproject.Interface.IRecyclerItemClickListener;
import com.nazmul.finalyearproject.Model.MyResponse;
import com.nazmul.finalyearproject.Model.Request;
import com.nazmul.finalyearproject.Model.User;
import com.nazmul.finalyearproject.Remote.IFCMService;
import com.nazmul.finalyearproject.Utils.Common;
import com.nazmul.finalyearproject.ViewHolder.UserViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter,searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        //init api

        ifcmService = Common.getFCMService();


        //init view

        searchBar = findViewById(R.id.material_search_bar);
        searchBar.setElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<>();

                for(String search:suggestList){

                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (!enabled){
                    if (adapter != null){

                        recycler_all_user.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_user = findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager) layoutManager).getOrientation()));


        firebaseLoadDone =this;

        loadUserList();
        loadSearchData();
    }

    private void loadSearchData() {

        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION);

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapShot:dataSnapshot.getChildren()){
                    User user = userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFaild(databaseError.getMessage());
            }
        });
    }

    private void loadUserList() {

        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if(model.getEmail().equals(Common.logedUser.getEmail())){

                    holder.text_user_email.setText(new StringBuilder(model.getEmail()).append("(me)"));
                    holder.text_user_email.setTypeface(holder.text_user_email.getTypeface(), Typeface.ITALIC);

                }
                else {
                    holder.text_user_email.setText(new StringBuilder(model.getEmail()));
                }
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                        showDailogRequest(model);

                    }
                });

            }
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);


            }
        };

        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    @Override
    protected void onStop() {

        if(adapter != null)
            adapter.startListening();

        if (searchAdapter != null)
            searchAdapter.startListening();

        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(adapter != null)
            adapter.startListening();

        if (searchAdapter != null)
            searchAdapter.startListening();
    }

    private void startSearch(String text_search) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if(model.getEmail().equals(Common.logedUser.getEmail())){

                    holder.text_user_email.setText(new StringBuilder(model.getEmail()).append("(me)"));
                    holder.text_user_email.setTypeface(holder.text_user_email.getTypeface(), Typeface.ITALIC);


                }
                else {
                    holder.text_user_email.setText(new StringBuilder(model.getEmail()));
                }

                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                        showDailogRequest(model);

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);

            }
        };

        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);


    }

    private void showDailogRequest(final User model) {
        AlertDialog.Builder alertDailog = new AlertDialog.Builder(this,R.style.MyRequestDailog);
        alertDailog.setTitle("Friend Request");
        alertDailog.setMessage("Do you want to send friend request "+model.getEmail());
        alertDailog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDailog.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDailog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.logedUser.getUid())
                        .child(Common.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null)
                                    sendFriendRequest(model);
                                else
                                    Toast.makeText(AllPeopleActivity.this,"You and "+model.getEmail()+" are already friend",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
        alertDailog.show();


    }

    private void sendFriendRequest(final User model) {

        DatabaseReference token = FirebaseDatabase.getInstance().getReference(Common.TOKEN);
        token.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            Toast.makeText(AllPeopleActivity.this,"token error",Toast.LENGTH_SHORT).show();
                        else {

                            Request request = new Request();

                            Map<String,String> sendData = new HashMap<>();
                            sendData.put(Common.FROM_UID,Common.logedUser.getUid());
                            sendData.put(Common.FROM_NAME,Common.logedUser.getEmail());
                            sendData.put(Common.TO_UID,model.getUid());
                            sendData.put(Common.TO_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));

                            request.setData(sendData);

                            //send

                            compositeDisposable.add(ifcmService.toFriendRequestToUser(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<MyResponse>() {
                                @Override
                                public void accept(MyResponse myResponse) throws Exception {
                                    if (myResponse.success == 1)
                                        Toast.makeText(AllPeopleActivity.this,"Request sent!",Toast.LENGTH_LONG).show();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(AllPeopleActivity.this,throwable.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFaild(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}

