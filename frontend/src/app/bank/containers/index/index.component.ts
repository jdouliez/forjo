import { Component, OnInit } from '@angular/core';
import { NgbCarouselConfig } from '@ng-bootstrap/ng-bootstrap';
import { AppService } from '../../../app.service';
import { IndexService } from "./index.service";

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  providers: [IndexService]
})

export class IndexComponent implements OnInit {
  model: any = {};
  emailPattern = "^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$";

  //connexion
  private email: string;
  private password: string;
  private prenom: string;
  private nom: string;
  private adresse: string;

  //var frontend
  private error: boolean;
  private errorMessage: string;
  public token: string;
  public register: boolean;
  public login: boolean;

  private lang: string;

  private passwordReset : boolean;
  private successPasswordReset : boolean;

  constructor(config: NgbCarouselConfig, private indexService: IndexService, private appService: AppService) {
    config.interval = 2000;
    config.wrap = true;
    config.keyboard = true;
  }

  ngOnInit() {
    this.successPasswordReset = false;
    this.passwordReset = false;
    this.login = false;
    this.register = true;
    if (localStorage.getItem("token")) {
      this.appService.verify(localStorage.getItem('token'))
      .then(res => {
            if (res.status !== 401) {
                this.token = localStorage.getItem("token");
            } else {
                this.token = null;
                location.reload();
            }
    })
      this.token = localStorage.getItem("token");
    }
  }

  private connection(check) {
    if (check) {
      this.error = true;
      this.indexService.adminconnect();
    }
    else
      this.indexService.connection(this.email, this.password)
        .then(
          res => {
            if (res.status === 401) {
                this.error = true;
            }
            else {
                this.token = res.token;
                localStorage.setItem("token", this.token);
                window.location.reload();
            }
          }
        )
  }

  private registerForm() {
    this.indexService.register(this.model.email, this.model.password, this.model.prenom, this.model.nom, this.model.adresse, this.model.secretAnswer)
      .then(
        res => {
          console.log('flag');-
          console.log(res);

          if (res.status === "OK") {
            this.token = res.token;
            localStorage.setItem("token", this.token);
            window.location.reload();
          } else {
              this.error = true;
              this.errorMessage = res.message;
              console.log(this.errorMessage);
          }
        }
      ).catch(
        error => {
          this.errorMessage = "An error occurred";
          this.error = true;
        }
      )
  }

  private resetPasswordForm() {
    this.indexService.resetPassword(this.model.email, this.model.password, this.model.secretAnswer)
      .then(
        res => {
          console.log(res);

          if (res.status === "OK") {
            this.error = false;
            this.login = false;
            this.register = true;
            this.passwordReset = false;
            this.successPasswordReset = true;
          } else {
              this.error = true;
              this.errorMessage = "An error occurred";
              console.log(this.errorMessage);
          }
        }
      ).catch(
        error => {
          this.errorMessage = "An error occurred";
          this.error = true;
        }
      )
  }

  private eventconnect() {
    this.error = false;
    this.login = false;
    this.register = true;
    this.passwordReset = false;
  }

  private eventregister() {
    this.error = false;
    this.register = false;
    this.passwordReset = false;
    this.login = true;
  }

  private eventresetPassword() {
    this.error = false;
    this.register = false;
    this.login = false;
    this.passwordReset = true;
  }
}
