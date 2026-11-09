import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';
import { IndexDevService } from './index-dev.service'
@Component({
  selector: 'app-index-dev',
  templateUrl: './index-dev.component.html',
  styleUrls: ['./index-dev.component.scss']
})
export class IndexDevComponent implements OnInit {

  public cmdResult:any[];
  public cmd;

  constructor(public router: Router, private appService: AppService, private indexDevService: IndexDevService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
      this.router.navigate(['/'])
    })
  }

  public exec() {
    if (this.cmd) {
    this.indexDevService.execCommand(this.cmd)
    .then(res =>this.cmdResult = res)
    }
  }
}
