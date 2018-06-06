package com.example.nickolasmorrison.mynote.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.example.nickolasmorrison.mynote.R;

public class NoteListTouchHelper extends ItemTouchHelper.SimpleCallback {

    Listener listener;
    boolean deleteSectionOpened;
    RelativeLayout deleteView;
    View deleteBG;
    View deleteContent;

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    public NoteListTouchHelper(int dragDirs, int swipeDirs, Listener listener, RelativeLayout deleteView) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.deleteView = deleteView;
        this.deleteBG = this.deleteView.findViewById(R.id.delete_section_background);
        this.deleteContent = this.deleteView.findViewById(R.id.delete_section_content);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(viewHolder != null){
            final View foreground = ((NoteListAdapter.NoteViewHolder)viewHolder).getCard();
            getDefaultUIUtil().onSelected(foreground);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foreground = ((NoteListAdapter.NoteViewHolder)viewHolder).getCard();
        NoteListAdapter.NoteViewHolder holder = (NoteListAdapter.NoteViewHolder)viewHolder;
        getDefaultUIUtil().onDrawOver(c,recyclerView,foreground,dX,
                0,actionState,isCurrentlyActive);

        // Render the background delete prompt
        final int containerWidth = holder.getCardContainer().getMeasuredWidth();
        final int cardWidth = holder.getCard().getMeasuredWidth();
        final int[] containerPos = new int[2];
        final int[] recyclerPos = new int[2];
        final int[] cardPos = new int[2];

        holder.getCardContainer().getLocationInWindow(containerPos);
        recyclerView.getLocationInWindow(recyclerPos);
        holder.getCard().getLocationInWindow(cardPos);

        if(cardPos[0] + cardWidth < containerWidth) {
            deleteBG.setX(cardPos[0] - containerWidth);
        } else {
            deleteBG.setX(0);
        }

        deleteView.setY(containerPos[1]-recyclerPos[1] + 24);
        if(isCurrentlyActive && !deleteSectionOpened){
            deleteContent.setVisibility(View.VISIBLE);
            deleteBG.animate().scaleY(1.0f)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(50).start();
            deleteSectionOpened = true;
        }
        else if (!isCurrentlyActive && deleteSectionOpened) {
            deleteBG.animate().scaleY(0f)
                    .setStartDelay(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(animation.getTotalDuration() != 150) return;
                            deleteContent.setVisibility(View.INVISIBLE);
                            super.onAnimationEnd(animation);
                        }
                    }).start();
            deleteSectionOpened = false;
        }
    }



    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(viewHolder instanceof NoteListAdapter.NoteViewHolder) {
            deleteSectionOpened = false;
            NoteListAdapter.NoteViewHolder holder = (NoteListAdapter.NoteViewHolder)viewHolder;
            deleteBG.clearAnimation();
            deleteContent.setVisibility(View.VISIBLE);

            AnimatorSet animSet = new AnimatorSet();
            ValueAnimator openFully = ObjectAnimator.ofFloat(deleteBG,"scaleY",1.0f);
            openFully.setDuration(50);
            openFully.setInterpolator(new AccelerateDecelerateInterpolator());
            ValueAnimator closeFully = ObjectAnimator.ofFloat(deleteBG, "scaleY", 0f);
            closeFully.setDuration(200);
            closeFully.setInterpolator(new AccelerateDecelerateInterpolator());
            closeFully.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    deleteContent.setVisibility(View.INVISIBLE);
                    listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
                    animation.removeListener(this);
                    deleteBG.clearAnimation();
                    super.onAnimationEnd(animation);
                }
            });
            animSet.play(closeFully).after(openFully);
            animSet.start();

        }
    }

    public interface Listener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
