package org.smartregister.chw.fragment;

import org.smartregister.chw.model.PncChildNoMotherFragmentModel;
import org.smartregister.chw.presenter.PncChildNoMotherFragmentPresenter;
import org.smartregister.chw.provider.ChildRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class PncChildNoMotherFragment extends PncRegisterFragment {


    @Override
    protected String getMainCondition() {
        return "";
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChildRegisterProvider provider = new ChildRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncChildNoMotherFragmentPresenter( this, new PncChildNoMotherFragmentModel(), null);
    }


}
