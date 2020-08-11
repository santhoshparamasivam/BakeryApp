//package com.example.bakkeryApp.model;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.Dialog;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.location.Address;
//import android.location.Geocoder;
//import android.media.RingtoneManager;
//import android.media.ThumbnailUtils;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Build;
//import android.os.CountDownTimer;
//import android.os.Environment;
//import android.os.Handler;
//import android.provider.Settings;
//import android.text.Spannable;
//import android.text.TextUtils;
//import android.text.style.ForegroundColorSpan;
//import android.text.style.RelativeSizeSpan;
//import android.util.Base64;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.common.BitMatrix;
//import com.kgisl.edaproject.BuildConfig;
//import com.kgisl.edaproject.DigitalPass.DigiPassCreate;
//import com.kgisl.edaproject.HerdChatActivity;
//import com.kgisl.edaproject.MainHomeActivity;
//import com.kgisl.edaproject.R;
//import com.kgisl.edaproject.RequestModels.HerdEmergencyContact;
//import com.kgisl.edaproject.RequestModels.MultiContact;
//import com.kgisl.edaproject.RequestModels.MultiDates;
//import com.kgisl.edaproject.RequestModels.MultiEmergencyContact;
//import com.kgisl.edaproject.RequestModels.MultiVehicle;
//import com.kgisl.edaproject.model.DigitalPassModel;
//import com.kgisl.edaproject.widgets.FontEditText;
//import com.virgilsecurity.sdk.crypto.VirgilCrypto;
//import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
//import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
//import com.virgilsecurity.sdk.utils.ConvertionUtils;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.RandomAccessFile;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.security.cert.CertificateException;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.EnumMap;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.TimeZone;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import androidmads.library.qrgenearator.QRGContents;
//import androidmads.library.qrgenearator.QRGEncoder;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//import static android.graphics.Color.BLACK;
//import static android.graphics.Color.WHITE;
//import static com.kgisl.edaproject.utils.Config.NOTIFICATION_ID;
//
///**
// * Created by developer on 24/1/19.
// */
//
//public class Utils {
//
//    static String WEB_PUBLIC_KEY = "MCowBQYDK2VwAyEARApsUmKm7IRRqh2b88geZL1yH6ucyz/MhbzTl6Fo7uE=";
//
//    static String USER_PRIVATE_KEY = "MC4CAQAwBQYDK2VwBCIEIADzNRi0B7F1csytlCmvsLLBkC/OO4d7fKMdXUZq0+Kv";
//
//    static String USER_PUBLIC_KEY = "MCowBQYDK2VwAyEAewcR35u67vt/YYTk0mE6FQtqSlq8R+S+X1zF6I5qnPQ=";
//
//    static String WEB_PRIVATE_KEY = "MC4CAQAwBQYDK2VwBCIEILQOTcO9ez2zM6Rr0UQppkJVZZxpTQV7AUcWvGJ2YqCj";
//
//    static Handler handler;
//    static Runnable r;
//
//    public static List<MultiContact> multiContacts = new ArrayList<>(Arrays.asList(new MultiContact("" + 0, "", "")));
//    public static List<MultiEmergencyContact> multiEmergencyContacts = new ArrayList<>(Arrays.asList(new MultiEmergencyContact("" + 0, "", "", "")));
//    public static List<MultiDates> multiDates = new ArrayList<>(Arrays.asList(new MultiDates("" + 0, "", "")));
//    public static List<MultiVehicle> multiVehicles = new ArrayList<>(Arrays.asList(new MultiVehicle("" + 0, "", "", "", "")));
//    public static List<HerdEmergencyContact> herd_EmergencyContacts = new ArrayList<>(Arrays.asList(new HerdEmergencyContact("", "", "")));
//
//    public static boolean isLollipopHigher() {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
//    }
//
//    public static ProgressDialog progressDialog;
//
//    public static Dialog progressDialog1 = null;
//
//    public static float convertDpToPixel(float dp, Context context) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//    }
//
//    public static boolean isEmailValid(String email) {
//        boolean isValid = false;
//
//        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
//        CharSequence inputStr = email;
//
//        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            isValid = true;
//        }
//        return isValid;
//    }
//
//    public static int verifyPassword(String password) {
//        int isValid = 0;
//
//        if (TextUtils.isEmpty(password))
//            isValid = 2;
//        else if (password.length() < 5)
//            isValid = 3;
//        else if (password.length() > 32)
//            isValid = 4;
//        else
//            isValid = 1;
//
//        return isValid;
//    }
//
//    public enum ValidateAction {
//        NONE, isValueNULL, isValidPassword, isValidSalutation, isValidFirstname, isValidLastname, isValidCard, isValidExpiry, isValidMail, isValidConfirmPassword, isNullPromoCode, isValidCvv, isNullMonth, isNullYear, isNullCardname
//    }
//
//    public static boolean validations(ValidateAction VA, Activity con, String stringtovalidate) {
//        String message = "";
//        boolean result = false;
//        switch (VA) {
//            case isValueNULL:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_mobile_number);
//                else
//                    result = true;
//                break;
//            case isValidPassword:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_password);
//                else if (stringtovalidate.length() < 5)
//                    message = "" + con.getResources().getString(R.string.password_min_character);
//                else if (stringtovalidate.length() > 32)
//                    message = "" + con.getResources().getString(R.string.password_max_character);
//                else
//                    result = true;
//                break;
//            case isValidSalutation:
//                if (TextUtils.isEmpty(stringtovalidate) || stringtovalidate == null)
//                    message = "" + con.getResources().getString(R.string.please_select_your_salutation);
//                else
//                    result = true;
//                break;
//            case isValidFirstname:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_first_name);
//                else
//                    result = true;
//                break;
//            case isValidLastname:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_last_name);
//                else
//                    result = true;
//                break;
//            case isValidExpiry:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_expiry_date);
//                else
//                    result = true;
//                break;
//            case isValidMail:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.email_empty);
//                else if (!isEmailValid(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_valid_email);
//                else
//                    result = true;
//                break;
//            case isValidConfirmPassword:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_confirmation_password);
//                else
//                    result = true;
//                break;
//
//            case isValidCvv:
//                if (TextUtils.isEmpty(stringtovalidate))
//                    message = "" + con.getResources().getString(R.string.enter_the_valid_CVV);
//                else
//                    result = true;
//                break;
//        }
//        return result;
//    }
//
//    /**
//     * Get the Retrofit instance
//     *
//     * @return Retrofit
//     */
//    public static Retrofit getRetrofit(String baseDomain, Context context) {
////        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
////        // set your desired log level
////        logging.setLevel(BuildConfig.isShowLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
////        OkHttpClient client = new OkHttpClient.Builder()
////                .addInterceptor(new NoConnectivityInterceptor(context))
////                .addInterceptor(logging)
////                .connectTimeout(20, TimeUnit.MINUTES)
////                .readTimeout(20, TimeUnit.SECONDS)
////                .writeTimeout(20, TimeUnit.SECONDS)
////                .build();
//
//        OkHttpClient okHttpClient = getUnsafeOkHttpClient(context);
//
//        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
//        return new Retrofit.Builder()
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .baseUrl(baseDomain)
//                .build();
//    }
//
//
//    public static OkHttpClient getUnsafeOkHttpClient(Context context) {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[]{};
//                        }
//                    }
//            };
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
////            OkHttpClient okHttpClient = builder.build();
//
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            // set your desired log level
//            logging.setLevel(BuildConfig.isShowLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
//            OkHttpClient okHttpClient = builder
//                    .addInterceptor(new NoConnectivityInterceptor(context))
//                    .addInterceptor(logging)
//                    .connectTimeout(20, TimeUnit.MINUTES)
//                    .readTimeout(20, TimeUnit.SECONDS)
//                    .writeTimeout(20, TimeUnit.SECONDS).build();
//
//            return okHttpClient;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
////    private static Response onOnIntercept(Interceptor.Chain chain) throws IOException {
////        try {
////            Response response = chain.proceed(chain.request());
////            String content = UtilityMethods.convertResponseToString(response);
////            Logger.d("Message:: ", lastCalledMethodName + " - " + content);
////            return response.newBuilder().body(ResponseBody.create(response.body().contentType(), content)).build();
////        }
////        catch (SocketTimeoutException exception) {
////            exception.printStackTrace();
////            if(listener != null)
////                listener.onConnectionTimeout();
////        }
////
////        return chain.proceed(chain.request());
////    }
//
//    /**
//     * To check network connection available or not
//     *
//     * @return true if available else no, not available
//     */
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager cm =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (cm == null) {
//            return false;
//        }
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//    }
//
//    public static String getDeviceId(Context context) {
//        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//    }
//
//    public static boolean passwordValidation(String password) {
//        String pattern = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!*(),-./:;<>?_'{|}~])(?=\\S+$).{8,}";
//        return password.matches(pattern);
//    }
//
//    public static void increaseFontSizeForPath(Spannable spannable, String path, float increaseTime, int color) {
//        int startIndexOfPath = spannable.toString().indexOf(path);
//        spannable.setSpan(new RelativeSizeSpan(increaseTime), startIndexOfPath, startIndexOfPath + path.length(), 0);
//        spannable.setSpan(new ForegroundColorSpan(color), startIndexOfPath, startIndexOfPath + path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//    }
//
//    //returns bitmap image for google map marker
//    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
//
//    // Delay mechanism
//
//    public interface DelayCallback {
//        void afterDelay();
//    }
//
//    public static void delay(int millisecs, final DelayCallback delayCallback) {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                delayCallback.afterDelay();
//            }
//        }, millisecs); // afterDelay will be executed after (secs*1000) milliseconds.
//    }
//
//    public static String getAddress(Context context, LatLng latlng) {
//        Geocoder geocoder;
//        List<Address> addresses = null;
//        geocoder = new Geocoder(context, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (addresses.size() > 0) {
//            String address_line = addresses.get(0).getAddressLine(0);
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            return address_line;
//        } else {
//            return "Location Not Found";
//        }
//    }
//
//    public static String getIpAddress(Context context) {
//        String ip = "";
//        try {
//            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
//            while (enumNetworkInterfaces.hasMoreElements()) {
//                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
//                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
//                while (enumInetAddress.hasMoreElements()) {
//                    InetAddress inetAddress = enumInetAddress.nextElement();
//                    if (inetAddress.isSiteLocalAddress()) {
//                        ip += inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            ip += "Something Wrong! " + e.toString() + "\n";
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ip;
//    }
//
//    public static void createNotification(Context context, String msg, String messages, boolean isNeoDownloading) {
//        final int icon = R.drawable.ic_eda_icon_black;
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        int notificationId = (int) System.currentTimeMillis();
//        String channelId = "channel-01";
//        String channelName = "Channel Name";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
//            notificationManager.createNotificationChannel(mChannel);
//        }
//
//        NotificationCompat.Builder builder;
//        Intent intent = new Intent(context, MainHomeActivity.class);
//        if (!isNeoDownloading) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("data", "incoming");
//            intent.putExtra("msg", msg);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder = new NotificationCompat.Builder(context, channelId);
////                inboxStyle.addLine("New Message - EDA");
//        builder.setTicker("EDA")
//                .setWhen(0)
//                .setAutoCancel(true)
//                .setContentTitle("New Message - EDA")
//                .setContentIntent(pendingIntent)
////                        .setStyle(inboxStyle)
////                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                .setSmallIcon(R.drawable.ic_eda_icon_black)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
//                .setContentText(messages)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .build();
//        Notification notification = builder.build();
//        notification.defaults |= Notification.DEFAULT_LIGHTS;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
//        notificationManager.notify(notificationId, notification);
//    }
//    /*Alert Dialog View
//     *
//     * */
//
//
//    public static void alert_view(Context mContext, String title, String message, String success_txt) {
//        Dialog alertDialog;
//        final View view = View.inflate(mContext, R.layout.alert_view, null);
//        alertDialog = new Dialog(mContext, R.style.NewDialog);
//        alertDialog.setContentView(view);
//        alertDialog.setCancelable(true);
//        //new FontUtil(mContext, alertDialog.findViewById(R.id.rootlay));
//        alertDialog.show();
//        final TextView title_text = (TextView) alertDialog.findViewById(R.id.titleTxt);
//        final TextView message_text = (TextView) alertDialog.findViewById(R.id.messageTxt);
//        final TextView button_success = (TextView) alertDialog.findViewById(R.id.submitBut);
//        title_text.setText(title);
//        message_text.setText(message);
//        button_success.setText(success_txt);
//        button_success.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                alertDialog.dismiss();
//            }
//        });
//    }
//
//    /*Show Progress Dialog*/
//    public static void showProgressDialog(Context context) {
//
//        if (context != null || !progressDialog1.isShowing()) {
//            Log.d("showing_progress", "showing_progress");
//            progressDialog1 = ViewUtils.getDialog(context);
//            progressDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progressDialog1.setCanceledOnTouchOutside(false);
//            progressDialog1.setCancelable(false);
//            progressDialog1.show();
//
//        }
//        Log.d("received", "received");
//
//        /*if (context!=null||!progressDialog.isShowing()) {
//            //  progressDialog = new ProgressDialog(context,R.style.MyTheme);
//            progressDialog = new ProgressDialog(context);
//
//            //  WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
//            // params.x = 1000;
//
//            // progressDialog.getWindow().setAttributes(params);
//
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            progressDialog.setContentView(R.layout.progress_dialog);
//            // dialog.setMessage(Message);
//            //  progressDialog.setCancelable(false);
//            // progressDialog.show();
//
//        }
//        return progressDialog;
//*/
//    }
//
//    /*Dismiss Progress Dialog*/
//    public static void dismissProgressDialog() {
//        try {
//            if (progressDialog1.isShowing() || progressDialog1 != null) {
//                Log.d("showing_progress", "dismiss_progress");
//                progressDialog1.dismiss();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void showKeyboard(Activity activity, FontEditText editText) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//    }
//
//    public static String encrypt(String messageToEncrypt) {
//        String encryptedMessage = "";
//        try {
//            VirgilCrypto crypto = new VirgilCrypto();
//            byte[] publicKeyData = ConvertionUtils.base64ToBytes(USER_PUBLIC_KEY);
//            VirgilPublicKey receiverPublicKey = crypto.importPublicKey(publicKeyData);
//            byte[] dataToEncrypt = ConvertionUtils.toBytes(messageToEncrypt);
//            byte[] encryptedData = crypto.encrypt(dataToEncrypt, receiverPublicKey);
//            encryptedMessage = ConvertionUtils.toBase64String(encryptedData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return encryptedMessage;
//    }
//
//    public static String decrypt(Object encryptedData) {
//        String decryptedMessage = "";
//        try {
//            if (encryptedData instanceof String) {
//                byte[] encryptedBytes = ConvertionUtils.base64ToBytes(((String) encryptedData));
//                VirgilCrypto crypto = new VirgilCrypto();
//                byte[] privateKeyData = ConvertionUtils.base64ToBytes(USER_PRIVATE_KEY);
//                VirgilPrivateKey receiverPrivateKey = crypto.importPrivateKey(privateKeyData);
//                byte[] decryptedData = crypto.decrypt(encryptedBytes, receiverPrivateKey);
//                decryptedMessage = ConvertionUtils.toString(decryptedData);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return decryptedMessage;
//    }
//
//    public static byte[] encrypt(byte[] dataToEncrypt) {
//        byte[] encryptedData = null;
//        try {
//            VirgilCrypto crypto = new VirgilCrypto();
//            byte[] publicKeyData = ConvertionUtils.base64ToBytes(USER_PUBLIC_KEY);
//            VirgilPublicKey receiverPublicKey = crypto.importPublicKey(publicKeyData);
//            encryptedData = crypto.encrypt(dataToEncrypt, receiverPublicKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return encryptedData;
//    }
//
//    public static byte[] decrypt(byte[] encryptedData) {
//        byte[] decryptedData = null;
//        try {
//            VirgilCrypto crypto = new VirgilCrypto();
//            byte[] privateKeyData = ConvertionUtils.base64ToBytes(USER_PRIVATE_KEY);
//            VirgilPrivateKey receiverPrivateKey = crypto.importPrivateKey(privateKeyData);
//            decryptedData = crypto.decrypt(encryptedData, receiverPrivateKey);
//        } catch (Exception e) {
//            try {
//                VirgilCrypto crypto = new VirgilCrypto();
//                byte[] privateKeyData = ConvertionUtils.base64ToBytes(WEB_PRIVATE_KEY);
//                VirgilPrivateKey receiverPrivateKey = crypto.importPrivateKey(privateKeyData);
//                decryptedData = crypto.decrypt(encryptedData, receiverPrivateKey);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//        return decryptedData;
//    }
//
//
//    public static String getIpAddressForAllDevices(boolean useIPv4) {
//        try {
//            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface intf : interfaces) {
//                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
//                for (InetAddress addr : addrs) {
//                    if (!addr.isLoopbackAddress()) {
//                        String sAddr = addr.getHostAddress();
//                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
//                        boolean isIPv4 = sAddr.indexOf(':') < 0;
//
//                        if (useIPv4) {
//                            if (isIPv4)
//                                return sAddr;
//                        } else {
//                            if (!isIPv4) {
//                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
//                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//        } // for now eat exceptions
//        return "";
//    }
//
//    public static String copyFile(String fileName, File sourceFile, String userId, String filetype) {
//        Log.d("file_name_util", sourceFile.getPath());
//        String destFilePath = "";
//        File sdcard = Environment.getExternalStorageDirectory();
//        File mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/");
//        if (!mainDir.exists() && !mainDir.isDirectory()) {
//            mainDir.mkdir();
//        }
////        mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/"+filetype+"/");
////        if (!mainDir.exists() && !mainDir.isDirectory()) {
////            mainDir.mkdir();
////        }
//        final File destinationFile = new File(mainDir, fileName);
//        destFilePath = destinationFile.getAbsolutePath();
//        try {
//
////            Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
////            ByteArrayOutputStream stream = new ByteArrayOutputStream();
////            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////            byte[] byteArray = stream.toByteArray();
////            String saveThis = Base64.encodeToString(byteArray, Base64.DEFAULT);
////            PreferenceConnector.writeString(MainApplication.context, PreferenceConnector.BYTE_ARR_THUMB, saveThis);
////            bitmap.recycle();
//
//            final int THUMBSIZE = 64;
//            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(sourceFile.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String saveThis = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//            //  PreferenceConnector.writeString(MainApplication.context, PreferenceConnector.BYTE_ARR_THUMB, saveThis);
//            bitmap.recycle();
//            Log.d("file_read", "file_read");
//            bytesToFile(encrypt(sourceFile), destFilePath);
////            FileUtils.copyFile(sourceFile, destinationFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return destFilePath;
//    }
//
//    public static String copyFileCropImage(String fileName, File sourceFile, String userId, String filetype) {
//        Log.d("file_name_util", sourceFile.getPath());
//        String destFilePath = "";
//        File sdcard = Environment.getExternalStorageDirectory();
//        File mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/");
//        if (!mainDir.exists() && !mainDir.isDirectory()) {
//            mainDir.mkdir();
//        }
//        mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/" + filetype + "/");
//        if (!mainDir.exists() && !mainDir.isDirectory()) {
//            mainDir.mkdir();
//        }
//        final File destinationFile = new File(mainDir, fileName);
//        destFilePath = destinationFile.getAbsolutePath();
//        try {
//
//        /*    final int THUMBSIZE = 64;
//            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(sourceFile.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String saveThis = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
//
//            //bitmap.recycle();
//            Log.d("file_read", "file_read");
//            bytesToFile(encrypt(sourceFile), destFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return destFilePath;
//    }
//
//    public static String copyDocument(String fileName, File sourceFile, String userId, String filetype) {
//        String destFilePath = "";
//        File mainDir = null;
//        File sdcard = Environment.getExternalStorageDirectory();
//        if (filetype.equals("video")) {
//            mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Videos/");
//            if (!mainDir.exists() && !mainDir.isDirectory()) {
//                mainDir.mkdirs();
//            } else {
//                mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Videos/");
//                if (!mainDir.exists() && !mainDir.isDirectory()) {
//                    mainDir.mkdirs();
//                }
//            }
//        } else if (filetype.equals("file")) {
//            mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Document/");
//            if (!mainDir.exists() && !mainDir.isDirectory()) {
//                mainDir.mkdirs();
//            } else {
//                mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Document/");
//                if (!mainDir.exists() && !mainDir.isDirectory()) {
//                    mainDir.mkdirs();
//                }
//            }
//        } else if (filetype.equals("image")) {
//            mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Image/");
//            if (!mainDir.exists() && !mainDir.isDirectory()) {
//                mainDir.mkdirs();
//
//            } else {
//                mainDir = new File(sdcard.getAbsolutePath() + "/EDA2/" + userId + "/Image/");
//                if (!mainDir.exists() && !mainDir.isDirectory()) {
//                    mainDir.mkdirs();
//                }
//            }
//        }
//        if (mainDir != null) {
//            final File destinationFile = new File(mainDir, fileName);
//            destFilePath = destinationFile.getAbsolutePath();
//        }
//        try {
//
////            Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
////            ByteArrayOutputStream stream = new ByteArrayOutputStream();
////            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////            byte[] byteArray = stream.toByteArray();
////            String saveThis = Base64.encodeToString(byteArray, Base64.DEFAULT);
////            PreferenceConnector.writeString(MainApplication.context, PreferenceConnector.BYTE_ARR_THUMB, saveThis);
////            bitmap.recycle();
///*
//            final int THUMBSIZE = 64;
//            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(sourceFile.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String saveThis = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
//
//            //  PreferenceConnector.writeString(MainApplication.context, PreferenceConnector.BYTE_ARR_THUMB, saveThis);
//            // bitmap.recycle();
//
//            bytesToFile(encrypt(sourceFile), destFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return destFilePath;
//    }
//
//
//    public static boolean saveFile(String filePath) {
//        if (TextUtils.isEmpty(filePath))
//            return false;
//        filePath = filePath.replace("file:", "");
//        File file = new File(filePath);
//
//        String destFilePath = "";
//        File sdcard = Environment.getExternalStorageDirectory();
//        File mainDir = new File(sdcard.getAbsolutePath() + "/Download/");
//        if (!mainDir.exists() && !mainDir.isDirectory()) {
//            mainDir.mkdir();
//        }
//        final File destinationFile = new File(mainDir, file.getName());
//        destFilePath = destinationFile.getAbsolutePath();
//        try {
//            bytesToFile(Utils.decrypt(file), destFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public static byte[] fileToBytes(File file) {
//        int size = (int) file.length();
//        byte[] bytes = new byte[size];
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return bytes;
//    }
//
//    public static File bytesToFile(byte[] bytes, String filePath) {
//        File file = new File(filePath);
//        try {
//            OutputStream os = new FileOutputStream(file);
//            os.write(bytes);
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return file;
//    }
//
//    public static byte[] decrypt(File encryptedFile) {
//        return decrypt(Utils.fileToBytes(encryptedFile));
//    }
//
//    public static byte[] encrypt(File file) {
//        return encrypt(Utils.fileToBytes(file));
//    }
//
//    public static byte[] readFile(String file) throws IOException {
//        file = file.replace("file:", "");
//        return readFiles(new File(file));
//    }
//
//    public static byte[] readFiles(File file) throws IOException {
//        // Open file
//        RandomAccessFile f = new RandomAccessFile(file, "r");
//        try {
//            // Get and check length
//            long longlength = f.length();
//            int length = (int) longlength;
//            if (length != longlength)
//                throw new IOException("File size >= 2 GB");
//            // Read file and return data
//            byte[] data = new byte[length];
//            f.readFully(data);
//            return data;
//        } finally {
//            f.close();
//        }
//    }
//
//    public static String UTCtoLocal(String UTC_time) {
//        String output_time = null;
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd, yyyy h:mm a");
//
//        Date inputDate = new Date();
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            inputDate = simpleDateFormat.parse(UTC_time);
//            output_time = simpleDateFormat1.format(inputDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (output_time != null) {
//            Log.i("output_time", output_time);
//        }
//        return output_time;
//
//    }
//
//    public static String UTCtoLocalDate(String UTC_time) {
//        String output_time = null;
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
//
//        Date inputDate = new Date();
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            inputDate = simpleDateFormat.parse(UTC_time);
//            output_time = simpleDateFormat1.format(inputDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (output_time != null) {
//            Log.i("output_time", output_time);
//        }
//        return output_time;
//
//    }
//
//
//    public static String loadJSONFromAsset(Context context) {
//        String json = null;
//        try {
//            InputStream is = context.getAssets().open("sample_json.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }
//
//
////    public static ArrayList<DigitalPassModel> getAssestList(Context context) {
////
////        ArrayList<DigitalPassModel> digital_list = new ArrayList<>();
////
////        try {
////            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
////            JSONArray jsonArray = obj.getJSONArray("users");
////            for (int i = 0; i < jsonArray.length(); i++) {
////                JSONObject jsonObject = jsonArray.getJSONObject(i);
////                String userId = jsonObject.getString("userId");
////                String firstName = jsonObject.getString("firstName");
////                String lastName = jsonObject.getString("lastName");
////                String phonenumber = jsonObject.getString("phoneNumber");
////                DigitalPassModel model = new DigitalPassModel();
////                model.setFirst_name(firstName);
////                model.setLast_name(lastName);
////                model.setMobile_number(phonenumber);
////                model.setUser_id(userId);
////                digital_list.add(model);
////            }
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        return digital_list;
////    }
//
//    public static String MessageType(String message) {
//        String msg_type = null;
//        if (message.contains("file")) {
//            msg_type = "File";
//        } else if (message.contains("image")) {
//            msg_type = "Image";
//        } else if (message.contains("video")) {
//            msg_type = "Video";
//        } else if (message.contains("audio")) {
//            msg_type = "Audio";
//        } else if (message.contains("jpg") || message.contains("jpeg") || message.contains("png")) {
//            msg_type = "Image";
//        } else if (message.contains("3gp") || message.contains("mpg") || message.contains("mpeg") || message.contains("mpe") || message.contains("mp4") || message.contains("avi")) {
//            msg_type = "Video";
//        } else if (message.equals("image")) {
//            msg_type = "Image";
//        } else if (message.equals("video")) {
//            msg_type = "Video";
//        } else if (message.equals("text")) {
//            msg_type = "text";
//        }
//
//        return msg_type;
//    }
//
//    //siva 20/04/2020
//    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
//        String contentsToEncode = contents;
//        if (contentsToEncode == null) {
//            return null;
//        }
//        Map<EncodeHintType, Object> hints = null;
//        String encoding = guessAppropriateEncoding(contentsToEncode);
//        if (encoding != null) {
//            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
//            hints.put(EncodeHintType.CHARACTER_SET, encoding);
//        }
//        MultiFormatWriter writer = new MultiFormatWriter();
//        BitMatrix result;
//        try {
//            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
//        } catch (IllegalArgumentException iae) {
//            // Unsupported format
//            return null;
//        }
//        int width = result.getWidth();
//        int height = result.getHeight();
//        int[] pixels = new int[width * height];
//        for (int y = 0; y < height; y++) {
//            int offset = y * width;
//            for (int x = 0; x < width; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        return bitmap;
//    }
//
//    private static String guessAppropriateEncoding(CharSequence contents) {
//        // Very crude at the moment
//        for (int i = 0; i < contents.length(); i++) {
//            if (contents.charAt(i) > 0xFF) {
//                return "UTF-8";
//            }
//        }
//        return null;
//    }
//
//    public static String formatDate(String strDate) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        Date mDate = null;
//        try {
//            mDate = sdf.parse(strDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy");
//        String today = formatter.format(mDate);
//        System.out.println("Formatted date : " + today);
//        return today;
//    }
//
//    public static boolean applicationInForeground(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
//        boolean isActivityFound = false;
//
//        if (services.get(0).processName.equalsIgnoreCase(context.getPackageName()) && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//            isActivityFound = true;
//        }
//
//        return isActivityFound;
//    }
//
//    public static Bitmap generateQR(String contents) {
//        String contentsToEncode = contents;
//        if (contentsToEncode == null) {
//            return null;
//        }
//        Bitmap bitmap = null;
//        try {
//            QRGEncoder qrgEncoder = new QRGEncoder(contents, null, QRGContents.Type.TEXT, 200);
//            bitmap = qrgEncoder.encodeAsBitmap();
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    public static Retrofit generateBaseUrl() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.DIGIPASS_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit;
//    }
//
//    public static Retrofit generateHerdBaseUrl() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.HERD_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit;
//    }
//
//    public static String getFileSize(long size) {
//        if (size <= 0)
//            return "0";
//
//        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
//        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//        String cachefile = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
//
//
//        return cachefile;
//    }
//
//    public String filesize(int size) {
//        String hrSize = "";
//        double m = size / 1024.0;
//        DecimalFormat dec = new DecimalFormat("0.00");
//
//        if (m > 1) {
//            hrSize = dec.format(m).concat(" MB");
//        } else {
//            hrSize = dec.format(size).concat(" KB");
//        }
//        return hrSize;
//    }
//
//    public static String SendNotification(final Context context, Long countdowntimer) {
//        handler = new Handler();
//        r = new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            public void run() {
//                Log.d("received", "received1");
//
//                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//                Intent intent = new Intent(context, HerdChatActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//
//                NotificationChannel notificationChannel;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    String CHANNEL_ID = "my_channel_01";
//                    String CHANNEL_NAME = "my Channel Name";
//
//                    notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
//                    notificationChannel.enableLights(true);
//                    notificationChannel.setLightColor(Color.WHITE);
//                    notificationChannel.setShowBadge(true);
//                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                    notificationManager.createNotificationChannel(notificationChannel);
//                } else {
//                    String CHANNEL_ID = "my_channel_01";
//                    String CHANNEL_NAME = "my Channel Name";
//
//                    notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
//                    notificationChannel.enableLights(true);
//                    notificationChannel.setLightColor(Color.WHITE);
//                    notificationChannel.setShowBadge(true);
//                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                    notificationManager.createNotificationChannel(notificationChannel);
//                }
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_01")
//                        .setSmallIcon(R.drawable.small_icon)
//                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                        .setContentTitle("Herd Timer Alert Triggered")
//                        .setContentIntent(pendingIntent)
//                        .setOngoing(true)
//                        .setAutoCancel(false);
//
//                notificationManager.notify(1, builder.build());
//
//            }
//        };
//        handler.postDelayed(r, countdowntimer);
//        Log.d("received", "received1");
//        return null;
//    }
//
//    public static String StopNotification() {
//        // handler=new Handler();
//        //  handler.removeCallbacks(r);
//        return null;
//    }
//}
