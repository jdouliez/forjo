import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AddAccountService {
  private apiUrl = '/api/account';

  constructor(private http: HttpClient) { }

  postFile(xml: any): Promise<String[]> {
    return this.http.post(`${this.apiUrl}`, xml,{ headers: new HttpHeaders({'Content-Type': 'application/xml', 'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as String[])
      .catch((e) => this.handleError(e));
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }

}
