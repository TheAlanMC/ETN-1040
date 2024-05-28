import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";

export interface ReportDto {
    reportId: number;
    user: UserDto;
    file: FileDto;
    reportName: string;
    reportType: string;
    reportStartDate: Date;
    reportEndDate: Date;
    txDate: Date;
}

