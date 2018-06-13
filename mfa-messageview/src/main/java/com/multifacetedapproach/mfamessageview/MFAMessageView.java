/*******************************************************************************
 * Copyright 2016 - 2017 Multifaceted Approach, LLC
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
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;

import com.multifacetedapproach.mfamessageview.Listeners.OnMFAClickListener;
import com.multifacetedapproach.mfamessageview.Listeners.OnMFALongClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This is the core class for this library. It is the RecyclerView
 * that displays our list of messages and sets up our custom adapter
 * optimized for performance. It also aides in configuring how the message
 * list should look by configuring timestamps and their visibility along with
 * the visibility of displaying names. Additional methods have been provided
 * to give user's flexibility for using this class.
 */
public class MFAMessageView extends RecyclerView
{
   private OnMFAClickListener _onMFAClickListener;

   private OnMFALongClickListener _onMFALongClickListener;
   // Tag for log messages
   private static final String TAG = "MFAMessageView";
   // Context of application
   private Context _context;
   // List of current MFAMessages
   private List<MFAMessage> _messages = new ArrayList<>();
   // List of timestamps converted to milliseconds as longs
   private List<Long> _timeInMillisList = new ArrayList<>();
   // List of names associatec with the message
   private List<String> _namesList = new ArrayList<>();
   // SimpleDateFormat used for provided timestamps
   private static SimpleDateFormat _sdf;
   // Template format suffix used for configuring timestamp
   private static final String TEMPLATE_SUFFIX = "h:mm a";
   // Ten minutes represented in milliseconds
   private static final long TEN_MINUTES = 600000;
   // Custom Adapter for the recycler view
   private MFAMessageAdapter _adapter;
   // Custom LinearLayoutManager for the recycler view
   private MFALinearLayoutManager _llm;
   // Custom Typeface for font
   private Typeface _font;

   public MFAMessageView(Context context)
   {
      super(context);
      init(context);
   }

   public MFAMessageView(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      init(context);
   }

   public MFAMessageView(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
      init(context);
   }

   /**
    * Custom init method
    * @param context context requesting class
    */
   public void init(Context context)
   {
      _context = context;
      // Initiate custom SimpleDateFormat
      _sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
      // This is simply done to guarantee _messages is not null and a fresh set of messages when
      // initiating this class
      _messages = new ArrayList<>();
      // Initiate custom LinearLayoutManager
      _llm = new MFALinearLayoutManager(_context);
      setLayoutManager(_llm);
      // Default message animator
      setItemAnimator(new DefaultItemAnimator());
   }

   /**
    * Helper method that initiates the MFAMessageAdapter and any other
    * variables that need to be initiated with it.
    */
   private void initAdapter()
   {
      _adapter = new MFAMessageAdapter(_context, _messages);
      // Set the apapter's custom font
      if (_font != null)
      {
         _adapter.setCustomFont(_font);
         // Setting it to null prevents us from repeatedly setting this font
         _font = null;
      }
      // Set the adapter's custom onClickListener
      if (_onMFAClickListener != null)
      {
         _adapter.setOnMFAClickListener(_onMFAClickListener);
         // Setting it to null prevents us from repeatedly setting this callback
         _onMFAClickListener = null;
      }
      // Set the adapter's custom onLongClickListener
      if (_onMFALongClickListener != null)
      {
         _adapter.setOnMFALongClickListener(_onMFALongClickListener);
         // Setting it to null prevents us from repeatedly setting this callback
         _onMFALongClickListener = null;
      }
   }

   @Override
   public void setAdapter(Adapter adapter)
   {
      super.setAdapter(adapter);
      // Because we cannot prevent users from using this function, we must account for it
      // by ensuring they provide an MFAMessageAdapter object. We also want to check that the
      // adapter provided isn't already equal to our current adapter to prevent thrashing of
      // messages.
      if (adapter != null && adapter instanceof MFAMessageAdapter)
      {
         MFAMessageAdapter temp = (MFAMessageAdapter) adapter;
         if (_adapter != null)
         {
            int tempHash = temp.hashCode();
            int hash = _adapter.hashCode();
            // setAdapter was called by an external class, change our current
            // adapter if the hash are unequal
            if (tempHash != hash)
            {
               _adapter = temp;
            }
         }
         else
         {
            // Adapter is being set for the first time
            _adapter = temp;
         }
      }
   }

   @Override
   protected void onLayout(boolean changed, int left, int top, int right, int bottom)
   {
      super.onLayout(changed, left, top, right, bottom);
      // changed is true any time the keyboard is opened or closed
      if (changed && !_messages.isEmpty())
      {
         smoothScrollToPosition(_messages.size()-1);
      }
   }

   /**
    * Provide a custom font for MFAMessageView TextViews.
    * @param font custom font
    */
   public void setCustomFont(@NonNull Typeface font)
   {
      if (_adapter != null)
      {
         _adapter.setCustomFont(font);
      }
      else
      {
         // Adapter hasn't been set yet, store the font
         // for now.
         _font = font;
      }
   }

