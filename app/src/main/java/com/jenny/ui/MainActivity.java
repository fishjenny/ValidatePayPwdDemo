package com.jenny.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jenny.view.PayPwdInputView;
import com.jenny.view.PayPwdValidatePopupWindow;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickEvent(View view) {

        PayPwdValidatePopupWindow<Void> payPwdValidatePopupWindow = new PayPwdValidatePopupWindow<Void>(
            this, null);
        payPwdValidatePopupWindow.show(this);
    }
}
