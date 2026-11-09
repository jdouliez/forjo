import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AdminMessageService {
  private apiUrl = '/api/messages';
  private apiReplyUrl = this.apiUrl + '/reply/';

  constructor(private http: HttpClient, public router: Router) { }

  getAdvisor(): Promise<Array<any>> {
    return this.http.get(this.apiUrl, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as any[])
      .catch(this.handleError);
  }

  deleteMessage(id: Number): Promise<Array<any>> {
    return this.http.delete(this.apiUrl + "/" + id, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as any[])
      .catch(this.handleError);
  }

  replyToMessage(id: Number): Promise<Array<any>> {
    return this.http.get(this.apiReplyUrl + id, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as any[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    if (error.status == 403) {
        return Promise.reject();
    } else {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
  }
}
