import {Injectable} from '@angular/core';
import {jsPDF} from 'jspdf';
import html2canvas from 'html2canvas';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class PdfGenerationService {

    constructor() {
    }


    public generatePdf(
        elementId: string,
        pdfFileName: string
    ): Observable<File> {
        return new Observable((observer) => {
            const element = document.getElementById(elementId);
            if (element) {
                html2canvas(element).then((canvas: any) => {
                    const imgWidth = 215.9; // width for letter format
                    const pageHeight = 279.4;
                    const imgHeight = (canvas.height * imgWidth) / canvas.width;
                    let heightLeft = imgHeight;
                    let position = 0;
                    heightLeft -= pageHeight;
                    const doc = new jsPDF('p',
                        'mm',
                        'letter');
                    doc.addImage(canvas,
                        'PNG',
                        0,
                        position,
                        imgWidth,
                        imgHeight,
                        '',
                        'FAST');
                    while (heightLeft >= 0) {
                        position = heightLeft - imgHeight;
                        doc.addPage();
                        doc.addImage(canvas,
                            'PNG',
                            0,
                            position,
                            imgWidth,
                            imgHeight,
                            '',
                            'FAST');
                        heightLeft -= pageHeight;
                    }
                    const pdfOutput = doc.output('blob');
                    const pdfFile = new File([pdfOutput], pdfFileName, {type: 'application/pdf'});
                    observer.next(pdfFile);
                    observer.complete();
                });
            } else {
                observer.error(new Error('Element not found'));
            }
        });
    }
}
