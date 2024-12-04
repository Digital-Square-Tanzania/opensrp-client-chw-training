package org.smartregister.chw.fragment;
import android.database.Cursor;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChwLinkageDetailsViewActivity;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.model.ReferralRegisterFragmentModel;
import org.smartregister.chw.presenter.LinkageRegisterFragmentPresenter;
import org.smartregister.chw.referral.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

import timber.log.Timber;

/**
 * Author issyzac on 07/05/2024
 */
public class LinkageRegisterFragment extends ReferralRegisterFragment {

    @Override
    public void setupViews(@NonNull android.view.View view) {
        super.setupViews(view);

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
            titleView.setText(R.string.nav_menu_linkage);
            titleView.setFontVariant(FontVariant.REGULAR);
        }
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = (BaseRegisterFragmentContract.Presenter) new LinkageRegisterFragmentPresenter((BaseReferralRegisterFragmentContract.View) this, (BaseReferralRegisterFragmentContract.Model) new ReferralRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void countExecute() {
        Cursor c = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() + " inner join " + Constants.TABLE_NAME.FAMILY_MEMBER +
                    " on " + presenter().getMainTable() + ".entity_id = " +
                    Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID +
                    " inner join task on task.for = ec_family_member.base_entity_id" +
                    " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }

//            if (dueFilterActive) {
//                query = query + " and ( "

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }

//            if (dueFilterActive) {
//                query = query + " and ( " + presenter().getDueFilterCondition() + " ) ";+ presenter().getDueFilterCondition() + " ) ";

//            if (dueFilterActive) {
//                query = query + " and ( " + presenter().getDueFilterCondition() + " ) ";
//            }

            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));
            Timber.v("total count here %s", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        ChwLinkageDetailsViewActivity.startChwLinkageDetailsViewActivity(getActivity(), new MemberObject(client), client);
    }

    @Override
    protected void openFollowUpVisit(CommonPersonObjectClient client) {
        //MalariaFollowUpVisitActivity.startMalariaFollowUpActivity(getActivity(), client.getCaseId());
    }

}
