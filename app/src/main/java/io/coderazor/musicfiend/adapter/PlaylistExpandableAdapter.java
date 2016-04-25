package io.coderazor.musicfiend.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.coderazor.musicfiend.R;
import io.coderazor.musicfiend.model.ParentListItem;
import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfiend.model.Track;
import io.coderazor.musicfiend.view.PlaylistViewHolder;
import io.coderazor.musicfiend.view.TrackViewHolder;

/**
 * Created by joelhartshorn on 4/23/16.
 */
public class PlaylistExpandableAdapter extends ExpandableRecyclerAdapter<PlaylistViewHolder, TrackViewHolder> {

    private static final String LOG_NAME = "PlaylistExpandableAdpt";

    private LayoutInflater mInflater;
    /**
     * Public primary constructor.
     *
     * @param parentItemList the list of parent items to be displayed in the RecyclerView
     */
    public PlaylistExpandableAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
    }

    public PlaylistExpandableAdapter(@NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(LOG_NAME,"onCreateViewHolder");
        return super.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(LOG_NAME,"onBindViewHolder");
        super.onBindViewHolder(holder, position);

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public List<? extends ParentListItem> getParentItemList() {
        return super.getParentItemList();
    }

    @Override
    public void onParentListItemExpanded(int position) {
        super.onParentListItemExpanded(position);
    }

    @Override
    public void onParentListItemCollapsed(int position) {
        super.onParentListItemCollapsed(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void setExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
        super.setExpandCollapseListener(expandCollapseListener);
    }

    @Override
    public void expandParent(int parentIndex) {
        super.expandParent(parentIndex);
    }

    @Override
    public void expandParent(ParentListItem parentListItem) {
        super.expandParent(parentListItem);
    }

    @Override
    public void expandParentRange(int startParentIndex, int parentCount) {
        super.expandParentRange(startParentIndex, parentCount);
    }

    @Override
    public void expandAllParents() {
        super.expandAllParents();
    }

    @Override
    public void collapseParent(int parentIndex) {
        super.collapseParent(parentIndex);
    }

    @Override
    public void collapseParent(ParentListItem parentListItem) {
        super.collapseParent(parentListItem);
    }

    @Override
    public void collapseParentRange(int startParentIndex, int parentCount) {
        super.collapseParentRange(startParentIndex, parentCount);
    }

    @Override
    public void collapseAllParents() {
        super.collapseAllParents();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected Object getListItem(int position) {
        return super.getListItem(position);
    }

    @Override
    public void notifyParentItemInserted(int parentPosition) {
        Log.d(LOG_NAME,"notifyParentItemInserted");
        super.notifyParentItemInserted(parentPosition);

    }

    @Override
    public void notifyParentItemRangeInserted(int parentPositionStart, int itemCount) {
        Log.d(LOG_NAME,"notifyParentItemRangeInserted");
        super.notifyParentItemRangeInserted(parentPositionStart, itemCount);
    }

    @Override
    public void notifyParentItemRemoved(int parentPosition) {
        Log.d(LOG_NAME,"notifyParentItemRemoved");
        super.notifyParentItemRemoved(parentPosition);
    }

    @Override
    public void notifyParentItemRangeRemoved(int parentPositionStart, int itemCount) {
        Log.d(LOG_NAME,"notifyParentItemRangeRemoved");
        super.notifyParentItemRangeRemoved(parentPositionStart, itemCount);
    }

    @Override
    public void notifyParentItemChanged(int parentPosition) {
        Log.d(LOG_NAME,"notifyParentItemChanged");
        super.notifyParentItemChanged(parentPosition);
    }

    @Override
    public void notifyParentItemRangeChanged(int parentPositionStart, int itemCount) {
        Log.d(LOG_NAME,"notifyParentItemRangeChanged");
        super.notifyParentItemRangeChanged(parentPositionStart, itemCount);
    }

    @Override
    public void notifyParentItemMoved(int fromParentPosition, int toParentPosition) {
        Log.d(LOG_NAME,"notifyParentItemMoved");
        super.notifyParentItemMoved(fromParentPosition, toParentPosition);
    }

    @Override
    public void notifyChildItemInserted(int parentPosition, int childPosition) {
        Log.d(LOG_NAME,"notifyChildItemInserted");
        super.notifyChildItemInserted(parentPosition, childPosition);
    }

    @Override
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart, int itemCount) {
        Log.d(LOG_NAME,"notifyChildItemRangeInserted");
        super.notifyChildItemRangeInserted(parentPosition, childPositionStart, itemCount);
    }

    @Override
    public void notifyChildItemRemoved(int parentPosition, int childPosition) {
        Log.d(LOG_NAME,"notifyChildItemRemoved");
        super.notifyChildItemRemoved(parentPosition, childPosition);
    }

    @Override
    public void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart, int itemCount) {
        super.notifyChildItemRangeRemoved(parentPosition, childPositionStart, itemCount);
    }

    @Override
    public void notifyChildItemChanged(int parentPosition, int childPosition) {
        super.notifyChildItemChanged(parentPosition, childPosition);
    }

    @Override
    public void notifyChildItemRangeChanged(int parentPosition, int childPositionStart, int itemCount) {
        super.notifyChildItemRangeChanged(parentPosition, childPositionStart, itemCount);
    }

    @Override
    public void notifyChildItemMoved(int parentPosition, int fromChildPosition, int toChildPosition) {
        super.notifyChildItemMoved(parentPosition, fromChildPosition, toChildPosition);
    }

    @Override
    public PlaylistViewHolder onCreateParentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.list_playlist_row, parent, false);
        Log.d(LOG_NAME,"onCreateParentViewHolder");
        return new PlaylistViewHolder(view);
    }

    @Override
    public TrackViewHolder onCreateChildViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.list_track_row, parent, false);
        Log.d(LOG_NAME,"onCreateParentViewHolder");
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(PlaylistViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Log.d(LOG_NAME,"onBindParentViewHolder");
        Playlist playlist = (Playlist) parentListItem;
        parentViewHolder.bind(playlist);
    }

    @Override
    public void onBindChildViewHolder(TrackViewHolder childViewHolder, int position, Object childListItem) {
        Log.d(LOG_NAME,"onBindChildViewHolder");
        Track track = (Track) childListItem;
        childViewHolder.bind(track);
    }
}
