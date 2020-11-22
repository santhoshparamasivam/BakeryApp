package com.cbe.bakery.createpdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.cbe.bakery.R;
import com.cbe.bakery.SummaryViewActivity;
import com.cbe.bakery.createpdf.PageNumeration;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PDFUtility
{
   private static final String TAG = PDFUtility.class.getSimpleName();
   private static Font FONT_TITLE     = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
   private static Font FONT_SUBTITLE      = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

   private static Font FONT_CELL      = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
   private static Font FONT_COLUMN    = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);



   public interface OnDocumentClose
   {
      void onPDFDocumentClose(File file);
   }

   public static void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, List<String[]> items, @NonNull String filePath, boolean isPortrait) throws Exception
   {
      if(filePath.equals(""))
      {
         throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
      }

      File file = new File(filePath);

      if(file.exists())
      {
         file.delete();
         Thread.sleep(50);
      }

      Document document = new Document();
      document.setMargins(24f,24f,32f,32f);
      document.setPageSize(isPortrait? PageSize.A4:PageSize.A4.rotate());

      PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
      pdfWriter.setFullCompression();
      pdfWriter.setPageEvent(new PageNumeration());

      document.open();

      setMetaData(document);

      addHeader(mContext,document);
      addEmptyLine(document, 3);

      document.add(createDataTable(items));

      addEmptyLine(document,2);
      document.add(createSignBox());

      document.close();

      try
      {
         pdfWriter.close();
      }
      catch (Exception ex)
      {
         Log.e(TAG,"Error While Closing pdfWriter : "+ex.toString());
      }

      if(mCallback!=null)
      {
         mCallback.onPDFDocumentClose(file);

      }
   }

   private static  void addEmptyLine(Document document, int number) throws DocumentException
   {
      for (int i = 0; i < number; i++)
      {
         document.add(new Paragraph(" "));
      }
   }

   private static void setMetaData(Document document)
   {
      document.addCreationDate();
      //document.add(new Meta("",""));
      document.addAuthor( "Santhosh");
      document.addCreator("Santhosh");
      document.addHeader("DEVELOPER","Santhosh");
   }

   private static void addHeader(Context mContext, Document document) throws Exception
   {
      PdfPTable table = new PdfPTable(3);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2,7,2});
      table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
      table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

      PdfPCell cell;
      {
         /*LEFT TOP LOGO*/
//         Drawable d= ContextCompat.getDrawable(mContext, R.drawable.logo);
//         Bitmap bmp=((BitmapDrawable) d).getBitmap();
//         ByteArrayOutputStream stream=new ByteArrayOutputStream();
//         bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
//
//         Image logo=Image.getInstance(stream.toByteArray());
//         logo.setWidthPercentage(80);
//         logo.scaleToFit(105,55);

         cell = new PdfPCell();
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setUseAscender(true);
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setPadding(2f);
         table.addCell(cell);
      }

      {
         /*MIDDLE TEXT*/
         cell = new PdfPCell();
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setPadding(8f);
         cell.setUseAscender(true);

         Paragraph temp = new Paragraph("Availabile Quantity" ,FONT_TITLE);
         temp.setAlignment(Element.ALIGN_CENTER);
         cell.addElement(temp);

         temp = new Paragraph("All Shops" ,FONT_SUBTITLE);
         temp.setAlignment(Element.ALIGN_CENTER);
         cell.addElement(temp);

         table.addCell(cell);
      }
      /* RIGHT TOP LOGO*/
      {
         PdfPTable logoTable=new PdfPTable(1);
         logoTable.setWidthPercentage(100);
         logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
         logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
         logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

         Drawable drawable=ContextCompat.getDrawable(mContext, R.drawable.logo);
         Bitmap bmp =((BitmapDrawable)drawable).getBitmap();

         ByteArrayOutputStream stream=new ByteArrayOutputStream();
         bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
         Image logo=Image.getInstance(stream.toByteArray());
         logo.setWidthPercentage(80);
         logo.scaleToFit(38,38);

         PdfPCell logoCell = new PdfPCell(logo);
         logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
         logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         logoCell.setBorder(PdfPCell.NO_BORDER);

         logoTable.addCell(logoCell);

         logoCell = new PdfPCell(new Phrase("Logo Text",FONT_CELL));
         logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
         logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         logoCell.setBorder(PdfPCell.NO_BORDER);
         logoCell.setPadding(4f);
         logoTable.addCell(logoCell);

         cell = new PdfPCell(logoTable);
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setUseAscender(true);
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setPadding(2f);
         table.addCell(cell);
      }

      document.add(table);
   }

   private static PdfPTable createDataTable(List<String[]> dataTable) throws DocumentException
   {
      PdfPTable table1 = new PdfPTable(4);
      table1.setWidthPercentage(100);
      table1.setWidths(new float[]{3f,3f,2f,2f});
      table1.setHeaderRows(1);
      table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
      table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

      PdfPCell cell;
      {
         cell = new PdfPCell(new Phrase("Shop Name", FONT_COLUMN));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPadding(4f);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase("Item Name", FONT_COLUMN));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPadding(4f);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase("Quantity", FONT_COLUMN));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPadding(4f);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase("Date", FONT_COLUMN));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPadding(4f);
         table1.addCell(cell);
      }

      float top_bottom_Padding = 8f;
      float left_right_Padding = 4f;
      boolean alternate = false;

      BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
      BaseColor cell_color;

      int size = dataTable.size();

      for (int i = 0; i < size; i++)
      {
         cell_color = alternate ? lt_gray : BaseColor.WHITE;
         String[] temp = dataTable.get(i);

         cell = new PdfPCell(new Phrase(temp[0], FONT_CELL));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPaddingLeft(left_right_Padding);
         cell.setPaddingRight(left_right_Padding);
         cell.setPaddingTop(top_bottom_Padding);
         cell.setPaddingBottom(top_bottom_Padding);
         cell.setBackgroundColor(cell_color);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase(temp[1], FONT_CELL));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPaddingLeft(left_right_Padding);
         cell.setPaddingRight(left_right_Padding);
         cell.setPaddingTop(top_bottom_Padding);
         cell.setPaddingBottom(top_bottom_Padding);
         cell.setBackgroundColor(cell_color);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase(temp[2], FONT_CELL));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPaddingLeft(left_right_Padding);
         cell.setPaddingRight(left_right_Padding);
         cell.setPaddingTop(top_bottom_Padding);
         cell.setPaddingBottom(top_bottom_Padding);
         cell.setBackgroundColor(cell_color);
         table1.addCell(cell);

         cell = new PdfPCell(new Phrase(temp[3], FONT_CELL));
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPaddingLeft(left_right_Padding);
         cell.setPaddingRight(left_right_Padding);
         cell.setPaddingTop(top_bottom_Padding);
         cell.setPaddingBottom(top_bottom_Padding);
         cell.setBackgroundColor(cell_color);
         table1.addCell(cell);

         alternate = !alternate;
      }

      return table1;
   }

   private static PdfPTable createSignBox() throws DocumentException
   {
      PdfPTable outerTable = new PdfPTable(1);
      outerTable.setWidthPercentage(100);
      outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

      PdfPTable innerTable = new PdfPTable(2);
      {
         innerTable.setWidthPercentage(100);
         innerTable.setWidths(new float[]{1,1});
         innerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

         //ROW-1 : EMPTY SPACE
         PdfPCell cell = new PdfPCell();
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setFixedHeight(60);
         innerTable.addCell(cell);

         //ROW-2 : EMPTY SPACE
         cell = new PdfPCell();
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setFixedHeight(60);
         innerTable.addCell(cell);

         //ROW-3 : Content Left Aligned
         cell = new PdfPCell();
         Paragraph temp = new Paragraph(new Phrase("Signature of Supervisor",FONT_SUBTITLE));
         cell.addElement(temp);

         temp = new Paragraph(new Phrase("( Santhosh )",FONT_SUBTITLE));
         temp.setPaddingTop(4f);
         temp.setAlignment(Element.ALIGN_LEFT);
         cell.addElement(temp);

         cell.setHorizontalAlignment(Element.ALIGN_LEFT);
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setPadding(4f);
         innerTable.addCell(cell);

         //ROW-4 : Content Right Aligned
         cell = new PdfPCell(new Phrase("Signature of Staff ",FONT_SUBTITLE));
         cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
         cell.setBorder(PdfPCell.NO_BORDER);
         cell.setPadding(4f);
         innerTable.addCell(cell);
      }

      PdfPCell signRow = new PdfPCell(innerTable);
      signRow.setHorizontalAlignment(Element.ALIGN_LEFT);
      signRow.setBorder(PdfPCell.NO_BORDER);
      signRow.setPadding(4f);

      outerTable.addCell(signRow);

      return outerTable;
   }

   private static Image getImage(byte[] imageByte, boolean isTintingRequired) throws Exception
   {
      Paint paint=new Paint();
      if(isTintingRequired)
      {
         paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
      }
      Bitmap input  = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
      Bitmap output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(output);
      canvas.drawBitmap(input,0,0,paint);

      ByteArrayOutputStream stream=new ByteArrayOutputStream();
      output.compress(Bitmap.CompressFormat.PNG,100,stream);
      Image image=Image.getInstance(stream.toByteArray());
      image.setWidthPercentage(80);
      return image;
   }

   private static Image getBarcodeImage(PdfWriter pdfWriter, String barcodeText)
   {
      Barcode128 barcode=new Barcode128();
      //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
      barcode.setFont(null);
      barcode.setCode(barcodeText);
      barcode.setCodeType(Barcode128.CODE128);
      barcode.setTextAlignment(Element.ALIGN_BASELINE);
      return barcode.createImageWithBarcode(pdfWriter.getDirectContent(),BaseColor.BLACK,null);
   }

   public static class DownloadTask extends AsyncTask<String, Integer, String> {
      private ProgressDialog mPDialog;
      private Context mContext;
      private PowerManager.WakeLock mWakeLock;
      private File mTargetFile;
      //Constructor parameters :
      // @context (current Activity)
      // @targetFile (File object to write,it will be overwritten if exist)
      // @dialogMessage (message of the ProgresDialog)
      public DownloadTask(Context context,File targetFile,String dialogMessage) {
         this.mContext = context;
         this.mTargetFile = targetFile;
         mPDialog = new ProgressDialog(context);

         mPDialog.setMessage(dialogMessage);
         mPDialog.setIndeterminate(true);
         mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         mPDialog.setCancelable(true);
         // reference to instance to use inside listener
         final DownloadTask me = this;
         mPDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
               me.cancel(true);
            }
         });
         Log.i("DownloadTask","Constructor done");
      }

      @Override
      protected String doInBackground(String... sUrl) {
         InputStream input = null;
         OutputStream output = null;
         HttpURLConnection connection = null;
         try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
               return "Server returned HTTP " + connection.getResponseCode()
                       + " " + connection.getResponseMessage();
            }
            Log.i("DownloadTask","Response " + connection.getResponseCode());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(mTargetFile,false);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
               // allow canceling with back button
               if (isCancelled()) {
                  Log.i("DownloadTask","Cancelled");
                  input.close();
                  return null;
               }
               total += count;
               // publishing the progress....
               if (fileLength > 0) // only if total length is known
                  publishProgress((int) (total * 100 / fileLength));
               output.write(data, 0, count);
            }
         } catch (Exception e) {
            return e.toString();
         } finally {
            try {
               if (output != null)
                  output.close();
               if (input != null)
                  input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
               connection.disconnect();
         }
         return null;
      }
      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         // take CPU lock to prevent CPU from going off if the user
         // presses the power button during download
         PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
         mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                 getClass().getName());
         mWakeLock.acquire();

         mPDialog.show();

      }

      @Override
      protected void onProgressUpdate(Integer... progress) {
         super.onProgressUpdate(progress);
         // if we get here, length is known, now set indeterminate to false
         mPDialog.setIndeterminate(false);
         mPDialog.setMax(100);
         mPDialog.setProgress(progress[0]);

      }

      @Override
      protected void onPostExecute(String result) {
         Log.i("DownloadTask", "Work Done! PostExecute");
         mWakeLock.release();
         mPDialog.dismiss();
         if (result != null)
            Toast.makeText(mContext,"Download error: "+result, Toast.LENGTH_LONG).show();
         else
            Toast.makeText(mContext,"File Downloaded", Toast.LENGTH_SHORT).show();
      }
   }
}