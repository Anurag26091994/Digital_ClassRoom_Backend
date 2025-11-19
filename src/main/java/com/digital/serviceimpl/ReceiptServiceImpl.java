package com.digital.serviceimpl;

import com.digital.entity.Invoice;
import com.digital.entity.Payment;
import com.digital.repository.InvoiceRepository;
import com.digital.repository.PaymentRepository;
import com.digital.servicei.ReceiptService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public ReceiptServiceImpl(InvoiceRepository invoiceRepository,
                              PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public byte[] generateReceipt(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        Payment payment = invoice.getPayments() == null || invoice.getPayments().isEmpty()
                ? null
                : invoice.getPayments().stream()
                .findFirst()
                .orElse(null);

        DecimalFormat amtFmt = new DecimalFormat("#,##0");

        String dateStr = "-";
        if (invoice.getGeneratedAt() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    .withZone(ZoneId.systemDefault());
            dateStr = df.format(invoice.getGeneratedAt());
        }

        String studentName = Optional.ofNullable(invoice.getStudent())
                .map(s -> (s.getFirstName() + " " + s.getLastName()).trim())
                .orElse("Student");

        String className = Optional.ofNullable(invoice.getStudent())
                .map(s -> s.getSchoolClass() != null ? s.getSchoolClass().getClassName() : "-")
                .orElse("-");

        String amountPaidStr = "₹" + amtFmt.format(payment != null ? payment.getAmountPaid() : invoice.getAmountPaid());
        String mode = payment != null && payment.getPaymentMode() != null ? payment.getPaymentMode().name() : "-";
        String txnId = payment != null ? Optional.ofNullable(payment.getTransactionId()).orElse("-") : "-";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);


        Paragraph header = new Paragraph("Payment Receipt")
                .setFont(bold)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(header);

        Table info = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth()
                .setMarginLeft(60)
                .setMarginRight(60)
                .setBackgroundColor(ColorConstants.WHITE);

        addRow(info, "Invoice:", "#INV-" + String.format("%06d", invoice.getInvoiceId()), bold, regular);
        addRow(info, "Student Name:", studentName, bold, regular);
        addRow(info, "Class:", className, bold, regular);
        addRow(info, "Date:", dateStr, bold, regular);
        addRow(info, "Amount Paid:", amountPaidStr, bold, regular);
        addRow(info, "Payment Mode:", mode, bold, regular);
        addRow(info, "Transaction ID:", txnId, bold, regular);

        document.add(info);

        document.add(new Paragraph("\nThank you for your payment!")
                .setFont(bold)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));

        document.close();
        return baos.toByteArray();
    }

    private void addRow(Table table, String label, String value, PdfFont bold, PdfFont regular) {
        Cell c1 = new Cell().add(new Paragraph(label).setFont(bold).setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5);
        Cell c2 = new Cell().add(new Paragraph(value).setFont(regular).setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5);
        table.addCell(c1);
        table.addCell(c2);
    }
}
