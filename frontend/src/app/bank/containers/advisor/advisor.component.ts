import { Component, OnInit, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser'
import { AdvisorService } from './advisor.service'
import { Advisor } from './advisor';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';
import { interval } from 'rxjs';
import { takeWhile } from 'rxjs/operators';

@Component({
  selector: 'app-advisor',
  templateUrl: './advisor.component.html',
  styleUrls: ['./advisor.component.scss'],
  providers: [AdvisorService]
})

export class AdvisorComponent implements OnInit {
  public lastMessage: any;
  public advisor: Advisor[];
  public showFaq = false;

  public isInjected = false;
  public exampleQuery = "SELECT * FROM ADVISOR WHERE ID = ";
  public queryParam: String;

  constructor(private route: ActivatedRoute, private sanitizer: DomSanitizer, private advisorService: AdvisorService, public router: Router, private appService: AppService) {
      //Check message status every 30 secondes
      setInterval(() => { this.checkMessage(); }, 1000 * 30);
  }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      if (res.status === 401) this.router.navigate(['/']);
    })

    this.route.params.subscribe(params => {
      if (params && params['id']) {
        if (isNaN(params['id'])) {
          this.isInjected = true;
          this.queryParam = params['id']

        } else {
          this.isInjected = false;
        }

        this.advisorService.getAdvisor(params['id'])
        .then(res => {
          this.advisor = res;
        })
      }
    });
  }

  public checkMessage() {
    if (this.lastMessage) {
      console.log("Checking last message status..");

      this.advisorService.getMessage(this.lastMessage.idMessage).then(data => {
        this.lastMessage = data;
      })
      .catch(error => {
        console.log(error);
      });
    }
  }

  public unsanitize(html: string) {
    return this.sanitizer.bypassSecurityTrustHtml(html)
  }

  public sendMessage(msg: string, obj: string) {
    this.route.params.subscribe(params => {
      if (params && params['id']) {
        this.advisorService.sendMessage(msg, obj, params['id']).then(data => {
          this.lastMessage = data;
        })
        .catch(error => {
          console.log(error);
        });
      }
    });
  }
}