   /**
    * Provide a custom click event listener.
    * @param listener click event listener
    */
   public void setOnMFAClickListener(@NonNull OnMFAClickListener listener)
   {
      if (_adapter != null)
      {
         _adapter.setOnMFAClickListener(listener);
      }
      else
      {
         // In case this is called before the adapter has been set
         _onMFAClickListener = listener;
      }
   }

   /**
    * Provide a custom long click event listener.
    * @param listener long click event listener
    */
   public void setOnMFALongClickListener(@NonNull OnMFALongClickListener listener)
   {
      if (_adapter != null)
      {
         _adapter.setOnMFALongClickListener(listener);
      }
      else
      {
         // In case this is called before the adapter has been set
         _onMFALongClickListener = listener;
      }
   }

   /**
    * Set the visibility of profile images in the message list.
    * @param show true if profile images should be shown, false otherwise
    */
   public void showProfileImages(boolean show)
   {
      if (_adapter != null)
      {
         _adapter.showProfileImg(show);
      }
   }

   /**
    * Set a custom SimpleDateFormat to configure timestamps
    * @param sdf custom SimpleDateFormat
    */
   public void setSimpleDateFormat(@NonNull SimpleDateFormat sdf)
   {
      _sdf = sdf;
   }

   /**
    * Get the current SimpleDateFormat being used
    * @return current SimpleDateFormat
    */
   public SimpleDateFormat getSimpleDateFormat()
   {
      return _sdf;
   }

   @Override
   public void setLayoutManager(LayoutManager layout)
   {
      super.setLayoutManager(layout);
   }

   /**
    * Provide a list of messages
    * @param messages list of all current messages
    */
   public void setMessages(@NonNull List<MFAMessage> messages)
   {
      _messages = messages;
      // Clear any existing names and timestamps as a new
      // set mmessages have been provided
      _timeInMillisList.clear();
      _namesList.clear();
      boolean showProfileImg = true;
      for (MFAMessage message : _messages)
      {
         configureContentVisibility(message);
         if (message.getProfileImg() == null)
         {
            showProfileImg = false;
         }
      }
      initAdapter();
      _adapter.showProfileImg(showProfileImg);
      setAdapter(_adapter);
   }

   /**
    * Add a new message to the recycler view
    * @param message most recent message
    */
   public void addMessage(@NonNull MFAMessage message)
   {
      if (message.getMessageImg() == null && message.getMessage().isEmpty())
      {
         Log.w(TAG, "Message content is empty");
         //return;
      }
      int prevPosition = _messages.size()-1;
      // Configure current messages time stamp
      configureContentVisibility(message);
      _messages.add(message);
      // In case this is the first message.
      if (_adapter == null)
      {
         initAdapter();
         setAdapter(_adapter);
      }
      showProfileImages(message.getProfileImg() != null);
      if (shouldAnimateNewMessage(prevPosition))
      {
         _adapter.notifyItemInserted(_messages.size()-1);
         scrollToPosition(_messages.size()-1);
      }
      else
      {
         // Last visible image was not the previous message, this prevents
         // adding multiple cell addition animations.
         _adapter.notifyItemInserted(_messages.size()-1);
         smoothScrollToPosition(_messages.size()-1);
      }
   }

   /**
    * Remove a message at a specific position.
    * @param position index position of the message
    * @return true if a message was removed at position, false otherwise
    */
   public boolean removeMessageAtPosition(int position)
   {
      if (_messages.size() > position && _adapter != null)
      {
         // Check that the list of timestamps in milliseconds is equal to that
         // of the messages list. If it is, remove the timestamp at the same index.
         if (_timeInMillisList.size() == _messages.size()) _timeInMillisList.remove(position);
         if (_namesList.size() == _messages.size()) _namesList.remove(position);

         boolean timeVisible = _messages.get(position).getTimeVisible();
         _messages.remove(position);
         _adapter.notifyItemRemoved(position);
         if (_messages.size() > position)
         {
            updateMessage(timeVisible, position, _messages.get(position));
         }
         return true;
      }
      return false;
   }

   /**
    * Clear all messages
    * @return true if messages were cleared, false otherwise
    */
   public boolean clearAllMessages()
   {
      if (!_messages.isEmpty() && _adapter != null)
      {
         //setItemAnimator(null);
         _timeInMillisList.clear();
         _namesList.clear();
         int size = _messages.size();
         _messages.clear();
         _adapter.notifyItemRangeRemoved(0, size);
         return true;
      }
      return false;
   }

