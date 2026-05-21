package com.ecoterminal.service;

import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.model.LeituraCO2;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioPdfService {

    // ── Paleta de cores ───
    private static final DeviceRgb COR_VERDE        = new DeviceRgb(34,  197,  94);
    private static final DeviceRgb COR_VERDE_ESCURO = new DeviceRgb(22,  163,  74);
    private static final DeviceRgb COR_FUNDO        = new DeviceRgb(15,  22,   18);
    private static final DeviceRgb COR_FUNDO_LINHA  = new DeviceRgb(20,  28,   24);
    private static final DeviceRgb COR_BORDA        = new DeviceRgb(30,  48,   39);
    private static final DeviceRgb COR_DANGER       = new DeviceRgb(239, 68,   68);
    private static final DeviceRgb COR_WARN         = new DeviceRgb(245, 158,  11);
    private static final DeviceRgb COR_TEXTO        = new DeviceRgb(232, 245, 238);
    private static final DeviceRgb COR_TEXTO_DIM    = new DeviceRgb(122, 171, 143);

    private static final DateTimeFormatter FMT      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Limite de NOx (ppm) para considerar acima do normal
    private static final double LIMITE_NOX = 5.0;


    public byte[] gerarPdf(List<LeituraCO2> leituras,
                           List<AlertaEmissao> alertas,
                           int dias) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter   writer   = new PdfWriter(baos);
        PdfDocument pdfDoc   = new PdfDocument(writer);
        Document    document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 36, 36, 36);

        PdfFont fontMono = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontNorm = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        adicionarCabecalho(document, fontMono, fontBold, fontNorm, dias);
        adicionarKpis(document, fontBold, fontNorm, leituras, alertas);
        adicionarTabelaLeituras(document, fontBold, fontNorm, fontMono, leituras);
        adicionarTabelaAlertas(document, fontBold, fontNorm, fontMono, alertas);
        adicionarRodape(document, fontNorm, dias);

        document.close();
        return baos.toByteArray();
    }

    // ── Cabeçalho ───

    private void adicionarCabecalho(Document doc, PdfFont mono, PdfFont bold,
                                    PdfFont norm, int dias) {
        Table header = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(COR_VERDE_ESCURO)
                .setMarginBottom(0);

        Cell titulo = new Cell()
                .add(new Paragraph("EcoTerminal — Relatorio de Auditoria")
                        .setFont(mono).setFontSize(14).setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.LEFT))
                .add(new Paragraph("Sistema de Monitoramento de Emissoes de Onibus — Lei Municipal 16.802/2018")
                        .setFont(norm).setFontSize(9).setFontColor(new DeviceRgb(180, 240, 200))
                        .setTextAlignment(TextAlignment.LEFT))
                .setBorder(Border.NO_BORDER)
                .setPadding(14);
        header.addCell(titulo);
        doc.add(header);

        Table meta = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(COR_FUNDO)
                .setBorder(new SolidBorder(COR_BORDA, 1))
                .setMarginBottom(14);

        String periodoTexto = "Ultimos " + dias + " dias — ate " +
                LocalDateTime.now().format(FMT_DATA);

        meta.addCell(celulaMetaDados("PERIODO DO RELATORIO", periodoTexto, mono, norm));
        meta.addCell(celulaMetaDados("GERADO EM", LocalDateTime.now().format(FMT), mono, norm));
        doc.add(meta);
    }

    private Cell celulaMetaDados(String label, String valor, PdfFont mono, PdfFont norm) {
        return new Cell()
                .add(new Paragraph(label)
                        .setFont(mono).setFontSize(7).setFontColor(COR_TEXTO_DIM).setMarginBottom(2))
                .add(new Paragraph(valor)
                        .setFont(norm).setFontSize(9).setFontColor(COR_TEXTO))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(COR_FUNDO)
                .setPadding(10);
    }

    // ── KPIs ────

    private void adicionarKpis(Document doc, PdfFont bold, PdfFont norm,
                                List<LeituraCO2> leituras,
                                List<AlertaEmissao> alertas) {

        long totalLeituras = leituras.size();
        long totalAlertas  = alertas.size();

        double noxMedio = leituras.stream()
                .filter(l -> l.getNoxPpm() != null)
                .mapToDouble(LeituraCO2::getNoxPpm)
                .average().orElse(0.0);

        long leiturasAlerta = leituras.stream()
                .filter(l -> l.getNoxPpm() != null && l.getNoxPpm() > LIMITE_NOX)
                .count();

        double kmTotal = leituras.stream()
                .mapToDouble(LeituraCO2::getKmPercorridos)
                .sum();

        Table kpiGrid = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(18);

        kpiGrid.addCell(cartaoKpi("LEITURAS", String.valueOf(totalLeituras),
                "registros no periodo", bold, norm, COR_FUNDO));
        kpiGrid.addCell(cartaoKpi("ALERTAS ATIVOS", String.valueOf(totalAlertas),
                "no periodo", bold, norm,
                totalAlertas > 0 ? new DeviceRgb(40, 10, 10) : COR_FUNDO));
        kpiGrid.addCell(cartaoKpi("NOx MEDIO", String.format("%.2f", noxMedio),
                "ppm", bold, norm, COR_FUNDO));
        kpiGrid.addCell(cartaoKpi("ACIMA DO LIMITE", String.valueOf(leiturasAlerta),
                "leituras NOx > " + LIMITE_NOX + " ppm", bold, norm,
                leiturasAlerta > 0 ? new DeviceRgb(40, 18, 0) : COR_FUNDO));
        kpiGrid.addCell(cartaoKpi("KM TOTAL", String.format("%.0f", kmTotal),
                "km percorridos", bold, norm, COR_FUNDO));

        doc.add(kpiGrid);
    }

    private Cell cartaoKpi(String label, String valor, String sub,
                           PdfFont bold, PdfFont norm, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(label)
                        .setFont(norm).setFontSize(7).setFontColor(COR_TEXTO_DIM).setMarginBottom(4))
                .add(new Paragraph(valor)
                        .setFont(bold).setFontSize(20).setFontColor(COR_VERDE).setMarginBottom(2))
                .add(new Paragraph(sub)
                        .setFont(norm).setFontSize(8).setFontColor(COR_TEXTO_DIM))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COR_BORDA, 1))
                .setPadding(12)
                .setMargin(3);
    }

    // ── Tabela de leituras ────

    private void adicionarTabelaLeituras(Document doc, PdfFont bold, PdfFont norm,
                                          PdfFont mono, List<LeituraCO2> leituras) {
        doc.add(tituloSecao("Historico de Leituras de Emissoes", bold));

        if (leituras.isEmpty()) {
            doc.add(new Paragraph("Nenhuma leitura registrada no periodo.")
                    .setFont(norm).setFontSize(9).setFontColor(COR_TEXTO_DIM).setMarginBottom(16));
            return;
        }

        // Colunas: Data/Hora | Prefixo | Terminal | KM | NOx (ppm) | MP (mg/m3) | Status
        Table tabela = new Table(UnitValue.createPercentArray(new float[]{2.2f, 1.2f, 1.6f, 1.0f, 1.1f, 1.1f, 1.0f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(18);

        String[] headers = {"Data / Hora", "Prefixo", "Terminal", "KM", "NOx (ppm)", "MP (mg/m3)", "Status"};
        for (String h : headers) {
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph(h)
                                    .setFont(bold).setFontSize(7.5f).setFontColor(COR_VERDE))
                            .setBackgroundColor(COR_FUNDO)
                            .setBorderBottom(new SolidBorder(COR_VERDE_ESCURO, 1))
                            .setBorderTop(Border.NO_BORDER)
                            .setBorderLeft(Border.NO_BORDER)
                            .setBorderRight(Border.NO_BORDER)
                            .setPaddingTop(8).setPaddingBottom(8).setPaddingLeft(6));
        }

        boolean linhaAlternada = false;
        for (LeituraCO2 l : leituras) {
            DeviceRgb bg = linhaAlternada ? COR_FUNDO_LINHA : COR_FUNDO;
            linhaAlternada = !linhaAlternada;

            // Alerta baseado em NOx (campo real disponível no modelo)
            boolean acima   = l.getNoxPpm() != null && l.getNoxPpm() > LIMITE_NOX;
            DeviceRgb corNox = acima ? COR_DANGER : COR_VERDE;
            String status    = acima ? "Acima" : "Normal";
            DeviceRgb corSt  = acima ? COR_DANGER : COR_VERDE;

            // Prefixo do ônibus (campo real: prefixo, não placa)
            String prefixo = (l.getOnibus() != null) ? l.getOnibus().getPrefixo() : "—";

            // Nome do terminal
            String terminal = (l.getOnibus() != null && l.getOnibus().getTerminal() != null)
                    ? l.getOnibus().getTerminal().getNome() : "—";

            tabela.addCell(celulaTabela(
                    l.getDataHora() != null ? l.getDataHora().format(FMT) : "—",
                    mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(prefixo, mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(terminal, norm, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(
                    String.format("%.1f", l.getKmPercorridos()),
                    mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(
                    l.getNoxPpm() != null ? String.format("%.2f", l.getNoxPpm()) : "—",
                    mono, 7.5f, corNox, bg));
            tabela.addCell(celulaTabela(
                    l.getMpMgM3() != null ? String.format("%.3f", l.getMpMgM3()) : "—",
                    mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(status, bold, 7.5f, corSt, bg));
        }

        doc.add(tabela);
    }

    // ── Tabela de alertas ───

    private void adicionarTabelaAlertas(Document doc, PdfFont bold, PdfFont norm,
                                         PdfFont mono, List<AlertaEmissao> alertas) {
        doc.add(tituloSecao("Alertas do Periodo", bold));

        if (alertas.isEmpty()) {
            doc.add(new Paragraph("Nenhum alerta registrado no periodo.")
                    .setFont(norm).setFontSize(9).setFontColor(COR_VERDE).setMarginBottom(16));
            return;
        }

        Table tabela = new Table(UnitValue.createPercentArray(new float[]{2.2f, 1.4f, 1.2f, 1.2f, 2.0f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(18);

        String[] headers = {"Data / Hora", "Prefixo", "Severidade", "Valor Medido", "Mensagem"};
        for (String h : headers) {
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph(h)
                                    .setFont(bold).setFontSize(7.5f).setFontColor(COR_DANGER))
                            .setBackgroundColor(COR_FUNDO)
                            .setBorderBottom(new SolidBorder(COR_DANGER, 1))
                            .setBorderTop(Border.NO_BORDER)
                            .setBorderLeft(Border.NO_BORDER)
                            .setBorderRight(Border.NO_BORDER)
                            .setPaddingTop(8).setPaddingBottom(8).setPaddingLeft(6));
        }

        boolean linhaAlternada = false;
        for (AlertaEmissao a : alertas) {
            DeviceRgb bg = linhaAlternada ? COR_FUNDO_LINHA : COR_FUNDO;
            linhaAlternada = !linhaAlternada;

            DeviceRgb corSev = corDeSeveridade(
                    a.getSeveridade() != null ? a.getSeveridade().name() : "LOW");

            // Prefixo do ônibus associado ao alerta (campo real: prefixo)
            String prefixo = (a.getOnibus() != null) ? a.getOnibus().getPrefixo() : "—";

            tabela.addCell(celulaTabela(
                    a.getDataHora() != null ? a.getDataHora().format(FMT) : "—",
                    mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(prefixo, mono, 7.5f, COR_TEXTO, bg));
            tabela.addCell(celulaTabela(
                    a.getSeveridade() != null ? a.getSeveridade().name() : "—",
                    bold, 7.5f, corSev, bg));
            tabela.addCell(celulaTabela(
                    a.getValorMedido() != null
                            ? String.format("%.2f", a.getValorMedido()) : "—",
                    mono, 7.5f, COR_DANGER, bg));
            tabela.addCell(celulaTabela(
                    a.getMensagem() != null ? a.getMensagem() : "—",
                    norm, 7.0f, COR_TEXTO_DIM, bg));
        }

        doc.add(tabela);
    }

    // ── Rodapé ────

    private void adicionarRodape(Document doc, PdfFont norm, int dias) {
        doc.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine())
                .setStrokeColor(COR_BORDA).setMarginTop(8).setMarginBottom(8));

        doc.add(new Paragraph(
                "EcoTerminal — Sistema de Monitoramento de Emissoes  •  " +
                "Periodo: ultimos " + dias + " dias  •  " +
                "Gerado em: " + LocalDateTime.now().format(FMT) +
                "  •  Lei Municipal 16.802/2018")
                .setFont(norm).setFontSize(7.5f)
                .setFontColor(COR_TEXTO_DIM)
                .setTextAlignment(TextAlignment.CENTER));
    }

    // ── Utilitários ────

    private Paragraph tituloSecao(String texto, PdfFont bold) {
        return new Paragraph(texto)
                .setFont(bold).setFontSize(11)
                .setFontColor(COR_TEXTO)
                .setBorderBottom(new SolidBorder(COR_BORDA, 1))
                .setMarginTop(6).setMarginBottom(8)
                .setPaddingBottom(6);
    }

    private Cell celulaTabela(String texto, PdfFont font, float size,
                               DeviceRgb cor, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(size).setFontColor(cor))
                .setBackgroundColor(bg)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(COR_BORDA, 0.5f))
                .setPaddingTop(6).setPaddingBottom(6).setPaddingLeft(6);
    }

    private DeviceRgb corDeSeveridade(String sev) {
        return switch (sev) {
            case "CRITICAL" -> new DeviceRgb(220, 38, 38);
            case "HIGH"     -> COR_DANGER;
            case "MEDIUM"   -> COR_WARN;
            default         -> COR_VERDE;
        };
    }
}