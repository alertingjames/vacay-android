package com.mv.vacay.main.provider;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.internal.WebDialog;
import com.google.android.gms.cast.framework.Session;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScreenshotActivity extends AppCompatActivity {

    NetworkImageView logo;
    ImageView back, facebookButton, linkedinButton, twitterButton, indeedButton;
    TextView jobName, jobName1, company, department,
            reqId, location, postingDate, description, empty,shareButton, down;
    ScrollView scroll;
    ImageLoader _imageLoader;
    String content="",title="", logoUrl="", desc="";
    File file;
    String selection="";

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESSION_QUALITY = 30; // from 0 to 100 works only for JPG

    private static final String TAG = "JobDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        title="Job title: "+Commons.job.getJobName();
        logoUrl=Commons.job.getLogoUrl();
        content="Department: "+Commons.job.getDepartment()+"\n"+
                "Location: "+Commons.job.getLocation()+"\n"+
                "Req ID: "+Commons.job.getJobReqId()+"\n"+
                "Posting Date: "+Commons.job.getPostingDate()+"\n"+
                "Extra: "+Commons.job.getEmptyField();
        desc="Description: "+Commons.job.getDescription();

        logo=(NetworkImageView)findViewById(R.id.logo);

        jobName1=(TextView)findViewById(R.id.name1);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        jobName1.setTypeface(font);

        company=(TextView)findViewById(R.id.company);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/futura-md-bt-bold-58e2b41ab199c.ttf");
        company.setTypeface(font);

        department=(TextView)findViewById(R.id.department);
        reqId=(TextView)findViewById(R.id.reqId);
        location=(TextView)findViewById(R.id.location);
        postingDate=(TextView)findViewById(R.id.postDate);
        description=(TextView)findViewById(R.id.description);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        description.setTypeface(font);

        empty=(TextView)findViewById(R.id.empty);

        if(Commons.job.getLogoUrl().length()>0)
            logo.setImageUrl(Commons.job.getLogoUrl(),_imageLoader);
        location.setText(Commons.job.getLocation());
        postingDate.setText(Commons.job.getPostingDate());

        jobName1.setText(Commons.job.getJobName());

        company.setText(Commons.job.getCompany());
        reqId.setText(Commons.job.getJobReqId());
        department.setText(Commons.job.getDepartment());
        description.setText(Commons.job.getDescription());
        empty.setText(Commons.job.getEmptyField());

        shareButton=(TextView)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        View rootView = (View) findViewById(R.id.rootView);

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                File file=makeScreenShot(rootView);

                if(Commons._is_instagram){

                    Commons._is_instagram=false;

                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    if (intent != null)
                    {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage("com.instagram.android");
                        try {
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                    Environment.getExternalStorageDirectory()
                                            + File.separator + "test.jpg", "I am Happy", "Share happy !")));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        shareIntent.setType("image/jpeg");

                        startActivity(shareIntent);
                    }
                    else
                    {
                        // bring user to the market to download the app.
                        // or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
                        startActivity(intent);
                    }

                }
                else {
                    showAlertDialogPost(file);
                }

            }
        });

    }
    public void showAlertDialogJobName(String name) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Job Title");
        alertDialog.setMessage(name);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void onShareClick() {
        Resources resources = getResources();

        Uri uri = Uri.fromFile(file);
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+"Please read this pdf.");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        //    emailIntent.setType("message/rfc822");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);


        Intent openInChooser = Intent.createChooser(emailIntent, "Share As...");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content);
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.

                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content);
                }
                else if(packageName.contains("linkedin")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, "Linkedin Text");
                }
                else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content+"\n"+desc);
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+"Please read this pdf.");
                    intent.putExtra(Intent.EXTRA_SUBJECT, title);
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void createPdfFile(){
        new Thread() {
            public void run() {
                // Get the directory for the app's private pictures directory.
                final File file = new File(Environment.getExternalStorageDirectory(), "PdfTest.pdf");

                if (file.exists ()) {
                    file.delete ();
                }

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);

                    PdfDocument document = new PdfDocument();
                    Point windowSize = new Point();
                    getWindowManager().getDefaultDisplay().getSize(windowSize);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(windowSize.x, windowSize.y, 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    View content = getWindow().getDecorView();
                    content.draw(page.getCanvas());
                    document.finishPage(page);
                    document.writeTo(out);
                    document.close();
                    out.flush();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScreenshotActivity.this, "File created: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.d("TAG_PDF", "File was not created: "+e.getMessage());
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE = 99;
    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Write Storage Permission Needed")
                        .setMessage("This app needs the Write Storage permission, please accept to use to write functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ScreenshotActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void sharePdfFile(){
        String emailAddress[] = {"alertingjames@gmail.com"}; // email: test@gmail.com

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "PdfTest.pdf"));
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Pdf");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Please get pdf");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.share_pdf:
//                sharePdfFile1();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void createPdf() throws IOException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("LOG_TAG", "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + timeStamp + ".pdf");

        file=myFile;

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content

        Bitmap image = drawableToBitmap(LoadImageFromWebOperations(Commons.job.getLogoUrl()));
//        if(image.getWidth()>800 && image.getHeight()>800)
//            image=getResizedBitmap(image,800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50 , stream);
        Image myImg = Image.getInstance(stream.toByteArray());
        myImg.setAlignment(Image.TOP);
        document.add(myImg);

        document.add(new Paragraph(title));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(content));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(desc));

        //Step 5: Close the document
        document.close();

        Toast.makeText(ScreenshotActivity.this, "File created: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        onShareClick();

        //    sharePdfFile1();

    }

    private void sharePdfFile1(){
        String emailAddress[] = {"alertingjames@gmail.com"}; // email: test@gmail.com

        String emailAddress1[] = {"alertingjames@gmail.com"};

        Uri uri = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress1);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, title+"\n"+ content+"\n"+"\n"+"Please read this pdf");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }

    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/PICTURES/Screenshots/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public File makeScreenShot(View root) {
        //i have different code here in my project
        //it is just a test case

        String path="";  Log.d("Width==>",String.valueOf(root.getWidth()));

//        Bitmap screenshot = getResizedBitmap(Bitmap.createBitmap(root.getWidth(),root.getHeight(), Bitmap.Config.RGB_565),500);
//
//        Log.d("ImageBytes===",String.valueOf(screenshot.getByteCount())+"/"+String.valueOf(screenshot.getRowBytes()));

        Bitmap screenshot = Bitmap.createBitmap(root.getWidth(),root.getHeight(), Bitmap.Config.RGB_565);

//        if(screenshot.getWidth()>800 && screenshot.getHeight()>800)
//            screenshot=getResizedBitmap(screenshot,800);

        Canvas canvas = new Canvas(screenshot);
        root.draw(canvas);
        File file = null;

        OutputStream fout = null;
        try {

            //        File imageFile = ScreenshotContentProvider.getFile(this);
            File imageFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "test.jpg");

            file=imageFile;
            Log.e("MA.makeScreenShot", "Saving File to: " + imageFile.getAbsolutePath());

            path=imageFile.getPath();
            fout = new FileOutputStream(imageFile);
            screenshot.compress(COMPRESS_FORMAT, COMPRESSION_QUALITY, fout);

            fout.flush();
            fout.close();

        } catch (Exception e) {
            Log.e("ScreenshotActivity", "Exception in makeScreenShot");
            e.printStackTrace();
        }

        return file;

    }

    public void takeScreenshot(View view){

        Bitmap bitmap;

        View v1=view.getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "test.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialogPost(final File bitmap) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle(Commons.job.getJobName());
        alertDialog.setMessage("Do you want to post?");

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, title+"\n"+"https://www.vacaycarpediem.com/posts");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmap));
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        //================================

                        PackageManager pm = getPackageManager();
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");


                        Intent openInChooser = Intent.createChooser(shareIntent, "Share As...");

                        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                        for (int ii = 0; ii < resInfo.size(); ii++) {
                            // Extract the label, append it, and repackage it in a LabeledIntent
                            ResolveInfo ri = resInfo.get(ii);
                            String packageName = ri.activityInfo.packageName;
                            if(packageName.contains("facebook") || packageName.contains("twitter") || packageName.contains("linkedin")) {
                                shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, title+"\n"+"https://www.vacaycarpediem.com/posts");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmap));
                                shareIntent.setType("image/jpeg");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intentList.add(new LabeledIntent(shareIntent, packageName, ri.loadLabel(pm), ri.icon));
                            }
                        }

                        // convert intentList to array
                        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                        startActivity(openInChooser);

                        //============================================

//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //        startActivity(Intent.createChooser(shareIntent, "Share as..."));



//                        SharePhoto photo = new SharePhoto.Builder()
//                                .setBitmap(bitmap)
//                                .build();
//                        ShareMediaContent content = new ShareMediaContent.Builder()
//                                .addMedium(photo)
////				.addMedium(shareVideo)
//                                .build();
//
//
//                        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

}
