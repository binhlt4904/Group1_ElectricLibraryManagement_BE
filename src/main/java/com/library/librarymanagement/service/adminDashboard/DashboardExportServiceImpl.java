package com.library.librarymanagement.service.adminDashboard;

import com.library.librarymanagement.dto.response.admin_dashboard.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardExportServiceImpl implements DashboardExportService {
    private final AdminStatisticsService stats;

    @Override
    public byte[] exportDashboardExcel(int year) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            // ===== Styles =====
            CellStyle header = headerStyle(wb);
            CellStyle money  = moneyStyle(wb);
            CellStyle number = numberStyle(wb);
            CellStyle text   = textStyle(wb);
            CellStyle dt     = dateTimeStyle(wb);

            // ===== Sheet 1: Overview =====
            Sheet overview = wb.createSheet("Overview");
            int r = 0;
            Row h = overview.createRow(r++);
            set(h, 0, "Metric", header);
            set(h, 1, "Value",  header);

            TotalRevenueResponse totalRevenue     = stats.getTotalRevenue();
            TotalBookResponse totalBooks          = stats.getTotalBooks();
            TotalActiveReaderResponse activeUsers = stats.getActiveReaders();
            CurrentBorrowalsResponse currentBor   = stats.getCurrentBorrowals();
            OverdueItemsResponse overdueItems     = stats.getOverdueItems();

            r = writeRow(overview, r, "Total Revenue",      totalRevenue.getTotalRevenue(), money);
            r = writeRow(overview, r, "Total Books",        totalBooks.getTotalBooks(),     number);
            r = writeRow(overview, r, "Active Readers",     activeUsers.getActiveReaders(), number);
            r = writeRow(overview, r, "Current Borrowals",  currentBor.getCurrentBorrowals(), number);
            r = writeRow(overview, r, "Overdue Items",      overdueItems.getOverdueItems(), number);

            // === Borrowing Trends (Current Year) ===
            List<BorrowingTrendResponse> trendData = stats.getBorrowingTrendsCurrentYear();

            int[] monthly = new int[12];
            if (trendData != null) {
                for (BorrowingTrendResponse t : trendData) {
                    int m = t.getMonth();
                    long c = t.getBorrowCount();
                    if (m >= 1 && m <= 12) monthly[m - 1] += c;
                }
            }

            int max = 0;
            int min = Integer.MAX_VALUE;
            for (int v : monthly) {
                if (v > max) max = v;
                if (v < min) min = v;
            }
            List<Integer> maxMonths = new ArrayList<>();
            List<Integer> minMonths = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                if (monthly[i] == max) maxMonths.add(i + 1);
                if (monthly[i] == min) minMonths.add(i + 1);
            }

            String peakText   = monthlySummary(maxMonths, monthly);
            String lowestText = monthlySummary(minMonths, monthly);

            set(overview.createRow(r), 0, "Peak Borrowing Month(s)", text);     set(overview.getRow(r++), 1, peakText, text);
            set(overview.createRow(r), 0, "Lowest Borrowing Month(s)", text);   set(overview.getRow(r++), 1, lowestText, text);

            autosize(overview, 2);

            // ===== Sheet 2: PopularBooks =====
            Sheet popular = wb.createSheet("PopularBooks");
            Row hp = popular.createRow(0);
            set(hp, 0, "#",           header);
            set(hp, 1, "BookId",      header);
            set(hp, 2, "BookName",    header);
            set(hp, 3, "Author",      header);
            set(hp, 4, "BorrowCount", header);

            List<PopularBookResponse> popularBooks = stats.getPopularBooks();
            for (int i = 0; i < popularBooks.size(); i++) {
                PopularBookResponse pb = popularBooks.get(i);
                Row row = popular.createRow(i + 1);
                set(row, 0, i + 1,                number);
                set(row, 1, pb.getBookId(),       number);
                set(row, 2, safe(pb.getBookName()), text);
                set(row, 3, safe(pb.getAuthorName()), text);
                set(row, 4, pb.getBorrowCount(),  number);
            }
            autosize(popular, 5);

            // ===== Sheet 3: RecentActivities =====
            Sheet acts = wb.createSheet("RecentActivities");
            Row ha = acts.createRow(0);
            set(ha, 0, "Id",          header);
            set(ha, 1, "Title",       header);
            set(ha, 2, "Description", header);
            set(ha, 3, "FromUser",    header);
            set(ha, 4, "CreatedDate", header);

            List<RecentActivityResponse> recent = stats.getRecentActivities();
            for (int i = 0; i < recent.size(); i++) {
                RecentActivityResponse a = recent.get(i);
                Row row = acts.createRow(i + 1);
                set(row, 0, a.getId(),              number);
                set(row, 1, safe(a.getTitle()),     text);
                set(row, 2, safe(a.getDescription()), text);
                set(row, 3, safe(a.getFromUser()),  text);
                setDate(row, 4, a.getCreatedDate(), dt);
            }
            autosize(acts, 5);

            // ===== Sheet 4: BorrowingTrends_<year> =====
            Sheet trends = wb.createSheet("BorrowingTrends_" + year);
            Row ht = trends.createRow(0);
            set(ht, 0, "Month",       header);
            set(ht, 1, "BorrowCount", header);

            for (int i = 0; i < 12; i++) {
                Row row = trends.createRow(i + 1);
                set(row, 0, i + 1,     number);
                set(row, 1, monthly[i], number);
            }
            autosize(trends, 2);

            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Export dashboard excel failed", e);
        }
    }

    // ========= Helpers =========

    private static String safe(String s) { return s == null ? "" : s; }

    private static int writeRow(Sheet s, int r, String label, Number value, CellStyle style) {
        Row row = s.createRow(r);
        set(row, 0, label, textLeft(s.getWorkbook()));
        set(row, 1, value, style);
        return r + 1;
    }

    private static void set(Row row, int col, String val, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(val == null ? "" : val);
        if (style != null) c.setCellStyle(style);
    }

    private static void set(Row row, int col, Number val, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(val == null ? 0 : val.doubleValue());
        if (style != null) c.setCellStyle(style);
    }

    private static void setDate(Row row, int col, Date val, CellStyle style) {
        Cell c = row.createCell(col);
        if (val != null) c.setCellValue(val);
        if (style != null) c.setCellStyle(style);
    }

    private static void autosize(Sheet s, int cols) {
        for (int i = 0; i < cols; i++) s.autoSizeColumn(i);
    }

    // ---------- Styles ----------
    private static void border(CellStyle cs) {
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
    }
    private static void alignLeft(CellStyle cs) {
        cs.setAlignment(HorizontalAlignment.LEFT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
    }
    private static void alignRight(CellStyle cs) {
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
    }
    private static void alignCenter(CellStyle cs) {
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private static CellStyle headerStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle cs = wb.createCellStyle();
        cs.setFont(font);
        cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        border(cs);
        alignCenter(cs);
        return cs;
    }

    private static CellStyle moneyStyle(Workbook wb) {
        CellStyle cs = numberStyle(wb);
        DataFormat df = wb.createDataFormat();
        cs.setDataFormat(df.getFormat("#,##0\" đ\""));
        return cs;
    }

    private static CellStyle numberStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        border(cs);
        DataFormat df = wb.createDataFormat();
        cs.setDataFormat(df.getFormat("#,##0"));
        alignRight(cs);
        return cs;
    }

    private static CellStyle textStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        cs.setWrapText(true);
        border(cs);
        alignLeft(cs);
        return cs;
    }

    private static CellStyle textLeft(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        border(cs);
        alignLeft(cs);
        return cs;
    }

    // Excel pattern: yyyy-mm-dd hh:mm  (mm = minutes trong Excel)
    private static CellStyle dateTimeStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        border(cs);
        DataFormat df = wb.createDataFormat();
        cs.setDataFormat(df.getFormat("yyyy-mm-dd hh:mm"));
        alignLeft(cs);
        return cs;
    }

    // ---------- Month helpers ----------
    private static final List<String> MONTH_NAMES = Arrays.asList(
            "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    );

    private static String monthName(int m) {
        if (m < 1 || m > 12) return "N/A";
        return MONTH_NAMES.get(m - 1);
    }

    private static String monthlySummary(List<Integer> months, int[] monthlyCounts) {
        if (months == null || months.isEmpty()) return "N/A";
        List<String> parts = new ArrayList<>();
        for (Integer m : months) {
            int cnt = (m >= 1 && m <= 12) ? monthlyCounts[m - 1] : 0;
            parts.add(monthName(m) + " (" + cnt + ")");
        }
        return String.join(", ", parts);
    }
}
