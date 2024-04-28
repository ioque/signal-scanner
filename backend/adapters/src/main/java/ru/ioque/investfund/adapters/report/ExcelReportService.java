package ru.ioque.investfund.adapters.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.TradingStateEmbeddable;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaEmulatedPositionRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerRepository;
import ru.ioque.investfund.application.adapters.ReportService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExcelReportService implements ReportService {
    JpaEmulatedPositionRepository emulatedPositionRepository;
    JpaInstrumentRepository instrumentRepository;
    JpaScannerRepository jpaScannerRepository;
    DecimalFormat formatter = new DecimalFormat("#,###.##");

    @Override
    public File buildDailyReport() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Report");
        Row header = sheet.createRow(0);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        headerStyle.setFont(buildFont(workbook));
        buildCell(header, 0, "Тикер", headerStyle);
        buildCell(header, 1, "Алгоритм", headerStyle);
        buildCell(header, 2, "Цена входа", headerStyle);
        buildCell(header, 3, "Цена закрытия дня", headerStyle);
        buildCell(header, 4, "Изменение цены", headerStyle);
        buildCell(header, 5, "Показатели", headerStyle);
        CellStyle style = getRowStyle(workbook);
        int rowIndex = 1;
        for (EmulatedPositionEntity emulatedPositionEntity : emulatedPositionRepository.findAll()) {
            if (emulatedPositionEntity.getIsOpen()) {
                Row row = sheet.createRow(rowIndex);
                InstrumentEntity instrument = emulatedPositionEntity.getInstrument();
                ScannerEntity scanner = emulatedPositionEntity.getScanner();
                SignalEntity signal = scanner
                    .getSignals()
                    .stream()
                    .filter(value -> value.getId().getInstrumentId().equals(instrument.getId()))
                    .findFirst().orElseThrow();
                double closePrice = instrument.getTradingState().map(TradingStateEmbeddable::getTodayLastPrice).orElse(emulatedPositionEntity.getLastPrice());
                double profit = (closePrice/emulatedPositionEntity.getOpenPrice() - 1) * 100;
                buildCell(row, 0, instrument.getTicker(), style);
                buildCell(row, 1, scanner.getAlgorithmProperties().getType().getName(), style);
                buildCell(row, 2, formatter.format(emulatedPositionEntity.getOpenPrice()), style);
                buildCell(row, 3, formatter.format(closePrice), style);
                buildCell(row, 4, formatter.format(profit), style);
                buildCell(row, 5, signal.getSummary(), style);
                rowIndex++;
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        return new File(fileLocation);
    }

    @Override
    public File buildHourlyReport() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Report");
        Row header = sheet.createRow(0);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        headerStyle.setFont(buildFont(workbook));
        buildCell(header, 0, "Тикер", headerStyle);
        buildCell(header, 1, "Алгоритм", headerStyle);
        buildCell(header, 2, "Цена входа", headerStyle);
        buildCell(header, 3, "Текущая цена", headerStyle);
        buildCell(header, 4, "Изменение цены", headerStyle);
        CellStyle style = getRowStyle(workbook);
        int rowIndex = 1;
        for (EmulatedPositionEntity emulatedPositionEntity : emulatedPositionRepository.findAll()) {
            if (emulatedPositionEntity.getIsOpen()) {
                Row row = sheet.createRow(rowIndex);
                InstrumentEntity instrument = emulatedPositionEntity.getInstrument();
                ScannerEntity scanner = emulatedPositionEntity.getScanner();
                buildCell(row, 0, instrument.getTicker(), style);
                buildCell(row, 1, scanner.getAlgorithmProperties().getType().getName(), style);
                buildCell(row, 2, formatter.format(emulatedPositionEntity.getOpenPrice()), style);
                buildCell(row, 3, formatter.format(emulatedPositionEntity.getLastPrice()), style);
                buildCell(row, 4, formatter.format(emulatedPositionEntity.getProfit()), style);
                rowIndex++;
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        return new File(fileLocation);
    }

    private CellStyle getRowStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        return style;
    }

    private void buildCell(Row row, int i, String value, CellStyle style) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private CellStyle buildHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    private XSSFFont buildFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        return font;
    }
}
