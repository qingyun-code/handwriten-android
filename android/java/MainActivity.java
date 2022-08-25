import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    /* 没连接上服务器 */
    private static final int NON_INFORMATION = -1;

    /* 返回结果 */
    private static final int ZERO_INFORMATION  = 0;
    private static final int ONE_INFORMATION   = 1;
    private static final int TWO_INFORMATION   = 2;
    private static final int THREE_INFORMATION = 3;
    private static final int FOUR_INFORMATION  = 4;
    private static final int FIVE_INFORMATION  = 5;
    private static final int SIX_INFORMATION   = 6;
    private static final int SEVEN_INFORMATION = 7;
    private static final int EIGHT_INFORMATION = 8;
    private static final int NINE_INFORMATION  = 9;


    /* 定义控件 */
    private Button takePhoto;
    private Button chooseFromAlbum;
    private Button sendImageToServer;
    private TextView myResult;
    private ImageView picture;

    private Uri imageUri;

    private String myImagePath = "null";

    private final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建控件对象
        takePhoto = (Button) findViewById(R.id.take_photo);
        chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        sendImageToServer = (Button) findViewById(R.id.send_image);
        myResult = (TextView) findViewById(R.id.get_result);
        picture = (ImageView) findViewById(R.id.picture);

        // 发送图片到服务器
        sendImageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myImagePath.equals("null")) {
                    File f = new File(myImagePath);
                    try {
                        runUpload(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "没有输入图片的路径", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 调用摄像头的点击事件
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 判断系统版本并选择相应的方法，小于24表明版本低于7.0
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.coalgangueidentification.fileprovider", outputImage);
                }

                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                // 开启当前活动
                startActivityForResult(intent, TAKE_PHOTO);

            }
        });

        // 调用手机相册的点击事件
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });
    }

    /* 定义UI线程 */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NON_INFORMATION:
                    myResult.setText("暂时没有识别结果！");
                    break;
                case ZERO_INFORMATION:
                    myResult.setText("识别结果为：0");
                    break;
                case ONE_INFORMATION:
                    myResult.setText("识别结果为：1");
                    break;
                case TWO_INFORMATION:
                    myResult.setText("识别结果为：2");
                    break;
                case THREE_INFORMATION:
                    myResult.setText("识别结果为：3");
                    break;
                case FOUR_INFORMATION:
                    myResult.setText("识别结果为：4");
                    break;
                case FIVE_INFORMATION:
                    myResult.setText("识别结果为：5");
                    break;
                case SIX_INFORMATION:
                    myResult.setText("识别结果为：6");
                    break;
                case SEVEN_INFORMATION:
                    myResult.setText("识别结果为：7");
                    break;
                case EIGHT_INFORMATION:
                    myResult.setText("识别结果为：8");
                    break;
                case NINE_INFORMATION:
                    myResult.setText("识别结果为：9");
                    break;
                default:
                    break;
            }
        }
    };

    /* 打开手机相册 */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");

        // 打开相册
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了打开相册请求！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // 拍照获取图片
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {

                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);

                        // 将拍摄到的图片保存到相册当中
                        String saveToMyAlbum = saveImageToGallery(this, bitmap);

                        // 若照片保存成功，则把照片路径赋值到要传输照片的路径，否则提示
                        if (!saveToMyAlbum.equals("false")){
                            myImagePath = saveToMyAlbum;
                        }
                        else{
                            Toast.makeText(this, "图片没有保存成功", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            // 从相册选择照片获取图片
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);

            // 赋值给要传送到服务器的图片路径
            myImagePath = imagePath;
        } else {
            Toast.makeText(this, "图片获取失败！", Toast.LENGTH_SHORT).show();
        }
    }

    /* 保存文件到指定路径 */
    public String saveImageToGallery(Context context, Bitmap bmp) {

        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);

        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";

        File file = new File(appDir, fileName);
        try {

            FileOutputStream fos = new FileOutputStream(file);

            // 通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            // 保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            // 如果保存成功则返回保存路径，否则返回错误码
            if (isSuccess) {
                return (storePath + "/" + fileName);
            } else {
                return "false";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "false";
    }

    /* 运行上传线程 */
    public void runUpload(File f) throws Exception {
        final File file = f;
        new Thread(() -> {

                // 子线程需要做的工作
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file","image.jpg",
                                RequestBody.create(MEDIA_TYPE_JPG, file))
                        .build();

                // 设置为自己的ip地址
                Request request = new Request.Builder()
                        .url(getString(R.string.server_ip))
                        .post(requestBody)
                        .build();

                // 获取请求回应对象
                Call call = client.newCall(request);

                // 对回应进行的处理
                call.enqueue(new Callback() {

                    @Override
                    // 当无法连接上服务器时，发出提示信号
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                                sendMessage(NON_INFORMATION);
                        });
                    }

                    @Override
                    // 当连接上服务器时，对请求的回复进行响应
                    public void onResponse(Call call, final Response response) throws IOException {

                        // 获取回调的字符串信息
                        final String res = response.body().string();

                        runOnUiThread(() -> {

                            switch(res.charAt(0)) {
                                case '0':
                                    sendMessage(ZERO_INFORMATION);
                                    break;
                                case '1':
                                    sendMessage(ONE_INFORMATION);
                                    break;
                                case '2':
                                    sendMessage(TWO_INFORMATION);
                                    break;
                                case '3':
                                    sendMessage(THREE_INFORMATION);
                                    break;
                                case '4':
                                    sendMessage(FOUR_INFORMATION);
                                    break;
                                case '5':
                                    sendMessage(FIVE_INFORMATION);
                                    break;
                                case '6':
                                    sendMessage(SIX_INFORMATION);
                                    break;
                                case '7':
                                    sendMessage(SEVEN_INFORMATION);
                                    break;
                                case '8':
                                    sendMessage(EIGHT_INFORMATION);
                                    break;
                                case '9':
                                    sendMessage(NINE_INFORMATION);
                                    break;
                                default:
                                    break;
                            }
                        });
                    }
                });
        }).start();
    }

    // 发送给UI线程显示信息
    public void sendMessage(int information) {

        runOnUiThread(() -> {

            Message msg = new Message();
            msg.what = information;
            handler.sendMessage(msg);

            if (information == NON_INFORMATION) {
                // 提示连接服务器失败信号
                Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

}