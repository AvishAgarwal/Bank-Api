package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.repositories.AccountRepository;
import com.bank.bankapi.repositories.TransactionRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;


    @Override
    public Integer createTransaction(Account from, Account to, double amount) throws BAuthException {
        from.setCurrent_balance(from.getCurrent_balance()-amount);
        to.setCurrent_balance(to.getCurrent_balance()+amount);

        int transactionId =transactionRepository.createTransaction(from.getAccount_number(),to.getAccount_number(),amount,
                from.getCurrent_balance(), to.getCurrent_balance());
        boolean flag1= accountRepository.updateBalance(from.getAccount_number(), from.getCurrent_balance());

        boolean flag2=accountRepository.updateBalance(to.getAccount_number(), to.getCurrent_balance());
        if(!flag1 && !flag2)
            throw new BAuthException("Unable to update balance");

        return transactionId;
    }

    @Override
    public boolean getTransaction(String start, String stop, int accountNumber) throws BAuthException, FileNotFoundException, DocumentException {
        List<Transaction> list= transactionRepository.getTransactions(start,stop,accountNumber);
        Document document=new Document();
        PdfWriter.getInstance(document, new FileOutputStream("transactions.pdf"));
        document.open();
        PdfPTable table= new PdfPTable(5);
        addTableHeader(table);
        addRows(table,list,accountNumber);
        document.add(table);
        document.close();
        return true;
    }
    private void addTableHeader(PdfPTable table) {
        Stream.of("Date", "Account", "Amount","Balance","Type")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }
    private void addRows(PdfPTable table,List<Transaction> transactions,int accountNumber) {
        for(Transaction transaction:transactions)
        {
            table.addCell(transaction.getCreated_at());//1
            if (transaction.getFrom_id() == accountNumber) {//2
                table.addCell(String.valueOf(transaction.getTo_id()));
            } else {
                table.addCell(String.valueOf(transaction.getFrom_id()));
            }
            table.addCell(String.valueOf(transaction.getAmount()));//3
            if (transaction.getFrom_id() == accountNumber) {//4 5
                table.addCell(String.valueOf(transaction.getFrom_balance()));
                table.addCell("Debited");
            } else {
                table.addCell(String.valueOf(transaction.getTo_balance()));
                table.addCell("Credited");
            }

        }
    }
}
