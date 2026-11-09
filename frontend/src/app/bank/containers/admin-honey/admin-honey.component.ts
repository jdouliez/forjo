import { Component, OnInit } from '@angular/core';
import { AdminHoneyService } from './admin-honey.service';
import { Router } from '@angular/router';
import { BankService } from '../../bank.service';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-admin-honey',
  templateUrl: './admin-honey.component.html',
  styleUrls: ['./admin-honey.component.scss']
})
export class AdminHoneyComponent implements OnInit {

  public fileContent: any;

  constructor(private honepotLogService: AdminHoneyService, public router: Router, private appService: AppService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
      .then(res => {
        res.status;
        if (res.status === 401)
          this.router.navigate(['/'])
      });

    this.honepotLogService.getLogs("honeypot")
      .then(res => {
        this.fileContent = res;
      })
      .catch(error => {
        if (error == undefined) {
          this.router.navigate(['/'])
        }
      })
  }
}
