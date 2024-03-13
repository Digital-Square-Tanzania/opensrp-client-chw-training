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
import org.smartregister.chw.activity.SbcMonthlySocialMediaReportDetailsActivity;
import org.smartregister.chw.model.SbcMonthlySocialMediaReportModel;

import java.util.List;

import timber.log.Timber;

public class SbcMonthlySocialMediaReportsRegisterAdapter extends RecyclerView.Adapter<SbcMonthlySocialMediaReportsRegisterAdapter.SbcMobilizationViewHolder> {
    private static final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

    private final Context context;

    private final List<SbcMonthlySocialMediaReportModel> sbcMonthlySocialMediaReportModels;


    public SbcMonthlySocialMediaReportsRegisterAdapter(List<SbcMonthlySocialMediaReportModel> sbcMonthlySocialMediaReportModels, Context context) {
        this.sbcMonthlySocialMediaReportModels = sbcMonthlySocialMediaReportModels;
        this.context = context;
    }

    private static void evaluateView(TextView tv, Context context, String stringValue) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(context.getString(R.string.sbc_monthly_social_media_report_organization), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");

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
        View followupLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sbc_monthly_social_media_report_card_view, viewGroup, false);
        return new SbcMobilizationViewHolder(followupLayout, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SbcMobilizationViewHolder holder, int position) {
        SbcMonthlySocialMediaReportModel sbcMonthlySocialMediaReportModel = sbcMonthlySocialMediaReportModels.get(position);
        holder.bindData(sbcMonthlySocialMediaReportModel);
    }

    @Override
    public int getItemCount() {
        return sbcMonthlySocialMediaReportModels.size();
    }

    protected static class SbcMobilizationViewHolder extends RecyclerView.ViewHolder {
        public TextView sbccSessionDate;

        public TextView typeOfCommunitySbcActivity;

        private Context context;

        public SbcMobilizationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
        }

        public void bindData(SbcMonthlySocialMediaReportModel sbcMonthlySocialMediaReportModel) {
            sbccSessionDate = itemView.findViewById(R.id.sbc_session_date);
            typeOfCommunitySbcActivity = itemView.findViewById(R.id.sbc_activity_provided);

            sbccSessionDate.setText(context.getString(R.string.sbcc_session_date, sbcMonthlySocialMediaReportModel.getReportingMonth()));

            evaluateView(typeOfCommunitySbcActivity, context, sbcMonthlySocialMediaReportModel.getOrganizationName());

            itemView.setOnClickListener(view -> SbcMonthlySocialMediaReportDetailsActivity.startMe(((Activity) context), sbcMonthlySocialMediaReportModel.getReportId()));
        }
    }
}
