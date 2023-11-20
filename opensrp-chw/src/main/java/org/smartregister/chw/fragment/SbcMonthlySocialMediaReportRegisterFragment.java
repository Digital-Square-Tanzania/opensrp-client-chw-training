package org.smartregister.chw.fragment;

import static android.view.View.GONE;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.isMultiPartForm;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.SbcMonthlySocialMediaReportsRegisterAdapter;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.dao.ChwSbcDao;
import org.smartregister.chw.model.SbcMobilizationSessionRegisterFragmentModel;
import org.smartregister.chw.model.SbcMonthlySocialMediaReportModel;
import org.smartregister.chw.presenter.SbcMonthlySocialMediaReportRegisterFragmentPresenter;
import org.smartregister.chw.provider.SbccRegisterProvider;
import org.smartregister.chw.sbc.fragment.BaseSbcRegisterFragment;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class SbcMonthlySocialMediaReportRegisterFragment extends BaseSbcRegisterFragment {
    protected Toolbar toolbar;

    protected LinearLayout emptyViewLayout;

    private android.view.View view;


    private SbcMonthlySocialMediaReportsRegisterAdapter adapter;

    public static Intent getStartFormActivity(JSONObject jsonForm, String title, Context context) {
        Intent intent = new Intent(context, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setWizard(false);
        form.setHomeAsUpIndicator(org.smartregister.chw.core.R.mipmap.ic_cross_white);
        form.setSaveLabel(context.getResources().getString(org.smartregister.chw.core.R.string.save));
        form.setDatePickerDisplayFormat("MMM yyyy");

        if (isMultiPartForm(jsonForm)) {
            form.setWizard(true);
            form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
            form.setName(title);
            form.setNextLabel(context.getResources().getString(org.smartregister.chw.core.R.string.next));
            form.setPreviousLabel(context.getResources().getString(org.smartregister.chw.core.R.string.back));
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        return intent;

    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        SbccRegisterProvider sbcMobilizationRegisterProvider = new SbccRegisterProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, sbcMobilizationRegisterProvider, null);
        clientAdapter.setTotalcount(0);
        clientAdapter.setCurrentlimit(20);
        setUpAdapter();
    }

    protected void setUpAdapter() {
        List<SbcMonthlySocialMediaReportModel> sbcMonthlySocialMediaReportModels = ChwSbcDao.getSbcMonthlySocialMediaReport();
        if (sbcMonthlySocialMediaReportModels != null && !sbcMonthlySocialMediaReportModels.isEmpty()) {
            showEmptyState();
            adapter = new SbcMonthlySocialMediaReportsRegisterAdapter(sbcMonthlySocialMediaReportModels, requireActivity());
            clientsView.setAdapter(adapter);
        } else {
            showEmptyState();
        }
    }

    protected void showEmptyState() {
        if (emptyViewLayout != null) {
            if (clientAdapter != null && clientAdapter.getItemCount() >= 1) {
                emptyViewLayout.setVisibility(GONE);
            } else {
                emptyViewLayout.setVisibility(android.view.View.VISIBLE);
            }
        }
    }

    @Override
    public void setupViews(android.view.View view) {
        initializePresenter();
        super.setupViews(view);
        this.view = view;

        emptyViewLayout = view.findViewById(org.smartregister.hivst.R.id.empty_view_ll);
        toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        try {
            NavigationMenu.getInstance(getActivity(), null, toolbar);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        android.view.View navbarContainer = view.findViewById(org.smartregister.chw.core.R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        CustomFontTextView titleView = view.findViewById(org.smartregister.chw.core.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(getString(R.string.sbc_monthly_social_media_report));
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        android.view.View searchBarLayout = view.findViewById(org.smartregister.chw.core.R.id.search_bar_layout);
        searchBarLayout.setVisibility(GONE);

        android.view.View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
        topLeftLayout.setVisibility(GONE);

        android.view.View topRightLayout = view.findViewById(org.smartregister.chw.core.R.id.top_right_layout);
        topRightLayout.setVisibility(android.view.View.VISIBLE);

        android.view.View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(GONE);

        android.view.View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(GONE);

        android.view.View dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
        dueOnlyLayout.setVisibility(GONE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);
        if (getSearchView() != null) {
            getSearchView().setVisibility(GONE);
        }
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new SbcMonthlySocialMediaReportRegisterFragmentPresenter(this, new SbcMobilizationSessionRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (clientsView.getAdapter() != null) {
            clientsView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);

        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::setUpAdapter, 2000);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void countExecute() {
        Cursor c = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() + " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }


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
    protected void openProfile(String baseEntityId) {
        //implement when needed
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(android.view.View.VISIBLE);
            syncButton.setPadding(0, 0, 10, 0);
            syncButton.setImageDrawable(context().getDrawable(R.drawable.ic_add_white_24));
            syncButton.setOnClickListener(view -> {
                JSONObject form;
                try {
                    form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(requireActivity(), Constants.FORMS.SBC_MONTHLY_SOCIAL_MEDIA_REPORT);
                    if (form != null) {
                        String randomId = generateRandomUUIDString();
                        form.put(ENTITY_ID, randomId);
                        requireActivity().startActivityForResult(getStartFormActivity(form, requireActivity().getString(R.string.sbc_monthly_social_media_report), requireActivity()), JsonFormUtils.REQUEST_CODE_GET_JSON);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                }
            });
        }
    }

    @Override
    protected int getLayout() {
        return org.smartregister.hivst.R.layout.fragment_mobilization_register;
    }
}
