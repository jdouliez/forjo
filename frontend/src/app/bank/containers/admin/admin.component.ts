import { Component, OnInit } from '@angular/core';
import { AdminService } from './admin.service';
import { DomSanitizer } from '@angular/platform-browser'
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
  providers: [AdminService]
})

export class AdminComponent implements OnInit {
  public adminpanel: any;
  constructor(private adminService: AdminService, private sanitizer: DomSanitizer,
    public router: Router, private appService: AppService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
        this.router.navigate(['/'])
    });

    this.adminService.admin()
    .then (res => {
      this.adminpanel = true;
    });
  }
}
