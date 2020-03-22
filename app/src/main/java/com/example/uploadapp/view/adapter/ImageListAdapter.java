package com.example.uploadapp.view.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uploadapp.R;
import com.example.uploadapp.service.model.ImagesItem;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder> {

    private final List<ImagesItem> imagesItemList;
    private final OnItemClickListener onItemClickListener;

    static class ImageListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.imgViewImageUrl)
        ImageView imgViewImageUrl;

        ImageListViewHolder(final View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public ImageListAdapter(List<ImagesItem> imagesItemList, OnItemClickListener onItemClickListener) {
        this.imagesItemList = imagesItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image, parent, false);
        return new ImageListViewHolder(view, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder imageListViewHolder, final int position) {
        ImagesItem imagesItem = getItem(position);
        if (!TextUtils.isEmpty(imagesItem.getImageUrl())) {
            imageListViewHolder.txtName.setText(FilenameUtils.removeExtension(URLUtil.guessFileName(imagesItem.getImageUrl(), null, null)));
            imageListViewHolder.imgViewImageUrl.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imagesItem.getImageUrl())
                    .into(imageListViewHolder.imgViewImageUrl);
        } else {
            imageListViewHolder.imgViewImageUrl.setVisibility(View.GONE);
        }
    }

    private ImagesItem getItem(int position) {
        return imagesItemList.get(position);
    }

    @Override
    public int getItemCount() {
        return imagesItemList.size();
    }

    public void clearData() {
        imagesItemList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}