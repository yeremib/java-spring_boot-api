package com.enigma.wmb_api.util;

import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.BillDetail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvGeneratorUtil {
    private static final String CSV_HEADER = "BillId, TransDate, UserId, TableId, TransType, BillDetailId, MenuId, Quantity, Price/n";

    public String generateCsv(List<Bill> bills) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(CSV_HEADER);

        for (Bill bill : bills) {

            String tableNumId = null;
            if (bill.getTransType().equals("EI")) tableNumId = bill.getTableNum().getId();

            for (BillDetail billDetail : bill.getBillDetails()) {
                csvContent.append(bill.getId()).append(",")
                        .append(bill.getTransDate()).append(",")
                        .append(bill.getUser().getId()).append(",")
                        .append(tableNumId)
                        .append(bill.getTransType().getId()).append(",")
                        .append(billDetail.getId()).append(",")
                        .append(billDetail.getMenu().getId()).append(",")
                        .append(billDetail.getQty()).append(",")
                        .append(billDetail.getPrice()).append("/n");
            }
        }

        return csvContent.toString();
    }
}
