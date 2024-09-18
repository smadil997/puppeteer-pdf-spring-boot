const puppeteer = require('puppeteer')

async function generatePDFFile() {
//    Catch all input parameter received from API
     const cmdArguments = process.argv.slice(2);
     const htmlContent = cmdArguments[0];
     const pdfPath = cmdArguments[1];
//    Here we need OS name for change default browser Executable path for print the PDF
     let browser = await puppeteer.launch({devtools: true,executablePath: 'C://Program Files//Google//Chrome//Application//chrome.exe'}); // Change path according to your Chrome installation location
     const page =await browser.newPage();
     await page.goto(htmlContent, { waitUntil: 'networkidle0', });
     const pdfFile = await page.pdf({ path: pdfPath, format: 'A4',preferCSSPageSize: true, });
     await browser.close();
     return pdfFile;
}
return generatePDFFile();