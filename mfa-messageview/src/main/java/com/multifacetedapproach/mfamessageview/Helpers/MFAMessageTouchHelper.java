package com.multifacetedapproach.mfamessageview.Helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class MFAMessageTouchHelper extends ItemTouchHelper.Callback
{
   private MFATouchCallback _callback;

   private boolean _swipeEnabled;

   public MFAMessageTouchHelper(MFATouchCallback callback)
   {
      _callback = callback;
   }

   public void setSwipeCapability(boolean enabled)
   {
      _swipeEnabled = enabled;
   }

   @Override
   public boolean isItemViewSwipeEnabled()
   {
      return _swipeEnabled;
   }

   @Override
   public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
   {
      int type = viewHolder.getItemViewType();
      if (type == 0 || type == 2 || type == 4)
      {
         final int swipeFlags = ItemTouchHelper.START;
         return makeMovementFlags(0, swipeFlags);
      }
      else
      {
         final int swipeFlags = ItemTouchHelper.END;
         return makeMovementFlags(0, swipeFlags);
      }
   }

   @Override
   public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
   {
      return true;
   }

   @Override
   public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
   {
      if (_callback != null) _callback.onItemSwiped(viewHolder.getAdapterPosition());
   }

   @Override
   public float getSwipeEscapeVelocity(float defaultValue)
   {
      return super.getSwipeEscapeVelocity(defaultValue);
   }

   @Override
   public float getSwipeVelocityThreshold(float defaultValue)
   {
      return super.getSwipeVelocityThreshold(defaultValue);
   }

   @Override
   public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
   {
      super.onSelectedChanged(viewHolder, actionState);
   }

   @Override
   public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
   {
      super.clearView(recyclerView, viewHolder);
      if (_callback != null) _callback.clearView(viewHolder.getAdapterPosition());
   }
}
