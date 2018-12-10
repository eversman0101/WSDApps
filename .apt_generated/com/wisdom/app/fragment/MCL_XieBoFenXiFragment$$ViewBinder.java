// Generated code from Butter Knife. Do not modify!
package com.wisdom.app.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MCL_XieBoFenXiFragment$$ViewBinder<T extends com.wisdom.app.fragment.MCL_XieBoFenXiFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131297063, "field 'chart'");
    target.chart = finder.castView(view, 2131297063, "field 'chart'");
    view = finder.findRequiredView(source, 2131297062, "field 'title'");
    target.title = finder.castView(view, 2131297062, "field 'title'");
    view = finder.findRequiredView(source, 2131296695, "field 'tv_thdi'");
    target.tv_thdi = finder.castView(view, 2131296695, "field 'tv_thdi'");
    view = finder.findRequiredView(source, 2131297064, "field 'tr_btn'");
    target.tr_btn = finder.castView(view, 2131297064, "field 'tr_btn'");
    view = finder.findRequiredView(source, 2131296691, "field 'tv_thdu'");
    target.tv_thdu = finder.castView(view, 2131296691, "field 'tv_thdu'");
  }

  @Override public void unbind(T target) {
    target.chart = null;
    target.title = null;
    target.tv_thdi = null;
    target.tr_btn = null;
    target.tv_thdu = null;
  }
}
