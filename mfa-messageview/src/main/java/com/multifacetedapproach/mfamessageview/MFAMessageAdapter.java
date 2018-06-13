/*******************************************************************************
 * Copyright 2016 Multifaceted Approach, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.multifacetedapproach.mfamessageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.multifacetedapproach.mfamessageview.Listeners.OnMFAClickListener;
import com.multifacetedapproach.mfamessageview.Listeners.OnMFALongClickListener;

import java.util.ArrayList;
import java.util.List;

public class MFAMessageAdapter extends RecyclerView.Adapter<MFAMessageAdapter.ViewHolder>
{
   // View on the right, text only
   private static final int SENT_TEXT = 0;
   // View on the left, text only
   private static final int RECEIVED_TEXT = 1;
   // View on the right, image only
   private static final int SENT_IMAGE = 2;
   // View on the left, image only
   private static final int RECEIVED_IMAGE = 3;
   // List of messages for the adpater
   private List<MFAMessage> _messageList = new ArrayList<>();
   // boolean for profileImg visibility
   private boolean _showProfileImg = false;
   // LayoutInflator for ViewHolder
   private LayoutInflater _inflater;
   // Typeface for custom font
   private Typeface _font;
   // OnClickListener for Adapter items
   private OnMFAClickListener _onMFAClickListener;
   // OnLongClickListener for Adapter items
   private OnMFALongClickListener _onMFALongClickListener;
   /**
    * Custom adapter for setting up MFAMessageView's cells
    * @param context context of calling class
    * @param messages list of MFAMessages
    */
   public MFAMessageAdapter(@NonNull Context context, @NonNull List<MFAMessage> messages)
   {
      // More efficient to initialize the inflater once rather than every time
      // onCreateViewHolder is called.
      _inflater = LayoutInflater.from(context);
      _messageList = messages;
   }

   /**
    * Custom ViewHolder class
    */
   public class ViewHolder extends RecyclerView.ViewHolder
   {
      // Your holder should contain a member variable
      // for any view that will be set as you render a row
      RelativeLayout container;
      ImageView profileImg;
      TextView message;
      TextView timestamp;
      TextView name;
      ImageView messageImg;
      RelativeLayout messageHolder;
      // We also create a constructor that accepts the entire item row
      // and does the view lookups to find each subview
      public ViewHolder(final View itemView)
      {
         // Stores the itemView in a public final member variable that can be used
         // to access the context from any ViewHolder instance.
         super(itemView);
         container = (RelativeLayout) itemView.findViewById(R.id.container);
         profileImg = (ImageView) itemView.findViewById(R.id.profileImg);
         message = (TextView) itemView.findViewById(R.id.message);
         timestamp = (TextView) itemView.findViewById(R.id.timestamp);
         name = (TextView) itemView.findViewById(R.id.name);
         messageImg = (ImageView) itemView.findViewById(R.id.messageImg);
         messageHolder = (RelativeLayout) itemView.findViewById(R.id.messageHolder);

         itemView.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View view)
            {
               int position = getAdapterPosition(); // gets item position
               if (position != RecyclerView.NO_POSITION && _messageList.size() > position)
               { // Check if the message was deleted, but the user clicked it before the UI removed it
                  MFAMessage msg = _messageList.get(position);
                  if (_onMFAClickListener != null) _onMFAClickListener.onClick(ViewHolder.this, msg, position);
               }
            }
         });

         itemView.setOnLongClickListener(new View.OnLongClickListener()
         {
            @Override
            public boolean onLongClick(View view)
            {
               int position = getAdapterPosition(); // gets item position
               if (position != RecyclerView.NO_POSITION && _messageList.size() > position)
               { // Check if an item was deleted, but the user clicked it before the UI removed it
                  MFAMessage msg = _messageList.get(position);
                  // We can access the data within the views
                  if (_onMFALongClickListener != null) _onMFALongClickListener.onLongClick(ViewHolder.this, msg, position);
                  return true;
               }
               return false;
            }
         });
      }
   }

   /**
    * Provide a custom font for text in the ViewHolders
    * @param font custom font
    */
   public void setCustomFont(@NonNull Typeface font)
   {
      _font = font;
   }

   /**
    * Provide a custom click event listener.
    * @param listener click event listener
    */
   public void setOnMFAClickListener(@NonNull OnMFAClickListener listener)
   {
      _onMFAClickListener = listener;
   }

   /**
    * Provide a custom long click event listener.
    * @param listener long click event listener
    */
   public void setOnMFALongClickListener(@NonNull OnMFALongClickListener listener)
   {
      _onMFALongClickListener = listener;
   }

   /**
    * Set whether or not you wish to display profile images.
    * @param show visible if true, gone if false
    */
   public void showProfileImg(boolean show)
   {
      _showProfileImg = show;
   }

   @Override
   public int getItemViewType(int position)
   {
      // This is done to increase the performance of the adapter, as this allows
      // us to position the layout of the view inside onCreateViewHolder rather than
      // in onBindViewHolder.
      MFAMessage message = _messageList.get(position);
      // Message was sent
      if (message.getIsSender())
      {
         // Message contains an image
         if (message.getCroppedImg() != null)
         {
            return SENT_IMAGE;
         }
         return SENT_TEXT;
      }
      // Message was received and contains an image
      if (message.getCroppedImg() != null)
      {
         return RECEIVED_IMAGE;
      }
      return RECEIVED_TEXT;
   }

   @Override
   public int getItemCount()
   {
      return _messageList.size();
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
   {
      // Return a new holder instance
      final ViewHolder holder;

      switch (viewType)
      {
         case SENT_TEXT:
         {
            View v = _inflater.inflate(R.layout.mfa_sent_text_cell, parent, false);
            holder = new ViewHolder(v);
            if (!_showProfileImg)
            {
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.profileImg.getLayoutParams();
               params.width = 0;
               holder.profileImg.setLayoutParams(params);
            }
            break;
         }
         case SENT_IMAGE:
         {
            View v = _inflater.inflate(R.layout.mfa_sent_image_cell, parent, false);
            holder = new ViewHolder(v);
            if (!_showProfileImg)
            {
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.profileImg.getLayoutParams();
               params.width = 0;
               holder.profileImg.setLayoutParams(params);
            }
            break;
         }
         case RECEIVED_TEXT:
         {
            View v = _inflater.inflate(R.layout.mfa_receive_text_cell, parent, false);
            holder = new ViewHolder(v);
            if (!_showProfileImg)
            {
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.profileImg.getLayoutParams();
               params.width = 0;
               holder.profileImg.setLayoutParams(params);
            }
            break;
         }
         case RECEIVED_IMAGE:
         {
            View v = _inflater.inflate(R.layout.mfa_receive_image_cell, parent, false);
            holder = new ViewHolder(v);
            if (!_showProfileImg)
            {
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.profileImg.getLayoutParams();
               params.width = 0;
               holder.profileImg.setLayoutParams(params);
            }
            break;
         }
         default:
         {
            View v = _inflater.inflate(R.layout.mfa_sent_text_cell, parent, false);
            holder = new ViewHolder(v);
            if (!_showProfileImg)
            {
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.profileImg.getLayoutParams();
               params.width = 0;
               holder.profileImg.setLayoutParams(params);
            }
            break;
         }
      }

      // Set the custom font here if provided
      if (_font != null) holder.message.setTypeface(_font);
      if (_font != null) holder.timestamp.setTypeface(_font);
      if (_font != null) holder.name.setTypeface(_font);

      return holder;
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position)
   {
      MFAMessage message = _messageList.get(position);
      switch (holder.getItemViewType())
      {
         case SENT_TEXT:
         {
            holder.timestamp.setText(message.getTimestamp());
            holder.timestamp.setVisibility(message.getTimeVisible() ? View.VISIBLE : View.GONE);
            holder.message.setText(message.getMessage());
            Bitmap profileBmp = message.getProfileImg();
            if (profileBmp != null) holder.profileImg.setImageBitmap(profileBmp);
            break;
         }
         case SENT_IMAGE:
         {
            holder.timestamp.setText(message.getTimestamp());
            holder.timestamp.setVisibility(message.getTimeVisible() ? View.VISIBLE : View.GONE);
            holder.messageImg.setImageBitmap(message.getCroppedImg());
            Bitmap profileBmp = message.getProfileImg();
            if (profileBmp != null) holder.profileImg.setImageBitmap(profileBmp);
            break;
         }
         case RECEIVED_TEXT:
         {
            holder.timestamp.setText(message.getTimestamp());
            holder.timestamp.setVisibility(message.getTimeVisible() ? View.VISIBLE : View.GONE);
            holder.message.setText(message.getMessage());
            holder.name.setVisibility(message.getNameVisible() ? View.VISIBLE : View.GONE);
            holder.name.setText(message.getName());
            Bitmap profileBmp = message.getProfileImg();
            if (profileBmp != null) holder.profileImg.setImageBitmap(profileBmp);
            break;
         }
         case RECEIVED_IMAGE:
         {
            holder.timestamp.setText(message.getTimestamp());
            holder.timestamp.setVisibility(message.getTimeVisible() ? View.VISIBLE : View.GONE);
            holder.messageImg.setImageBitmap(message.getCroppedImg());
            holder.name.setVisibility(message.getNameVisible() ? View.VISIBLE : View.GONE);
            holder.name.setText(message.getName());
            Bitmap profileBmp = message.getProfileImg();
            if (profileBmp != null) holder.profileImg.setImageBitmap(profileBmp);
            break;
         }
         default:
         {
            holder.timestamp.setText(message.getTimestamp());
            holder.timestamp.setVisibility(message.getTimeVisible() ? View.VISIBLE : View.GONE);
            holder.message.setText(message.getMessage());
            break;
         }
      }
   }
}
