// Generated code from Butter Knife. Do not modify!
package com.wisdom.app.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DataViewActivity$$ViewBinder<T extends com.wisdom.app.activity.DataViewActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296886, "field 'et_no'");
    target.et_no = finder.castView(view, 2131296886, "field 'et_no'");
    view = finder.findRequiredView(source, 2131296883, "field 'btn_search'");
    target.btn_search = finder.castView(view, 2131296883, "field 'btn_search'");
    view = finder.findRequiredView(source, 2131296887, "field 'lv_data'");
    target.lv_data = finder.castView(view, 2131296887, "field 'lv_data'");
    view = finder.findRequiredView(source, 2131296885, "field 'et_stuffName'");
    target.et_stuffName = finder.castView(view, 2131296885, "field 'et_stuffName'");
    view = finder.findRequiredView(source, 2131296881, "field 'sp_type'");
    target.sp_type = finder.castView(view, 2131296881, "field 'sp_type'");
    view = finder.findRequiredView(source, 2131296884, "field 'et_username'");
    target.et_username = finder.castView(view, 2131296884, "field 'et_username'");
    view = finder.findRequiredView(source, 2131296882, "field 'sp_category'");
    target.sp_category = finder.castView(view, 2131296882, "field 'sp_category'");
    view = finder.findRequiredView(source, 2131296880, "field 'title'");
    target.title = finder.castView(view, 2131296880, "field 'title'");
  }

  @Override public void unbind(T target) {
    target.et_no = null;
    target.btn_search = null;
    target.lv_data = null;
    target.et_stuffName = null;
    target.sp_type = null;
    target.et_username = null;
    target.sp_category = null;
    target.title = null;
  }
}
