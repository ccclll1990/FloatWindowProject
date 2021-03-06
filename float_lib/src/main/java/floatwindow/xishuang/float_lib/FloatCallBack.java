package floatwindow.xishuang.float_lib;

import android.view.View;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:暴露一些与悬浮窗交互的接口
 */
public interface FloatCallBack {
    void guideUser(int type);

    void show();

    void hide();

    void addObtainNumer();

    void setObtainNumber(int number);

    void setOnClickListener(View.OnClickListener onClickListener);

    void setOnLongClickListener(View.OnLongClickListener onLongClickListener);
}
