package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.ContactAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.device.entity.Contact;

import com.zjw.apps3pluspro.module.device.entity.PhoneDtoModel;
import com.zjw.apps3pluspro.sql.entity.PhoneInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.view.widget.SideBar;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.PhoneUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.maillist.HanziToPinyin;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加通讯录列表
 */

public class AddMyMailListActivity extends Activity implements SideBar.OnTouchingLetterChangedListener, TextWatcher, View.OnClickListener {
    private final String TAG = AddMyMailListActivity.class.getSimpleName();
    private Context mContext;

    private ListView mListView;
    private TextView mFooterView;

    private ArrayList<Contact> datas = new ArrayList<>();
    private ContactAdapter mAdapter;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, true, R.color.base_activity_bg);
        setContentView(R.layout.activity_add_my_mail_list);
        mContext = AddMyMailListActivity.this;
        initView();
        initData();
    }


    public void initView() {
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.my_mail_list_title));

        mListView = (ListView) findViewById(R.id.school_friend_member);

        SideBar mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
        TextView mDialog = (TextView) findViewById(R.id.school_friend_dialog);
        EditText mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);

        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mSearchInput.addTextChangedListener(this);

        // 给listView设置adapter
        mFooterView = (TextView) View.inflate(mContext, R.layout.item_list_contact_count, null);
        mListView.addFooterView(mFooterView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 > -1 && arg2 < datas.size()) {
                    Contact mContact = datas.get(arg2);
                    if (mContact != null) {
                        MyLog.i(TAG, "initView mContact = " + mContact);
                        addMailList(mContact);
                    } else {
                        MyLog.i(TAG, "initView mContact = null");
                    }
                }
            }
        });
    }


    private void initData() {


        List<PhoneDtoModel> my_list = PhoneUtil.getPhone(AddMyMailListActivity.this);
        MyLog.i(TAG, "通讯录数据输出 size = " + my_list.size());
        for (int i = 0; i < my_list.size(); i++) {

            MyLog.i(TAG, "通讯录数据输出 i = " + i + "  my_list = " + my_list.get(i).toString());

            Contact data = new Contact();
            data.setName(my_list.get(i).getName());
            data.setPinyin(HanziToPinyin.getPinYin(my_list.get(i).getName()));
            data.setPhone_number(my_list.get(i).getTelPhone());
            datas.add(data);
        }


//        mFooterView.setText(datas.size() + "位联系人");
        mAdapter = new ContactAdapter(mListView, datas);
        mListView.setAdapter(mAdapter);


    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

        }
    }


    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (mAdapter != null) {
            position = mAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            mListView.setSelection(position);
        } else if (s.contains("#")) {
            mListView.setSelection(0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ArrayList<Contact> temp = new ArrayList<>(datas);
        for (Contact data : datas) {
            if (data.getName().contains(s) || data.getPinyin().contains(s)) {
            } else {
                temp.remove(data);
            }
        }
        if (mAdapter != null) {
            mAdapter.refresh(temp);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    /**
     * 弹出设置对话框
     *
     * @param mContact
     */
    void addMailList(final Contact mContact) {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.my_mail_list_dialog_add_title))//设置对话框标题
                .setMessage(getString(R.string.my_mail_list_dialog_name) + ":" + mContact.getName()
                        + "\n" + getString(R.string.my_mail_list_dialog_phone) + ":" + mContact.getPhone_number()
                )//设置显示的内容
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        PhoneInfo mPhoneInfo = new PhoneInfo();
                        mPhoneInfo.setUser_id(BaseApplication.getUserId());
                        mPhoneInfo.setPhone_name(mContact.getName());
                        mPhoneInfo.setPhone_number(mContact.getPhone_number());

                        MyLog.i(TAG, "点到了 = mPhoneInfo = " + mPhoneInfo.toString());

                        Intent intent = new Intent(mContext, AddMyMailListActivity.class);
                        intent.putExtra(IntentConstants.Intent_PhoneInfo, mPhoneInfo);
                        setResult(ContactListActivity.MyMailListResultAdd, intent);
                        finish();

                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框

    }
}
