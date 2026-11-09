import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { Advisor } from './advisor';

@Injectable({
  providedIn: 'root'
})
export class AdvisorService {
  private apiAdvisorUrl = '/api/advisor/';
  private apiMsgUrl = '/api/messages/'

  constructor(private http: HttpClient) { }

  sendMessage(msg: string, obj: string, advisorId: number) {
      var message = {
          "to" : advisorId,
          "subject" : obj,
          "content" : msg
      }
      return this.http.post(this.apiMsgUrl, message, {headers: new HttpHeaders({'Content-type': 'application/json;charset=UTF-8', 'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .catch(this.handleError);
  }

  getAdvisor(id : number): Promise<Array<Advisor>> {
    return this.http.get(this.apiAdvisorUrl + id,  {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as Advisor[])
      .catch(this.handleError);
  }

  getMessage(id : number): Promise<Array<Advisor>> {
    return this.http.get(this.apiMsgUrl + id,  {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as Advisor[])
      .catch(this.handleError);
  }


  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }

}
