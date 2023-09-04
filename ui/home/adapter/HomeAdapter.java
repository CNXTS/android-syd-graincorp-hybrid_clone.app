package com.webling.graincorp.ui.home.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.webling.graincorp.R;
import com.webling.graincorp.databinding.HomeBidItemBinding;
import com.webling.graincorp.model.Bid;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private final int viewWidth;
    private List<Bid> bidList;

    public HomeAdapter(List<Bid> bidList, Context context) {
        this.bidList = bidList;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        viewWidth = (int) (metrics.widthPixels * 0.70);
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomeBidItemBinding binding = HomeBidItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().getLayoutParams().width = viewWidth;
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(HomeAdapter.ViewHolder holder, int position) {
        Bid bid = bidList.get(position);
        holder.onBind(TAG, bid);
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public List<Bid> getItems() {
        return bidList;
    }

    public void setItems(List<Bid> bidList) {
        this.bidList = bidList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final HomeBidItemBinding binding;

        public ViewHolder(HomeBidItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(String TAG, Bid bid) {
            String commodityGrade = bid.getCommodityDesc() + " — " + bid.getGrade(); //em dash &#8212;
            binding.bidCommodityGrade.setText(commodityGrade);
            binding.bidSiteName.setText(bid.getSiteName());

            String priceChangeString = bid.getPriceChange();

            if (priceChangeString != null) {
                if (!priceChangeString.equals("-")) {
                    binding.bidPriceChange.setText(String.format("$%s", priceChangeString));
                    try {
                        float priceChange = Float.parseFloat(priceChangeString);
                        binding.bidPriceChangeIcon.setVisibility(View.VISIBLE);
                        if (priceChange > 0) {
                            binding.bidPriceChangeIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_mkt_up_green));
                        } else if (priceChange < 0) {
                            binding.bidPriceChangeIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_mkt_down_red));
                        } else if (priceChange == 0) {
                            binding.bidPriceChangeIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_mkt_equal_yellow));
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Exception", e);
                    }
                } else {
                    binding.bidPriceChangeIcon.setVisibility(View.GONE);
                    binding.bidPriceChange.setText(priceChangeString);
                }
                binding.bidSeasonYear.setText(bid.getSeasonYearDesc());
                binding.bidBuyerName.setText(bid.getCustomerName());
                binding.bidPrefferedBuyer.setVisibility(bid.isPreferredBuyer() ? View.VISIBLE : View.GONE);
                String price = bid.getPrice();
                if (price != null) {
                    binding.bidPrice.setText(String.format("$%s", price));
                }
                binding.bidActiveContainer.setVisibility(View.VISIBLE);
                binding.bidNoActiveBids.setVisibility(View.GONE);
            } else {
                binding.bidPriceChange.setText("");
                binding.bidPriceChangeIcon.setVisibility(View.GONE);
                binding.bidActiveContainer.setVisibility(View.INVISIBLE);
                binding.bidNoActiveBids.setVisibility(View.VISIBLE);
            }
        }
    }
}
