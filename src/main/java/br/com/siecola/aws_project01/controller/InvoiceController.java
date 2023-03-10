package br.com.siecola.aws_project01.controller;

import br.com.siecola.aws_project01.models.Invoice;
import br.com.siecola.aws_project01.models.UrlResponse;
import br.com.siecola.aws_project01.repository.InvoiceRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/invoices")
public class InvoiceController {
    @Value("${aws.s3.bucket.invoice.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public InvoiceController() {
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createInvoiceUrl() {
        UrlResponse urlResponse = new UrlResponse();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest presignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processId)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expirationTime));

        urlResponse.setUrl(amazonS3.generatePresignedUrl(presignedUrlRequest).toString());
        urlResponse.setExpirationTime(expirationTime.getEpochSecond());


        return new ResponseEntity<>(urlResponse, HttpStatus.OK);
    }

    @GetMapping
    public Iterable<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @GetMapping(value = "/bycustomername")
    public Iterable<Invoice> findByCustomerName(@RequestParam String customerName) {
        return invoiceRepository.findAllBycustomerName(customerName);
    }
}
