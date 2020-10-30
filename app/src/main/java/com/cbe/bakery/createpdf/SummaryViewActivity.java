//package com.cbe.bakery.createpdf;
//
//import android.os.Bundle;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.appcompat.widget.AppCompatEditText;
//
//import com.cbe.bakery.R;
//
//public class SummaryViewActivity extends AppCompatActivity implements PDFUtility.OnDocumentClose
//{
//   private static final String TAG = SummaryViewActivity.class.getSimpleName();
//   private AppCompatEditText rowCount;
//
//   @Override
//   protected void onCreate(Bundle savedInstanceState)
//   {
//      super.onCreate(savedInstanceState);
//      setContentView(R.layout.activity_summary_view);
//      rowCount = findViewById(R.id.rowCount);
//      AppCompatButton button1 = findViewById(R.id.button1);
//      button1.setOnClickListener(new View.OnClickListener()
//      {
//         @Override
//         public void onClick(View v)
//         {
//            String path = Environment.getExternalStorageDirectory().toString() + "/Availability.pdf";
//            try
//            {
//               PDFUtility.createPdf(v.getContext(), SummaryViewActivity.this,getSampleData(),path,true);
//            }
//            catch (Exception e)
//            {
//               e.printStackTrace();
//               Log.e(TAG,"Error Creating Pdf");
//               Toast.makeText(v.getContext(),"Error Creating Pdf",Toast.LENGTH_SHORT).show();
//            }
//         }
//      });
//   }
//
//   @Override
//   public void onPDFDocumentClose(File file)
//   {
//      Toast.makeText(this," Pdf Created",Toast.LENGTH_SHORT).show();
//   }
//
//   private List<String[]> getSampleData()
//   {
//      int count = 20;
//      if(!TextUtils.isEmpty(rowCount.getText()))
//      {
//         count = Integer.parseInt(rowCount.getText().toString());
//      }
//
//      List<String[]> temp = new ArrayList<>();
//      for (int i = 0; i < count; i++)
//      {
//         temp.add(new String[] {"C1-R"+ (i+1),"C2-R"+ (i+1)});
//      }
//      return  temp;
//   }
//}