package com.example.examplefeature.pdf;

import com.example.examplefeature.Task;
import com.example.examplefeature.TaskRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final TaskRepository taskRepository;

    public PdfService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /** Gera um PDF com todas as tarefas (descrição + data limite). */
    public byte[] generateTasksPdf() {
        // Buscar todas as tarefas (o repo tem findAllBy(Pageable) -> Slice)
        List<Task> tasks = taskRepository.findAllBy(Pageable.unpaged()).toList();
        return render(tasks);
    }

    private byte[] render(List<Task> tasks) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Título
            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph p = new Paragraph("Lista de Tarefas", title);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            doc.add(Chunk.NEWLINE);

            // Tabela
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12, 58, 30});
            Font header = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            table.addCell(new Phrase("ID", header));
            table.addCell(new Phrase("Descrição", header));
            table.addCell(new Phrase("Data Limite", header));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (tasks.isEmpty()) {
                table.addCell("-");
                table.addCell("Sem tarefas");
                table.addCell("-");
            } else {
                for (Task t : tasks) {
                    // Ajusta getters conforme a tua entidade
                    String id = safe(() -> String.valueOf(t.getId()));
                    String desc = safe(t::getDescription);
                    String due = safe(() -> t.getDueDate() == null ? "" : fmt.format(t.getDueDate()));

                    table.addCell(id);
                    table.addCell(desc);
                    table.addCell(due);
                }
            }

            doc.add(table);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            // Nunca rebenta a UI: devolve PDF com mensagem de erro
            return renderError(e.getMessage());
        }
    }

    private byte[] renderError(String message) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            doc.add(new Paragraph("Erro ao gerar PDF", f));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph(message != null ? message : "(sem detalhes)"));
            doc.close();
            return baos.toByteArray();
        } catch (Exception ignored) {
            return new byte[0];
        }
    }

    // Helper para não falhar em nulls
    private static String safe(SupplierEx<String> s) {
        try { String v = s.get(); return v == null ? "" : v; } catch (Exception e) { return ""; }
    }
    @FunctionalInterface private interface SupplierEx<T> { T get() throws Exception; }
}


