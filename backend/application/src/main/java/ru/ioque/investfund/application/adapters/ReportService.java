package ru.ioque.investfund.application.adapters;

import java.io.File;
import java.io.IOException;

public interface ReportService {
    File buildDailyReport() throws IOException;
    File buildHourlyReport() throws IOException;
}
