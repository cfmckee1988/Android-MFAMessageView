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
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class MFALinearLayoutManager extends LinearLayoutManager
{

   private static final float MILLISECONDS_PER_INCH = 50f; // Slows down the default scroll speed

   public MFALinearLayoutManager(Context context)
   {
      super(context);
   }

   public MFALinearLayoutManager(Context context, int orientation, boolean reverseLayout)
   {
      super(context, orientation, reverseLayout);
   }

   public MFALinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
   {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override
   public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
   {
      final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext())
      {

         @Override
         public PointF computeScrollVectorForPosition(int targetPosition)
         {
            return MFALinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
         }

         @Override
         protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
         {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
         }
      };

      linearSmoothScroller.setTargetPosition(position);
      startSmoothScroll(linearSmoothScroller);
   }

   @Override
   public boolean supportsPredictiveItemAnimations()
   {
      return true;
   }

   @Override
   public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
   {
      super.onLayoutChildren(recycler, state);
   }
}