package com.imp.tipsanimation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.imp.tipslibrary.TipsEnum;
import com.imp.tipslibrary.TipsView;
import com.imp.tipslibrary.TipsViewClickListener;

public class MainActivity extends AppCompatActivity {

    private TipsView tv;
    private TipsView triangleTv;
    private TipsView switchTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.right_tv);
        tv.addTipsViewClickListener(new TipsViewClickListener() {
            @Override
            public void onClickListener(int repeatTimes, int radium, String tipsType) {
                if (tipsType.equals(TipsEnum.RIGHT.getCode())) {
                    tv.setAnimationIcon(TipsEnum.WRONG);
                } else if (tipsType.equals(TipsEnum.WRONG.getCode())) {
                    tv.setAnimationIcon(TipsEnum.LAMENT);
                } else {
                    tv.setAnimationIcon(TipsEnum.RIGHT);
                }
            }
        });
        triangleTv = findViewById(R.id.triangle_tv);

        switchTv = findViewById(R.id.switch_tv);

        switchTv.addTipsViewClickListener(new TipsViewClickListener() {
            @Override
            public void onClickListener(int repeatTimes, int radium, String tipsType) {
                if (tipsType.equals(TipsEnum.STOP.getCode())) {
                    switchTv.setAnimationIcon(TipsEnum.START);
                } else if (tipsType.equals(TipsEnum.START.getCode())) {
                    switchTv.setAnimationIcon(TipsEnum.STOP);
                }
            }
        });
    }
}
