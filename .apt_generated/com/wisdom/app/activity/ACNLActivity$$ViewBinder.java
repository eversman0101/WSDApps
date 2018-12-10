// Generated code from Butter Knife. Do not modify!
package com.wisdom.app.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ACNLActivity$$ViewBinder<T extends com.wisdom.app.activity.ACNLActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296823, "field 'title'");
    target.title = finder.castView(view, 2131296823, "field 'title'");
    view = finder.findRequiredView(source, 2131296827, "field 'btn_acnl_stop'");
    target.btn_acnl_stop = finder.castView(view, 2131296827, "field 'btn_acnl_stop'");
    view = finder.findRequiredView(source, 2131296826, "field 'btn_acnl_test'");
    target.btn_acnl_test = finder.castView(view, 2131296826, "field 'btn_acnl_test'");
    view = finder.findRequiredView(source, 2131296824, "field 'tv_scheme'");
    target.tv_scheme = finder.castView(view, 2131296824, "field 'tv_scheme'");
    view = finder.findRequiredView(source, 2131296825, "field 'tv_jindu'");
    target.tv_jindu = finder.castView(view, 2131296825, "field 'tv_jindu'");
  }

  @Override public void unbind(T target) {
    target.title = null;
    target.btn_acnl_stop = null;
    target.btn_acnl_test = null;
    target.tv_scheme = null;
    target.tv_jindu = null;
  }
}
