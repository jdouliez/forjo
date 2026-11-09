import { Component, OnInit } from '@angular/core';
import { AdminMessageService } from './admin-message.service'
import { DomSanitizer } from '@angular/platform-browser'
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';

@Component({
  selector: 'app-admin-message',
  templateUrl: './admin-message.component.html',
  styleUrls: ['./admin-message.component.scss']
})
export class AdminMessageComponent implements OnInit {
  public messages: Array<any>;

  constructor(private advisorService: AdminMessageService, private sanitizer: DomSanitizer,
    public router: Router, private appService: AppService) { }

  ngOnInit() {
    this.appService.verify(localStorage.getItem('token'))
    .then(res => {
      res.status;
      if (res.status === 401)
        this.router.navigate(['/'])
    });

    this.advisorService.getAdvisor()
    .then(res => {
      this.messages = res;
      for (var i in this.messages) {
        this.messages[i].subject = this.sanitizer.bypassSecurityTrustHtml(this.messages[i].subject);
        this.messages[i].content = this.sanitizer.bypassSecurityTrustHtml(this.messages[i].content);
      }
    })
    .catch(error => {
      if (error == undefined) {
        this.router.navigate(['/'])
      }
    });
  }

  reply(messageId: Number) {
    console.log("Replying to message (ID : \"" + messageId + "\")");

    this.advisorService.replyToMessage(messageId)
    .then(res => {
      for (var i in this.messages) {
        if (this.messages[i].idMessage == messageId) {
          this.messages[i] = res;
        }
      }
    })
    .catch(error => {
      if (error == undefined) {
        this.router.navigate(['/'])
      }
      else {
        console.log(error);
      }
    });
  }

  delete(messageId: Number) {
    console.log("Deleting message (ID : \"" + messageId + "\")");

    this.advisorService.deleteMessage(messageId)
    .then(res => {
      this.messages = this.messages.filter(item => item.idMessage !== messageId);
    })
    .catch(error => {
      if (error == undefined) {
        this.router.navigate(['/'])
      }
      else {
        console.log(error);
      }
    });
  }
}
