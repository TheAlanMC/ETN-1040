import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";

export interface ReplacedPartDto {
    replacedPartId: number;
    replacedPartDescription: string;
    txDate: Date;
    replacedPartFiles: FileDto[];
}
