package com.pw.box.ui.fragments.data.item_adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.App;
import com.pw.box.R;
import com.pw.box.ui.fragments.data.MyXRecyclerView;

/**
 * recyclerview长按排序的工具
 * Created by Administrator on 2017/2/21.
 */

public class AddItemTouchHelper extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0F;
    private final ItemTouchHelperCallback callback;
    int l, r;
    private MyXRecyclerView myXRecyclerView;

    public AddItemTouchHelper(ItemTouchHelperCallback adapter, XRecyclerView recyclerView) {
        this(0, 0, adapter, recyclerView);
    }

    public AddItemTouchHelper(int l, int r, ItemTouchHelperCallback adapter, XRecyclerView recyclerView) {
        this.l = l;
        this.r = r;

        this.callback = adapter;
        this.myXRecyclerView = (MyXRecyclerView) recyclerView;
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        if (myXRecyclerView.downX > App.getContext().getResources().getDimensionPixelSize(R.dimen.add_item_name_width)) {
            dragFlags = 0;
            swipeFlags = 0;
        }

        if (viewHolder.getItemViewType() == AddItemAdapter.VIEW_TYPE_TEXT
                || viewHolder.getItemViewType() == AddItemAdapter.VIEW_TYPE_SUB_TYPE
                || viewHolder.getItemViewType() == AddItemAdapter.VIEW_TYPE_PASSWORD
                ) {
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        return makeMovementFlags(0, 0);
    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        /*if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        } else*/
        {
            return this.callback.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        }
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        this.callback.onItemDismiss(viewHolder.getAdapterPosition());
    }

    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == 1) {
            View itemView = viewHolder.itemView;
            float alpha = 1.0F - Math.abs(dX) / (float) itemView.getWidth();
            itemView.setAlpha(alpha);
        }
    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != 0) {
            viewHolder.itemView.setBackgroundColor(-3355444);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(1.0F);
        viewHolder.itemView.setBackgroundColor(0);
    }

    public interface ItemTouchHelperCallback {
        boolean onItemMove(int var1, int var2);

        void onItemDismiss(int var1);
    }

}
