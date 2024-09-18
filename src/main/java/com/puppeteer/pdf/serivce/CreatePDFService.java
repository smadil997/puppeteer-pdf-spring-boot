package com.puppeteer.pdf.serivce;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class CreatePDFService {


    @Autowired
    private TemplateEngine templateEngine;
    @Value("${js-file}")
    private Resource pdfGenerator;


    public File createPDF(String name) throws IOException {
        Map<String,String> pdfData=new HashMap<>();
        pdfData.put("name",name); // Add param to pass the value on HTML page

        //Pass template name and send dynamic data that you want to set on HTML page
        File htmlFile=generatePDFFileByTempName("pdf-report.html",pdfData);
        File pdfFile;
        try {
            pdfFile= generatePDFByPuppeteerJS(htmlFile);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return pdfFile;
    }


    private File generatePDFByPuppeteerJS(File htmlFile) throws IOException, InterruptedException {
        String fileNamePdf = UUID.randomUUID().toString();
        final File pdfFile = File.createTempFile("pdf-"+fileNamePdf, ".pdf");
        String osName= System.getProperty("os.name").toLowerCase();
        final List<String> cmd = new ArrayList<String>();
        // As I am working on window OS so implementation done by window only
        if (osName.equalsIgnoreCase("Windows 10")) {
            String jsFilePath=pdfGenerator.getFile().getAbsolutePath();
            cmd.add("cmd");
            cmd.add("/C");
            cmd.add("node");
            cmd.add(jsFilePath);
            cmd.add(htmlFile.getAbsolutePath());
            cmd.add(pdfFile.getAbsolutePath());
            cmd.add(osName);
            cmd.add("start");
        }


        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();


        if (process.waitFor(60, TimeUnit.SECONDS)) {
            return pdfFile;
        } else {
            process.destroyForcibly();
            throw new RuntimeException("We are unable to convert the pdf");
        }
    }



    public File generatePDFFileByTempName(String thymeleafFileName, Map otherPorperty){
        Context fileContex= setContextValue(otherPorperty);
        String htmlStringTemp=templateEngine.process(thymeleafFileName,fileContex);
        String tempHtmlFileForPDF = UUID.randomUUID().toString();
        final File htmlFile;
        try {
            htmlFile = File.createTempFile(tempHtmlFileForPDF, ".html");

            try (FileOutputStream fileOutputStream = new FileOutputStream(htmlFile)) {
                fileOutputStream.write(htmlStringTemp.getBytes(Charset.forName("UTF-8")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return htmlFile;
    }


    private Context setContextValue(Map otherPorperty){
        Context context = new Context();
        otherPorperty.forEach((k, v) -> {
            context.setVariable(k.toString(),v);
        });
        return context;
    }

}
