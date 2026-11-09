import { Component, OnInit } from '@angular/core';
import { RibService } from './rib.service'
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-rib',
  templateUrl: './rib.component.html',
  styleUrls: ['./rib.component.scss'],
  providers: [RibService]
})
export class RibComponent implements OnInit {

  public compteCourant: any;

  constructor(private ribService: RibService, private route: ActivatedRoute,
    public router: Router, private appService: AppService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
        this.router.navigate(['/']);
      else
          this.getCompteCourant();
    })
  }

  getCompteCourant() {
      this.ribService.getCompteCourant().then(response => this.compteCourant = response);
  }

  download(file: string) {
      this.ribService.downloadRib(file);
  }
}
