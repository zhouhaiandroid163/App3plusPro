package com.zjw.apps3pluspro.module.home.ecg;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.ecg.ECGAllView;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.zjw.apps3pluspro.R.id.public_head_back;

/**
 * 心电报告
 */

public class EcgReportActivity extends BaseActivity implements OnClickListener {
    private final String TAG = EcgReportActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private Context mContext;

    private WaitDialog waitDialog;
    private RelativeLayout r_ecg_view;
    private ECGAllView input_ecg_view;
    private Handler mHandler;

    private TextView ecg_time;
    private LinearLayout public_share;
    private static final int MSG_DATA_ECG = 0x14;

    private String[] ecg_data = null;
    private String[] old_ecg_data = null;

    private String date;

    private TextView presentation_user_nickanme, presentation_user_sex, presentation_user_age, presentation_user_height, presentation_user_weight, presentation_user_heart;


    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    private Handler myHandler = new Handler();
    String health_data1 = "";

    HealthInfo mHealthInfo = null;
    UserData mUserData = null;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void initViews() {
        super.initViews();
        mContext = EcgReportActivity.this;
        waitDialog = new WaitDialog(mContext);

        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        initView();
        ecg_init();
        handler_init();
        initData();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_ecg_report;
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }


    private int MaxWidth = 1250;
    private float MaxHeight = 300;
    private int LineNumber = 3;

    void ecg_init() {
        input_ecg_view = (ECGAllView) findViewById(R.id.input_ecg_view);
        input_ecg_view.setMaxPointAmount(MaxWidth);
        input_ecg_view.setMaxYNumber(MaxHeight);
        input_ecg_view.setRemovedPointNum(20);
        input_ecg_view.setEveryNPoint(5);//
        input_ecg_view.setEffticeValue(50);//
        input_ecg_view.setEveryNPointRefresh(1);//
        input_ecg_view.setTitle("");
        initEcgData();

    }


    double HandlerEcg(int end) {

        double reult = 0;
        if (mUserSetTools.get_user_wear_way() == 1) {
            reult = handlerEcg(myHandler, end, true);
        } else {
            reult = handlerEcg(myHandler, end, false);
        }

        reult = MaxHeight / LineNumber / 2 - reult * MaxHeight / LineNumber * Constants.DrawEcgZip;


        return reult;

    }


    void handler_init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_DATA_ECG:


//                        lod_lenght


                        int aa1 = 250;
                        int aa2 = 285;
                        int aa3 = 250;

                        int ecg_data = (int) (msg.arg2);
                        int jishu = (int) (MaxHeight / LineNumber);

                        if (abc >= 0 && abc <= 20) {
//                            input_ecg_view.setLinePoint(aa1);
                            ecg_data = aa1;
                        } else if (abc > 20 && abc <= 50) {
//                            input_ecg_view.setLinePoint(aa2);
                            ecg_data = aa2;
                        } else if (abc > 50 && abc <= 70) {
//                            input_ecg_view.setLinePoint(aa3);
                            ecg_data = aa3;
                        } else if (abc > 70 && abc <= MaxWidth) {
//                            input_ecg_view.setLinePoint(ecg_data + jishu * 2);
                            ecg_data = ecg_data + jishu * 2;
                            if (ecg_data > 300) {
                                ecg_data = 300;
                            } else if (ecg_data < 200) {
                                ecg_data = 200;
                            }

                        } else if (abc > MaxWidth && abc <= MaxWidth * 2) {
//                            input_ecg_view.setLinePoint(ecg_data + jishu * 1);
                            ecg_data = ecg_data + jishu * 1;

                            if (ecg_data > 200) {
                                ecg_data = 200;
                            } else if (ecg_data < 100) {
                                ecg_data = 100;
                            }

                        } else if (abc > MaxWidth * 2 && abc <= MaxWidth * 3) {
//                            input_ecg_view.setLinePoint(ecg_data);

                            if (ecg_data > 100) {
                                ecg_data = 100;
                            } else if (ecg_data < 0) {
                                ecg_data = 0;
                            }
                        }


                        if (abc <= MaxWidth * 3) {

                            input_ecg_view.setLinePoint(ecg_data);

                            if (ecg_data > 200) {
                                ecg_data = 300;
                            } else if (ecg_data < 200) {
                                ecg_data = 200;
                            }

                        }

                        abc += 1;


