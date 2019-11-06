package com.example.kaylortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener, TextToSpeech.OnInitListener {
    public static final String EXTRA_MESSAGE ="com.example.kaylortest.Message";
    private static final String TAG = "MainActivity";
    // 按钮控制开始朗读
    private Button speechBtn;
    private Button btn_morning;
    private Button btn_bye;
    private EditText speechTxt; // 需要朗读的内容
    private TextToSpeech textToSpeech; // TTS对象
    private VoicePlayer voicePlayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTextToSpeech();
    }

    private void  initTextToSpeech(){
        textToSpeech = new TextToSpeech(this,this);
        /*pitch 语调，最高2.0
        *speed  语速，最高1.5
        * */
        textToSpeech.setPitch(1.0f);
        textToSpeech.setSpeechRate(1.0f);
    }
    private void initView(){
        speechTxt = findViewById(R.id.editText);
        speechBtn = findViewById(R.id.speechBtn);
        speechBtn.setOnClickListener(this);
        btn_morning = findViewById(R.id.btn_morning);
        btn_morning.setOnClickListener(this);
        btn_bye = findViewById(R.id.btn_bye);
        btn_bye.setOnClickListener(this);
    }
    /**
     * 用来初始化TextToSpeech引擎
     * status:SUCCESS或ERROR这2个值
     * setLanguage设置语言，帮助文档里面写了有22种
     * TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失。
     * TextToSpeech.LANG_NOT_SUPPORTED:不支持
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA){
                Toast.makeText(this, "数据丢失", Toast.LENGTH_SHORT).show();
            }
            else if (result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "数据不支持", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG,"onInit:TTS引擎初始化成功");
        }
        else{
            Log.i(TAG,"onInit:TTs引擎初始化失败");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speechBtn:
                submit();
                break;
            case R.id.btn_morning:
                speech_mor();
                Log.v("debug","按下早上好按钮！");
                break;
            case R.id.btn_bye:
                Log.v("debug","按下再见按钮！");
                speech_bye();
            default:
                break;
        }
    }
    private void submit() {
        // validate
        String text = speechTxt.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请您输入要朗读的文字", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            /*
                TextToSpeech的speak方法有两个重载。
                // 执行朗读的方法
                speak(CharSequence text,int queueMode,Bundle params,String utteranceId);
                // 将朗读的的声音记录成音频文件
                synthesizeToFile(CharSequence text,Bundle params,File file,String utteranceId);
                第二个参数queueMode用于指定发音队列模式，两种模式选择
                （1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
                （2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，
                等前面的语音任务执行完了才会执行新的语音任务
             */
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        textToSpeech.shutdown(); // 关闭，释放资源
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void speech_mor(){
        voicePlayer = VoicePlayer.getInstance(this);
        voicePlayer.addVoiceUnit(new VoiceUnit(new int[]{R.raw.in})); //加入播放队列
        voicePlayer.play();
    }

    private void speech_bye(){
        voicePlayer = VoicePlayer.getInstance(this);
        voicePlayer.addVoiceUnit(new VoiceUnit(new int[]{R.raw.bye}));//加入播放队列
        voicePlayer.play();
    }
}