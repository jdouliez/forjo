import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Md5 } from 'ts-md5/dist/md5';

import { constantService } from '../../../constant.service';

@Injectable({
  providedIn: 'root'
})
export class IndexService {
  private apiUrl = '/api/authenticate';
  private registerUrl = '/api/registration';
  public static langUrl = '/api/lang';
  private resetPasswordUrl = '/api/resetPassword';

  public constructor(public http: HttpClient) {
  }

  adminconnect() {
    return this.http.get(`${this.apiUrl}/honeypot`).toPromise();
  }

  connection(email: string, password: string) {
    var data = {
        "username" : email,
        "password" : password
    }
    return this.http.post(`${this.apiUrl}`, data, { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) })
      .toPromise()
      .then(response => response as any)
      .catch(err => err as any)
  }

  register(email: string, password: string, prenom: string, nom: string, adresse: string, secretAnwser: string) {
    var data = { "email": email, "password": password, "matchingpassword": password, "firstname": prenom, "lastname": nom, "address": adresse, "secretAnswer": secretAnwser };
    return this.http.post(`${this.registerUrl}`, data, { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) })
      .toPromise()
      .then(response => response as any)
      .catch(this.handleError);
  }

  resetPassword(email: string, password: string, secretAnwser: string) {
    var data = { "email": email, "newPassword": password, "secretAnswer": secretAnwser };
    return this.http.post(`${this.resetPasswordUrl}`, data, { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) })
      .toPromise()
      .then(response => response as any)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
