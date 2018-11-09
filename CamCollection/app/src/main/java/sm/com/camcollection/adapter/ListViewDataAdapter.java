package sm.com.camcollection.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import sm.com.camcollection.R;
import sm.com.camcollection.data.MetaDataEntity;

public class ListViewDataAdapter extends RecyclerView.Adapter<ListViewDataAdapter.MetaDataViewHolder> {

    private List<MetaDataEntity> metaDataList;

    public ListViewDataAdapter(List<MetaDataEntity> metaData) {
        this.metaDataList = metaData;
    }

    @Override
    public MetaDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_metadata, parent, false);

        return new MetaDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MetaDataViewHolder holder, int position) {

        if (metaDataList != null) {
            ColorGenerator generator = ColorGenerator.MATERIAL;

            holder.domain.setText(metaDataList.get(position).getDomain());
            holder.length.setText(String.valueOf(metaDataList.get(position).getLength()));
            holder.iteration.setText(String.valueOf(metaDataList.get(position).getPwVersion()));

            if (metaDataList.get(position).getUserName().length() == 0) {
                holder.username.setText("-");
            } else {
                holder.username.setText(metaDataList.get(position).getUserName());
            }

            String characterset = "";

            if (metaDataList.get(position).getHasLetterLow() == 1) {
                characterset += "abc";
            }

            if (metaDataList.get(position).getHasLettersUp() == 1) {
                characterset += " ABC";
            }

            if (metaDataList.get(position).getHasNumber() == 1) {
                characterset += " 123";
            }

            if (metaDataList.get(position).getHasSymbols() == 1) {
                characterset += " +!#";
            }

            holder.characterset.setText(characterset);

            int color = generator.getColor(metaDataList.get(position).getDomain());
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(String.valueOf(metaDataList.get(position).getDomain().toUpperCase().charAt(0)), color);
            holder.imageView.setImageDrawable(textDrawable);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        int i = 0;
        if (metaDataList != null) {
            i = metaDataList.size();
        }
        return i;
    }

    public void setMetaDataList(List<MetaDataEntity> metaDataList) {
        if (metaDataList != null) {
            this.metaDataList = metaDataList;
        }
    }

    public MetaDataEntity removeItem(int position) {
        MetaDataEntity metaData = null;
        if (metaDataList != null) {
            metaData = metaDataList.remove(position);
            notifyItemRemoved(position);
        }
        return metaData;
    }

    public void addItem(int position, MetaDataEntity metaData) {
        if (metaDataList != null) {
            metaDataList.add(position, metaData);
            notifyItemInserted(position);
        }
    }

    public MetaDataEntity getItem(int position) {
        MetaDataEntity metaData = null;
        if (metaDataList != null) {
            metaData = metaDataList.get(position);
        }
        return metaData;
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (metaDataList != null) {
            final MetaDataEntity metaData = metaDataList.remove(fromPosition);
            metaDataList.add(toPosition, metaData);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    private void applyAndAnimateRemovals(List<MetaDataEntity> newMetaData) {
        if (metaDataList != null) {
            for (int i = metaDataList.size() - 1; i >= 0; i--) {
                final MetaDataEntity metaData = metaDataList.get(i);
                if (!newMetaData.contains(metaData)) {
                    removeItem(i);
                }
            }
        }
    }

    private void applyAndAnimateAdditions(List<MetaDataEntity> newMetaData) {
        if (metaDataList != null) {
            for (int i = 0, count = newMetaData.size(); i < count; i++) {
                final MetaDataEntity metaData = newMetaData.get(i);
                if (!metaDataList.contains(metaData)) {
                    addItem(i, metaData);
                }
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MetaDataEntity> newMetaData) {
        if (metaDataList != null) {
            for (int toPosition = newMetaData.size() - 1; toPosition >= 0; toPosition--) {
                final MetaDataEntity metaData = newMetaData.get(toPosition);
                final int fromPosition = metaDataList.indexOf(metaData);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }

    public void animateTo(List<MetaDataEntity> metaDataList) {
        applyAndAnimateRemovals(metaDataList);
        applyAndAnimateAdditions(metaDataList);
        applyAndAnimateMovedItems(metaDataList);
    }

    public class MetaDataViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView domain;
        TextView username;
        TextView length;
        TextView iteration;
        TextView characterset;
        ImageView imageView;

        public MetaDataViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            domain = (TextView) itemView.findViewById(R.id.domainTextView);
            username = (TextView) itemView.findViewById(R.id.username);
            length = (TextView) itemView.findViewById(R.id.length);
            iteration = (TextView) itemView.findViewById(R.id.iteration);
            characterset = (TextView) itemView.findViewById(R.id.characterSet);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);

        }
    }
}
