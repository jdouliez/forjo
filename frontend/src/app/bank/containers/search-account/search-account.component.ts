import { Component, OnInit } from '@angular/core';
import { SearchAccountService } from './search-account.service';
import { SearchAccount } from './search-account';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-search-account',
  templateUrl: './search-account.component.html',
  styleUrls: ['./search-account.component.scss'],
  providers: [SearchAccountService]
})
export class SearchAccountComponent implements OnInit {
  public files: SearchAccount[];

  constructor(private route: ActivatedRoute, private searchAccountService: SearchAccountService,
    public router: Router, private appService: AppService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
      this.router.navigate(['/'])
    })
    this.route.params.subscribe(params => {
      if (params && params['id']) {
          this.searchAccountService.searchAccount(encodeURIComponent(params['id']))
          .then(
            files => {
              this.files = files;
            }
          )
      }
    });
  }
}
