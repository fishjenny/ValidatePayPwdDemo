package com.jenny.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jenny.ui.R;

import java.util.HashMap;
import java.util.Map;

/**
 *Created by ${Jenny} on 2017/10/15.
 *E-mail: fishloveqin@gmail.com
 * <p>
 * 验证支付密码弹出框
 */

public class PayPwdValidatePopupWindow<T> extends BasePushPopupWindow<T> {

    protected View            rootView;
    protected ImageView       ivClose;
    protected PayPwdInputView password;

    public PayPwdValidatePopupWindow(Context context, T t) {
        super(context, t);
    }

    @Override
    protected View generateCustomView(T t) {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.pay_pwd_popupwindow, null, false);
        initView(view);
        //必须加这两行，不然不会显示在键盘上方，实现输入框跟随弹框位置
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void show(Activity activity) {
        //弹框设置为居中显示
        showAtLocation(activity.getWindow().getDecorView(),
            Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void initView(View rootView) {
        ivClose = (ImageView) rootView.findViewById(R.id.iv_close);
        TextView forgetPwdTv = rootView.findViewById(R.id.forgetPwdTv);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
        password = (PayPwdInputView) rootView.findViewById(R.id.password);

        password.setPwdLengthListener(new PayPwdInputView.PwdLengthListener() {
            @Override
            public void onLength(String pwd) {

            }
        });

        forgetPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

    }

    /**
     * 验证密码成功、失败 回调
     */
    public interface PayPwdValidateListener {

        void onSuccess(String pwd);

        void onFailure(String pwd);
    }

    private PayPwdValidateListener mListener;

    public void setValidateListener(PayPwdValidateListener listener) {

        this.mListener = listener;
    }
}
