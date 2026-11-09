import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'
import { NavService } from './nav.service';
import { Cookie } from 'ng2-cookies';
import { AppService } from '../../../app.service';
import { HeaderService } from '../header/header.service';
import { constantService } from '../../../constant.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {
  public token;
  private idAdvisor: number;

  constructor(private router: Router, private navService: NavService, private appService: AppService, private headerService: HeaderService) { }

  public changeLanguage(language: string) {
      var currentLocal = localStorage.getItem("local") ? localStorage.getItem("local") : constantService.app.LANG_APP_DEFAULT;
      if (currentLocal == language) {
          return;
      }
      if (constantService.app.LANG_AVAILABLE.indexOf(language) === -1) {
          console.log("This language is not yet supported");
          return;
      }
      console.log("Switching pages language to " + language);
      localStorage.setItem('local', language);
      this.headerService.updateLanguage(language);
  }

  ngOnInit() {
      if (localStorage.getItem('token')) {
        var token = localStorage.getItem('token');
        this.appService.verify(token)
        .then(res => {
            if (res.status === 401) {
                this.token = false;
                this.removeUserStorage();
            } else {
                this.token = true;
                this.navService.getAdvisor()
                .then( res => {
                    this.idAdvisor = res;
                })
            }
        })
      }
  }
  
  removeUserStorage() {
    if (localStorage.getItem('token')) {
      localStorage.removeItem('token');
    }
    Cookie.delete("user");
  }
  
  logout() {
    this.removeUserStorage();
    window.location.reload()
  }
}
