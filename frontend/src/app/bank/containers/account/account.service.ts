import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = '/api/account/';
  
  constructor(private http: HttpClient) { }
  
  getAccounts(token : string) : Promise<Array<any>> {
      return this.http.get(this.apiUrl + "user", {headers: new HttpHeaders({ 'Authorization': 'Bearer ' + localStorage.getItem('token') }) })
          .toPromise()
          .then(response => response as any[])
          .catch(this.handleError)
  }
  
  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