                        break;


                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }


    private void initView() {
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.healthdy_presentation_title));
        findViewById(public_head_back).setOnClickListener(this);

        ecg_time = (TextView) findViewById(R.id.ecg_time);

        public_share = (LinearLayout) findViewById(R.id.public_share);
        public_share.setVisibility(View.VISIBLE);
        public_share.setOnClickListener(this);
        findViewById(R.id.pdf_share).setOnClickListener(this);
        findViewById(R.id.presentation_back).setOnClickListener(this);
        findViewById(R.id.presentation_share).setOnClickListener(this);
        r_ecg_view = (RelativeLayout) findViewById(R.id.r_ecg_view);

        presentation_user_nickanme = (TextView) findViewById(R.id.presentation_user_nickanme);
        presentation_user_sex = (TextView) findViewById(R.id.presentation_user_sex);
        presentation_user_age = (TextView) findViewById(R.id.presentation_user_age);
        presentation_user_height = (TextView) findViewById(R.id.presentation_user_height);
        presentation_user_weight = (TextView) findViewById(R.id.presentation_user_weight);
        presentation_user_heart = (TextView) findViewById(R.id.presentation_user_heart);

    }


    int lenght = 0;


    void initData() {


        Intent intent = getIntent();
        mHealthInfo = intent.getParcelableExtra(IntentConstants.HealthInfo);
        mUserData = intent.getParcelableExtra(IntentConstants.UserInfo);

        MyLog.i(TAG, "接收到 = mHealthInfo = " + mHealthInfo);

        if (mHealthInfo != null) {
            health_data1 = !JavaUtil.checkIsNull(mHealthInfo.getEcg_data()) ? mHealthInfo.getEcg_data() : "";
            date = !JavaUtil.checkIsNull(mHealthInfo.getMeasure_time()) ? mHealthInfo.getMeasure_time() : "";
            String user_heart = !JavaUtil.checkIsNull(mHealthInfo.getHealth_heart()) ? mHealthInfo.getHealth_heart() : "";
            presentation_user_heart.setText(": " + user_heart + getString(R.string.bpm));
            ecg_time.setText(": " + date);
        }


        if (mUserData != null) {
            if (!JavaUtil.checkIsNull(mUserData.getNikname())) {
                presentation_user_nickanme.setText(": " + mUserData.getNikname());
            }

            if (!JavaUtil.checkIsNull(mUserData.getHeight())) {
                if (mUserSetTools.get_user_unit_type()) {
                    presentation_user_height.setText(": " + mUserData.getHeight() + getString(R.string.centimeter));
                } else {
                    presentation_user_height.setText(": " + MyUtils.CmToInString(mUserData.getHeight()) + getString(R.string.unit_in));
                }

            }

            if (!JavaUtil.checkIsNull(mUserData.getWeight())) {

                if (mUserSetTools.get_user_unit_type()) {
                    presentation_user_weight.setText(": " + mUserData.getWeight() + getString(R.string.kg));
                } else {
                    presentation_user_weight.setText(": " + MyUtils.KGToLBString(mUserData.getWeight(), this) + getString(R.string.unit_lb));
                }
            }

            if (!JavaUtil.checkIsNull(mUserData.getBirthday())) {
                presentation_user_age.setText(": " + MyTime.getAge(mUserData.getBirthday()));
            }


            if (mUserData.getSex() != null && !mUserData.getSex().equals("")) {

                if (mUserData.getSex().equals("0")) {
                    presentation_user_sex.setText(": " + getString(R.string.boy));
                } else {
                    presentation_user_sex.setText(": " + getString(R.string.girl));
                }

            }


        }

        if (!health_data1.equals("")) {
            ecg_data = health_data1.split(",");

            lenght = ecg_data.length;
            if (ecg_data.length > MaxWidth * 3) {
                lenght = MaxWidth * 3;
                old_ecg_data = new String[lenght];
                for (int i = ecg_data.length - MaxWidth * 3; i < ecg_data.length; i++) {
                    old_ecg_data[i - (ecg_data.length - MaxWidth * 3)] = ecg_data[i];
                }
                ecg_data = old_ecg_data;
            }
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            }, 100);
        } else {
            AppUtils.showToast(mContext, R.string.no_ecg_data);
        }

    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                finish();
                break;

            //返回
            case R.id.presentation_back:
                finish();
                break;

            //分享
            case R.id.public_share:
                MyUtils.SharePhoto(getViewBitmap(r_ecg_view), EcgReportActivity.this);
                break;

            //分享
            case R.id.presentation_share:
                MyUtils.SharePhoto(getViewBitmap(r_ecg_view), EcgReportActivity.this);
                break;

            // PDF分享
            case R.id.pdf_share:
                ShareAppTask shareAppTask = new ShareAppTask(this);
                shareAppTask.execute();
                break;
        }
    }


    void sendEcgDate(int ecg_data) {
        double ecg_yy = HandlerEcg(ecg_data);
        Message message = new Message();
        message.what = MSG_DATA_ECG;
        message.arg2 = (int) ecg_yy;
        mHandler.sendMessage(message);
    }

    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


    private void update() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle(getString(R.string.generating_report));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条对话框//样式（水平，旋转）
        progressDialog.setMax(lenght);
        // 是否可以按回退键取消
        progressDialog.setCancelable(false);
        UpdateTextTask updateTextTask = new UpdateTextTask(this);
        updateTextTask.execute();

    }

    int abc = 0;

    class UpdateTextTask extends AsyncTask<Void, Integer, Integer> {
        private Context context;

        UpdateTextTask(Context context) {
            this.context = context;
        }

        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            progressDialog.show();
//            input_ecg_view.setVisibility(View.GONE);

        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {

            for (int i = 0; i < lenght; i++) {
                sendEcgDate(Integer.valueOf(ecg_data[i]));
                publishProgress(i);
            }

            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {


            AppUtils.showToast(mContext, R.string.report_generation_completed);
//            input_ecg_view.setVisibility(View.VISIBLE);
            progressDialog.dismiss();


        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {


            progressDialog.setProgress(values[0] + 1);


        }
    }

    ProgressDialog progressDialog;
    Bitmap my_bitmap;
    String path = Environment.getExternalStorageDirectory() + "/wear_heart" + "/report";

    public void createPDF() {

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

        float width = wm.getDefaultDisplay().getWidth();
        float height = wm.getDefaultDisplay().getHeight();
        MyLog.i(TAG, "得到尺寸是 = width = " + width);
        MyLog.i(TAG, "得到尺寸是 = height = " + height);


        //        Bitmap my_bitmap = MyScreenShot.takeScreenShot(PresentationDetailsActivity2.this);

        my_bitmap = getViewBitmap(r_ecg_view);


        MyLog.i(TAG, "Rotating an Image");

        // step 1: creation of a document-object
        Document document = new Document();
        Rectangle myRectangle = new Rectangle(width, height);
        document.setPageSize(myRectangle);

        try {


            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {


            }

            File file = new File(dir, date + ".pdf");
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file

            PdfWriter.getInstance(document, fOut);

            // step 3: we open the document
            document.open();

            // step 4: we add content
            //Can't use filename => use byte[] instead
//          Image jpg4 = Image.getInstance("otsoe.jpg");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ;
            my_bitmap.compress(Bitmap.CompressFormat.PNG /* FileType */, 100 /* Ratio */, stream);
            Image jpg = Image.getInstance(stream.toByteArray());
            jpg.setAlignment(Image.MIDDLE);


//
//            BaseFont bfChinese = null;
//            try {
//                bfChinese = BaseFont.createFont("assets/fonts/DIN-BoldAlternate.otf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
//            } catch (DocumentException e) {
//                // Do sth. here
//            } catch (IOException e) {
//                // Do sth. here
//            }
//            Font font = new Font(bfChinese, 16, Font.NORMAL);
//            Paragraph p = new Paragraph("这是中文", font);

//            Paragraph text1 =  new Paragraph(getString(R.string.ecg_report));
//            document.add(p);
            document.add(jpg);

        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // step 5: we close the document
        document.close();

    }


    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }


    class ShareAppTask extends AsyncTask<Void, Integer, Integer> {
        private Context context;

        ShareAppTask(Context context) {
            this.context = context;
        }

        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            waitDialog.show(getString(R.string.loading0));

        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {

            createPDF();
            String pp = path + "/" + date + ".pdf";
            MyLog.i(TAG, "需要分享的文件名字 = " + pp);
            MyUtils.SharePdfFile(pp, EcgReportActivity.this);

            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {

            waitDialog.close();


        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {


        }
    }


}
