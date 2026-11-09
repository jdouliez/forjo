import { Component, OnInit } from '@angular/core';
import { AddAccountService } from './add-account.service';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-add-account',
  templateUrl: './add-account.component.html',
  styleUrls: ['./add-account.component.scss'],
  providers: []
})
export class AddAccountComponent implements OnInit {

  /*
    A4 XXE add account
  */
  public error: boolean;
  private xmlFile: File;
  public receivedFile:any[];

  constructor(public addAccountService: AddAccountService, public router: Router, private appService: AppService) {
  }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
      .then(res => {
        res.status;
        if (res.status === 401)
        this.router.navigate(['/'])
      })
  }

  handleFileInput(files: FileList) {
    this.xmlFile = files.item(0);
  }

  downloadAccountTemplateFile(){
    let link = document.createElement("a");
    link.download = "account-template.xml";
    link.href = "assets/files/account-template.xml";
    link.click();
  }

  upload(): Promise<any> {
    let fileReader = new FileReader();

    return new Promise((resolve, reject) => {
      fileReader.onerror = () => {
        fileReader.abort();
        reject(new DOMException("Problem parsing input file."));
      };

      fileReader.onload = () => {
        resolve(fileReader.result)
      }
      fileReader.readAsText(this.xmlFile);
    })
      .then(data => {
        this.addAccountService.postFile(data)
          .then(res => {
            this.error = false;
            this.receivedFile = res;
          })
          .catch(error =>
            this.error = true)
      })
      .catch(error =>
        this.error = true)
  }
}