   private void updateMessage(boolean timeVisible, int position, MFAMessage msgToUpdate)
   {
      if (_adapter != null)
      {
         msgToUpdate.setTimeVisible(timeVisible);
         boolean update = timeVisible;
         // Only consider displaying the name if it's non-null, not empty, and not the sender
         // as the sender should only be the current user.
         if (msgToUpdate.getName() != null && !msgToUpdate.getName().isEmpty() && !msgToUpdate.getIsSender())
         {
            if (!_namesList.isEmpty() && position > 0)
            {
               String previousName = _namesList.get(position-1);
               // This check is done so that if a string of messages arrive from the same
               // entity in a row we only display the name of the with the first message of that
               // string.
               if (previousName != null)
               {
                  boolean setVisible = !previousName.equalsIgnoreCase(msgToUpdate.getName());
                  msgToUpdate.setNameVisible(setVisible);
                  if (setVisible) update = true;
               }
            }
            else
            {
               // First message received
               msgToUpdate.setNameVisible(true);
               update = true;
            }
         }
         if (update) _adapter.notifyItemChanged(position);
      }
   }

   /**
    * Determine if an animation should occur.
    * @param position index position of the last message before new message add
    * @return true if the adapter should animate, false otherwise
    */
   private boolean shouldAnimateNewMessage(int position)
   {
      int lastVisibleMessage = _llm.findLastVisibleItemPosition();
      return position < 0 || position == lastVisibleMessage;
   }

   /**
    * Helper method to configure the timestamps so that they display in a terse format
    * and only display those that are necessary, and to display names associated with
    * received messages so that they appear only when necessary
    * @param message message to be configured
    */
   private void configureContentVisibility(MFAMessage message)
   {
      long time = getTimeInMillis(message.getTimestamp());
      // If time equals zero, our timestamp did not match
      // our SimpleDateFormat template.
      if (time != 0)
      {
         if (!_timeInMillisList.isEmpty())
         {
            // Calculates the difference in milliseconds between a message and it's previous
            // message. This is done so that the timestamp isn't shown in every single cell.
            long t = _timeInMillisList.get(_timeInMillisList.size() - 1);
            long delta = time - t;
            // Only display if more than 10 minutes has elapsed since previous message
            message.setTimeVisible(delta > TEN_MINUTES);
         }
         else
         {  // First message in queue
            message.setTimeVisible(true);
         }
         message.setTimestamp(getFormattedDate(time));
      }
      // Only consider displaying the name if it's non-null, not empty, and not the sender
      // as the sender should only be the current user.
      if (message.getName() != null && !message.getName().isEmpty() && !message.getIsSender())
      {
         if (!_namesList.isEmpty())
         {
            String previousName = _namesList.get(_namesList.size()-1);
            // This check is done so that if a string of messages arrive from the same
            // entity in a row we only display the name of the with the first message of that
            // string.
            if (previousName != null)
            {
               message.setNameVisible(!previousName.equalsIgnoreCase(message.getName()));
            }
            else
            {
               message.setNameVisible(true);
            }
         }
         else
         {
            // First message received
            message.setNameVisible(true);
         }
      }

      _timeInMillisList.add(time);
      _namesList.add(message.getName());
   }

   /**
    * Helper method to convert a timestamp into milliseconds represented as
    * a long
    * @param formattedTimestamp timestamp represented by a string
    * @return timestamp in milliseconds
    */
   private static long getTimeInMillis(String formattedTimestamp)
   {
      long timeMillis = 0;

      try
      {
         Date time = _sdf.parse(formattedTimestamp);
         // The time in milliseconds of the messages formattedTimeStamp
         timeMillis = time.getTime();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return timeMillis;
   }

   /**
    * Helper method to to format a timestamp represented in milliseconds to
    * a terse timestamp represented as a string
    * @param smsTimeInMilis timestamp in milliseconds
    * @return reformatted timestamp
    */
   private static String getFormattedDate(long smsTimeInMilis)
   {
      Calendar now = Calendar.getInstance();
      Calendar smsTime = Calendar.getInstance();
      smsTime.setTimeInMillis(smsTimeInMilis);

      // Message was sent today
      if (now.get(Calendar.DAY_OF_YEAR) == smsTime.get(Calendar.DAY_OF_YEAR)
            && now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR))
      {
         return "Today "+ DateFormat.format(TEMPLATE_SUFFIX, smsTime).toString();
      }
      // Message was sent yesterday
      else if (now.get(Calendar.DAY_OF_YEAR) - smsTime.get(Calendar.DAY_OF_YEAR) == 1
            && now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR))
      {
         return "Yesterday " + DateFormat.format(TEMPLATE_SUFFIX, smsTime).toString();
      }
      // Message was sent within a week
      else if (now.get(Calendar.DAY_OF_YEAR) - smsTime.get(Calendar.DAY_OF_YEAR) <= 6
            && now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR))
      {
         return DateFormat.format("EEE "+TEMPLATE_SUFFIX, smsTime).toString();
      }
      // Message is more than a week old but was sent within the calendar year
      else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR))
      {
         return DateFormat.format("EEE, MMM d, "+TEMPLATE_SUFFIX, smsTime).toString();
      }
      // Message is more than a week old and was sent from a different year
      else
      {
         return DateFormat.format("M/d/yyyy " + TEMPLATE_SUFFIX, smsTime).toString();
      }
   }
}
