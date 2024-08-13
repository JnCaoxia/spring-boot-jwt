package murraco.service;

import org.springframework.util.StopWatch;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibreOfficeConverter {

    private static final String LIBREOFFICE_PATH = "C:\\Program Files\\LibreOffice\\program\\soffice.exe"; // Windows路径示例
    // private static final String LIBREOFFICE_PATH = "/Applications/LibreOffice.app/Contents/MacOS/soffice"; // macOS路径示例
    // private static final String LIBREOFFICE_PATH = "/usr/bin/libreoffice"; // Linux路径示例

    public static void main(String[] args) {
        String inputDir = "E:\\uploads\\/input\\";
        String outputDir = "E:\\uploads\\/output\\";

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        File dir = new File(inputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".docm"));
        System.out.println("core cpu size:" + Runtime.getRuntime().availableProcessors());
        if (files != null) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (File file : files) {
                executorService.submit(() -> convertToPDF(file, outputDir));
            }

            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // 等待所有任务完成
            }
            stopwatch.stop();
            System.out.println("所有文件已成功转换为PDF！" + stopwatch.getTotalTimeSeconds());
        }
    }

    private static void convertToPDF(File inputFile, String outputDir) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    LIBREOFFICE_PATH,
                    "--headless",
                    "--convert-to", "pdf",
                    "--outdir", outputDir,
                    inputFile.getAbsolutePath()
            );
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("成功转换文件: " + inputFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
