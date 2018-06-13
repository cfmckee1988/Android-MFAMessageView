package com.multifacetedapproach.mfamessageview.Listeners;

import android.support.annotation.NonNull;

import com.multifacetedapproach.mfamessageview.MFAMessage;
import com.multifacetedapproach.mfamessageview.MFAMessageAdapter;

public interface OnMFALongClickListener
{
   /**
    * Returns the MFAMessage object and position of the message that was
    * long clicked.
    * @param message MFAMessage object of the message clicked
    * @param position position in the MFAMessageAdapter
    */
   void onLongClick(MFAMessageAdapter.ViewHolder view, @NonNull MFAMessage message, int position);
}
