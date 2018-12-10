// Generated code from Butter Knife. Do not modify!
package com.wisdom.app.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ACNLSchemeActivity$$ViewBinder<T extends com.wisdom.app.activity.ACNLSchemeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296966, "field 'title'");
    target.title = finder.castView(view, 2131296966, "field 'title'");
    view = finder.findRequiredView(source, 2131296866, "field 'btn_autocheck_delete'");
    target.btn_autocheck_delete = finder.castView(view, 2131296866, "field 'btn_autocheck_delete'");
    view = finder.findRequiredView(source, 2131296967, "field 'btn_autocheck_ok_plan'");
    target.btn_autocheck_ok_plan = finder.castView(view, 2131296967, "field 'btn_autocheck_ok_plan'");
    view = finder.findRequiredView(source, 2131296861, "field 'btn_autocheck_add_plan'");
    target.btn_autocheck_add_plan = finder.castView(view, 2131296861, "field 'btn_autocheck_add_plan'");
    view = finder.findRequiredView(source, 2131296864, "field 'lv_autocheck_result'");
    target.lv_autocheck_result = finder.castView(view, 2131296864, "field 'lv_autocheck_result'");
    view = finder.findRequiredView(source, 2131296968, "field 'btn_autocheck_update'");
    target.btn_autocheck_update = finder.castView(view, 2131296968, "field 'btn_autocheck_update'");
  }

  @Override public void unbind(T target) {
    target.title = null;
    target.btn_autocheck_delete = null;
    target.btn_autocheck_ok_plan = null;
    target.btn_autocheck_add_plan = null;
    target.lv_autocheck_result = null;
    target.btn_autocheck_update = null;
  }
}
