package com.enigma.wmb_api.util;

import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.BillDetail;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BillPdfExporter {
    private List<Bill> listBill;

    public BillPdfExporter(List<Bill> listBill) {
        this.listBill = listBill;
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();

        cell.setPhrase(new Phrase("Bill Id"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Transaction Date"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Username"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Menu"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Total Price"));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        List<String> menus = new ArrayList<>();
        Integer totalPrice = 0;
        for (Bill bill : listBill) {
            for (BillDetail billDetail : bill.getBillDetails()) {
                menus.add(billDetail.getMenu().getName());
                totalPrice += billDetail.getPrice();
            }
            table.addCell(bill.getId());
            table.addCell(bill.getTransDate().toString());
            table.addCell(bill.getUser().getName());
            table.addCell(menus.toString());
            table.addCell(totalPrice.toString());
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4.rotate());

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("WMB Transaction Bills"));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();
    }
}
