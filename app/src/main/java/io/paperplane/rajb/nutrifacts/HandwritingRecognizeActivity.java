package io.paperplane.rajb.nutrifacts;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.microsoft.projectoxford.vision.VisionServiceClient;
        import com.microsoft.projectoxford.vision.VisionServiceRestClient;
        import com.microsoft.projectoxford.vision.contract.HandwritingRecognitionOperation;
        import com.microsoft.projectoxford.vision.contract.HandwritingRecognitionOperationResult;
        import com.microsoft.projectoxford.vision.contract.HandwritingTextLine;
        import com.microsoft.projectoxford.vision.contract.HandwritingTextWord;
        import com.microsoft.projectoxford.vision.rest.VisionServiceException;

        import io.paperplane.rajb.nutrifacts.R;
        import io.paperplane.rajb.nutrifacts.helper.ImageHelper;

        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.lang.ref.WeakReference;
        import java.util.ArrayList;

public class HandwritingRecognizeActivity extends ActionBarActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private Button buttonSelectImage;

    // The URI of the image selected to detect.
    private Uri imagUrl;

    // The image selected to detect.
    private Bitmap bitmap;

    // The edit to show status and result.
    private EditText editText;

    private VisionServiceClient client;

    //max retry times to get operation result
    private int retryCountThreshold = 30;

    private static int index = 0;

    private static boolean isPrimed = false;
    private static String[] keywords = new String[]{"calories", "fat", "sodium", "carbohydrate" , "protein", "sugars"};
    private static int[] foodProperties = new int[keywords.length];

    private static ArrayList<String> passedTypes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_handwriting);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        buttonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
        editText = (EditText) findViewById(R.id.editTextResult);
    }

    public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void switchIntents(){
        Intent i = new Intent(this, Graphing.class);
        startActivity(i);
    }

    private static boolean isKeyword(String s){
        boolean ashwatcm = false;
        for(String p: keywords){
           if(p.equals(s)){ashwatcm=true;}
        }
        return ashwatcm;
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        editText.setText("");

        Intent intent;
        intent = new Intent(HandwritingRecognizeActivity.this, io.paperplane.rajb.nutrifacts.helper.SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AnalyzeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    imagUrl = data.getData();

                    bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imagUrl, getContentResolver());
                    if (bitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(bitmap);

                        // Add detection log.
                        Log.d("AnalyzeActivity", "Image: " + imagUrl + " resized to " + bitmap.getWidth()
                                + "x" + bitmap.getHeight());

                        doRecognize();
                    }
                }
                break;
            default:
                break;
        }
    }


    public void doRecognize() {
        buttonSelectImage.setEnabled(false);
        editText.setText("Analyzing...");

        try {
            new doRequest(this).execute();
        } catch (Exception e) {
            editText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException, InterruptedException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray())) {
                //post image and got operation from API
                HandwritingRecognitionOperation operation = this.client.createHandwritingRecognitionOperationAsync(inputStream);

                HandwritingRecognitionOperationResult operationResult;
                //try to get recognition result until it finished.

                int retryCount = 0;
                do {
                    if (retryCount > retryCountThreshold) {
                        throw new InterruptedException("Can't get result after retry in time.");
                    }
                    Thread.sleep(1000);
                    operationResult = this.client.getHandwritingRecognitionOperationResultAsync(operation.Url());
                }
                while (operationResult.getStatus().equals("NotStarted") || operationResult.getStatus().equals("Running"));

                String result = gson.toJson(operationResult);
                Log.d("result", result);
                return result;

            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }

    }


    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        private WeakReference<HandwritingRecognizeActivity> recognitionActivity;

        public doRequest(HandwritingRecognizeActivity activity) {
            recognitionActivity = new WeakReference<HandwritingRecognizeActivity>(activity);
        }


        @Override
        protected String doInBackground(String... args) {
            try {
                if (recognitionActivity.get() != null) {
                    return recognitionActivity.get().process();
                }
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (recognitionActivity.get() == null) {
                return;
            }
            // Display based on error existence
            if (e != null) {
                recognitionActivity.get().editText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                HandwritingRecognitionOperationResult r = gson.fromJson(data, HandwritingRecognitionOperationResult.class);

                StringBuilder resultBuilder = new StringBuilder();
                //if recognition result status is failed. display failed
                if (r.getStatus().equals("Failed")) {
                    resultBuilder.append("Error: Recognition Failed");
                } else {
                    for (HandwritingTextLine line : r.getRecognitionResult().getLines()) {
                        for (HandwritingTextWord word : line.getWords()) {

                            resultBuilder.append(word.getText() + " ");

                        }
                        resultBuilder.append("\n");
                    }
                    resultBuilder.append("\n");
                }

                /*Log.d("DEBUG", foodProperties[0]+"");
                Log.d("DEBUG", foodProperties[1] + "");
                Log.d("DEBUG", foodProperties[2]+"");
                Log.d("DEBUG", foodProperties[3]+"");
                Log.d("DEBUG", foodProperties[4] + "");*/


            }
            recognitionActivity.get().buttonSelectImage.setEnabled(true);
            switchIntents();

        }

    }



}