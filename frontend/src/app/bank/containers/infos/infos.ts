export class Infos {
    surname: string;
    firstname: string;
    address: string;
    account: Array<String>;
  
    constructor(surname: string, firstname: string, address: string, account: Array<String>) { 
      this.surname = surname;
      this.firstname = firstname;
      this.address = address;
      this.account = account;
    }
}
