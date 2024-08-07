package org.smartregister.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.SbcMobilizationSessionDetailsActivity;
import org.smartregister.chw.model.SbcMobilizationSessionModel;

import java.util.List;

import timber.log.Timber;

public class SbcMobilizationRegisterAdapter extends RecyclerView.Adapter<SbcMobilizationRegisterAdapter.SbcMobilizationViewHolder> {
    private static final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

    private final Context context;

    private final List<SbcMobilizationSessionModel> sbccSessionModels;


    public SbcMobilizationRegisterAdapter(List<SbcMobilizationSessionModel> sbccSessionModels, Context context) {
        this.sbccSessionModels = sbccSessionModels;
        this.context = context;
    }

    private static void evaluateView(TextView tv, Context context, String stringValue) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(context.getString(R.string.sbc_type_of_sbc_activity), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");

        String[] stringValueArray;
        if (stringValue.contains(",")) {
            stringValueArray = stringValue.substring(1, stringValue.length() - 1).split(",");
            for (String value : stringValueArray) {
                spannableStringBuilder.append(getStringResource(context, "sbc_", value.trim()) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (stringValue.charAt(0) == '[' && stringValue.charAt(stringValue.length() - 1) == ']') {
            spannableStringBuilder.append(getStringResource(context, "sbc_", stringValue.substring(1, stringValue.length() - 1))).append("\n");
        } else {
            spannableStringBuilder.append(getStringResource(context, "sbc_", stringValue)).append("\n");
        }
        tv.setText(spannableStringBuilder);
    }

    private static String getStringResource(Context context, String prefix, String resourceName) {
        int resourceId = context.getResources().
                getIdentifier(prefix + resourceName.trim(), "string", context.getPackageName());
        try {
            return context.getString(resourceId);
        } catch (Exception e) {
            Timber.e(e);
            return resourceName;
        }
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

            evaluateView(typeOfCommunitySbcActivity, context, sbccSessionModel.getCommunitySbcActivityType());

            itemView.setOnClickListener(view -> SbcMobilizationSessionDetailsActivity.startMe(((Activity) context), sbccSessionModel.getSessionId()));
        }
    }
}
