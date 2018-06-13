package com.multifacetedapproach.mfamessageview.Listeners;

import android.support.annotation.NonNull;

import com.multifacetedapproach.mfamessageview.MFAMessage;
import com.multifacetedapproach.mfamessageview.MFAMessageAdapter;

public interface OnMFAClickListener
{
   /**
    * Returns the MFAMessage object and position of the message clicked.
    * @param message MFAMessage object of the message clicked
    * @param position position in the MFAMessageAdapter
    */
   void onClick(MFAMessageAdapter.ViewHolder holder, @NonNull MFAMessage message, int position);
}
