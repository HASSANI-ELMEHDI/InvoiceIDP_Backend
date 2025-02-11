package ensias.tim.phd.invoice_IDP;

import ensias.tim.phd.invoice_IDP.layer1.OcrService;
import ensias.tim.phd.invoice_IDP.layer2.LlmService;
import ensias.tim.phd.invoice_IDP.layer3.ValidationService;
import ensias.tim.phd.invoice_IDP.layer3.InvoiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/invoice")
public class Controller {

    private final LlmService llmService;
    private final OcrService ocrService;
    private final ValidationService validationService;

    @Autowired
    public Controller(LlmService llmService, OcrService ocrService, ValidationService validationService) {
        this.llmService = llmService;
        this.ocrService = ocrService;
        this.validationService = validationService;
    }

    @PostMapping("/extract-data")
    public String extractInvoiceData(@RequestParam("file") MultipartFile file) {
        // Step 1: Perform OCR to extract text from the image
        String ocrText = ocrService.extractText(file);

        String prompt = "Extract structured JSON data from the following invoice text :"
                +ocrText +"\n Return only valid JSON without additional text.";

        // Step 2: Call LLM service to structure the data from OCR text
        String structuredData = llmService.extractStructuredData(prompt);

        // Step 3: Validate the structured data
        InvoiceModel invoiceModel = validationService.validateData(structuredData);

        return structuredData;
    }
}
