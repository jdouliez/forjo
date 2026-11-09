import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, Subject } from 'rxjs';
import { take } from 'rxjs/operators';
import { Router } from '@angular/router';

import { HeaderService } from './header.service';
import { constantService } from '../../../constant.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  language = "";
  public static subject = new Subject();
  title = 'Gem & LeCash Banking corporation';
  sub: Subscription;
  constructor(private headerService: HeaderService, public router: Router) {

  }

  ngOnInit() {
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  public changeLanguage(language: string) {

      //Retrieve current local value
      var currentLocal = localStorage.getItem("local") ? localStorage.getItem("local") : constantService.app.LANG_APP_DEFAULT;

      //Check if the selected language is the same or not
      if (currentLocal == language) {
          return
      }

      //Check if language is in list of available language
      if (constantService.app.LANG_AVAILABLE.indexOf(language) === -1) {
        console.log("This language is not yet supported");
        return
      }

      console.log("Switching pages language to " + language);

      //Set the local value
      localStorage.setItem('local', language);

      //Update language
      this.headerService.updateLanguage(language);
  }
}
