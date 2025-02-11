package ensias.tim.phd.invoice_IDP.layer1;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("C:\\Users\\061672\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata"); // Chemin vers les fichiers .traineddata
        this.tesseract.setLanguage("eng"); // Langue utilisée pour l'OCR
    }

    public String extractText(MultipartFile file) {
        try {
            // Vérifie si c'est un PDF
            if (file.getContentType() != null && file.getContentType().equalsIgnoreCase("application/pdf")) {
                return extractTextFromPdf(file);
            }
            // Vérifie si c'est une image (formats pris en charge)
            else if (isImage(file)) {
                return extractTextFromImage(file);
            } else {
                return "Format de fichier non pris en charge. Veuillez télécharger une image (png, jpg, jpeg, bmp, gif, tiff) ou un PDF.";
            }
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return "Erreur lors de l'extraction du texte : " + e.getMessage();
        }
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.startsWith("image/png") || contentType.startsWith("image/jpeg") || contentType.startsWith("image/jpg") || contentType.startsWith("image/bmp") || contentType.startsWith("image/gif") || contentType.startsWith("image/tiff");
    }

    private String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        return tesseract.doOCR(bufferedImage);
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException, TesseractException {
        PDDocument document = Loader.loadPDF(convertMultipartFileToFile(file));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder extractedText = new StringBuilder();

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            extractedText.append(tesseract.doOCR(bufferedImage)).append("\n");
        }

        document.close();
        return extractedText.toString();
    }
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".tmp"; // Par défaut, ajoute ".tmp" si aucune extension n'est trouvée

        File tempFile = File.createTempFile("uploaded_", suffix);
        file.transferTo(tempFile);
        return tempFile;
    }


}
