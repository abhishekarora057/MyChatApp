package com.example.mychatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Image image;

    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public ImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int pos)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(pos);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.default_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message);
                messageViewHolder.senderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.senderMessageText.setText(messages.getMessage());
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());
            }
        }
        else if (fromMessageType.equals("image"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.default_image).into(messageViewHolder.messageSenderPicture);
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.default_image).into(messageViewHolder.messageReceiverPicture);
            }
        }
        else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/mychatapp-2fd02.appspot.com/o/Image%2Ffile_image.png?alt=media&token=b1fc3c55-c197-40e8-8240-c4191a203636")
                .into(messageViewHolder.messageSenderPicture);
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(pos).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/mychatapp-2fd02.appspot.com/o/Image%2Ffile_image.png?alt=media&token=b1fc3c55-c197-40e8-8240-c4191a203636")
                        .into(messageViewHolder.messageReceiverPicture);
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(pos).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
        if(fromUserID.equals(messageSenderId))
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userMessagesList.get(pos).getType().equals("pdf")||userMessagesList.get(pos).getType().equals("docx"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete","Download"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete/Download");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    deleteBothMessages(pos,messageViewHolder);
                                    //Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    //messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                if(i==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(pos).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if(userMessagesList.get(pos).getType().equals("text"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete this message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    deleteBothMessages(pos,messageViewHolder);
                                    //Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    //messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if(userMessagesList.get(pos).getType().equals("image"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete","View"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete/View");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    deleteBothMessages(pos,messageViewHolder);
                                    //Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    //messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                if(i==1)
                                {
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),ImageViewActivity.class);
                                    intent.putExtra("url",userMessagesList.get(pos).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        else
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userMessagesList.get(pos).getType().equals("pdf")||userMessagesList.get(pos).getType().equals("docx"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Download"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Download this file ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(pos).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if(userMessagesList.get(pos).getType().equals("image"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "View"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("View this image ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),ImageViewActivity.class);
                                    intent.putExtra("url",userMessagesList.get(pos).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }
    private void deleteBothMessages(final int position,final MessageViewHolder holder)
    {
        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    Toast.makeText(holder.itemView.getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                else
                {
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful())
                                Toast.makeText(holder.itemView.getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }
}