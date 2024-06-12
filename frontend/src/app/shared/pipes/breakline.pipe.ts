import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'breakLine'
})
export class BreakLinePipe implements PipeTransform {
    transform(value: string): string {
        return value.replace(/\n/g,
            '<br/>');
    }
}
