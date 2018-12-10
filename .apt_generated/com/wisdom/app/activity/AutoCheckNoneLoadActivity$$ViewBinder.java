// Generated code from Butter Knife. Do not modify!
package com.wisdom.app.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AutoCheckNoneLoadActivity$$ViewBinder<T extends com.wisdom.app.activity.AutoCheckNoneLoadActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296868, "field 'btn_stop'");
    target.btn_stop = finder.castView(view, 2131296868, "field 'btn_stop'");
    view = finder.findRequiredView(source, 2131296863, "field 'title'");
    target.title = finder.castView(view, 2131296863, "field 'title'");
    view = finder.findRequiredView(source, 2131296866, "field 'btn_delete'");
    target.btn_delete = finder.castView(view, 2131296866, "field 'btn_delete'");
    view = finder.findRequiredView(source, 2131296864, "field 'lv_result'");
    target.lv_result = finder.castView(view, 2131296864, "field 'lv_result'");
    view = finder.findRequiredView(source, 2131296867, "field 'btn_start'");
    target.btn_start = finder.castView(view, 2131296867, "field 'btn_start'");
    view = finder.findRequiredView(source, 2131296865, "field 'lv_option'");
    target.lv_option = finder.castView(view, 2131296865, "field 'lv_option'");
    view = finder.findRequiredView(source, 2131296861, "field 'btn_add'");
    target.btn_add = finder.castView(view, 2131296861, "field 'btn_add'");
  }

  @Override public void unbind(T target) {
    target.btn_stop = null;
    target.title = null;
    target.btn_delete = null;
    target.lv_result = null;
    target.btn_start = null;
    target.lv_option = null;
    target.btn_add = null;
  }
}
