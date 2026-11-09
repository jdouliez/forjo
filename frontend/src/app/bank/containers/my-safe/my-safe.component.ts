import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-my-safe',
  templateUrl: './my-safe.component.html',
  styleUrls: ['./my-safe.component.scss']
})
export class MySafeComponent implements OnInit {

  private safeSecretCodeApi = "/api/safe/secretcode";

  constructor(private http: HttpClient) { }

  private success: Boolean;
  private error: Boolean;

  ngOnInit() {
    this.success = false;
    this.error = false;
  }

  getSecretCode() {
    this.http.get(this.safeSecretCodeApi, { responseType: 'json' })
      .toPromise()
      .then(data => {
        this.error = false;
        this.success = true;
        alert("Your secret code has been retrieved.\nSave it carefully: \"" +  data['message'] + "\"");
      })
      .catch(error => {
        this.error = true;
        this.success = false;
      });
  }
}
