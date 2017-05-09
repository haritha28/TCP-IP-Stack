package nl.vu.cs.cn;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import nl.vu.cs.cn.app.TCPListener;
import nl.vu.cs.cn.app.TCPManager;

public class Chat extends Activity implements View.OnClickListener, TCPListener {

	private static final String TAG = "TCPChat";

	private static final int REQ_CODE_PICK_IMAGE_1 = 1;
	private static final int REQ_CODE_PICK_IMAGE_2 = 2;

	private TCPManager tcpManager1, tcpManager2;

	private LinearLayout messageView1, messageView2;
	private EditText messageInput1, messageInput2;
	private Button sendBtn1, sendBtn2, sendImageBtn1, sendImageBtn2;
	private ScrollView scrollView1, scrollView2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initViews();

		// by creating a new TCP manager every time we start, it's easier to make sure states
		// get cleaned correctly
		tcpManager1 = new TCPManager(this, true, this);
		tcpManager2 = new TCPManager(this, false, this);

		// open connection
		tcpManager1.open();
		tcpManager2.open();
	}

	private void initViews() {
		messageView1 = (LinearLayout) findViewById(R.id.messages_container_1);
		messageView2 = (LinearLayout) findViewById(R.id.messages_container_2);

		scrollView1 = (ScrollView) findViewById(R.id.scrollview1);
		scrollView2 = (ScrollView) findViewById(R.id.scrollview2);

		sendBtn1 = (Button) findViewById(R.id.btn_send_1);
		sendBtn2 = (Button) findViewById(R.id.btn_send_2);
		sendImageBtn1 = (Button) findViewById(R.id.btn_send_picture_1);
		sendImageBtn2 = (Button) findViewById(R.id.btn_send_picture_2);
		sendBtn1.setOnClickListener(this);
		sendBtn2.setOnClickListener(this);
		sendImageBtn1.setOnClickListener(this);
		sendImageBtn2.setOnClickListener(this);

		messageInput1 = (EditText) findViewById(R.id.msg_input_1);
		messageInput2 = (EditText) findViewById(R.id.msg_input_2);

		messageInput1.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				sendBtn1.setEnabled(editable.toString().trim().length() > 0);
			}
		});

		messageInput2.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				sendBtn2.setEnabled(editable.toString().trim().length() > 0);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onStop();

		// close connection
		tcpManager1.close();
		tcpManager2.close();
	}

	@Override
	public void onClick(View view) {
		if(view.equals(sendBtn1)){
			String msg = messageInput1.getText().toString().trim();
			if(msg.length() > 0){
				tcpManager1.send(msg);
				messageInput1.setText("");
			}
		} else if(view.equals(sendBtn2)){
			String msg = messageInput2.getText().toString().trim();
			if(msg.length() > 0){
				tcpManager2.send(msg);
				messageInput2.setText("");
			}
		} else if(view.equals(sendImageBtn1)){
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE_1);
		} else if(view.equals(sendImageBtn2)){
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE_2);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch(requestCode) {
			case REQ_CODE_PICK_IMAGE_1:
			case REQ_CODE_PICK_IMAGE_2:
				if(resultCode == RESULT_OK){
					Uri selectedImage = imageReturnedIntent.getData();
					String[] filePathColumn = {MediaStore.Images.Media.DATA};

					Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();

					if(requestCode == REQ_CODE_PICK_IMAGE_1){
						tcpManager1.sendImage(filePath);
					} else {
						tcpManager2.sendImage(filePath);
					}
				}
		}
	}

	@Override
	public void onConnected(boolean isServer) {
		messageInput1.setEnabled(true);
		messageInput2.setEnabled(true);
		sendImageBtn1.setEnabled(true);
		sendImageBtn2.setEnabled(true);
	}

	@Override
	public synchronized void onMessage(boolean isServer, String msg) {
		Log.d(TAG, "Received message: " + msg);

		TextView textView = new TextView(this);
		textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		textView.setText(msg);

		if(isServer){
			textView.setBackgroundColor(getResources().getColor(
					messageView1.getChildCount() % 2 == 0
							? android.R.color.transparent
							: R.color.light_blue));

			messageView1.addView(textView);
			scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
		} else {
			textView.setBackgroundColor(getResources().getColor(
					messageView2.getChildCount() % 2 == 0
							? android.R.color.transparent
							: R.color.light_blue));

			messageView2.addView(textView);
			scrollView2.fullScroll(ScrollView.FOCUS_DOWN);
		}

	}

	@Override
	public synchronized void onImage(boolean isServer, Bitmap bitmap) {
		Log.d(TAG, "Received image!");

		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		imageView.setImageBitmap(bitmap);

		if(isServer){
			imageView.setBackgroundColor(getResources().getColor(
					messageView1.getChildCount() % 2 == 0
							? android.R.color.transparent
							: R.color.light_blue));

			messageView1.addView(imageView);
			scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
		} else {
			imageView.setBackgroundColor(getResources().getColor(
					messageView2.getChildCount() % 2 == 0
							? android.R.color.transparent
							: R.color.light_blue));

			messageView2.addView(imageView);
			scrollView2.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}


	@Override
	public void onSendFailed(boolean isServer) {
		Toast.makeText(this, this.getString(R.string.failed_sending_msg), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClosed(boolean isServer) {

	}

	@Override
	public void onConnectionFailed(boolean isServer) {
		if(!isServer){
			// only show message when client fails to connect
			Toast.makeText(this, this.getString(R.string.failed_connecting_msg), Toast.LENGTH_SHORT).show();
		}
	}

}