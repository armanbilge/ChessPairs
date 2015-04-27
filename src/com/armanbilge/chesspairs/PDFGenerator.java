package com.armanbilge.chesspairs;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

/**
 * @author Arman Bilge
 */
public class PDFGenerator {

    private final Tournament tournament;
    private final Set<Pair> bestPairing;

    public PDFGenerator(final Tournament tournament, final Set<Pair> bestPairing) {
        this.tournament = tournament;
        this.bestPairing = bestPairing;
    }

    public void write(final File file) throws FileNotFoundException, DocumentException {
        final Document pdf = new com.itextpdf.text.Document(PageSize.LETTER);
        PdfWriter.getInstance(pdf, new FileOutputStream(file));
        pdf.open();
        pdf.add(generateBestPairingTable());
        pdf.add(new Paragraph("\n"));
        pdf.add(generatePlayerInfoTable());
        pdf.close();
    }

    private PdfPTable generateBestPairingTable() {

        PdfPTable table = new PdfPTable(2);

        PdfPCell cell = new PdfPCell(new Phrase("WHITE"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("BLACK"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (Pair p : bestPairing) {
            table.addCell(p.getWhite().getName());
            table.addCell(p.getBlack().getName());
        }

        return table;
    }

    private PdfPTable generatePlayerInfoTable() {

        PdfPTable table = new PdfPTable(new float[]{32, 24, 32, 64});

        PdfPCell cell = new PdfPCell(new Phrase("PLAYER"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("SCORE"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("COLORS"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("OPPONENTS"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (Player p : tournament.getPlayers()) {
            table.addCell(p.getName());
            table.addCell(Double.toString(p.getScore()));
            table.addCell(String.join(", ", (Iterable<String>) p.getGames().stream().map(g -> {
                final Color c = g.getNullableColor(p);
                if (c != null)
                    return c.toString().substring(0, 1);
                else
                    return "N";
            })::iterator));
            table.addCell(String.join(", ", (Iterable<String>) p.getGames().stream().map(g -> g.getOpponent(p).toString())::iterator));
        }

        return table;
    }

}
