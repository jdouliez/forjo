import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NavService {
  private apiUrl = '/api/advisor/user';

  constructor(private http: HttpClient) { }

  getAdvisor() {
    return this.http.get(this.apiUrl, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as any)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
