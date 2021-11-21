package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
import com.bank.bankapi.repositories.AccountRepository;
import com.bank.bankapi.repositories.TransactionRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    Logger logger= LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;


    @Override
    public Integer createTransaction(Account from, Account to, double amount) throws BBadRequestException {
        logger.info("From account {} to account {} amount {}",from,to,amount);
        from.setCurrent_balance(from.getCurrent_balance() - amount);
        to.setCurrent_balance(to.getCurrent_balance() + amount);

        int transactionId = transactionRepository.createTransaction(from.getAccount_number(), to.getAccount_number(), amount,
                from.getCurrent_balance(), to.getCurrent_balance());
        boolean flag1 = accountRepository.updateBalance(from.getAccount_number(), from.getCurrent_balance());

        boolean flag2 = accountRepository.updateBalance(to.getAccount_number(), to.getCurrent_balance());
        if (!flag1 || !flag2)
            throw new BBadRequestException("Unable to update balance");

        return transactionId;
    }

    @Override
    public boolean getTransaction(String start, String stop, int accountNumber) throws BNotFoundException, FileNotFoundException, DocumentException {
        logger.info("Getting transactions from {} to {} for account number {}",start,stop,accountNumber);
        List<Transaction> list = transactionRepository.getTransactions(start, stop, accountNumber);
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("transactions.pdf"));
        document.open();
        PdfPTable table = new PdfPTable(5);
        addTableHeader(table);
        addRows(table, list, accountNumber);
        document.add(table);
        document.close();
        return true;
    }

    @Override
    public Integer addInterest(Account from, Account to) throws BNotFoundException, ParseException {
        long days = getDaysBetween(to.getLast_interest_added());
        long years = days / 365;
        double interest = to.getCurrent_balance() * 0.035 * years;
        double amount = to.getCurrent_balance() + interest;
        if (years >= 1) {
            to.setCurrent_balance(amount);
            from.setCurrent_balance(from.getCurrent_balance() - interest);
            accountRepository.updateInterest(to.getAccount_number(), to.getCurrent_balance());
            accountRepository.updateBalance(from.getAccount_number(), from.getCurrent_balance());
            return transactionRepository.createTransaction(from.getAccount_number(), to.getAccount_number(), amount, from.getCurrent_balance(), to.getCurrent_balance());
        }
        return 0;
    }

    private long getDaysBetween(String date) {
        CharacterIterator it
                = new StringCharacterIterator(date);
        String prevDate = "";

        while (it.current() != CharacterIterator.DONE) {
            if (it.current() == ' ')
                break;
            prevDate += it.current();
            it.next();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date1 = LocalDate.parse(prevDate, dtf);
        String now = dtf.format(LocalDateTime.now());
        LocalDate date2 = LocalDate.parse(now, dtf);
        long daysInBetween = ChronoUnit.DAYS.between(date1, date2);

        return daysInBetween;
    }


    private void addTableHeader(PdfPTable table) {
        Stream.of("Date", "Account", "Amount", "Balance", "Type")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<Transaction> transactions, int accountNumber) {
        for (Transaction transaction : transactions) {
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
