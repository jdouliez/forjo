import {Component, Inject, OnInit, Optional} from '@angular/core';
import {RESPONSE} from '@nguniversal/express-engine/tokens';

import {NotFoundService} from './not-found.service'
@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent implements OnInit {

    constructor(@Optional()
    @Inject(RESPONSE)
    private request: any){}

	  ngOnInit() {
	    document.getElementsByTagName('title')[0].text = "Page not found - Error 404";


	  }

}


