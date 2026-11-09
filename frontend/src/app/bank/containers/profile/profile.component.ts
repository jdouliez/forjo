import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { ProfileService } from './profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  public picture_base64: SafeResourceUrl;
  private error: String;

  public infoForm: FormGroup;
  public pictureForm: FormGroup;

  private url_pattern = '((https?)|(file)|(ftp))://([\\da-z.-\/:]+)[/\\w .-]*(\\?+[&a-zA-Z0-9]*)*(\.png|\.jpg|\.jpeg){1,}$';
  private simple_name_pattern = '[a-zA-Z-\s]{3,}';

  private infoUpdateFailure = false;
  private infoUpdateSuccess = false;

  private pictureUpdateFailure = false;
  private pictureUpdateSuccess = false;

  constructor(private profileService: ProfileService,
    public router: Router,
    private appService: AppService,
    private _sanitizer: DomSanitizer,
    private formBuilder: FormBuilder) {
  }

  ngOnInit() {

    this.infoForm = this.formBuilder.group({
      id: ["", Validators.required],
      firstname: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(30), Validators.pattern(this.simple_name_pattern)]],
      lastname: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(30), Validators.pattern(this.simple_name_pattern)]],
      address: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    });

    this.pictureForm = this.formBuilder.group({
      id: ["", Validators.required],
      picture_url: ["", [Validators.required, Validators.minLength(10), Validators.maxLength(200), Validators.pattern(this.url_pattern)]],
    });

    this.appService.verify(localStorage.getItem('token'))
      .then(res => {
        res.status;
        if (res.status === 401)
          this.router.navigate(['/']);
        else {
          this.profileService.getProfileInfos()
            .then(data => {

              this.infoForm.controls['id'].setValue(data['id']);
              this.infoForm.controls['firstname'].setValue(data['firstname']);
              this.infoForm.controls['lastname'].setValue(data['lastname']);
              this.infoForm.controls['address'].setValue(data['address']);

              this.pictureForm.controls['id'].setValue(data['id']);
              this.pictureForm.controls['picture_url'].setValue(data['picture_url']);

              if (data && data['picture_base64']) {
                this.picture_base64 = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + data['picture_base64']);
              }
              else {
                this.picture_base64 = "../assets/img/profilepicture.jpg"
              }
            })
            .catch(error => {
              if (error == undefined) {
                this.router.navigate(['/'])
              }
            });
        }
      });
  }

  modifyPicture() {

    let data = this.pictureForm.getRawValue();

    this.profileService.updateProfileInfos(data)
      .then(data => {
        if (data && data['picture_base64']) {
          console.log("Image data received : ");
          console.log(data['picture_base64']);
          this.picture_base64 = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + data['picture_base64']);
        }
        else {
          this.picture_base64 = "../assets/img/profilepicture.jpg"
        }

        this.pictureUpdateFailure = false;
        this.pictureUpdateSuccess = true;
      })
      .catch(error => {
        this.pictureUpdateFailure = true;
        this.pictureUpdateSuccess = false;
        console.log(error);
      });
  }

  modifyInfos() {

    let data = this.infoForm.getRawValue();

    this.profileService.updateProfileInfos(data)
      .then(data => {
        this.infoUpdateSuccess = true;
        this.infoUpdateFailure = false;
      })
      .catch(error => {
        this.infoUpdateFailure = true;
        this.infoUpdateSuccess = false;
        console.log(error);
      });
  }
}
