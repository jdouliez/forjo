import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AdminHoneyService {
  private apiUrl = '/api/log/';

  constructor(private http: HttpClient) { }

  getLogs(name: string): Promise<any> {
    return this.http.get(this.apiUrl + name, { responseType: 'text', headers: new HttpHeaders({ 'Authorization': 'Bearer ' + localStorage.getItem('token') }) })
      .toPromise()
      .then(response => {
        response as any
        return response;
      })
      .catch (this.handleError);
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
