package cn.georgeyang.temperaturesafe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.adapter.NameAdapter;
import cn.georgeyang.temperaturesafe.entity.RecorderEntity;
import cn.georgeyang.temperaturesafe.impl.BaseActivity;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;
import cn.georgeyang.utils.DialogUtil;

/**
 * Created by george.yang on 16/10/15.
 */

public class NameMangerActivity extends BaseActivity {
    public ListView listView;
    private NameAdapter adapter;
    private List<RecorderEntity> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namemanger);

        TitleUtil.init(this).setTitle("成员管理").autoBack();
        mList = Mdb.getInstance().findAll(RecorderEntity.class);
        adapter = new NameAdapter(this,mList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecorderEntity recorderEntity;
                if (position==mList.size()) {
                    if (mList.size()>=10) {
                        DialogUtil.showOKDialog(getActivity(),"最多只能添加10个成员哦!",null);
                        return;
                    }
                    recorderEntity = new RecorderEntity();
                } else {
                    recorderEntity = mList.get(position);
                }
                showDialog(recorderEntity);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position<mList.size()) {
                    DialogUtil.showYNDialog(getActivity(), "确认删除该成员?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecorderEntity entity = mList.get(position);
                            entity.delete();
                            mList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            }
        });
    }

    public void showDialog (final RecorderEntity recorder) {
        final EditText et = new EditText(this);
        if (!TextUtils.isEmpty(recorder.name)) {
            et.setText(recorder.name);
            et.setSelection(recorder.name.length());
        }
        new AlertDialog.Builder(this)
                .setTitle("请输入")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        recorder.name = et.getText().toString();
                        if (recorder._id<=0) {
                            recorder._id = Mdb.getInstance().getLastId(RecorderEntity.class) + 1;
                            mList.add(recorder);
                        }
                        recorder.save();
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
    }
}
