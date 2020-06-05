package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private RecyclerView.Adapter adapter;

    private int dragFlags;
    private int swipeFlags;

    private boolean isLongPressDragEnabled;
    private boolean isItemViewSwipeEnabled;

    private boolean fadeout;

    private OnMoveListener onMoveListener;
    private OnSwipedListener onSwipedListener;
    private OnClearListener onClearListener;

    public ItemTouchHelperCallback(int dragFlags, int swipeFlags) {
        this.dragFlags = dragFlags;
        this.swipeFlags = swipeFlags;

        this.isLongPressDragEnabled = false;
        this.isItemViewSwipeEnabled = false;

        this.fadeout = true;
    }

    public void setLongPressDragEnabled(boolean isLongPressDragEnabled) {
        this.isLongPressDragEnabled = isLongPressDragEnabled;
    }

    public void setItemViewSwipeEnabled(boolean isItemViewSwipeEnabled) {
        this.isItemViewSwipeEnabled = isItemViewSwipeEnabled;
    }

    public void setFadeout(boolean fadeout) {
        this.fadeout = fadeout;
    }

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    public void setOnSwipedListener(OnSwipedListener onSwipedListener) {
        this.onSwipedListener = onSwipedListener;
    }

    public void setOnClearListener(OnClearListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isLongPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isItemViewSwipeEnabled;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType())
            return false;

        if (onMoveListener != null) {
            onMoveListener.onMove(recyclerView, source, target);
            return true;
        }
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (onSwipedListener != null) {
            onSwipedListener.onSwiped(viewHolder, direction);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (fadeout && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out view
            final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (fadeout) viewHolder.itemView.setAlpha(1.0f);

        if (onClearListener != null) {
            onClearListener.onClearView(recyclerView, viewHolder);
        }
    }

    public interface OnMoveListener {
        void onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target);
    }

    public interface OnSwipedListener {
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction);
    }

    public interface OnClearListener {
        void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder);
    }

}
