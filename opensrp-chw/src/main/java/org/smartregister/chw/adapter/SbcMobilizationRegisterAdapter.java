package org.smartregister.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.model.SbcMobilizationSessionModel;

import java.util.List;

public class SbcMobilizationRegisterAdapter extends RecyclerView.Adapter<SbcMobilizationRegisterAdapter.SbcMobilizationViewHolder> {
    private final Context context;

    private final List<SbcMobilizationSessionModel> sbccSessionModels;


    public SbcMobilizationRegisterAdapter(List<SbcMobilizationSessionModel> sbccSessionModels, Context context) {
        this.sbccSessionModels = sbccSessionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public SbcMobilizationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View followupLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sbc_mobilization_session_card_view, viewGroup, false);
        return new SbcMobilizationViewHolder(followupLayout, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SbcMobilizationViewHolder holder, int position) {
        SbcMobilizationSessionModel sbccSessionModel = sbccSessionModels.get(position);
        holder.bindData(sbccSessionModel);
    }


    @Override
    public int getItemCount() {
        return sbccSessionModels.size();
    }

    protected static class SbcMobilizationViewHolder extends RecyclerView.ViewHolder {
        public TextView sbccSessionDate;

        public TextView typeOfCommunitySbcActivity;

        private Context context;

        public SbcMobilizationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
        }

        public void bindData(SbcMobilizationSessionModel sbccSessionModel) {
            sbccSessionDate = itemView.findViewById(R.id.sbc_session_date);
            typeOfCommunitySbcActivity = itemView.findViewById(R.id.sbc_activity_provided);

            sbccSessionDate.setText(context.getString(R.string.sbcc_session_date, sbccSessionModel.getSessionDate()));
            typeOfCommunitySbcActivity.setText(context.getString(R.string.sbc_type_of_sbc_activity, sbccSessionModel.getCommunitySbcActivityType()));
        }
    }
}
