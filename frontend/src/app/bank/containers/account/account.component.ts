import { Component, OnInit } from '@angular/core';
import { AccountService } from './account.service';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})


export class AccountComponent implements OnInit {

  public accounts: Array<any>;

  constructor(public router: Router, private appService: AppService, private accountService: AccountService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
      .then(res => {
        res.status;
        if (res.status === 401)
          this.router.navigate(['/'])
        else
            var token = localStorage.getItem('token');
            this.accountService.getAccounts(token)
              .then(response => {
                  this.accounts = response;
                  for (var i in this.accounts) {
                      var doc = this.accounts[i].document.split('.')[0];
                      this.accounts[i].document = doc;
                  }
              })
      })
  }

}
