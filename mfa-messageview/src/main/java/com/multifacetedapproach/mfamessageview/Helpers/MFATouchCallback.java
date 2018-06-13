package com.multifacetedapproach.mfamessageview.Helpers;


public interface MFATouchCallback
{
   /**
    * Let's the MFAMessageTouchHelper notify MFAMessageView that the
    * item has been swiped.
    * @param position position in the adapter that has been swiped
    */
   void onItemSwiped(int position);

   /**
    * Let's the MFAMessageTouchHelper notify MFAMessageView that the
    * item animation has completed.
    * @param position position in the adapter that has finished animating
    */
   void clearView(int position);
}
