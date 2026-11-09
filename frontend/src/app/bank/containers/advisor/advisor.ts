export class Advisor {
    idAdvisor: number;
    firstnameAdvisor: string;
    lastnameAdvisor: string;
    office: string;

    constructor(idAdvisor: number, firstnameAdvisor: string, lastnameAdvisor: string, office: string) {
        this.idAdvisor = idAdvisor;
        this.firstnameAdvisor = firstnameAdvisor;
        this.lastnameAdvisor = lastnameAdvisor;
        this.office = office;
    }
}
