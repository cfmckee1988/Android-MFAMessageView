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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MFAMessage implements Parcelable
{
   private static final String TAG = "MFAMessage";
   public long uid;
   public String name;
   public Bitmap profileImg;
   public @NonNull String message = "";
   public String timestamp;
   public Bitmap messageImg;
   public boolean isSender;
   // Let this class decide these values
   private boolean timeVisible;
   private boolean nameVisible;
   private Bitmap croppedImg;

   /**
    * Default constructor
    */
   public MFAMessage()
   {
      uid = 0;
      name = "";
      profileImg = null;
      message = "   ";
      timestamp = "";
      messageImg = null;
      isSender = true;
      timeVisible = true;
      nameVisible = false;
      croppedImg = null;
   }

   /**
    * Constructor with partial text message info.
    * @param name name associated with message
    * @param message message text
    * @param timestamp timestamp of message
    * @param isSender true if message was sent, false if received
    */
   public MFAMessage(String name, String message, String timestamp, boolean isSender)
   {
      this(0, name, null, message, null, timestamp, isSender);
   }

   /**
    * Constructor with partial image message info.
    * @param name name associated with message
    * @param messageImg message image
    * @param timestamp timestamp of message
    * @param isSender true if message was sent, false if received
    */
   public MFAMessage(String name, Bitmap messageImg, String timestamp, boolean isSender)
   {
      this(0, name, null, "", messageImg, timestamp, isSender);
   }

   /**
    * Constructor with all parameters
    * @param uid unique identifier
    * @param name name associated with message
    * @param profileImg profile image associated with message
    * @param message message text
    * @param messageImg message image
    * @param timestamp timestamp of message
    * @param isSender true if message was sent, false if received
    */
   public MFAMessage(long uid, String name, Bitmap profileImg, String message, Bitmap messageImg, String timestamp, boolean isSender)
   {
      this.uid = uid;
      this.name = name;
      this.profileImg = profileImg;
      this.message = message;
      this.timestamp = timestamp;
      this.messageImg = messageImg;
      this.isSender = isSender;
      timeVisible = true;
      nameVisible = false;
      setCroppedMessageImg(messageImg);
   }

   /**
    * Constructor method with a Parcel.
    * @param in MFAMessageObject represented as a Parcel
    */
   public MFAMessage(Parcel in)
   {
      uid = in.readLong();
      name = in.readString();
      String profileImgBase64 = in.readString();
      profileImg = (profileImgBase64 != null && !profileImgBase64.isEmpty()) ? decodeBase64(profileImgBase64) : null;
      message = in.readString();
      timestamp = in.readString();
      String messageImgBase64 = in.readString();
      messageImg = (messageImgBase64 != null && !messageImgBase64.isEmpty()) ? decodeBase64(messageImgBase64) : null;;
      isSender = in.readInt() != 0;
      timeVisible = in.readInt() != 0;
      nameVisible = false;
      setCroppedMessageImg(messageImg);
   }

   /**
    * Constructor method with a JSONObject.
    * @param in MFAMessage object represented as a JSONObject
    */
   public MFAMessage(JSONObject in)
   {
      try
      {
         uid = in.getLong("uid");
         name = in.getString("name");

         String profileImgBase64 = in.getString("profileImg");
         profileImg = (profileImgBase64 != null && !profileImgBase64.isEmpty()) ? decodeBase64(profileImgBase64) : null;
         message = in.getString("message");
         timestamp = in.getString("timestamp");

         String messageImgBase64 = in.getString("messageImg");
         messageImg = (messageImgBase64 != null && !messageImgBase64.isEmpty()) ? decodeBase64(messageImgBase64) : null;
         isSender = in.getBoolean("isSender");
         timeVisible = in.getBoolean("timeVisible");
         setCroppedMessageImg(messageImg);
      }
      catch (JSONException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Convert MFAMessage object to a JSONObject.
    * @return MFAMessage object represented as a JSONObject
    */
   public JSONObject writeToJSON()
   {
      JSONObject jo = new JSONObject();
      try
      {
         jo.put("uid", uid);
         jo.put("name", name);
         String profileImgBase64 = (profileImg != null) ? encodeBase64(profileImg) : "";
         jo.put("profileImg", profileImgBase64);
         jo.put("message", message);
         jo.put("timestamp", timestamp);
         String messageImgBase64 = (messageImg != null) ? encodeBase64(messageImg) : "";
         jo.put("messageImg", messageImgBase64);
         jo.put("isSender", isSender);
         jo.put("timeVisible", timeVisible);
      }
      catch (JSONException e)
      {
         e.printStackTrace();
      }
      return jo;
   }

   /**
    * Get the name associated with the message.
    * @return name associated with message
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name associated with the message.
    * @param name name associated with the message
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Get the message text.
    * @return message text
    */
   public @NonNull String getMessage()
   {
      return message;
   }

   /**
    * Set the text of the message.
    * @param message message text (non-nullable0
    */
   public void setMessage(@NonNull String message)
   {
      // Message cannot be both an image and text
      messageImg = null;
      this.message = message;
   }

   /**
    * Get the bitmap of the profile image associated with the message.
    * @return profile image associated with the message
    */
   public Bitmap getProfileImg()
   {
      return profileImg;
   }

   /**
    * Set the profile image's bitmap.
    * @param profileImg profile image associated with the message
    */
   public void setProfileImg(Bitmap profileImg)
   {
      this.profileImg = profileImg;
   }

   /**
    * Get the bitmap of the message image.
    * @return message image
    */
   public Bitmap getMessageImg()
   {
      return messageImg;
   }

   /**
    * Set the message image's bitmap.
    * @param messageImg message image
    */
   public void setMessage(@NonNull Bitmap messageImg)
   {
      // Message cannot be both an image and text.
      message = "";
      this.messageImg = messageImg;
      setCroppedMessageImg(messageImg);
   }

   public String getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(String time)
   {
      this.timestamp = time;
   }

   public long getUID()
   {
      return uid;
   }

   public void setUID(long uid)
   {
      this.uid = uid;
   }

   public boolean getIsSender()
   {
      return isSender;
   }

   public void setIsSender(boolean isSender)
   {
      this.isSender = isSender;
   }

   public boolean getTimeVisible()
   {
      return timeVisible;
   }

   public void setTimeVisible(boolean timeVisible)
   {
      this.timeVisible = timeVisible;
   }

   public boolean getNameVisible()
   {
      return nameVisible;
   }

   public void setNameVisible(boolean nameVisible)
   {
      this.nameVisible = nameVisible;
   }

   /**
    * Sets the message image to a square and rounds it's corners.
    * @param bmp bitmap to crop and round
    */
   private void setCroppedMessageImg(@NonNull Bitmap bmp)
   {
      bmp = cropBitmapToSquare(bmp);

      croppedImg = Bitmap.createBitmap(bmp.getWidth(), bmp
            .getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(croppedImg);

      final int color = 0xff424242;
      final Paint paint = new Paint();
      final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
      final RectF rectF = new RectF(rect);

      paint.setAntiAlias(true);
      canvas.drawARGB(0, 0, 0, 0);
      paint.setColor(color);
      canvas.drawRoundRect(rectF, 20f, 20f, paint);

      paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
      canvas.drawBitmap(bmp, rect, rect, paint);
   }

   /**
    * Helper method for cropping the Bitmap to a square.
    * @param bmp bitmap to crop
    * @return bitmap cropped to a square
    */
   private static Bitmap cropBitmapToSquare(@NonNull Bitmap bmp)
   {
      if (bmp.getWidth() >= bmp.getHeight())
      {
         bmp = Bitmap.createBitmap(
               bmp,
               bmp.getWidth()/2 - bmp.getHeight()/2,
               0,
               bmp.getHeight(),
               bmp.getHeight()
         );
      }
      else
      {
         bmp = Bitmap.createBitmap(
               bmp,
               0,
               bmp.getHeight()/2 - bmp.getWidth()/2,
               bmp.getWidth(),
               bmp.getWidth()
         );
      }
      return bmp;
   }

   public Bitmap getCroppedImg()
   {
      return croppedImg;
   }

   @Override
   public int describeContents()
   {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i)
   {
      parcel.writeLong(uid);
      parcel.writeString(name);

      String profileImgBase64 = (profileImg != null) ? encodeBase64(profileImg) : "";
      parcel.writeString(profileImgBase64);
      parcel.writeString(message);
      parcel.writeString(timestamp);

      String messageImgBase64 = (messageImg != null) ? encodeBase64(messageImg) : "";
      parcel.writeString(messageImgBase64);
      parcel.writeInt(isSender ? 1 : 0);
      parcel.writeInt(timeVisible ? 1 : 0);
   }

   // Parcelable creator
   public static final Parcelable.Creator<MFAMessage> CREATOR = new Parcelable.Creator<MFAMessage>()
   {
      @Override
      public MFAMessage createFromParcel(Parcel source)
      {
         return new MFAMessage(source);
      }

      @Override
      public MFAMessage[] newArray(int size)
      {
         return new MFAMessage[size];
      }
   };

   /**
    * Helper method to decode a base64 String to a bitmap
    * @param input base64 String
    * @return bitmap decoded from base64 String
    */
   private Bitmap decodeBase64(String input)
   {
      Bitmap bmp = null;
      try
      {
         byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
         if (decodedByte != null)
         {
            bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      return bmp;
   }

   /**
    * Helper method to encode a Bitmap to a base64 String.
    * @param bmp bitmap to encode
    * @return base64 String encoded from Bitmap
    */
   private String encodeBase64(@NonNull Bitmap bmp)
   {
      String base64 = "";
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
         base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return base64;
   }

   private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
   {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth)
      {

         final int halfHeight = height / 2;
         final int halfWidth = width / 2;

         // Calculate the largest inSampleSize value that is a power of 2 and keeps both
         // height and width larger than the requested height and width.
         while ((halfHeight / inSampleSize) > reqHeight
               && (halfWidth / inSampleSize) > reqWidth)
         {
            inSampleSize *= 2;
         }
      }
      Log.d(TAG,"calculate "+inSampleSize);
      return inSampleSize;
   }
}
