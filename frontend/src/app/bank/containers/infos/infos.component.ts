import { Component, OnInit } from '@angular/core';
import { Infos } from "./infos";
import { InfosService } from "./infos.service";
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-infos',
  templateUrl: './infos.component.html',
  styleUrls: ['./infos.component.scss'],
  providers: [InfosService]
})

/*
  A8 Deserialisation cookie
*/

export class InfosComponent implements OnInit {
  public infos: Infos[];

  constructor(private router: Router, private infosService: InfosService, private appService: AppService, private sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
      this.router.navigate(['/'])
    })
    .catch( error => {
      console.log(error);
    });


    this.infosService.findAll()
    .then(
      infos => {
        this.infos = infos;
      }
    )
    .catch( error => {
      console.log(error);
    });
  }


  public unsanitize(html: string) {
    return this.sanitizer.bypassSecurityTrustHtml(html)
  }
}
