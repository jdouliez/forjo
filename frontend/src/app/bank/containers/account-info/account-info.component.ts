import { Component, OnInit } from '@angular/core';
import { AccountInfoService } from './account-info.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from "../../../app.service";

@Component({
  selector: 'app-account-info',
  templateUrl: './account-info.component.html',
  styleUrls: ['./account-info.component.scss']
})


export class AccountInfoComponent implements OnInit {

  public accounts: Array<any> = [];
  public isInjected = false;
  public exampleQuery = 'SELECT * FROM ACCOUNT WHERE ID = ';
  public queryParam: String;

  constructor(private route: ActivatedRoute, public router: Router,
    private appService: AppService, private accountInfoService: AccountInfoService) { }

  ngOnInit() {
    const token = localStorage.getItem('token');
    this.appService.verify(localStorage.getItem('token'))
      .then(res => {
        if (res.status === 401) {
          this.router.navigate(['/']);
        }
        this.route.params.subscribe(params => {
          if (params && params['id']) {
            if (isNaN(params['id'])) {
              this.isInjected = true;
              this.queryParam = params['id'];

            } else {
              this.isInjected = false;
            }

            this.accountInfoService.getAccount(token, params['id'])
              .then(account => {
                this.accounts = account;
              });
          }
        });
      });
  }
}
