import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-userinterface',
  templateUrl: './userinterface.component.html',
  styleUrls: ['./userinterface.component.scss']
})
export class UserinterfaceComponent implements OnInit {
  myColor = 'red';
  constructor(public router: Router) { }

  ngOnInit() {
  }
}
