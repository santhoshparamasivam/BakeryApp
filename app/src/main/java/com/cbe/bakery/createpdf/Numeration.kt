//package com.cbe.bakery.createpdf
//
//import android.util.Log
//import com.itextpdf.text.*
//import com.itextpdf.text.pdf.PdfPCell
//import com.itextpdf.text.pdf.PdfPTable
//import com.itextpdf.text.pdf.PdfPageEventHelper
//import com.itextpdf.text.pdf.PdfWriter
//
//class Numeration internal constructor() : PdfPageEventHelper() {
//    override fun onEndPage(writer: PdfWriter, document: Document) {
//        try {
//            var cell: PdfPCell
//            val table = PdfPTable(2)
//            table.widthPercentage = 100f
//            table.setWidths(floatArrayOf(3f, 1f))
//
//            //1st Column
//            val anchor = Anchor(Phrase("iText Pdf Sample Project", FONT_FOOTER))
//            anchor.reference = "http://mywebsite.com/"
//            cell = PdfPCell(anchor)
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = 0
//            cell.setPadding(2f)
//            table.addCell(cell)
//            table.totalWidth =
//                document.pageSize.width - document.leftMargin() - document.rightMargin()
//            table.writeSelectedRows(
//                0,
//                -1,
//                document.leftMargin(),
//                document.bottomMargin() - 5,
//                writer.directContent
//            )
//
//            //2nd Column
//            cell = PdfPCell(Phrase("Page - " + writer.pageNumber.toString(), FONT_FOOTER))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = 0
//            cell.setPadding(2f)
//            table.addCell(cell)
//            table.totalWidth =
//                document.pageSize.width - document.leftMargin() - document.rightMargin()
//            table.writeSelectedRows(
//                0,
//                -1,
//                document.leftMargin(),
//                document.bottomMargin() - 5,
//                writer.directContent
//            )
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            Log.e(TAG, ex.toString())
//        }
//    }
//
//    companion object {
//        private val TAG = Numeration::class.java.simpleName
//       var FONT_FOOTER: Font =
//            Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL, BaseColor.DARK_GRAY)
//
//        private fun Font(
//            timesRoman: Font.FontFamily,
//            i: Int,
//            normal: Int,
//            darkGray: BaseColor?
//        ): Font {
//            TODO("Not yet implemented")
//        }
//    }
//}