package com.coddect.whyareyouhere.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.coddect.whyareyouhere.R;
import com.coddect.whyareyouhere.activity.ResultActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setFlag();
    }

    public void setFlag()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void search(View v)
    {
        startActivity(new Intent(this, ResultActivity.class));
    }
    public void guide(View v)
    {
        startActivity(new Intent(this, GuideActivity.class));
    }
    public void developer(View v)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("CODDECT");
        dialog.setMessage("유용민 : 개발, 기획\n김영현 : 개발, 기획\n강민서 : 디자인, 개발");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        dialog.show();
    }
}
