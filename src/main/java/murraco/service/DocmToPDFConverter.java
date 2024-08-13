package murraco.service;

import org.springframework.util.StopWatch;

import java.io.File;

public class DocmToPDFConverter {
    public static void main(String[] args) {
        String inputFilePath = "E:\\uploads\\cs01.docm";
        String outputFilePath = "E:\\uploads\\output.pdf";

        try {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String libreOfficePath = "C:\\Program Files\\LibreOffice\\program\\soffice.exe"; // Windows路径示例

            ProcessBuilder processBuilder = new ProcessBuilder(
                    libreOfficePath,
                    "--headless",
                    "--infilter='writer_pdf_export:PaperSize=A4'",
                    "--convert-to", "pdf",
                    "--outdir", new File(outputFilePath).getParent(),
                    inputFilePath
            );
            Process process = processBuilder.start();
            process.waitFor();
            stopWatch.stop();
            System.out.println("DOCM 文档成功转换为 PDF 文件！秒数：" +stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
